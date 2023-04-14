
package com.ztoncloud.jproxytools.functional.proxypanel.gui;

import static atlantafx.base.theme.Styles.TEXT;
import static atlantafx.base.theme.Styles.TEXT_MUTED;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_HGAP;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_VGAP;


import atlantafx.base.theme.Styles;
import com.ztoncloud.jproxytools.functional.proxypanel.gui.settings.ProxySettingsDialog;
import com.ztoncloud.jproxytools.layout.page.AbstractPage;
import com.ztoncloud.jproxytools.layout.page.SampleBlock;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxySettingsPage extends AbstractPage {
    private  static final Logger log = LoggerFactory.getLogger(ProxySettingsPage.class);
    public static final ObservableValue<String> NAME = new SimpleStringProperty("代理设置");
    private static final int CONTROL_WIDTH = 200;
    private ProxySettingsDialog proxySettingsDialog;

    @Override
    public ObservableValue<String> getName() { return NAME; }

    @Override
    public boolean canDisplaySourceCode() { return false; }

    @Override
    public boolean canChangeThemeSettings() { return false; }

    public ProxySettingsPage() {
        super();

        createView();
    }

    private void createView() {
        setUserContent(new VBox(
                PAGE_VGAP,
                portSettings(),
                providerChooser()
        ));
    }


    /**
     * 端口设置
     *
     * @return {@link SampleBlock}
     */
    private SampleBlock portSettings() {
        var leftText2 = new TextField("5000");
        leftText2.getStyleClass().add(Styles.LEFT_PILL);
        leftText2.setPrefWidth(100);

        var centerLabel2 = new Label("-");
        centerLabel2.getStyleClass().add(Styles.CENTER_PILL);

        var rightText2 = new TextField("5010");
        rightText2.getStyleClass().add(Styles.RIGHT_PILL);
        rightText2.setPrefWidth(100);

        var sample2 = new HBox(leftText2, centerLabel2, rightText2);
        sample2.setAlignment(Pos.CENTER_LEFT);


        Text text = new Text("PS: 端口最多能设置30个，范围在1025-65535之间");
        text.getStyleClass().addAll("Muted", TEXT, TEXT_MUTED);

        Button applyBtn = new Button("应用");

        var flowPane = new FlowPane(
                BLOCK_HGAP, BLOCK_VGAP,
                new Label("端口设置： "),

                sample2,
                text,
                applyBtn

        );

        return new SampleBlock("端口设置", flowPane
                );
    }

    /**
     * 供应商选择器
     *
     * @return {@link SampleBlock}
     */
    private SampleBlock providerChooser() {
        //IP提供商选择框
        ComboBox<String> comboBox = new ComboBox<>();
        //comboBox.getItems().add(DEFAULT_FONT_ID); // keyword to reset font family to its default value
        comboBox.getItems().addAll(providerArrayList());
        comboBox.setPrefWidth(CONTROL_WIDTH);
        //读取设置语言，默认显示
        //comboBox.getSelectionModel().select(getLanguage(UserSettings.getValue("Language","zh-CN")));
        comboBox.getSelectionModel().selectFirst();
        comboBox.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                //TM.setFontFamily(DEFAULT_FONT_ID.equals(val) ? DEFAULT_FONT_FAMILY_NAME : val);
                //UserSettings.setValue("Theme_FontFamily",val);
                //暂存到设置里面
                //UserSettings.setValue("Language",getLocale(val));

            }
        });

        Button applyBtn = new Button("应用");



        var flowPane = new FlowPane(
                BLOCK_HGAP, BLOCK_VGAP,
                new Label("IP提供商： "),
            comboBox,
            applyBtn

        );

        return new SampleBlock("IP提供商", flowPane);
    }

    /**
     * 提供者数组列表
     *
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
