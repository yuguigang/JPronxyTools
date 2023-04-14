package com.ztoncloud.jproxytools.functional.proxychecker.commands;


import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyModel;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyAnonymity;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyStatus;
import com.ztoncloud.jproxytools.functional.proxychecker.gui.AlertBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 将代理导出到磁盘上的静态方法
 */
public class ExportCommand {

    /**
     *
     * 获取用户选择的目标文件，将所有加载的代理保存到该文件中，格式为ip:port
     * @param listView - collection component
     */
    public static void save(ListView<String> listView) {
        if (listView.getItems().size() != 0) {
            File file = FileCommand.getFileToSave(false);
            if (file != null) {
                try {
                    PrintWriter printWriter = new PrintWriter(new FileWriter(file));
                    for (String ip_port : listView.getItems()) {
                        printWriter.write(ip_port + "\n");
                    }
                    printWriter.close();
                    AlertBox.show(Alert.AlertType.INFORMATION, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_completed_header"),
                            LanguageResource.getLanguageBinding("ExportCommand_AlertBox_completed_content")
                    );
                } catch (IOException e) {
                    AlertBox.show(Alert.AlertType.ERROR, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_Failed_header") ,
                            LanguageResource.getLanguageBinding("ExportCommand_AlertBox_Failed_content")
                             );
                }
            }
        } else {
            AlertBox.show(Alert.AlertType.INFORMATION, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_NoProxies_header"),
                    LanguageResource.getLanguageBinding("ExportCommand_AlertBox_NoProxies_content")
                    );
        }
    }

    /**
     * Gets a user selected destination file to export the proxy table in a comma separated value file.
     * @param tableView - collection component
     */
    public static void save(TableView<ProxyModel> tableView) {
        if (tableView.getItems().size() != 0) {
            File file = FileCommand.getFileToSave(true);
            if (file != null) {
                try {
                    PrintWriter printWriter = new PrintWriter(new FileWriter(file));
                    for (ProxyModel proxyModel : tableView.getItems()) {
                        printWriter.write(getCSV(proxyModel));
                    }
                    printWriter.close();
                    AlertBox.show(Alert.AlertType.INFORMATION, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_TableExported_header"),
                            LanguageResource.getLanguageBinding("ExportCommand_AlertBox_TableExported_content")
                            );
                } catch (IOException e) {
                    AlertBox.show(Alert.AlertType.ERROR, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_Failed_header"),
                            LanguageResource.getLanguageBinding("ExportCommand_AlertBox_Failed_content"));
                }
            }
        } else {
            AlertBox.show(Alert.AlertType.INFORMATION, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_NoData_header"),
                    LanguageResource.getLanguageBinding("ExportCommand_AlertBox_NoData_content")
                    );
        }
    }

    /**
     * Gets a user selected destination file to export the proxy table based on the given ProxyStatus and
     * ProxyAnonymity values, in the format ip:port
     * @param tableView - collection component
     * @param proxyStatus - The ProxyStatus
     * @param proxyAnonymity - The ProxyAnonymity (null for all proxies belonging to ProxyStatus)
     */
    public static void save(TableView<ProxyModel> tableView, ProxyStatus proxyStatus, ProxyAnonymity proxyAnonymity) {
        if (tableView.getItems().size() != 0) {
            File file = FileCommand.getFileToSave(false);
            if (file != null) {
                try {
                    PrintWriter printWriter = new PrintWriter(new FileWriter(file));
                    for (ProxyModel proxyModel : tableView.getItems()) {

                        String line = getLine(proxyModel, proxyStatus, proxyAnonymity);
                        if (line != null) { // a valid line was given
                            printWriter.write(line);
                        }
                    }
                    printWriter.close();
                    AlertBox.show(Alert.AlertType.INFORMATION, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_DataExported_header"),
                            LanguageResource.getLanguageBinding("ExportCommand_AlertBox_DataExported_content")
                            );
                } catch (IOException e) {
                    AlertBox.show(Alert.AlertType.ERROR, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_Failed_header"),
                            LanguageResource.getLanguageBinding("ExportCommand_AlertBox_Failed_content")
                            );
                }
            }
        } else {
            AlertBox.show(Alert.AlertType.INFORMATION, LanguageResource.getLanguageBinding("ExportCommand_AlertBox_NoData_header"),
                    LanguageResource.getLanguageBinding("ExportCommand_AlertBox_NoData_content")
                    );
        }
    }

    /**
     * 为ProxyModel生成逗号分隔的字符串（.csv格式）
     * @param proxyModel - The Proxy object
     * @return String - csv
     */
    private static String getCSV(ProxyModel proxyModel) {
        return proxyModel.getIp() + "," +
                proxyModel.getPort() + "," +
                proxyModel.getProxyStatus() + "," +
                proxyModel.getProxyAnonymity() + "," +
                proxyModel.getCountry() + "," +
                proxyModel.getResponseTime() + "\n";
    }

    /**
     * Generates a line in the form ip:port for a given proxy confining to ProxyStatus and ProxyAnonymity
     * @param proxyModel - The Proxy object
     * @param proxyStatus - The ProxyStatus
     * @param proxyAnonymity - The ProxyAnonymity (null for all proxies belonging to ProxyStatus)
     * @return String - in the form ip:port,
     *         null if the proxy does not confine to the given proxyStatus and/or proxyAnonymity
     */
    private static String getLine(ProxyModel proxyModel, ProxyStatus proxyStatus, ProxyAnonymity proxyAnonymity) {
        if (proxyModel.getProxyStatus() == proxyStatus) {
            if (proxyAnonymity != null) {
                if (proxyAnonymity == proxyModel.getProxyAnonymity()) {
                    return proxyModel.getIp() + ":" + proxyModel.getPort() + "\n";
                } else {
                    return null;
                }
            } else {
                return proxyModel.getIp() + ":" + proxyModel.getPort() + "\n";
            }
        }
        return null;
    }
}
