package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity;

import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyAnonymity;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyStatus;
import java.net.Proxy;
import java.security.Timestamp;

/**
 * 代理池模型
 * @Author yugang
 * @create 2022/11/22 4:52
 */
public class ProxyPoolBean {

    public int id;
    public int port;
    public   String ip;

    public String country;// 国家/地区
    public String State;// 地/市/州
    //响应时间
    public String response_time;

    public Proxy.Type type;
    public ProxyStatus status;
    public ProxyAnonymity level;

    public Timestamp usageTime;//使用时间戳。

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getResponse_time() {
        return response_time;
    }

    public void setResponse_time(String response_time) {
        this.response_time = response_time;
    }

    public Proxy.Type getType() {
        return type;
    }

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    public ProxyStatus getStatus() {
        return status;
    }

    public void setStatus(ProxyStatus status) {
        this.status = status;
    }

    public ProxyAnonymity getLevel() {
        return level;
    }

    public void setLevel(ProxyAnonymity level) {
        this.level = level;
    }

    public Timestamp getUsageTime() {
        return usageTime;
    }

    public void setUsageTime(Timestamp usageTime) {
        this.usageTime = usageTime;
    }

    @Override
    public String toString() {
        return "ProxyPoolBean{" +
                "id=" + id +
                ", port=" + port +
                ", ip='" + ip + '\'' +
                ", country='" + country + '\'' +
                ", State='" + State + '\'' +
                ", response_time='" + response_time + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", level=" + level +
                ", usageTime=" + usageTime +
                '}';
    }
}
