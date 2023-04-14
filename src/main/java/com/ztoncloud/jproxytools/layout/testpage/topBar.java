package com.ztoncloud.jproxytools.layout.testpage;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

/**
 * @Author yugang
 * @create 2023/2/19 21:32
 */
public class topBar extends HBox {


  public topBar (){
    setPrefHeight(38);
    setMinWidth(40);
    setAlignment(Pos.CENTER);
    //setPadding(new Insets(10));
    setStyle("-fx-background-color: -color-bg-subtle;-fx-border-color: transparent transparent -color-border-default transparent;");
  }

}
