package com.ztoncloud.jproxytools.functional.proxypanel.gui;

import static atlantafx.base.theme.Styles.BUTTON_CIRCLE;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Styles.SMALL;
import static atlantafx.base.theme.Styles.SUCCESS;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import com.ztoncloud.jproxytools.functional.proxychecker.components.ProxyCheckerSettingsProp;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.layout.page.Overlay;

import com.ztoncloud.jproxytools.theme.CSSFragment;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yugang
 * @create 2022/11/16 16:44
 */
public class InfoGrid extends HBox  {
    private static final int CARD_WIDTH = 220;
    private static final int GRID_HGAP = 20;
    private static final int GRID_VGAP = 10;
    private  static final Logger log = LoggerFactory.getLogger(InfoGrid.class);
    private static final SimpleBooleanProperty isRun = new SimpleBooleanProperty(false);
    private Button proxyPanelBtn;


    public InfoGrid() {
        super();
        createView();
    }

    private void createView() {

        new CSSFragment(Card.CSS).addTo(this);

        getChildren().setAll(

                stillHaveBlock(),
                new Spacer(),
                localIP(),
                new Spacer(),
                localProxy(),
                new Spacer(),
                proxyPanel()
        );

        setId("info-grid");
    }

    private Card stillHaveBlock() {

        var grid = new GridPane();
        grid.setHgap(GRID_HGAP);
        grid.setVgap(GRID_VGAP);

        var stillHaveBtn = new Button("", new FontIcon(Material2MZ.WIDGETS));

        stillHaveBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT, SUCCESS);
        stillHaveBtn.setMouseTransparent(true);
        grid.add(stillHaveBtn,0,0);

        var leftData = new Label("12.87", new FontIcon(Material2AL.ARROW_UPWARD));
        leftData.getStyleClass().addAll(Styles.SUCCESS);
        grid.add(leftData, 0, 1);

        grid.add(new Spacer(),1,0);
        var rightHead = new Text("IP 总数");
        rightHead.getStyleClass().add(Styles.TEXT_MUTED);
        var right = new Label("IP");
        right.setAlignment(Pos.CENTER_RIGHT);
        grid.add(rightHead, 2, 0);

        grid.add(new Spacer(),1,1);
        var rightData = new Label("3.74", new FontIcon(Material2AL.ARROW_DOWNWARD));
        rightData.getStyleClass().addAll(Styles.DANGER);
        grid.add(rightData, 2, 1);
        //grid.setAlignment(Pos.CENTER_RIGHT);



        var card = new Card();
        card.setPrefWidth(CARD_WIDTH);
        //card.setMinWidth(CARD_WIDTH);
        //card.setMaxWidth(CARD_WIDTH);


        card.setBody(grid);

        return card;

    }
    private Card localIP() {
        var grid = new GridPane();
        grid.setHgap(GRID_HGAP);
        grid.setVgap(GRID_VGAP);
        //card.setBody(grid);

        //var leftHead = new Text("Active");
        //leftHead.getStyleClass().add(Styles.TEXT_MUTED);
        //grid.add(leftHead, 0, 0);
        String InternetIP = ProxyCheckerSettingsProp.getValue("InternetIP","NULL");

        var localBtn = new Button("本机IP", new FontIcon(Material2MZ.PIN_DROP));

        localBtn.getStyleClass().addAll( FLAT, SUCCESS);
        localBtn.setMouseTransparent(true);

        var leftLabel = new Label("本机IP",new FontIcon(Material2MZ.PIN_DROP));
        leftLabel.getStyleClass().addAll(BUTTON_CIRCLE, FLAT, SUCCESS);
        grid.add(localBtn,0,0);

        var leftData = new Label(InternetIP);
        leftData.getStyleClass().addAll(Styles.SUCCESS);
        grid.add(leftData, 0, 1);

        grid.add(new Spacer(),1,0);

        var rightHead = new Text("本机 IP");
        rightHead.getStyleClass().add(Styles.TEXT_MUTED);


        grid.add(rightHead, 2, 0);

        String networkDelay = ProxyCheckerSettingsProp.getValue("networkDelay","");
        var rightData = new Label(networkDelay+" ms");
        Tooltip tooltip = new Tooltip();
        tooltip.setText("本机到API服务器延迟时间");
        rightData.setTooltip(tooltip);
        if (Integer.parseInt(networkDelay)>500||networkDelay.isEmpty()) {
            //延时>500ms显示警告色
            rightData.getStyleClass().addAll(Styles.DANGER);
        } else {
            rightData.getStyleClass().addAll(SUCCESS);
        }
        //grid.add(rightData, 1, 1);


        var card = new Card();
        card.setPrefWidth(CARD_WIDTH);
        //card.setMinWidth(CARD_WIDTH);
        //card.setMaxWidth(CARD_WIDTH);
        card.setBody(grid);
        return card;



    }
    private Card localProxy() {
        var grid = new GridPane();
        grid.setHgap(GRID_HGAP);
        grid.setVgap(GRID_VGAP);
        //card.setBody(grid);

        //var leftHead = new Text("Active");
        //leftHead.getStyleClass().add(Styles.TEXT_MUTED);
        //grid.add(leftHead, 0, 0);

        var localProxyBtn = new Button("", new FontIcon(Material2MZ.ROTATE_90_DEGREES_CCW));

        localProxyBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT, SUCCESS);
        localProxyBtn.setMouseTransparent(true);
        grid.add(localProxyBtn,0,0);

        String localIP ;

