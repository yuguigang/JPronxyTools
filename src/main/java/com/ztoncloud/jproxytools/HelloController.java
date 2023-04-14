package com.ztoncloud.jproxytools;

import com.ztoncloud.jproxytools.i18n.LanguageResource;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

  @FXML
  private Label welcomeText;

  @FXML
  protected void onHelloButtonClick() {
    LanguageResource.setLanguage("i18n/Language", Locale.forLanguageTag( "en_US"));
    welcomeText.setText("Welcome to JavaFX Application!");
  }
}