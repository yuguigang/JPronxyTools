
package com.ztoncloud.jproxytools.layout.page;

import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;

import com.ztoncloud.jproxytools.Utils.JColorUtils;
import com.ztoncloud.jproxytools.config.PreferencesBean;
import com.ztoncloud.jproxytools.theme.AccentColor;
import com.ztoncloud.jproxytools.theme.ThemeManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 强调色选择HBox
 */
public class AccentColorSelector extends HBox {
    private static final Logger log = LoggerFactory.getLogger(AccentColorSelector.class.toString());

    private final PreferencesBean preferences;
    public AccentColorSelector(final PreferencesBean preferences) {
        super();
        this.preferences = preferences;
        createView();
    }

    private void createView() {
        //还原强调色按钮
        var resetBtn = new Button("", new FontIcon(Material2AL.CLEAR));
        resetBtn.getStyleClass().addAll(BUTTON_ICON, FLAT);
        resetBtn.setOnAction(e -> {
                ThemeManager.getInstance().resetAccentColor();
                //复位时，移除强调色键值
            preferences.setAccentColor(null);
        }
        );

        setAlignment(Pos.CENTER_LEFT);
        //颜色选择
        getChildren().setAll(
                colorButton(AccentColor.primerPurple()),
                colorButton(AccentColor.primerPink()),
                colorButton(AccentColor.primerCoral()),
                resetBtn
        );
        getStyleClass().add("color-selector");
    }

    /**
     * 转换颜色为颜色按钮
     * @param accentColor
     * @return
     */
    private Button colorButton(AccentColor accentColor) {
        var icon = new Region();
        icon.getStyleClass().add("icon");

        var btn = new Button("", icon);
        btn.getStyleClass().addAll(BUTTON_ICON, FLAT, "color-button");
        btn.setStyle("-color-primary:" + JColorUtils.toHexWithAlpha(accentColor.primaryColor()) + ";");
        btn.setUserData(accentColor);
        btn.setOnAction(event -> {
            log.debug("选择强调色：" + accentColor.pseudoClass().toString());
            ThemeManager.getInstance().setAccentColor((AccentColor) btn.getUserData());
            preferences.setAccentColor(accentColor.pseudoClass().toString());


        });
        return btn;
    }
}
