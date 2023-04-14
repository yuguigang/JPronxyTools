package com.ztoncloud.jproxytools.functional.proxypanel.gui.settings;


import static atlantafx.base.theme.Styles.TEXT;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.layout.Priority.ALWAYS;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;

import com.ztoncloud.jproxytools.Utils.Containers;

import com.ztoncloud.jproxytools.functional.proxypanel.ServerContext;
import com.ztoncloud.jproxytools.i18n.LanguageResource;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一般设置 Tab
 *
 * @Author yugang
 * @create 2023/4/9 5:41
 */
public class generalSetting extends Tab {

  private static final Logger logger = LoggerFactory.getLogger(generalSetting.class);
  private static final int CONTROL_WIDTH = 200;
  private final ServerContext serverContext = ServerContext.getInstance();
  private final HBox infoBox = new HBox();
  private final Label infoLabel = new Label();


  generalSetting() {
    super();
    createView();

  }

  private void createView() {

    var generalVBox = new VBox();
    //所有一般设置选项从这里添加
    VBox settingsList = new VBox();
    settingsList.getStyleClass().add("theme-list");
    settingsList.getChildren().addAll(
        portSet(),
        providerSet()
    );

    var scrollPane = new ScrollPane(settingsList);
    Containers.setScrollConstraints(scrollPane, AS_NEEDED, true, AS_NEEDED, true);
    scrollPane.setMaxHeight(4000);
    VBox.setVgrow(scrollPane, ALWAYS);



    //设置带颜色背景的提示栏
    ProxySettingsDialog.setColorIcon(infoBox,infoLabel,false);


    generalVBox.setId("theme-repo-manager");
    generalVBox.getChildren().addAll(
        scrollPane,
        infoBox
    );

    textProperty().bind(LanguageResource.getLanguageBinding("WindowDialog_Title"));
    //tab.setGraphic(new FontIcon(randomIcon()));
    setContent(generalVBox);

  }

  /**
   * 端口设置
   *
   * @return {@link HBox}
   */
  private HBox portSet() {
    var portSetHBox = new HBox();
    portSetHBox.setAlignment(Pos.CENTER_LEFT);
    portSetHBox.getStyleClass().add("theme");
    //下划线，不需要可以注释掉。
    //portSetHBox.setStyle("-fx-border-color: transparent transparent -color-border-default transparent;");
    // == TITLE ==

    var text = new Text("端口设置");
    text.getStyleClass().addAll("text");

    var subText = new Text("端口最多能设置30个，范围在1025-65535之间");
    subText.getStyleClass().addAll(TEXT, "sub-text");

    var titleBox = new VBox(5);
    titleBox.getStyleClass().addAll("title");
    titleBox.getChildren().setAll(text, subText);

    var beginPort = serverContext.getServerBeginPort();
    var leftText2 = new TextField(String.valueOf(beginPort));
    leftText2.getStyleClass().add(Styles.LEFT_PILL);
    leftText2.setPrefWidth(100);

    var centerLabel2 = new Label("-");
    centerLabel2.getStyleClass().add(Styles.CENTER_PILL);

    var endPort = serverContext.getServerEndPort();
    var rightText2 = new TextField(String.valueOf(String.valueOf(endPort)));

    rightText2.getStyleClass().add(Styles.RIGHT_PILL);
    rightText2.setPrefWidth(100);

    Button applyBtn = new Button("应用");

    applyBtn.setOnAction(event -> {
      int finalBeginPort = Integer.parseInt(leftText2.getText());
      int finalEndPort = Integer.parseInt(rightText2.getText());
      //端口基本校验
      if (ServerContext.checkPort(finalBeginPort,finalEndPort)) {

        logger.debug("保存端口设置，BeginPort {} EndPort {}" , finalBeginPort , finalEndPort);
        //暂存到serverContext，注意，这里没有保存到系统磁盘。
        serverContext.setServerBeginPort(finalBeginPort);
        serverContext.setServerEndPort(finalEndPort);

        //设置info提示栏
        ProxySettingsDialog.setColorIcon(infoBox,infoLabel,false);

      } else {
        //设置warning提示栏
        ProxySettingsDialog.setColorIcon(infoBox,infoLabel,true);

      }

    });

    var sample2 = new HBox(leftText2, centerLabel2, rightText2);
    sample2.setAlignment(Pos.CENTER_RIGHT);

    portSetHBox.getChildren().addAll(
        titleBox,
        new Spacer(),
        new Region(/* placeholder */),
        sample2,
        applyBtn
    );
    return portSetHBox;

  }

  /**
   * 选择IP供应商
   *
   * @return {@link HBox}
   */
  private HBox providerSet() {
    var providerSetHBox = new HBox();
    providerSetHBox.setAlignment(Pos.CENTER_LEFT);
    providerSetHBox.getStyleClass().add("theme");
    //下划线，不需要可以注释掉。
    //providerSetHBox.setStyle("-fx-border-color: transparent transparent -color-border-default transparent;");
    // == TITLE ==

    var text = new Text("IP提供商");
    text.getStyleClass().addAll("text");

    var subText = new Text("选择IP提供商");
    subText.getStyleClass().addAll(TEXT, "sub-text");

    var titleBox = new VBox(5);
    titleBox.getStyleClass().addAll("title");
    titleBox.getChildren().setAll(text, subText);

    //IP提供商选择框
    ComboBox<String> comboBox = new ComboBox<>();
    comboBox.getItems().addAll(providerArrayList());
    comboBox.setPrefWidth(CONTROL_WIDTH);
    comboBox.getSelectionModel().selectFirst();
    comboBox.valueProperty().addListener((obs, old, val) -> {
      if (val != null) {
        //TM.setFontFamily(DEFAULT_FONT_ID.equals(val) ? DEFAULT_FONT_FAMILY_NAME : val);
        //UserSettings.setValue("Theme_FontFamily",val);
        //暂存到设置里面
        logger.debug("保存IP提供商： {}",val);
        serverContext.setProvider(val);
        //UserSettings.setValue("Language",getLocale(val));

      }
    });

    Button applyBtn = new Button("应用");
    providerSetHBox.getChildren().addAll(
        titleBox,
        new Spacer(),
        new Region(/* placeholder */),
        comboBox,

        applyBtn
    );
    return providerSetHBox;

  }

  /**
   * IP提供者备选列表
   *
   * @return {@link List}<{@link String}>
   */
  private List<String> providerArrayList() {
    List<String> list = new ArrayList<>();
    list.add("iproyal.com");
    list.add("zhizhuip.com");
    return list;
  }
}
