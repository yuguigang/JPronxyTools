package com.ztoncloud.jproxytools;

import com.ztoncloud.jproxytools.Env.BaseEnv;
import com.ztoncloud.jproxytools.Utils.Resources;
import com.ztoncloud.jproxytools.config.SettingsConfig;
import com.ztoncloud.jproxytools.exception.DefaultExceptionHandler;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.layout.MainStage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher extends Application {

  private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

  @Override
  public void start(Stage stage) {
    //stage异常，使用提示框
    Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler(stage));

    //读取首选项
    var preferencesConfig = new SettingsConfig();
    var preferences = preferencesConfig.getPreferences();
    var language = preferences.getLanguage();
    // 设置语言
    LanguageResource.setLanguage( language.getLocale());


    //设置窗口布局
    var scene = new Scene(new AnchorPane(), 1280, 768);



    //构建Stage ,读取设置，主题、样式。。。
    MainStage.create(stage,scene);

    //设置任务栏图标
    stage.getIcons().add(new Image(Resources.getResourceAsStream(BaseEnv.APP_LOGO)));

    Platform.runLater(() -> {
      stage.show();
      stage.toFront();
    });
  }

  public static void main(String[] args) {
    launch();
  }
}

