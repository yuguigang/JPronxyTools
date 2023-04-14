
package com.ztoncloud.jproxytools.functional.proxypanel.gui.settings;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import com.ztoncloud.jproxytools.Env.BaseEnv;
import com.ztoncloud.jproxytools.Utils.Resources;
import com.ztoncloud.jproxytools.functional.proxypanel.ServerContext;
import com.ztoncloud.jproxytools.functional.proxypanel.gui.settings.ProxySettings;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.layout.page.Dialog.OverlayDialog;
import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

public class ProxySettingsDialog extends OverlayDialog<ProxySettings> {

  private final ProxySettings proxySettings = new ProxySettings();


  public ProxySettingsDialog() {
    setId("proxy-settings-dialog");


    setLogoImage(new ImageView(new Image(Resources.getResourceAsStream(BaseEnv.APP_LOGO))));
    var settingsText = new Text();
    settingsText.textProperty().bind(LanguageResource.getLanguageBinding("Settings"));
    setTitle(settingsText.getText());
    setContent(proxySettings);

    var saveBtn = new Button("Add", new FontIcon(Material2MZ.PLUS));
    saveBtn.getStyleClass().add(Styles.ACCENT);
    saveBtn.setOnAction(e -> {
      ServerContext.saveConfig();//保存ServerContext到磁盘
    });

    //footerBox.getChildren().add(1 , saveBtn);
    footerBox.setAlignment(Pos.CENTER_LEFT);
  }

  /**
   * 设置带颜色背景的提示栏
   *
   * @param bool      true 显示警告格式， flash 显示INFO格式
   * @param infoBox   信息框
   * @param infoLabel 信息标签
   */
  public static void setColorIcon(HBox infoBox , Label infoLabel , boolean bool) {

    var btn = new Button("保存。。");
    btn.getStyleClass().addAll(Styles.SMALL);
    btn.setPadding(new Insets(10));
    btn.setAlignment(Pos.CENTER_RIGHT);
    infoBox.getStyleClass().clear();
    if (bool) {
      var warningIcon = new FontIcon(Material2MZ.REPORT_PROBLEM);
      warningIcon.getStyleClass().add(Styles.WARNING);

      infoLabel.setGraphic(warningIcon);
      infoLabel.setText("出错了，请检查设置！ ");

      infoBox.getStyleClass().add("warn");
    } else {
      var accentIcon = new FontIcon(Material2AL.INFO);
      accentIcon.getStyleClass().add(Styles.ACCENT);

      infoLabel.setGraphic(accentIcon);
      infoLabel.setText("注意：这里所有的设置改变后，需要重启服务器才能生效！ ");

      infoBox.getStyleClass().add("info");
    }
    infoBox.getChildren().clear();
    infoBox.setAlignment(Pos.CENTER_LEFT);
    infoBox.getChildren().addAll(infoLabel);

  }

  public ProxySettings getContent() {
    return proxySettings;
  }
}
