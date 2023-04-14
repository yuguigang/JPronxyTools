package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.handler.SocksHandler;

import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.handler.DirectClientHandler;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.handler.RelayHandler;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.handler.newBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v4.DefaultSocks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandRequest;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public final class SocksServerConnectHandler extends SimpleChannelInboundHandler<SocksMessage> {

  private static final Logger logger = LoggerFactory.getLogger(SocksServerConnectHandler.class);

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, final SocksMessage message) {
    //Socks4
    if (message instanceof final Socks4CommandRequest request) {
      Promise<Channel> promise = ctx.executor().newPromise();
      promise.addListener(
          (FutureListener<Channel>) future -> {
            final Channel outboundChannel = future.getNow();
            if (future.isSuccess()) {
              ChannelFuture responseFuture = ctx.channel().writeAndFlush(
                  new DefaultSocks4CommandResponse(Socks4CommandStatus.SUCCESS));

              responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                ctx.pipeline().remove(SocksServerConnectHandler.this);
                outboundChannel.pipeline()
                    .addLast(new RelayHandler(ctx.channel()));
                ctx.pipeline().addLast(new RelayHandler(outboundChannel));
              });
            } else {
              ctx.channel().writeAndFlush(
                  new DefaultSocks4CommandResponse(
                      Socks4CommandStatus.REJECTED_OR_FAILED));
              SocksServerUtils.closeOnFlush(ctx.channel());
            }
          });
      Bootstrap bootstrap = new newBootstrap(request.dstAddr(), request.dstPort()).creat();
      if (bootstrap != null) {
        bootstrap.group(ctx.channel().eventLoop());
        bootstrap.connect(request.dstAddr(), request.dstPort())
            .addListener((ChannelFutureListener) future -> {
              if (future.isSuccess()) {
                future.channel().pipeline()
                    .addLast(new DirectClientHandler(promise));
                // Connection established use handler provided results
              } else {
                // Close the connection if the connection attempt has failed.
                ctx.channel().writeAndFlush(
                    new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED)
                );
                SocksServerUtils.closeOnFlush(ctx.channel());
              }
            });
      } else {
        ctx.close();
      }
      //Socks5
    } else if (message instanceof final Socks5CommandRequest request) {

      logger.debug("开始创建管道，Type ：{} ,地址： {} ，端口： {}", request.dstAddrType(), request.dstAddr(),
          request.dstPort());
      // Create a new promise object
Promise<Channel> promise = ctx.executor().newPromise();
      promise.addListener(
          (FutureListener<Channel>) future -> {
            final Channel outboundChannel = future.getNow();
            if (future.isSuccess()) {
              logger.debug("管道已连通，Type ：{} ,地址： {} ，端口： {}", request.dstAddrType(), request.dstAddr(),
                  request.dstPort());
              ChannelFuture responseFuture =
                  ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                      Socks5CommandStatus.SUCCESS,
                      request.dstAddrType(),
                      request.dstAddr(),
                      request.dstPort()));

              responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                ctx.pipeline().remove(SocksServerConnectHandler.this);
                outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                ctx.pipeline().addLast(new RelayHandler(outboundChannel));
              });
            } else {
              ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                  Socks5CommandStatus.FAILURE, request.dstAddrType()));
              SocksServerUtils.closeOnFlush(ctx.channel());
            }
          });
      Bootstrap bootstrap = new newBootstrap(request.dstAddr(), request.dstPort()).creat();
      if (bootstrap != null) {

        bootstrap.group(ctx.channel().eventLoop());
        bootstrap.connect(request.dstAddr(), request.dstPort())
            .addListener((ChannelFutureListener) future -> {
              if (future.isSuccess()) {
                future.channel().pipeline()
                    .addLast(new DirectClientHandler(promise));
                // Connection established use handler provided results
              } else {
                // Close the connection if the connection attempt has failed.
                ctx.channel().writeAndFlush(
                    new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE,
                        request.dstAddrType()));
                SocksServerUtils.closeOnFlush(ctx.channel());
              }
            });
      } else {
        ctx.close();
      }
      //未知Socks协议，直接关闭
    } else {
      ctx.close();
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    SocksServerUtils.closeOnFlush(ctx.channel());
  }
}
