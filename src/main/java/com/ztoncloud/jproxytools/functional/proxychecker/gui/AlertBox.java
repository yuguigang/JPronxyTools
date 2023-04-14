package com.ztoncloud.jproxytools.functional.proxychecker.gui;

import com.ztoncloud.jproxytools.functional.proxychecker.components.Settings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Creates an AlertBox
 */
public class AlertBox {

    /**
     * Displays an AlertBox
     * @param alertType - javafx.scene.control.Alert.AlertType
     * @param header - Heading of the Alert
     * @param content - Content of the Alert
     */
    public static void show(Alert.AlertType alertType, ObservableValue<String> header, ObservableValue<String> content)  {
        Alert alert = new Alert(alertType, null, ButtonType.OK);
        Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
        Image image = new Image("file:icon.png");
        //or Image image = new Image(new File("za.png").toURI().toString());
        //or Image image = new Image(getClass().getResource("za.jpg").toURI().toString());
        //or new javafx.scene.image.Image(getClass().getResource("za.jpg").toExternalForm());

        dialogStage.getIcons().add(image
                //new Image(Objects.requireNonNull(AlertBox.class.getResourceAsStream("assets/icon.png")))
                //new Image("assets/icon.png",true)

        );
        alert.contentTextProperty().bind(content);
        alert.headerTextProperty().bind(header);
        alert.setTitle(Settings.getApplicationName());
        alert.show();
    }
}
