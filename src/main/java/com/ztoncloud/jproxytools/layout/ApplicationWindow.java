
package com.ztoncloud.jproxytools.layout;


import com.ztoncloud.jproxytools.Utils.Containers;
import com.ztoncloud.jproxytools.layout.mainlayer.MainLayer;
import com.ztoncloud.jproxytools.layout.page.Overlay;
import com.ztoncloud.jproxytools.layout.testpage.MainBar;
import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/***
 * 所有窗口页面入口
 * @author yugang
 * @date 2023/03/01
 */
public class ApplicationWindow extends  AnchorPane {

    public ApplicationWindow() {

        //这是应用用户自定义CSS的地方，
        //在 ':root' 之下
        var body = new StackPane();
        body.getStyleClass().add("body");
        //加入页面
        body.getChildren().setAll(
                new Overlay(),//切换页面或者弹窗后重写背景
                new MainLayer()
        );
        //设置锚点,这里一定要重设锚点，不然挤一堆。
        Containers.setAnchors(body, Insets.EMPTY);
        getChildren().setAll(body);
    }


}
