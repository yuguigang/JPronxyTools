package com.ztoncloud.jproxytools.functional.proxychecker.gui.events;



import com.ztoncloud.jproxytools.functional.proxychecker.commands.ProxyCheckCommand;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyModel;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

/**
 * Event Handler for ProxyCheckerController
 */
@SuppressWarnings("unchecked")//仅仅是抑制警告信息。
public class ProxyCheckerKeyEvent implements EventHandler<KeyEvent> {
    @Override
    public void handle(KeyEvent event) {
        if(event.getSource() instanceof ListView) {
            ListView<String> view = (ListView<String>)event.getSource();

            // allow deleting items from a ListView
            if ((event.getCode().equals(KeyCode.DELETE)) || (event.getCode().equals(KeyCode.BACK_SPACE))) {
                if(!ProxyCheckCommand.isRunning()) { // only if proxy checker isn't running
                    view.getItems().remove(view.getSelectionModel().getSelectedItem());
                }
            }
        } else if(event.getSource() instanceof TableView) {
            if (event.getCode().equals(KeyCode.C)) { // copy from TableView
                TableView<ProxyModel> tableView = (TableView<ProxyModel>)event.getSource();

                ProxyModel proxyModel = tableView.getSelectionModel().getSelectedItem();
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                        new StringSelection(proxyModel.getIp() + ":" + proxyModel.getPort()), null
                );
            }
        }
    }
}
