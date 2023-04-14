package com.ztoncloud.jproxytools.functional.proxypanel.JProxy;

import com.ztoncloud.jproxytools.functional.proxypanel.ServerContext;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.handler.IpFilterRuleHandler.IpFilterRuleHandler;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.PortModel;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.UpstreamProxy;

import com.ztoncloud.jproxytools.functional.proxypanel.gui.InfoGrid;
import com.ztoncloud.jproxytools.functional.proxypanel.gui.SearchBox;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yugang
 * @create 2023/1/13 12:56
 */
public class JServer {

  private static final Logger logger = LoggerFactory.getLogger(JServer.class);

  private static final SimpleBooleanProperty isRun = new SimpleBooleanProperty(false);
  //private boolean isRun;
  //private ProxyServerSetting serverSetting;
  private final ServerContext serverContext;
  private Channel[] channels;
  private List<Integer> ports;
  //端口管理器
  private PortsManager portsManager;
  //上级代理链
  private Queue<? extends UpstreamProxy> upstreamProxies;


  public JServer(ServerContext serverContext) {
    this.serverContext = serverContext;
    initServer();
  }

  private void initServer() {
    this.ports = serverContext.getServerPorts();
    this.upstreamProxies = serverContext.getUpstreamProxies();
    this.portsManager = PortsManager.getInstance();
    //服务器状态监听。
    isRun.addListener((observable, oldValue, newValue) -> {
      logger.debug("服务器状态监听： 服务器状态：{} 端口管理器：{} 管道管理器： {}", newValue, portsManager,
          Arrays.toString(channels));
      //这里必须开启主线程，才能去设置运行状态到界面
      //Platform.runLater(() -> SearchBox.setRun(newValue));
      Platform.runLater(() -> InfoGrid.setRun(newValue));
    });

  }


  public void startServer() throws Exception {

    //先设置过滤规则，缓存在IpFilterRuleHandler.ruleBasedIpFilter里，避免在每个管道管道初始化时去转换过滤规则
    //允许过滤规则：
    var EnableIpFilter =
        serverContext.isEnableIpFilter() && serverContext.getIpFilterList() != null;

    if (EnableIpFilter) {
      //先设置过滤规则
      IpFilterRuleHandler.convertRules(serverContext.getIpFilterList());
    }

    ServerBootstrap bootstrap = NettyBootstrapFactory.initMultiplexBootstrap();
    bootstrap.childHandler(
        new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(@NotNull SocketChannel ch) {
            int timeoutSeconds = serverContext.getTimeout();
            var idleStateHandler = new IdleStateHandler(timeoutSeconds, timeoutSeconds,
                timeoutSeconds, TimeUnit.SECONDS);
            if (logger.isDebugEnabled()) {
              ch.pipeline().addLast(JProxyNames.LOGGING, new LoggingHandler(LogLevel.DEBUG));
            }

            if (EnableIpFilter) {
              //再加入管道
              ch.pipeline().addFirst(
                  IpFilterRuleHandler.getRuleBasedIpFilter()); // (filter;//(new RuleBasedIpFilter(filter));
            }
            ch.pipeline().addLast(idleStateHandler); // 加入超时设置
            ch.pipeline().addLast(JProxyNames.TIMEOUT, new CloseTimeoutChannelHandler()); // 超时关闭管道
            ch.pipeline().addLast(JProxyNames.ROOT, new MixinProtocolSelector());
          }
        });
    //绑定端口
    if (ports != null) {
      if (channels == null) {
        channels = new Channel[ports.size()];
      }
      try {
        int i = 0;
        for (Integer port : ports) {
          //绑定端口，同步等待成功 绑定的服务器，Host 0.0.0.0表示可以接受外部请求，为安全考虑，如果只限本机请求，可改为 127.0.0.1
          ChannelFuture channelFuture = bootstrap.bind(port).sync()
              .addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                  Channel channel = future.channel();
                  //循环，同时设置到PortModels和Pots列表。
                  PortModel portModel = new PortModel();
                  portModel.setLocalPort(port);
                  //portModel.setRemoteIP("geo.iproyal.com");
                  //portModel.setRemotePort(12321);
                  //portModel.setRemoteProtocol("HTTP");
                  //portModel.setRemoteUserName("yugang");
                  //portModel.setRemoteUserPassword("yugang1975_country-kr");
                  portsManager.addToPortModels(portModel);
                  //通知成功
                  logger.info("netty server 启动成功! " + channel.localAddress());
                  logger.debug("portModels: " + portsManager.getAllPortModels());

                } else {
                  logger.error("netty server {} 启动失败! {} ", future.channel().localAddress(),
                      future.cause());
                }

              });
          channels[i] = channelFuture.channel();
          i++;
          logger.debug("开启输入管道,管道大小：{} Channel列表{}  ", channels.length, Arrays.toString(channels));
          //channel.closeFuture().sync();

        }
      } catch (Exception e) {
        logger.error("服务器启动错误！ " + e);
        setRun(false);
        throw new Exception(e.getMessage(), e.getCause());

      }

      setRun(true);
    }

  }

  /**
   * 停止服务器
   */
  public void stopServer() {

    logger.debug("开始停止线程。服务器状态：{} 端口管理器：{} 管道管理器： {} ", isRun(), portsManager,
        Arrays.toString(channels));

    //关闭线程组
    NettyBootstrapFactory.shutdownGroup(channels);
    //端口重置
    if (portsManager != null) {
      this.portsManager = null;
    }
    if (channels != null) {
      channels = null;
    }


  }

  /**
   * 获取服务器运行状态
   *
   * @return boolean true 正在运行，flash 关闭
   */
  public static boolean isRun() {
    return isRun.getValue();
  }

  public static void setRun(boolean bool) {
    isRun.setValue(bool);
  }

}
