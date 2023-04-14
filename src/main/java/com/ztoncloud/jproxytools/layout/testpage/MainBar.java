package com.ztoncloud.jproxytools.layout.testpage;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;

import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

/**
 * @Author yugang
 * @create 2023/2/18 22:36
 */
public class MainBar extends BorderPane {

  Button navDrawerBtn;

  public MainBar () {

    createView();
  }

  private void createView() {

    navDrawerBtn = createButton(Material2OutlinedAL.APPS, "tools");
    var toolsbar = new ToolBar();
    //toolsbar.getStyleClass().add("tool-bar");

    var appsBtn = new Button("",new FontIcon(Material2OutlinedAL.APPS));
    appsBtn.getStyleClass().addAll(BUTTON_ICON,FLAT,ACCENT);

    Button button1 = new Button("button 1");
    //toolsbar.getItems().addAll(appsBtn,button1);

    ToolTabPane toolTabPane = new ToolTabPane();
    toolsbar.getItems().addAll(toolTabPane,button1,appsBtn);
    HBox hBox2 = new HBox();
    hBox2.getChildren().addAll(appsBtn,navDrawerBtn,button1);
    hBox2.setPrefWidth(250);


    HBox hBox = new HBox();
    hBox.setStyle("-fx-background-color: -color-border-muted;");
    hBox.getChildren().addAll(

        hBox2,
        toolTabPane
    );
   //HBox.setHgrow(toolTabPane,Priority.ALWAYS);
   //var tabPane = new ToolTabPane();
   //tabPane.setStyle("-fx-background-color: -color-border-muted;");
    var center = new CenterView();
    var Test = new TEST_TabPane();

    //setTop(hBox);
    setLeft(new LiftView());
    setCenter(Test);
    //setRight(Test);





    //setId("sidebar");
    //setPrefWidth(Region.USE_COMPUTED_SIZE);
    //setMaxWidth(Region.USE_COMPUTED_SIZE);
    //getStyleClass().add(Styles.BORDERED);
//var root = this;
    HBox.setHgrow(Test, Priority.ALWAYS);
    VBox.setVgrow(Test, Priority.ALWAYS);


  }

  /**
   * 创建按钮
   *
   * @param iconCode 图标代码
   * @param tooltip  工具提示
   * @return {@link Button}
   */
  private Button createButton(final Ikon iconCode, final String tooltip) {
    var button = new Button("", new FontIcon(iconCode));
    button.getStyleClass().addAll(BUTTON_ICON, FLAT);
    if (tooltip != null) {
      button.setTooltip(new Tooltip(tooltip));
    }
    return button;
  }
}
