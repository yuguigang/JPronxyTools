package com.ztoncloud.jproxytools.functional.proxychecker.components;


import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyAnonymity;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * 管理用户可配置的系统设置
 */
public class ProxyCheckerSettings {

    private  static final Logger logger = LoggerFactory.getLogger(ProxyCheckerSettings.class);

    private int timeout ; // 超时设置
    private int threads ; // 最大线程
    private String ip ; // 互联网地址，注意不是局域网地址
    // 代理类型，
    // DIRECT 表示直接连接或缺少代理。
    // HTTP 表示高级协议（如HTTP或FTP）的代理。
    // SOCKS 表示SOCKS（V4或V5）代理。
    private Proxy.Type type ;//代理协议
    //颜色管理
    private  static List<Pair<ProxyAnonymity, String>> colorScheme ;



    /**
     *
     * 初始化设置
     */
    public ProxyCheckerSettings() {
        threads = Integer.parseInt(ProxyCheckerSettingsProp.getValue("maxThreads","100"));  // 最大线程
        timeout = Integer.parseInt(ProxyCheckerSettingsProp.getValue("timeout","5000")); // 超时设置
        ip = ProxyCheckerSettingsProp.getValue("InternetIP","0.0.0.0"); // 互联网地址，注意不是局域网地址
        type = Proxy.Type.valueOf(ProxyCheckerSettingsProp.getValue("ProxyType","HTTP"));//代理协议
        colorScheme = initColorScheme();
    }
    private static ArrayList<Pair<ProxyAnonymity, String>> initColorScheme() {
       final ArrayList<Pair<ProxyAnonymity, String>> colorScheme = new ArrayList<>();
        colorScheme.add(new Pair<>(ProxyAnonymity.ELITE, "-color-base-2"));//高匿代理颜色
        colorScheme.add(new Pair<>(ProxyAnonymity.ANONYMOUS, ProxyCheckerSettingsProp.getValue("color_ANONYMOUS","#e6e6b3")));//匿名代理颜色
        colorScheme.add(new Pair<>(ProxyAnonymity.TRANSPARENT, ProxyCheckerSettingsProp.getValue("color_TRANSPARENT","#ffb3b3")));//透明代理颜色
        return colorScheme;
    }
    /**
     *
     * 获取用户的当前IP地址
     * @return String - IP address
     */
    public String getIp() {
        return this.ip;
    }

    /**
     *
     * 获取当前允许的最大线程数
     * @return int - number of threads
     */
    public int getThreads() {
        return threads;
    }

    /**
     *
     * 获取超时设置
     * @return int - the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     *
     * 设置当前用户IP地址
     * @param ip - String IP address
     * @return UserSettings
     */
    public ProxyCheckerSettings setIp(String ip) {
        this.ip = ip;
        return this;
    }

    /**
     *
     * 设置超时
     * @param timeout - int
     * @return UserSettings
     */
    public ProxyCheckerSettings setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     *
     * 设置最大线程
     * @param threads - int
     * @return UserSettings
     */
    public ProxyCheckerSettings setThreads(int threads) {
        this.threads = threads;
        return this;
    }

    /**
     *
     * 设置代理协议
     * @param type - java.net.Proxy.Type (HTTP or SOCKS)
     * @return ProxyCheckerSettings
     */
    public ProxyCheckerSettings setProxyType(Proxy.Type type) {
        this.type = type;
        return this;
    }

    /**
     *
     * 获取代理协议
     * @return java.net.Proxy.Type
     */
    public Proxy.Type getProxyType() {
        return this.type;
    }

    /**
     *
     * 获取代理表的颜色方案
     * @return List
     */
    public static List<Pair<ProxyAnonymity, String>> getColorScheme() {
        //if(colorScheme == null) {
            //暂时注释掉
            //Settings.saveConfig(new UserSettings());
        //}
        return colorScheme;
    }

    /**
     *
     * 设置代理表的颜色方案
     * @param colorScheme 颜色方案。
     */
    public void setColorScheme(List<Pair<ProxyAnonymity, String>> colorScheme) {
        ProxyCheckerSettings.colorScheme = colorScheme;
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "timeout=" + timeout +
                ", threads=" + threads +
                ", ip='" + ip + '\'' +
                ", type=" + type +
                ", colorScheme=" + colorScheme +
                '}';
    }

}
