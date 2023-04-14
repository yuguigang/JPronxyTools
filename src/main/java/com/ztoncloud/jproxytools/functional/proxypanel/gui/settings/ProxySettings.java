
package com.ztoncloud.jproxytools.functional.proxypanel.gui.settings;

import static atlantafx.base.theme.Styles.TEXT;
import static atlantafx.base.theme.Styles.TEXT_MUTED;
import static atlantafx.base.theme.Styles.TEXT_SMALL;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_HGAP;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_VGAP;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.control.TabPane.TabClosingPolicy.UNAVAILABLE;
import static javafx.scene.layout.Priority.ALWAYS;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import com.ztoncloud.jproxytools.Utils.Containers;
import com.ztoncloud.jproxytools.functional.proxypanel.ServerContext;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.layout.page.SampleBlock;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

class ProxySettings extends StackPane {

    private static final int CONTROL_WIDTH = 200;
    private VBox settingsList;

    public ProxySettings() {
        super();
        createTabPane();
    }

    private  void createTabPane() {
        var tabs = new TabPane();
        //Styles.toggleStyleClass(tabs, TabPane.STYLE_CLASS_FLOATING);//floating样式。
        tabs.setTabClosingPolicy(UNAVAILABLE);//禁止关闭按钮

        // NOTE: Individually disabled tab is still closeable even while it looks
        //       like disabled. To prevent it from closing one can use "black hole"
        //       event handler. #javafx-bug

        tabs.getTabs().addAll(
            new generalSetting(),
            new securitySetting()
        );
        getChildren().add(tabs);
    }
}
