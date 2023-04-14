package com.ztoncloud.jproxytools.functional.proxypanel.JProxy;

import static java.util.Objects.requireNonNull;

import com.ztoncloud.jproxytools.exception.AppException;
import com.ztoncloud.jproxytools.exception.DefaultExceptionHandler;
import com.ztoncloud.jproxytools.functional.proxypanel.ServerContext;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yugang
 *
 * @create 2023/1/13 21:00
 */
public class RunServer {

  private static final Logger logger = LoggerFactory.getLogger(RunServer.class);
  private ServerContext serverContext;
  private static JServer jServer;

  public RunServer() {
    //首先要拿到服务器设置
    this.serverContext = ServerContext.getInstance();
  }

  public void start() {
    // 访问Java虚拟机的系统属性。
    RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
    logger.info("Starting JServer on {} ({})", mx.getName(), System.getProperty("user.dir"));
    logger.debug("ServerContext: "+serverContext.toString());
    JServer jServer = new JServer(serverContext);
    try {
      jServer.startServer();
    } catch (Exception e) {
      logger.error("运行错误。");
    }
  }

  public static void startRun () throws Exception {
    // 访问Java虚拟机的系统属性。
    RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
    logger.info("Starting JServer on {} ({})", mx.getName(), System.getProperty("user.dir"));
    var serverContext = ServerContext.getInstance();
    logger.debug("ServerContext: "+serverContext.toString());
    jServer = new JServer(serverContext);
    try {
      jServer.startServer();
    } catch (Exception e) {
      logger.error("运行错误。"+e);
      throw new Exception(e.getMessage(),e.getCause());
      //throw new AppException(e.getMessage(),e.getCause());

    }
  }

  public static void stopServer () {
    if (jServer != null ) {
      jServer.stopServer();
    }
  }

  public static void main(String[] args) {
    new RunServer().start();
  }

}
