package org.ripple.power.server.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import javax.swing.JTextArea;

import org.ripple.power.ui.RPComboBox;
import org.ripple.power.utils.DateUtils;

public class MessageClient {
	private EventLoopGroup group = new NioEventLoopGroup();
	private Channel channel;
	private JTextArea messageShow;
	private RPComboBox combobox;
	private String username;
	private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

	public void init(String username, JTextArea messageShow, String ip,
			int port, RPComboBox combobox) throws Exception {
		this.messageShow = messageShow;
		this.username = username;
		this.combobox = combobox;
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(
								new ByteMesDecoder(new MessageRecognizer()))
								.addLast(new MesToByteEncoder());
						pipeline.addLast("LOGGING_HANDLER", LOGGING_HANDLER);
						pipeline.addLast(new GameClientHandler());
					}
				});
		ChannelFuture f = b.connect(ip, port).sync();
		this.channel = f.channel();
		LoginMessage msg = new LoginMessage(username);
		channel.writeAndFlush(msg);
		channel.closeFuture().sync();
	}

	@Sharable
	private class GameClientHandler extends
			SimpleChannelInboundHandler<AMessage> {
		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			super.channelInactive(ctx);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			cause.printStackTrace();
			ctx.close();
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx)
				throws Exception {
			super.channelUnregistered(ctx);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, AMessage msg)
				throws Exception {

			short type = msg.getMessageType();
			switch (type) {
			case MessageType.CS_CHAT: {
				ChatMessage _msg = (ChatMessage) msg;
				messageShow.append(_msg.getUsername() + " "
						+ DateUtils.toDate() + "\n");
				messageShow.append("    " + _msg.getMsg() + "\n");
				break;
			}
			case MessageType.CS_LOGIN: {
				LoginMessage _msg = (LoginMessage) msg;
				if (!username.equalsIgnoreCase(_msg.getUsername())) {
					combobox.addItem(_msg.getUsername());
				}
				break;
			}
			case MessageType.CS_LOGIN_OUT: {
				LoginOutMessage _msg = (LoginOutMessage) msg;
				if (!username.equalsIgnoreCase(_msg.getUsername())) {
					combobox.removeItem(_msg.getUsername());
				}
				break;
			}
			case MessageType.CS_USER_NAME_LIST: {
				LoginUsersMessage _msg = (LoginUsersMessage) msg;
				combobox.removeAllItems();
				for (String name : _msg.getUsers()) {
					if (!username.equalsIgnoreCase(name)) {
						combobox.addItem(name);
					}
				}
				break;
			}
			default:
				break;
			}

		}
	}

	public Channel getChannel() {
		return this.channel;
	}

	public void write(AMessage msg) {
		channel.writeAndFlush(msg);
	}

	public void destory() {
		group.shutdownGracefully();
	}
}
