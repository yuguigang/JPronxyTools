package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.handler;

import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.NettyBootstrapFactory;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.UpstreamProxy;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.rule.Action;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.rule.dstRule;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yugang
 * @create 2023/3/21 2:47
 */
public record newBootstrap(String inetHost, int inetPort) {

  private static final Logger logger = LoggerFactory.getLogger(newBootstrap.class);

  public newBootstrap(final String inetHost, final int inetPort) {
    this.inetHost = Objects.requireNonNull(inetHost, "inetHost");
    this.inetPort = ObjectUtil.checkInRange(inetPort, 1, 65_535, "inetPort");

  }

  /**
   * 根据规则构建Bootstrap
   *
   * @return {@link Bootstrap}
   */
  public Bootstrap creat() {
    final Bootstrap b = NettyBootstrapFactory.initBootstrap();

    //将请求Host、Port去比较。
    Action action = dstRule.match(inetHost, inetPort);
    switch (action) {

      case REJECT -> {
        logger.debug("Rule {}", Action.REJECT);
        //如果规则是拦截，这里返回null，后面判断null后会关闭管道。
        return null;
      }

      case DIRECT -> {
        logger.debug("Rule {}", Action.DIRECT);
        b.handler(new ChannelInitializer<>() {
          @Override
          protected void initChannel(@NotNull Channel ch) {
            //如果直连，这里不加入处理
          }
        });

      }

      case PROXY -> {
        logger.debug("Rule {}", Action.PROXY);
        b.handler(new ChannelInitializer<>() {
          @Override
          protected void initChannel(@NotNull Channel ch) {
            ch.pipeline().addLast(addProxy());//加入代理
            ch.pipeline().addLast(addUpstreamProxy());//加入前置代理
          }


        });

      }

      case PRE_PROXIES -> {
        logger.debug("Rule {}", Action.PRE_PROXIES);
        b.handler(new ChannelInitializer<>() {
          @Override
          protected void initChannel(@NotNull Channel ch) {
            ch.pipeline().addLast(addUpstreamProxy());//只加入前置代理，而不经过代理。
          }


        });
      }

      case DEFAULT -> {
      }

      default -> throw new IllegalArgumentException("无效规则（Invalid Action）: " + action);
    }

    return b;
  }

  /**
   * 添加上游代理
   *
   * @return {@link ChannelHandler}
   */
  private ChannelHandler addUpstreamProxy() {
    UpstreamProxy upstreamProxyHandler = new UpstreamProxy(UpstreamProxy.Protocol.SOCKS5,
        new InetSocketAddress("192.168.31.193", 7007));
    return upstreamProxyHandler.newProxyHandler();
  }

  /**
   * 添加代理
   *
   * @return {@link ChannelHandler}
   */
  private ChannelHandler addProxy() {
    UpstreamProxy proxyHandler = new UpstreamProxy(UpstreamProxy.Protocol.SOCKS5,
        new InetSocketAddress("192.168.31.193", 7007));
    return proxyHandler.newProxyHandler();
  }


}
