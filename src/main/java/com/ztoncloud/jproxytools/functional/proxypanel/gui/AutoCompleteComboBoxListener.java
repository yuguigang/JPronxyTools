package com.ztoncloud.jproxytools.functional.proxypanel.gui;

import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 下拉搜索框
 * @param <T>
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private final ComboBox<T> comboBox;
    private final StringBuilder sb;
    private final ObservableList<T> data;
    private boolean moveCaretToPos = false;
    private int caretPos;

    public AutoCompleteComboBoxListener(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        sb = new StringBuilder();
        data = comboBox.getItems();

        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                comboBox.hide();
            }
        });


        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
    }

    @Override
    public void handle(KeyEvent event) {

        if(event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.DOWN) {
            if(!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;
        } else if(event.getCode() == KeyCode.BACK_SPACE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        } else if(event.getCode() == KeyCode.DELETE) {
            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }

        if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                || event.isControlDown() || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
            return;
        }

        ObservableList<T> list = FXCollections.observableArrayList();
        for (T datum : data) {
            if (datum.toString().toUpperCase(Locale.ROOT).contains(
                    AutoCompleteComboBoxListener.this.comboBox
                            .getEditor().getText().toUpperCase(Locale.ROOT))) {
                list.add(datum);
            }
        }
        String t = comboBox.getEditor().getText();//回显字符串

        comboBox.setItems(list);

        comboBox.getEditor().setText(t);

        if(!moveCaretToPos) {
            caretPos = -1;
        }
        moveCaret(t.length());
        if(!list.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        if(caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }

}
