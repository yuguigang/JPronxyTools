package com.ztoncloud.jproxytools.layout.testpage;

import static javafx.collections.FXCollections.observableArrayList;

import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.FilterRule;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @Author yugang
 * @create 2023/4/12 0:10
 */
public class test_ListView extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    ObservableList<Model> myModels = FXCollections.observableArrayList(model -> new Observable[]{model.nameProperty()});

    ListView<Model> modelListView = new ListView<>();
    modelListView.setCellFactory(c -> {
      MyListCell cell = new MyListCell();
      //TODO is there a way to replace this listener with something less verbose?
      cell.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue!=null){
          cell.getItem().setName(newValue);
        }
      });

      return cell;
    });
    modelListView.setItems(myModels);
    myModels.add(new Model());

    Button addButton = new Button("Add");
    addButton.setOnAction(event -> myModels.add(new Model()));

    StackPane root = new StackPane();
    root.getChildren().addAll(new HBox(20, modelListView, addButton));
    primaryStage.setTitle("ListView with bidirectional binding");
    primaryStage.setScene(new Scene(root, 600, 500));
    primaryStage.show();
  }

  private class Model {
    private StringProperty name = new SimpleStringProperty(this, "name", "");
    private ReadOnlyStringWrapper computedName = new ReadOnlyStringWrapper(this, "computedName");

    public Model() {
      computedName.bind(Bindings.createStringBinding(() -> name.get().toUpperCase(), name));
    }

    public String getName() {
      return name.get();
    }

    public StringProperty nameProperty() {
      return name;
    }

    public void setName(String name) {
      this.name.set(name);
    }

    public String getComputedName() {
      return computedName.get();
    }

    public ReadOnlyStringWrapper computedNameProperty() {
      return computedName;
    }


  }

  private class MyListCell extends ListCell<Model> {
    private HBox content = new HBox(10);
    private TextField textField = new TextField();
    private Label label = new Label();

    public MyListCell() {
      content.getChildren().addAll(textField, label);
    }

    @Override
    protected void updateItem(Model item, boolean empty) {
      super.updateItem(item, empty);
      if (item == null || empty) {
        setGraphic(null);
      } else {
        setGraphic(content);
        textField.textProperty().set(item.getName());
        label.textProperty().set(item.getComputedName());
      }
    }

    public TextField getTextField() {
      return textField;
    }

  }
  public static void main(String[] args) {
    launch(args);
  }
}