try {
    localIP =  InetAddress.getLocalHost().getHostAddress();

}catch (UnknownHostException e){
    localIP = "0.0.0.0";
    log.error("读取本地IP失败！");
}

        var leftData = new Label(localIP);
        leftData.getStyleClass().addAll(Styles.SUCCESS);
        grid.add(leftData, 0, 1);

        grid.add(new Spacer(),1,0);

        var rightHead = new Text("本机代理");
        rightHead.getStyleClass().add(Styles.TEXT_MUTED);
        grid.add(rightHead, 2, 0);

        var rightData = new Label("3.74", new FontIcon(Material2AL.ARROW_DOWNWARD));
        rightData.getStyleClass().addAll(Styles.DANGER);
        grid.add(rightData, 1, 1);
        var card = new Card();
        card.setPrefWidth(CARD_WIDTH);
        //card.setMinWidth(CARD_WIDTH);
        //card.setMaxWidth(CARD_WIDTH);
        card.setBody(grid);
        return card;


    }

    /**
     * 代理设置信息面板
     *
     * @return {@link Card}
     */
    private Card proxyPanel() {
        var grid = new GridPane();
        grid.setHgap(GRID_VGAP);
        grid.setVgap(GRID_VGAP);
        //card.setBody(grid);

        //var leftHead = new Text("Active");
        //leftHead.getStyleClass().add(Styles.TEXT_MUTED);
        //grid.add(leftHead, 0, 0);

        proxyPanelBtn = new Button("", new FontIcon(Feather.SETTINGS));

        proxyPanelBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT);
        var toolTip = new Tooltip();
        toolTip.textProperty().bind(LanguageResource.getLanguageBinding("Settings"));
        proxyPanelBtn.setTooltip(toolTip);
        //proxyPanelBtn.setMouseTransparent(true);
        grid.add(proxyPanelBtn,0,0);

        grid.add(new Spacer(),1,0);

        var rightHead = new Text("设置状态");
        rightHead.getStyleClass().add(Styles.TEXT_MUTED);

        grid.add(rightHead, 2, 0);


        var leftData = new Label("12.87", new FontIcon(Material2AL.INFO));
        leftData.getStyleClass().addAll(Styles.SUCCESS);

        grid.add(createLabel(), 0, 1);






/*
        var rightData = new Label("3.74", new FontIcon(Material2AL.ARROW_DOWNWARD));
        rightData.getStyleClass().addAll(Styles.DANGER);
        grid.add(rightData, 1, 1);

 */


        var card = new Card();
        //card.setMinWidth(CARD_WIDTH);
        //card.setMaxWidth(CARD_WIDTH);
        card.setPrefWidth(CARD_WIDTH);
        card.setBody(grid);
        return card;

    }

    private Label createLabel() {
        var label = new Label();


        if (isRun.getValue()) {
            label.setText("运行中.....");
            label.getStyleClass().addAll(Styles.SUCCESS);
            label.setGraphic(new FontIcon(Material2AL.ARROW_RIGHT));
        } else {
            label.setText("已停止.....");
            label.setGraphic(new FontIcon(Material2MZ.PAUSE));
            label.getStyleClass().addAll(Styles.WARNING);
        }

        //var bool = isRun();

        isRun.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                Boolean newValue) {
                log.debug("布尔值改变： " + newValue);
                if (newValue) {
                    label.getStyleClass().remove(Styles.WARNING);
                    label.getStyleClass().addAll(Styles.SUCCESS);
                    label.setText("运行中.....");
                    label.setGraphic(new FontIcon(Material2AL.ARROW_RIGHT));
                    //label.setText("运行");
                } else {
                    //label.setText("停止");
                    label.getStyleClass().remove(Styles.SUCCESS);
                    label.getStyleClass().addAll(Styles.WARNING);
                    label.setText("已停止.....");
                    label.setGraphic(new FontIcon(Material2MZ.PAUSE));
                }

            }
        });

        //label.setPadding(new Insets(10));
        return label;
    }

        public Button getSettingsBtn () {
        return proxyPanelBtn;
    }

    /**
     * 运行状态
     *
     * @return boolean
     */
    public static boolean isRun() {
        return isRun.getValue() ;
    }

    /**
     * 设置运行状态
     *
     * @param bool 保龄球
     */
    public static void setRun ( boolean bool ) {
        isRun.setValue(bool);
    }

}
