package com.ztoncloud.jproxytools.layout.testpage;

import atlantafx.base.theme.Styles;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * @Author yugang
 * @create 2023/2/20 17:21
 */
public class TEST_TabPane extends TabPane {


public TEST_TabPane (){
  super();
  createView();
}

private void createView() {

  final int[] counter = {0};


  for (int i = 0; i < 5; i++) {

    // create Tab
    Tab tab = new Tab("Tab_" + (int)(counter[0] + 1));

    // create a label
    Label label = new Label("This is Tab:"
        + (int)(counter[0] + 1));

    counter[0]++;

    // add label to the tab
    tab.setContent(label);
//tab.getStyleClass().addAll(Styles.TEXT);
    // add tab
    getTabs().add(tab);
  }

  // create a tab which
  // when pressed creates a new tab
  Tab newtab = new Tab();

  // action event
  EventHandler<Event> event =
      new EventHandler<Event>() {

        public void handle(Event e)
        {
          if (newtab.isSelected())
          {

            // create Tab
            Tab tab = new Tab("Tab_" + (int)(counter[0] + 1));


            // create a label
            Label label = new Label("This is Tab:"
                + (int)(counter[0] + 1));

            counter[0]++;

            // add label to the tab


            tab.setContent(label);


            // add tab
            getTabs().add(
                getTabs().size() - 1, tab);

            // select the last tab
            getSelectionModel().select(
                getTabs().size() - 2);
          }
        }
      };

  // set event handler to the tab
  newtab.setOnSelectionChanged(event);

  // add newtab
  getTabs().add(newtab);

  setTabMaxHeight(26);
  //getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
  getStyleClass().addAll(Styles.ACCENT);
  getStyleClass().add("c-tab-pane");
}
}
