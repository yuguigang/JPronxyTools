package com.ztoncloud.jproxytools.Env;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author yugang
 * @create 2023/2/13 4:32
 */
public class BaseEnv {


  /**
   * 应用程序名称
   */
  public static final String APP_NAME = "JProxyTools";

  /**
   * 应用程序图标
   */
  //public static String APP_LOGO = "/assets/images/ic-launcher.png";
  public static String APP_LOGO = "/assets/icons/application.png";

  public static final String CONFIG_DIR_NAME = "config";
  /**
   * 设置保存路径
   * 这里获取配置路径如： C:\Users\使用者名\.JProxy\config
   * @return {@link Path}
   */
  public static Path getConfigDIR() {
    return createParentPath().resolve(CONFIG_DIR_NAME);
  }

  /**
   * 设置保存目录的root路径
   * UNIX/Linux 系统中的文件名不区分大小写,所以目录名为：".jproxytools",win下目录为".JProxyTool"
   */
  private static final Path parentPath = Paths.get(System.getProperty("user.home"), ".JProxyTools");
  /**
   * 获取设置保存路径，如果不存在则新建。
   *
   *
   * @return {@link Path}
   */
  public static Path createParentPath() {
    if (!Files.exists(parentPath)) {
      try {
        Files.createDirectory(parentPath);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
    return parentPath;

  }

}
