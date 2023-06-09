package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.OS;

import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: wy
 * @Date: Created in 16:49 2021-06-23
 * @Description: 当前os相关
 * @Modified: By：
 */

public class OSHelper {

    private  static final Logger log = LoggerFactory.getLogger(OSHelper.class);

    private static final Class<? extends Channel> channelType;

    private static final Class<? extends ServerChannel> acceptChannelType;

    private volatile static ThreadFactory threadFactory;

    static {
        if (OSinfo.isLinux()) {
            channelType = EpollSocketChannel.class;
            acceptChannelType = EpollServerSocketChannel.class;
        } else if (OSinfo.isMacOSX()) {
            channelType = KQueueSocketChannel.class;
            acceptChannelType = KQueueServerSocketChannel.class;
        } else {
            channelType = NioSocketChannel.class;
            acceptChannelType = NioServerSocketChannel.class;
        }
    }

    public static Class<? extends Channel> channelType() {
        return channelType;
    }

    public static Class<? extends ServerChannel> acceptChannelType() {
        return acceptChannelType;
    }

    public static EventLoopGroup eventLoopGroup(int num, String name) {

        if (OSinfo.isLinux()) {
            return new EpollEventLoopGroup(num, getFactory(name + "-epoll-"));
        } else if (OSinfo.isMacOSX()) {
            return new KQueueEventLoopGroup(num, getFactory(name + "-kqueue-"));
        } else {
            return new NioEventLoopGroup(num, getFactory(name + "-nio-"));
        }
    }

    public static ThreadFactory getFactory(String name) {
        return new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public synchronized Thread newThread(Runnable r) {
                return new Thread(r, name + "-" + counter.getAndIncrement());
            }
        };

    }

    /**
     * 转发策略
     *
     * @return {@link PassThroughStrategy}
     */
    /*
    public static PassThroughStrategy nativePassThrough() {
        if (OSinfo.isLinux()) {
            log.info("使用Splice透传策略");
            return new SpliceImpl();
        }
        log.info("使用默认透传策略");
        return new DefaultImpl();
    }

    //默认策略
    public static class DefaultImpl implements PassThroughStrategy {

        @Override
        public void accept(Channel channel1, Channel channel2) {
            channel1.pipeline().addLast(new ForwardHandler(channel2));
            channel2.pipeline().addLast(new ForwardHandler(channel1));
            channel1.config().setAutoRead(true);
            channel2.config().setAutoRead(true);
        }
    }

    //splice策略
    public static class SpliceImpl implements PassThroughStrategy {

        @Override
        public void accept(Channel channel1, Channel channel2) {
            EpollSocketChannel ch1 = (EpollSocketChannel) channel1;
            EpollSocketChannel ch2 = (EpollSocketChannel) channel2;
            channel1.pipeline().addLast(new LinuxSpliceHandler());
            channel2.pipeline().addLast(new LinuxSpliceHandler());

            ch1.spliceTo(ch2, Integer.MAX_VALUE).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (!future.isSuccess()) {
                        future.cancel(true);
                        future.channel().close();
                    }
                }
            });
            ch2.spliceTo(ch1, Integer.MAX_VALUE).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (!future.isSuccess()) {
                        future.cancel(true);
                        future.channel().close();
                    }
                }
            });
            channel1.config().setAutoRead(true);
            channel2.config().setAutoRead(true);
        }
    }

     */

}
