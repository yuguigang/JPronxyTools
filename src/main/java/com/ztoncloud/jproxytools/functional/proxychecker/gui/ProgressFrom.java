package com.ztoncloud.jproxytools.functional.proxychecker.gui;

import atlantafx.base.controls.RingProgressIndicator;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.theme.CSSFragment;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import static atlantafx.base.theme.Styles.*;


/**
 * 透明 loading 窗口
 * @Author yugang
 * @create 2022/10/28 1:28
 */
public class ProgressFrom {


    private final Stage dialogStage;

    public ProgressFrom() {

        dialogStage = new Stage();


        // 窗口父子关系
        //dialogStage.initOwner();
        dialogStage.initStyle(StageStyle.UNDECORATED);//取消关闭按钮
        dialogStage.initStyle(StageStyle.TRANSPARENT);//透明窗口
        /*
        通过initModality()方法来设置窗体模态
        Modality.APPLICATION_MODAL： (默认)阻止用户访问所有其他的窗体，直到该窗体关闭
        Modality.WINDOW_MODAL： 仅阻止用户访问所有该窗体的owner(父级)窗体，直到该窗体关闭
        Modality.NONE： 不会阻止用户访问任何窗体
         */
        dialogStage.initModality(Modality.APPLICATION_MODAL);

        var reverseIndicator = new RingProgressIndicator(-1d,false);
        reverseIndicator.setMinSize(200, 200);
        reverseIndicator.setBackground(Background.EMPTY);


        var reverseIndicatorLabel = new Label();
       reverseIndicatorLabel.textProperty().bind(LanguageResource.getLanguageBinding("ProgressFrom_Label_Title"));

        reverseIndicatorLabel.getStyleClass().addAll(TITLE_4,TEXT_BOLD, WARNING);

        var stopButton = new Button("", new FontIcon(Material2MZ.STOP));
        stopButton.getStyleClass().addAll("favorite-button", BUTTON_CIRCLE, FLAT, DANGER);
        new CSSFragment("""
                .favorite-button.button >.ikonli-font-icon {                 
                    -fx-font-size:  32px;
                    -fx-icon-size:  32px;
                }
                """).addTo(stopButton);
        var reverseBox = new VBox(10, reverseIndicatorLabel, stopButton);
        reverseBox.setBackground(Background.EMPTY);
        reverseBox.setAlignment(Pos.CENTER);
        reverseIndicator.setGraphic(reverseBox);

        Scene scene = new Scene(reverseIndicator);
        scene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(scene);
    }

    public void ProgressBarShow () {
        dialogStage.show();
    }

    public Stage getDialogStage(){
        return dialogStage;
    }

    public void ProgressBarClose() {
        dialogStage.close();
    }
}

