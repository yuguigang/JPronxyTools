package com.ztoncloud.jproxytools.functional.proxypanel.JProxy;

import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.TCP_NODELAY;
import static io.netty.channel.epoll.EpollChannelOption.EPOLL_MODE;
import static io.netty.channel.epoll.EpollMode.LEVEL_TRIGGERED;

import com.ztoncloud.jproxytools.exception.AppException;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.OS.OSHelper;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.OS.OSinfo;
import com.ztoncloud.jproxytools.functional.proxypanel.gui.SearchBox;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 启动引导工厂
 *
 * @author yugang
 * @date 2022/12/25
 * 根据OS启动不同EventLoopGroup,windows\MacOSX\Linux
 * 这里设定线程都为1的情况下.多端口每次都启动一个 ServerBootstrap
 * 如果使用端口复用，需要启动多线程。
 */
public class NettyBootstrapFactory {

    private  static final Logger logger = LoggerFactory.getLogger(NettyBootstrapFactory.class);
    private static EventLoopGroup boss ;
    private static EventLoopGroup worker ;

    public static ServerBootstrap initServerBootstrap() {
         boss = OSHelper.eventLoopGroup(1, "ztoncloudproxy-boss");
         worker = OSHelper.eventLoopGroup(1, "ztoncloudproxy-worker");
        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, worker)
                .childOption(TCP_NODELAY, true)
                .option(SO_BACKLOG, 16384)//最大连接队列
                .channel(OSHelper.acceptChannelType());

        //b.childOption(TCP_NODELAY, true);
        //b.option(SO_BACKLOG, 16384);
        if (OSinfo.isLinux()) {
            b.childOption(EPOLL_MODE, LEVEL_TRIGGERED);
        }

        //b.channel(OSHelper.acceptChannelType());
        //Netty自带内存泄露检测，生产环境时，关掉。
        //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        return b;
    }

    /**
     * init连接引导
     *
     * @return {@link Bootstrap}
     */
    public static Bootstrap initBootstrap() {
        Bootstrap b = new Bootstrap();

        b.channel(OSHelper.channelType());//管道
        b.option(TCP_NODELAY, true);//TCP
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        if (OSinfo.isLinux()) {
            b.option(EPOLL_MODE, LEVEL_TRIGGERED);
        }
        return b.clone();
    }

    /**
     * 多路复用ServerBootstrap
     *
     * @return {@link ServerBootstrap}
     */
    public static ServerBootstrap initMultiplexBootstrap() {
        //Runtime.getRuntime().availableProcessors() + 1返回当前cpu核心数+1，表示开启childEventLoopNums线程数
        int num = Math.min(Runtime.getRuntime().availableProcessors() + 1, 12);//控制使用线程在12以下
        boss = OSHelper.eventLoopGroup(1, "jproxy-boss");
        worker = OSHelper.eventLoopGroup(num, "jproxy-worker");
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss,worker)
                .channel(OSHelper.acceptChannelType())//根据操作系统不同，加入相应的ServerChannel管道
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)//多路复用
                .option(SO_BACKLOG, 16384)//最大连接队列
                .childOption(TCP_NODELAY, true);//TCP模式
        if (OSinfo.isLinux()) {
            serverBootstrap.childOption(EPOLL_MODE, LEVEL_TRIGGERED);
        }
        return serverBootstrap;

    }

    /**
     * 关闭组 关闭线程组
     *
     * @param channels 需关闭的管道组
     * @return boolean 关闭成功 true ，关闭失败 false
     */
    public static void shutdownGroup ( Channel[] channels ) {

        if (boss != null && worker !=null ) {
            Future<?> bossFuture = boss.shutdownGracefully();
            Future<?> workerFuture = worker.shutdownGracefully();
            try {

                bossFuture.addListener(new GenericFutureListener<Future<Object>>() {
                    @Override
                    public void operationComplete(@NotNull Future<Object> future) throws Exception {
                        for (Channel channel : channels) {
                            if (channel != null && channel.isActive()) {
                                channel.close().sync();
                            }
                        }
                        logger.debug(" 服务器BossEventLoopGroup已经停止");
                    }
                });

                workerFuture.addListener(new GenericFutureListener<Future<Object>>() {
                    @Override
                    public void operationComplete(@NotNull Future<Object> future) throws Exception {
                        logger.debug("服务器WorkEventLoopGroup已经停止");
                       //设置服务器状态。
                        JServer.setRun(false);
                    }
                });
            } catch (Exception e) {
                logger.error("停止服务器失败！ "+e);
               throw new AppException(e.getMessage(),e.getCause());

            }

        }

    }

}
