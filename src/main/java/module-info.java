module jproxytools {
  requires javafx.controls;
  requires javafx.fxml;
  requires java.desktop;
  //字体图标库
  requires org.kordamp.ikonli.core;
  requires org.kordamp.ikonli.feather;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.material2;
  //主题库
  requires atlantafx.base;
  //netty依赖
  requires io.netty.transport;
  requires io.netty.codec;
  requires io.netty.codec.http;
  requires io.netty.handler;
  requires io.netty.buffer;
  requires io.netty.codec.socks;
  requires io.netty.common;
  requires io.netty.transport.classes.epoll;
  requires io.netty.transport.classes.kqueue;
  requires io.netty.handler.proxy;
  //注解
  requires org.jetbrains.annotations;
  //logo
  requires org.slf4j;
  requires ch.qos.logback.classic;
  requires ch.qos.logback.core;
  //org.apache.commons.lang3
  requires org.apache.commons.lang3;
  //fasterxml
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.fasterxml.jackson.dataformat.xml;
  requires com.ctc.wstx;
  requires com.fasterxml.jackson.databind;
  //GSON
  requires com.google.gson;
  //JVM运时模块
  requires java.management;
  requires jdk.unsupported;
  //requires com.fasterxml.jackson.module.jakarta.xmlbind;
  //temp
  requires reactor.core;
  requires inet.ipaddr;

  //开放模块给jackson访问
  opens com.ztoncloud.jproxytools to javafx.fxml;
  opens com.ztoncloud.jproxytools.config to com.fasterxml.jackson.databind;
  opens com.ztoncloud.jproxytools.Env  to com.fasterxml.jackson.databind;
  opens com.ztoncloud.jproxytools.functional.proxypanel  to com.fasterxml.jackson.databind;
  opens com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity  to com.fasterxml.jackson.databind;
  //opens com.ztoncloud.jproxytools.i18n to com.fasterxml.jackson.databind;
  //java 11 模块化有点头疼，模块需要逐一开放使用。
  opens com.ztoncloud.jproxytools.functional.proxychecker.components.entities to javafx.base ,com.google.gson;
  opens com.ztoncloud.jproxytools.functional.proxychecker.components to com.google.gson, javafx.base;
  //opens com.ztoncloud.jproxytools.utils to com.google.gson;
  opens com.ztoncloud.jproxytools.layout.testpage to javafx.graphics;

  exports com.ztoncloud.jproxytools;
  exports com.ztoncloud.jproxytools.functional.proxypanel;
  exports com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity;

}