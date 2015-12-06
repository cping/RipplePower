package org.ripple.power.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JTextField;

import org.ripple.power.server.chat.AMessage;
import org.ripple.power.server.chat.ByteMesDecoder;
import org.ripple.power.server.chat.ChatMessage;
import org.ripple.power.server.chat.HeartHandler;
import org.ripple.power.server.chat.LoginMessage;
import org.ripple.power.server.chat.LoginOutMessage;
import org.ripple.power.server.chat.LoginUsersMessage;
import org.ripple.power.server.chat.MesToByteEncoder;
import org.ripple.power.server.chat.MessageRecognizer;
import org.ripple.power.server.chat.MessageType;
import org.ripple.power.ui.RPComboBox;

public class P2PServer extends ChannelInitializer<SocketChannel> {

	private final ChannelGroup channels = new DefaultChannelGroup(
			GlobalEventExecutor.INSTANCE);
	protected final BlockingQueue<AMessage> queue = new LinkedBlockingQueue<AMessage>();
	private ExecutorService EXECUTOR = Executors.newCachedThreadPool();
	private LinkList userLinkList;
	private RPComboBox combobox;

	public P2PServer(RPComboBox combobox, final JTextField sysMessage) {
		this.combobox = combobox;
		final RPComboBox co = combobox;
		EXECUTOR.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						AMessage msg = queue.take();
						short type = msg.getMessageType();
						switch (type) {
						case MessageType.CS_CHAT: {
							ChatMessage _msg = (ChatMessage) msg;
							short channelType = _msg.getType();
							if (channelType == 2) {
								broadcasts(_msg);
								sysMessage.setText("");
							} else {
								Node node = userLinkList.findUser(_msg
										.getToUser());
								try {
									node.channel.writeAndFlush(_msg);
								} catch (Exception e) {
									e.printStackTrace();
								}
								sysMessage.setText("");
							}
							break;
						}
						case MessageType.CS_LOGIN: {
							LoginMessage _msg = (LoginMessage) msg;
							co.addItem(_msg.getUsername());
							broadcasts(new LoginUsersMessage(userLinkList
									.users()));
							break;
						}
						case MessageType.CS_LOGIN_OUT: {
							LoginOutMessage _msg = (LoginOutMessage) msg;
							broadcasts(_msg);
							co.removeItem(_msg.getUsername());
							break;
						}
						default:
							break;
						}
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	private final LoggingHandler LOGGING_HANDLER = new LoggingHandler();
	private final ServerHandler serverHandler = new ServerHandler();

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new ByteMesDecoder(new MessageRecognizer())).addLast(
				new MesToByteEncoder());
		pipeline.addLast("LOGGING_HANDLER", LOGGING_HANDLER);
		pipeline.addLast("handler", serverHandler);
		pipeline.addLast("idleStateHandler", new IdleStateHandler(5, 5, 8,
				TimeUnit.SECONDS));
		pipeline.addLast("heartHandler", new HeartHandler());
	}

	EventLoopGroup bossGroup = new NioEventLoopGroup();
	EventLoopGroup workerGroup = new NioEventLoopGroup();

	public void connect(int port, LinkList userLinkList) throws Exception {
		this.userLinkList = userLinkList;
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(this);
		ChannelFuture f = b.bind(port).sync();
		f.channel().closeFuture().sync();
	}

	public void broadcasts(AMessage msg) {
		channels.writeAndFlush(msg);
	}

	public void addChannel(Channel channel) {
		channels.add(channel);
	}

	public void stopServer() throws Exception {
		EXECUTOR.shutdown();
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	@Sharable
	private class ServerHandler extends SimpleChannelInboundHandler<AMessage> {

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}

		private AttributeKey<Node> STATE = AttributeKey.valueOf("client");

		@Override
		public void channelRegistered(final ChannelHandlerContext ctx) {
			Channel channel = ctx.channel();
			Node client = new Node();
			channel.attr(STATE).setIfAbsent(client);
			client.channel = channel;
			channels.add(channel);
			userLinkList.addUser(client);
			ctx.fireChannelRegistered();
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			Channel channel = ctx.channel();
			channels.remove(channel);
			Node client = channel.attr(STATE).get();
			userLinkList.delUser(client);
			combobox.removeItem(client.username);
			super.channelUnregistered(ctx);
			broadcasts(new LoginOutMessage(client.username));
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, AMessage msg)
				throws Exception {
			Channel channel = ctx.channel();
			if (msg instanceof LoginMessage) {
				Node client = channel.attr(STATE).get();
				client.username = ((LoginMessage) msg).getUsername();
			}
			queue.add(msg);

		}
	}
}
