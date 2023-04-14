package com.ztoncloud.jproxytools.functional.proxychecker.commands;


import com.ztoncloud.jproxytools.i18n.LanguageResource;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

/**
 * Contains static methods that allow selecting a file to save or files to open in a dialog.
 * 包含允许在对话框中选择要保存的文件或要打开的文件的静态方法。
 */
public class FileCommand {

    private static  FileChooser fileChooser;
    private static final  Text text =  new Text();

    /**
     * Show a dialog and get a File to save
     * 显示对话框并获取要保存的文件
     * @param table - Whether or not the file dialog is to save a table (csv).
     * @return File - The selected File object.
     */
    public static File getFileToSave(boolean table) {
        fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(LanguageResource.getLanguageBinding("FileCommand_Save_File"));
        //fileChooser.setTitle(I18n.getString("FileCommand_Save_File"));

        text.textProperty().bind(LanguageResource.getLanguageBinding("FileCommand_File"));

        if(table) {
            fileChooser.getExtensionFilters().addAll(

                    new FileChooser.ExtensionFilter("CSV "+ text.getText(), "*.csv")

            );
        } else {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("TXT "+text.getText(), "*.txt")
            );
        }
        return fileChooser.showSaveDialog(null);
    }

    /**
     * Shows a dialog and gets one or more files to open
     * 显示对话框并获取一个或多个要打开的文件
     * @return List - A list of files selected
     */
    public static List<File> getFilesToOpen() {
        fileChooser = new FileChooser();
        fileChooser.titleProperty().bind(LanguageResource.getLanguageBinding("FileCommand_Open_File"));

        text.textProperty().bind(LanguageResource.getLanguageBinding("FileCommand_File"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT "+text.getText(), "*.txt")
        );
        return fileChooser.showOpenMultipleDialog(null);
    }
}
