package com.ztoncloud.jproxytools.functional.proxypanel.gui;

import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.ProxyPoolBean;
import com.ztoncloud.jproxytools.i18n.LanguageResource;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

/**
 * @Author yugang
 * @create 2022/11/22 5:09
 */
public class ProxyPoolTable extends VBox {

   public ProxyPoolTable () {
       getChildren().setAll(createTable());

       //createTable();

    }
    @SuppressWarnings("unchecked")
    private TableView<ProxyPoolBean> createTable() {
        // 设置表格工厂
        TableColumn<ProxyPoolBean, String> column_ip = new TableColumn<>();
        column_ip.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_ip"));
        column_ip.setMinWidth(130);
        //禁止排序
        column_ip.setSortable(false);
        column_ip.setCellValueFactory(new PropertyValueFactory<>("Ip"));

        TableColumn<ProxyPoolBean, String> column_port = new TableColumn<>();
        column_port.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_Port"));
        column_port.setSortable(false);
        column_port.setCellValueFactory(new PropertyValueFactory<>("Port"));//Port

        TableColumn<ProxyPoolBean, String> column_status = new TableColumn<>();
        column_status.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_Status"));
        column_status.setCellValueFactory(new PropertyValueFactory<>("ProxyStatus"));//ProxyStatus

        TableColumn<ProxyPoolBean, String> column_anonymity = new TableColumn<>();
        column_anonymity.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_Anonymity"));
        column_anonymity.setMinWidth(100);
        column_anonymity.setCellValueFactory(new PropertyValueFactory<>("ProxyAnonymity"));//ProxyAnonymity

        TableColumn<ProxyPoolBean, String> column_country = new TableColumn<>();
        column_country.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_Country"));
        column_country.setMinWidth(120);
        column_country.setCellValueFactory(new PropertyValueFactory<>("Country"));//Country

        TableColumn<ProxyPoolBean, String> column_response_time = new TableColumn<>();
        column_response_time.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_ResponseTime"));
        column_response_time.setMinWidth(100);
        column_response_time.setCellValueFactory(new PropertyValueFactory<>("ResponseTime"));//ResponseTime

        var tableView = new TableView<ProxyPoolBean>();
        tableView.getColumns().setAll(column_ip,column_port,column_status,column_anonymity,column_country,column_response_time);
        //按响应排序
        tableView.getSortOrder().add(column_response_time);

        return tableView;
    }
}
