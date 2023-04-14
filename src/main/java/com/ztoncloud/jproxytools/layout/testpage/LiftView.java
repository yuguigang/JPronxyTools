package com.ztoncloud.jproxytools.layout.testpage;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;

import atlantafx.base.controls.Spacer;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

/**
 * @Author yugang
 * @create 2023/2/19 21:12
 */
public class LiftView extends VBox {


  public LiftView() {
    createView();
  }

  private void createView() {



    var appsBtn = new Button("", new FontIcon(Material2OutlinedAL.APPS));
    appsBtn.setStyle("-fx-padding:4px");
    appsBtn.getStyleClass().addAll(BUTTON_ICON, FLAT, ACCENT);

    var top = new topBar();
    top.getChildren().addAll(appsBtn);
    getChildren().add(top);
    getChildren().add(toolsVbox());
/*
    getChildren().addAll(

        top,
        toolsVbox()



    );

 */
    VBox.setVgrow(toolsVbox(), Priority.ALWAYS);
  }
  private VBox toolsVbox() {

    var appsBtn1 = new Button("", new FontIcon(Material2OutlinedAL.ADMIN_PANEL_SETTINGS));
    appsBtn1.setStyle("-fx-padding:4px");
    appsBtn1.getStyleClass().addAll(BUTTON_ICON, FLAT, ACCENT);

    var appsBtn2 = new Button("", new FontIcon(Material2OutlinedAL.AC_UNIT));
    appsBtn2.setStyle("-fx-padding:4px");
    appsBtn2.getStyleClass().addAll(BUTTON_ICON, FLAT, ACCENT);
    appsBtn2.setAlignment(Pos.BOTTOM_CENTER);

    var vbox = new VBox();
    vbox.setMinWidth(40);
    //vbox.setAlignment(Pos.CENTER);
    //setStyle("-fx-background-color: red");
    //setAlignment(Pos.CENTER);
    //setPadding(new Insets(10));
    //vbox.setStyle("-fx-background-color: -color-border-muted;-fx-border-color: transparent");
    vbox.setStyle("-fx-border-color: transparent -color-border-default transparent transparent;");
    vbox.setAlignment(Pos.CENTER);
    vbox.setPadding(new Insets(10,0,10,0));
    vbox.getChildren().addAll(
        appsBtn1,
        new Spacer(Orientation.VERTICAL),//拉撑到垂直
        appsBtn2
    );
    setVgrow(vbox,Priority.ALWAYS);
    return vbox;
  }

}
