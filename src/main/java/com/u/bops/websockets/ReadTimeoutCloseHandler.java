package com.u.bops.websockets;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;

public class ReadTimeoutCloseHandler extends ChannelDuplexHandler {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (cause instanceof ReadTimeoutException) {
			ctx.close();
		} else {
			super.exceptionCaught(ctx, cause);
		}
	}
}
