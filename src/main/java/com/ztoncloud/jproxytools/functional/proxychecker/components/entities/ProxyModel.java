package com.ztoncloud.jproxytools.functional.proxychecker.components.entities;

import javafx.util.Pair;

import java.net.Proxy.Type;

/**
 *
 * 代理实体，为每个代理建模
 */
public class ProxyModel {

    public int port;
    public   String ip;

    public String country;
    //响应时间
    public String response_time;

    public Type type;
    public ProxyStatus status;
    public ProxyAnonymity level;


    /**
     *
     * 代理对象的默认值
     */
    private void initProxyDefaults() {
        this.response_time = null; // 响应时间为null，假设代理已不可用
        this.setProxyStatus(ProxyStatus.DEAD);
        //匿名性，假设为透明代理
        this.setProxyAnonymity(ProxyAnonymity.TRANSPARENT);
    }

    /**
     * Proxy构建函数，输入有效的参数，创建新的代理对象
     * 注意：入参格式
     * @param ip_port - 字符串格式：ip:port
     * @param type - java.net.Proxy.Type 支持的Type HTTP or SOCKS
     */
    public ProxyModel(String ip_port, Type type) {
        Pair<String, Integer> proxyPair = format(ip_port);
        if(proxyPair == null) {
            throw new RuntimeException("使用无效的格式化字符串调用Proxy()！");
        } else {
            this.ip = proxyPair.getKey();
            this.port = proxyPair.getValue();
            this.setProxyType(type);
            this.initProxyDefaults();
        }
    }

    /**
     * 重载的构建函数，注意格式
     * @param ip - 代理的IP地址，字符串形式
     * @param port - 代理端口
     * @param type - java.net.Proxy.Type支持的Type HTTP or SOCKS
     */
    public ProxyModel(String ip, int port, Type type) {
        this.ip = ip;
        this.port = port;
        this.setProxyType(type);
        this.initProxyDefaults();

    }

    /**
     * 将字符串格式化为新的javafx.util.Pair对象
     * @param ip_port - 格式为ip:port的字符串
     * @return     如果IP地址无法格式化（无效格式），返回null，
     *             如果是有效格式，则返回javafx.util.Pair对象，将IP作为key值，Port作为value值配对
     */
    private static Pair<String, Integer> format(String ip_port) {
        String[] explode = ip_port.split(":");
        if(explode.length == 2) {
            try {
                int port = Integer.parseInt(explode[1]);
                return new Pair<>(explode[0], port);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 判断给定字符串是否为有效的代理格式
     * @param ip_port - 格式为ip:port的字符串
     * @return Boolean - 给定字符串的格式是否为有效的ip:port
     */
    public static boolean isValidFormat(String ip_port) {
        return (format(ip_port) != null);
    }

    /**
     * 获取代理的IP地址
     * @return String - IP address
     */
    public String getIp() {
        return this.ip;
    }

    /**
     * 获取代理端口
     * @return int - Port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * 设置代理的国家/地区
     * @param country - 国家/地区
     * @return Proxy - 当前代理对象
     */
    public ProxyModel setCountry(String country) {
        this.country = country;
        return this;
    }

    /**
     * 获取代理的国家/地区
     * @return String - 国家/地区
     */
    public String getCountry() {
        return this.country;
    }


    /**
     * 设置代理响应时间
     * @param response_time - String 响应时间 (毫秒 milliseconds)
     * @return Proxy - 当前代理
     */
    public ProxyModel setResponseTime(String response_time) {
        this.response_time = response_time;
        return this;
    }

    /**
     * 获取响应时间
     * @return String - 响应时间
     */
    public String getResponseTime() {
        return this.response_time;
    }

    /**
     * 设置代理类型
     * @param type - java.net.Proxy.Type 支持的类型 HTTP or SOCKS
     * @return Proxy - 当前代理对象
     */
    public ProxyModel setProxyType(Type type) {
        this.type = type;
        return this;
    }

    /**
     * 获取代理类型
     * @return java.net.Proxy.Type
     */
    public Type getProxyType() {
        return this.type;
    }

    /**
     * 设置代理状态
     * @param status - 代理状态  ALIVE or DEAD （可用或者不可用）
     * @return Proxy - 当前代理对象
     */
    public ProxyModel setProxyStatus(ProxyStatus status) {
        this.status = status;
        return this;
    }

    /**
     * 获取代理状态
     * @return ProxyStatus - Either ALIVE or DEAD
     */
    public ProxyStatus getProxyStatus() {
        return this.status;
    }

    /**
     * 设置代理匿名性,三种：高匿代理 ELITE，匿名代理 ANONYMOUS，透明代理 TRANSPARENT
     * @param level - ProxyAnonymity 匿名性  ANONYMOUS 或者 TRANSPARENT， 匿名代理或者透明代理
     * @return Proxy - 当前代理对象
     */
    public ProxyModel setProxyAnonymity(ProxyAnonymity level) {
        this.level = level;
        return this;
    }

    /**
     * 获取代理匿名性
     * @return ProxyAnonymity - Either ELITE ANONYMOUS or TRANSPARENT 高匿代理、匿名代理或者透明代理
     */
    public ProxyAnonymity getProxyAnonymity() {
        return this.level;
    }

    @Override
    public String toString() {
        return "ProxyModel{" +
                "port=" + port +
                ", ip='" + ip + '\'' +
                ", country='" + country + '\'' +
                ", response_time='" + response_time + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", level=" + level +
                '}';
    }
}
