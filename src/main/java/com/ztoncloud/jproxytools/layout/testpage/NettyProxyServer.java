package com.ztoncloud.jproxytools.layout.testpage;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
public class NettyProxyServer {
  private int startPort;
  private int endPort;
  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private Channel[] channels;
  public NettyProxyServer(int startPort, int endPort) {
    this.startPort = startPort;
    this.endPort = endPort;
    bossGroup = new NioEventLoopGroup();
    workerGroup = new NioEventLoopGroup();
  }
  public void start() throws InterruptedException {
    channels = new Channel[endPort - startPort + 1];
    for (int port = startPort; port <= endPort; port++) {
      final int finalPort = port;
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              pipeline.addLast(new HttpRequestDecoder());
              pipeline.addLast(new HttpObjectAggregator(65536));
              pipeline.addLast(new HttpResponseEncoder());
              pipeline.addLast(new HttpProxyHandler(finalPort));
            }
          });
      ChannelFuture future = bootstrap.bind(port).sync();
      channels[port - startPort] = future.channel();
    }
  }
  public void stop() {
    Future<?> bossFuture = bossGroup.shutdownGracefully();
    Future<?> workerFuture = workerGroup.shutdownGracefully();
    bossFuture.addListener(new GenericFutureListener<Future<Object>>() {
      @Override
      public void operationComplete(Future<Object> future) throws Exception {
        for (Channel channel : channels) {
          if (channel != null && channel.isActive()) {
            channel.close().sync();
          }
        }
      }
    });
    workerFuture.addListener(new GenericFutureListener<Future<Object>>() {
      @Override
      public void operationComplete(Future<Object> future) throws Exception {
        System.out.println("Proxy server stopped.");
      }
    });
  }
  private static class HttpProxyHandler extends ChannelInboundHandlerAdapter {
    private int serverPort;
    public HttpProxyHandler(int serverPort) {
      this.serverPort = serverPort;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      // 对客户端的请求进行处理，并将请求发送到后端的服务。
      // ...
    }
  }
}