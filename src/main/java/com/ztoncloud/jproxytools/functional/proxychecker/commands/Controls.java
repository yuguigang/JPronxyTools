
package com.ztoncloud.jproxytools.functional.proxychecker.commands;

import static atlantafx.base.theme.Styles.BUTTON_ICON;

import com.ztoncloud.jproxytools.event.BrowseEvent;
import com.ztoncloud.jproxytools.event.DefaultEventBus;
import java.net.URI;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

public final class Controls {

    public static Button iconButton(Ikon icon, boolean disable) {
        return button("", icon, disable, BUTTON_ICON);
    }

    public static Button button(String text, Ikon icon, boolean disable, String... styleClasses) {
        var button = new Button(text);
        if (icon != null) { button.setGraphic(new FontIcon(icon)); }
        button.setDisable(disable);
        button.getStyleClass().addAll(styleClasses);
        return button;
    }

    public static MenuItem menuItem(String text, Ikon graphic, KeyCombination accelerator) {
        return menuItem(text, graphic, accelerator, false);
    }
    public static MenuItem menuItem( Ikon graphic, KeyCombination accelerator) {
        return menuItem( graphic, accelerator, false);
    }

    public static MenuItem menuItem(String text, Ikon graphic, KeyCombination accelerator, boolean disable) {
        var item = new MenuItem(text);

        if (graphic != null) { item.setGraphic(new FontIcon(graphic)); }
        if (accelerator != null) { item.setAccelerator(accelerator); }
        item.setDisable(disable);

        return item;
    }
    public static MenuItem menuItem( Ikon graphic, KeyCombination accelerator, boolean disable) {
        var item = new MenuItem();
        if (graphic != null) { item.setGraphic(new FontIcon(graphic)); }
        if (accelerator != null) { item.setAccelerator(accelerator); }
        item.setDisable(disable);

        return item;
    }

    public static ToggleButton toggleButton(String text,
                                            Ikon icon,
                                            ToggleGroup group,
                                            boolean selected,
                                            String... styleClasses) {
        var toggleButton = new ToggleButton(text);
        if (icon != null) { toggleButton.setGraphic(new FontIcon(icon)); }
        if (group != null) { toggleButton.setToggleGroup(group); }
        toggleButton.setSelected(selected);
        toggleButton.getStyleClass().addAll(styleClasses);

        return toggleButton;
    }

    public static Hyperlink hyperlink(String text, URI uri) {
        var hyperlink = new Hyperlink(text);
        if (uri != null) {
            hyperlink.setOnAction(event -> DefaultEventBus.getInstance().publish(new BrowseEvent(uri)));
        }
        return hyperlink;
    }
}
