package com.ztoncloud.jproxytools.layout.testpage;

import javafx.scene.layout.BorderPane;

/**
 * @Author yugang
 * @create 2023/2/20 0:26
 */
public class CenterView extends BorderPane {

  public CenterView () {
    createView();
  }

  private void createView() {

    setTop(new topBar());
    setMaxWidth(220);
  }
}
