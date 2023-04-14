package com.ztoncloud.jproxytools;

import com.ztoncloud.jproxytools.Utils.Resources;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloApplication extends Application {
  private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);
  @Override
  public void start(Stage stage) throws IOException {
    logger.info("BaseLanguage: "+ Resources.getResource("/i18n/Language.properties"));
    LanguageResource.setLanguage(Resources.getResource("/i18n/Language.properties").toString(),Locale.forLanguageTag( "zh-CN"));

    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 320, 240);
    stage.titleProperty().bind(LanguageResource.getLanguageBinding("Sidebar_GENERAL"));
    //stage.setTitle(AppResource.getLanguageBinding("Sidebar_GENERAL").getValue());
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}