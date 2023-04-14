package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity;

/**
 * @Author yugang
 * @create 2022/12/26 7:01
 */
public class PortModel {


    /**
     * 本地端口
     */
    public Integer localPort ;

    /**
     * 远程端口
     */
    public Integer remotePort;

    /**
     * 远程ip
     */
    public String remoteIP;

    /**
     * 用户名
     */
    public String remoteUserName;

    /**
     * 用户密码
     */
    public String remoteUserPassword;

    /**
     * 远程协议
     */
    public String remoteProtocol;

    /**
     * 延迟
     */
    public String latency;


    public Integer getLocalPort() {
        return localPort;
    }

    public void setLocalPort(Integer localPort) {
        this.localPort = localPort;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(Integer remotePort) {
        this.remotePort = remotePort;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public void setRemoteIP(String remoteIP) {
        this.remoteIP = remoteIP;
    }

    public String getRemoteUserName() {
        return remoteUserName;
    }

    public void setRemoteUserName(String remoteUserName) {
        this.remoteUserName = remoteUserName;
    }

    public String getRemoteUserPassword() {
        return remoteUserPassword;
    }

    public void setRemoteUserPassword(String remoteUserPassword) {
        this.remoteUserPassword = remoteUserPassword;
    }

    public String getRemoteProtocol() {
        return remoteProtocol;
    }

    public void setRemoteProtocol(String remoteProtocol) {
        this.remoteProtocol = remoteProtocol;
    }

    public String getLatency() {
        return latency;
    }

    public void setLatency(String latency) {
        this.latency = latency;
    }

    @Override
    public String toString() {
        return "PortModel{" +
                "localPort=" + localPort +
                ", remotePort=" + remotePort +
                ", remoteIP='" + remoteIP + '\'' +
                ", remoteUserName='" + remoteUserName + '\'' +
                ", remoteUserPassword='" + remoteUserPassword + '\'' +
                ", remoteProtocol='" + remoteProtocol + '\'' +
                ", latency='" + latency + '\'' +
                '}';
    }
}
