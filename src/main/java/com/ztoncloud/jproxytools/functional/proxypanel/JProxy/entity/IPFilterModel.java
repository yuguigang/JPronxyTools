package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity;

public class IPFilterModel {

  private String ip;



  private FilterRule rule;

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public FilterRule getRule() {
    return rule;
  }

  public void setRule(FilterRule rule) {
    this.rule = rule;
  }

  @Override
  public String toString() {
    return "IPFilterModel{" +
        "ip='" + ip + '\'' +
        ", rule=" + rule +
        '}';
  }
}
