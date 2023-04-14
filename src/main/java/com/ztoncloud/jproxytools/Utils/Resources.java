

package com.ztoncloud.jproxytools.Utils;

import com.ztoncloud.jproxytools.Launcher;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;


/**
 * 获取资源的帮助类。JavaFX资源获取有点混乱，这是个帮助类
 * 固定在启动类Launcher.class路径为根目录获取资源，也就是说，资源目录 resources为根；
 * 资源字符串前面以“/”开头的表示在资源根目录下 resources/，否则默认在资源目录 resources/com/ztoncloud/jproxytools/
 *
 * @author yugang
 * @date 2023/02/28
 */
public final class Resources {

    public static final String MODULE_DIR = "/com/ztoncloud/jproxytools/";

    public static InputStream getResourceAsStream(String resource) {
        String path = resolve(resource);
        return Objects.requireNonNull(
            //固定启动类Launcher.class的路径
            Launcher.class.getResourceAsStream(resolve(path)),
            "Resource not found: " + path
        );
    }

    public static URI getResource(String resource) {
        String path = resolve(resource);
        URL url = Objects.requireNonNull(Launcher.class.getResource(resolve(path)), "Resource not found: " + path);
        return URI.create(url.toExternalForm());
    }

    public static String resolve(String resource) {
        Objects.requireNonNull(resource);
        return resource.startsWith("/") ? resource : MODULE_DIR + resource;
    }

    public static String getPropertyOrEnv(String propertyKey, String envKey) {
        return System.getProperty(propertyKey, System.getenv(envKey));
    }
/*
    public static Preferences getPreferences() {
        return Preferences.userRoot().node("atlantafx");
    }

 */
}
