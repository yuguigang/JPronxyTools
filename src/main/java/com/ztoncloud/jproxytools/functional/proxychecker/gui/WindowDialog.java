package com.ztoncloud.jproxytools.functional.proxychecker.gui;



import com.ztoncloud.jproxytools.Env.BaseEnv;
import com.ztoncloud.jproxytools.Utils.Resources;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.functional.proxychecker.components.ProxyCheckerSettingsProp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import static atlantafx.base.theme.Styles.STATE_SUCCESS;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_HGAP;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_VGAP;
import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;


/**
 * 构建一个新弹窗
 */
public class WindowDialog {

    private static  String defaultType ;
    private static  String defaultTimeout ;
    private static  String defaultMaxThreads ;



    private static final ChoiceBox<String> proxyType = new ChoiceBox<>();
    private static final TextField proxyTimout = new TextField();
    private static final TextField proxyMaxThreads = new TextField();
    private static final Button button_save = new Button();

    public WindowDialog() {
        defaultType = ProxyCheckerSettingsProp.getValue("ProxyChecker_Type","SOCKS");
        defaultTimeout = ProxyCheckerSettingsProp.getValue("ProxyChecker_Timeout","5000");
        defaultMaxThreads = ProxyCheckerSettingsProp.getValue("ProxyChecker_MaxThreads","100");

    }

    /**
     * 显示弹窗
     *
     *
     */
    public  void show() {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.titleProperty().bind(LanguageResource.getLanguageBinding("WindowDialog_Title"));
            stage.getIcons().add(new Image(Resources.getResourceAsStream(BaseEnv.APP_LOGO)));//("/assets/images/ic-launcher-rounded-192.png")));
            stage.setResizable(false);
            stage.setScene(settingsTabPane());
            stage.show();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static Scene settingsTabPane() {
        var body = new StackPane();
        body.getChildren().setAll(
           createTabPane()
        );
        return new Scene(body, 468, 225);
    }
    private static TabPane createTabPane() {
        var tabs = new TabPane();
        //Styles.toggleStyleClass(tabs, TabPane.STYLE_CLASS_FLOATING);//floating样式。
        tabs.setTabClosingPolicy(UNAVAILABLE);//禁止关闭按钮

        // NOTE: Individually disabled tab is still closeable even while it looks
        //       like disabled. To prevent it from closing one can use "black hole"
        //       event handler. #javafx-bug
        tabs.getTabs().addAll(
                settingsTab()
        );

        return tabs;
    }

    /**
     * 设置 Tab
     * @return
     */
    private static Tab settingsTab() {

        //设置默认样式button
        button_save.setDefaultButton(true);

        button_save.setDisable(true);
        button_save.setAlignment(Pos.CENTER_LEFT);
        button_save.textProperty().bind(LanguageResource.getLanguageBinding("WindowDialog_btn_save_Text"));
        button_save.setOnAction(event -> {
            ProxyCheckerSettingsProp.setValue("ProxyChecker_Type", proxyType.getSelectionModel().getSelectedItem());
            ProxyCheckerSettingsProp.setValue("ProxyChecker_Timeout", proxyTimout.getText());
            ProxyCheckerSettingsProp.setValue("ProxyChecker_MaxThreads", proxyMaxThreads.getText());
            if (ProxyCheckerSettingsProp.saveSettingsFile()) {
                Stage stage = (Stage) button_save.getScene().getWindow();
                stage.close();
            }
        });
        //设置选择框
        proxyType.setPrefWidth(200);
        proxyType.getItems().clear();//这里要先清除，不然一直加入
        proxyType.getItems().addAll("HTTP","SOCKS");
        proxyType.pseudoClassStateChanged(STATE_SUCCESS, true);
        //读取设置，默认"SOCKS"
        proxyType.getSelectionModel().select(defaultType);
        proxyType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(!defaultType.equals(newValue)) {
                button_save.setDisable(false);

            }
            if(!settingsChanged()) {
                button_save.setDisable(true);
            }

        });
        //设置超时设置
        proxyTimout.setPrefWidth(200);
        proxyTimout.setText(defaultTimeout);
        proxyTimout.textProperty().addListener((observable, oldValue, newValue) ->
                integerTextField(proxyTimout, oldValue, newValue)
        );
        //设置最大线程
        proxyMaxThreads.setPrefWidth(200);
        proxyMaxThreads.setText(defaultMaxThreads);
        proxyMaxThreads.textProperty().addListener((observable, oldValue, newValue) ->
                integerTextField(proxyMaxThreads, oldValue, newValue)
        );

        var controls = new GridPane();
        controls.setHgap(BLOCK_HGAP);
        controls.setVgap(BLOCK_VGAP);
        controls.setPadding(new Insets(20));
        controls.setAlignment(Pos.CENTER);

        var type = new Label();
        type.textProperty().bind(LanguageResource.getLanguageBinding("WindowDialog_Label_Type"));
        controls.add(type, 0, 0);
        controls.add(proxyType, 1, 0);

        var timout = new Label();
        timout.textProperty().bind(LanguageResource.getLanguageBinding("WindowDialog_Label_Timout"));
        controls.add(timout, 0, 1);
        controls.add(proxyTimout, 1, 1);

        var maxThreads = new Label();
        maxThreads.textProperty().bind(LanguageResource.getLanguageBinding("WindowDialog_Label_MaxThreads"));
        controls.add(maxThreads, 0, 2);
        controls.add(proxyMaxThreads, 1, 2);

        controls.add(button_save, 1, 3);

        var tab = new Tab();
        tab.textProperty().bind(LanguageResource.getLanguageBinding("WindowDialog_Title"));
        //tab.setGraphic(new FontIcon(randomIcon()));
        tab.setContent(controls);
        return tab;
    }
    /**
     * 当设置改变时，boolean翻转。
     * @return Boolean - 当设置改变时，为true，没有发生改变时，为flash
     */
    private static boolean settingsChanged() {
        return (
                !(defaultType.equals(proxyType.getSelectionModel().getSelectedItem())) ||
                        !(defaultTimeout.equals(proxyTimout.getText())) ||
                        !(defaultMaxThreads.equals(proxyMaxThreads.getText()))
        );
    }
    /**
     * 确保用户只能在文本字段中输入数字
     * @param field - 输入文本
     * @param oldValue - 输入前字符串
     * @param newValue - 新输入的字符串
     */
    private static void integerTextField(TextField field, String oldValue, String newValue) {
        try {
            if(field.getText().length() > 0 ) {
                int value = Integer.parseInt(newValue);
                if(field.getText().length() == 1) { // 输入第一个数字
                    if(value == 0) { // 如果是第一个数字是0，则不改变值
                        field.setText(oldValue);
                    }
                }
            }
        } catch (NumberFormatException e) {
            field.setText(oldValue);//如果不是数字，也不改变输入值
        }
        button_save.setDisable(!settingsChanged()); // 仅在值已更改时启用保存按钮
    }
}
