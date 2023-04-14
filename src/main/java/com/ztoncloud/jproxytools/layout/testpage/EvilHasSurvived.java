package com.ztoncloud.jproxytools.layout.testpage;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.time.LocalDate;

/**
 * @author dmitrynelepov
 * Modified by Jewelsea
 */
public class EvilHasSurvived extends Application {

  static class TestClassForListView {
    public String fieldString;
    public LocalDate fieldDate;

    @Override
    public String toString() {
      return "TestClassForListView{" +
          "fieldString='" + fieldString + '\'' +
          ", fieldDate=" + fieldDate +
          '}';
    }
  }

  static class MyListCell extends ListCell<Field> {
    private TextField textField;
    private DatePicker datePicker;
    private Object editedObject;
    private ChangeListener<Boolean> editCommitHandler;

    public MyListCell(Object editedObject) {
      this.editedObject = editedObject;
      setContentDisplay(ContentDisplay.RIGHT);
    }

    @Override
    protected void updateItem(Field t, boolean bln) {
      super.updateItem(t, bln);

      if (datePicker != null) {
        datePicker.focusedProperty().removeListener(editCommitHandler);
      }
      if (textField != null) {
        textField.focusedProperty().removeListener(editCommitHandler);
      }

      if (t == null) {
        setText(null);
        setGraphic(null);
        return;
      }

      if (t.getType().equals(String.class)) {
        if (textField == null) {
          textField = new TextField();
        }

        editCommitHandler = (observable, oldValue, newValue) -> {
          try {
            t.set(editedObject, textField.getText());
            System.out.println(editedObject + " for " + textField + " value " + textField.getText());
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        };
        textField.focusedProperty().addListener(editCommitHandler);

        setText(t.getName());
        setGraphic(textField);
      } else if (t.getType().equals(LocalDate.class)) {
        if (datePicker == null) {
          datePicker = new DatePicker();
        }

        editCommitHandler = (observable, oldValue, newValue) -> {
          try {
            t.set(editedObject, datePicker.getValue());
            System.out.println(editedObject + " for " + datePicker + " value " + datePicker.getValue());
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        };
        datePicker.focusedProperty().addListener(editCommitHandler);

        setText(t.getName());
        setGraphic(datePicker);
      }
    }
  }

  @Override
  public void start(Stage stage) throws Exception {
    ListView<Field> listView = new ListView<>();
    listView.setItems(
        FXCollections.observableArrayList(
            TestClassForListView.class.getFields()
        )
    );
    TestClassForListView testObject = new TestClassForListView();
    listView.setCellFactory(p -> new MyListCell(testObject));

    stage.setScene(new Scene(listView));
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}