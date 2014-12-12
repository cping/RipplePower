package org.ripple.power.server.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class FlashPolicyServer extends ChannelInitializer<SocketChannel> {

	public void connect() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100).childHandler(this);
			ChannelFuture f = b.bind(843).sync();
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast("timeout",
				new ReadTimeoutHandler(30, TimeUnit.SECONDS));
		pipeline.addLast("decoder", new PolicyDecoder());
		pipeline.addLast("handler", new FlashPolicyServerHandler());
	}

	@Sharable
	private class FlashPolicyServerHandler extends
			SimpleChannelInboundHandler<ByteBuf> {
		private static final String NEWLINE = "\r\n";
		private static final String FLASH_POLICY_FILE = "<?xml version=\"1.0\"?><cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\"/></cross-domain-policy>";

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
				throws Exception {
			ctx.close();
		}

		private ByteBuf getPolicyFileContents() throws Exception {
			return Unpooled.copiedBuffer(FLASH_POLICY_FILE + NEWLINE,
					CharsetUtil.US_ASCII);
		}

		@SuppressWarnings("unused")
		protected void messageReceived(ChannelHandlerContext ctx, ByteBuf msg)
				throws Exception {
			ByteBuf crossdomain = this.getPolicyFileContents();
			ChannelFuture f = ctx.writeAndFlush(crossdomain);
			f.addListener(ChannelFutureListener.CLOSE);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg)
				throws Exception {
			ByteBuf crossdomain = this.getPolicyFileContents();
			ChannelFuture f = ctx.writeAndFlush(crossdomain);
			f.addListener(ChannelFutureListener.CLOSE);

		}
	}
}
