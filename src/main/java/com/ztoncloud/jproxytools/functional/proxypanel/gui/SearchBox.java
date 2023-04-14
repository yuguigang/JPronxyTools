package com.ztoncloud.jproxytools.functional.proxypanel.gui;

import static javafx.collections.FXCollections.observableArrayList;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import com.ztoncloud.jproxytools.exception.DefaultExceptionHandler;
import com.ztoncloud.jproxytools.functional.proxychecker.commands.ProxyCheckCommand;
import com.ztoncloud.jproxytools.functional.proxychecker.gui.AlertBox;
import com.ztoncloud.jproxytools.functional.proxychecker.gui.ProgressFrom;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.JServer;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.RunServer;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.layout.MainStage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yugang
 * @create 2022/11/19 1:39
 */
public class SearchBox extends HBox {

  //Logger log = LogManager.getLogger(SearchBox.class);
  private static final Logger log = LoggerFactory.getLogger(SearchBox.class);
  private static final int CARD_WIDTH = 150;
  private static final int PREF_WIDTH = 100;
  private static final int PREF_HGAP = 20;
  private static final int PREF_VGAP = 10;
  private static final int PREF_SPACER = 10;
  private final StringProperty searchText = new SimpleStringProperty();
  private List<String> country = new ArrayList<>();


  public SearchBox() {
    createView();

  }

  private void createView() {
    country.add("JP");
    country.add("KR");
    country.add("US");
    country.add("TW");
    country.add("CN");
    //new CSSFragment(Card.CSS).addTo(this);
    HBox hBox = new HBox(
        createCountryComboBox(),
        createStateComboBox(),
        createZipComboBox()
        //new Button("搜索。"),
        //new Spacer(),
        //new Button("开始。。")

    );

    HBox.setMargin(hBox, new Insets(PREF_SPACER));
    getChildren().setAll(
/*
               hBox,
                new Spacer(),
                createSearchBtn(),
            createStartBtn()

 */
        createCountryComboBox(),
        new Spacer(PREF_SPACER),
        createStateComboBox(),
        new Spacer(PREF_SPACER),
        createZipComboBox(),
        new Spacer(PREF_SPACER),
        createSearchBtn(),
        new Spacer(),
        barToggleButton()

    );
    setPadding(new Insets(PREF_SPACER));

    getStyleClass().addAll("card", Styles.BORDERED);
    setId("proxy-search");
  }


  /**
   * 创建国家组合框
   *
   * @return {@link HBox}
   */
  private HBox createCountryComboBox() {
    ComboBox<String> countryComboBox = createComboBoxWith(country);

    countryComboBox.getSelectionModel().selectedItemProperty()
        .addListener(new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue,
              String newValue) {
            log.info("选中值（国家/地区）： " + newValue);
          }
        });
    HBox searchCountryBox = new HBox(PREF_SPACER, new Label("国家/地区:"), countryComboBox);

    searchCountryBox.setMinWidth(CARD_WIDTH);
    searchCountryBox.setAlignment(Pos.CENTER);

    return searchCountryBox;
  }

  /**
   * 创建（地/市/州）组合框
   *
   * @return {@link HBox}
   */
  private HBox createStateComboBox() {

    ComboBox<String> stateComboBox = createComboBoxWith(country);

    stateComboBox.getSelectionModel().selectedItemProperty()
        .addListener(new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue,
              String newValue) {
            log.info("选中值（地/市/州）： " + newValue);
          }
        });
    HBox searchStateBox = new HBox(PREF_SPACER, new Label("地/市/州:"), stateComboBox);

    searchStateBox.setMinWidth(CARD_WIDTH);
    searchStateBox.setAlignment(Pos.CENTER);

    return searchStateBox;
  }

  /**
   * 创建（ZIP）组合框
   *
   * @return {@link HBox}
   */
  private HBox createZipComboBox() {

    ComboBox<String> zipComboBox = createComboBoxWith(country);

    zipComboBox.getSelectionModel().selectedItemProperty()
        .addListener(new ChangeListener<String>() {
          @Override
          public void changed(ObservableValue<? extends String> observable, String oldValue,
              String newValue) {
            log.info("选中值（ZIP）： " + newValue);
          }
        });
    HBox searchZipBox = new HBox(PREF_SPACER, new Label("ZIP:"), zipComboBox);

    searchZipBox.setMinWidth(CARD_WIDTH);
    searchZipBox.setAlignment(Pos.CENTER);

    return searchZipBox;
  }

  private ObservableList<String> createItems() {
    return observableArrayList(country);
  }

  private Label createLabel(String text) {
    var label = new Label(text);
    GridPane.setHalignment(label, HPos.CENTER);
    return label;
  }


  /**
   * 创建组合框
   *
   * @param srcDatalist src datalist 选择数据源
   * @return {@link ComboBox}<{@link String}>
   */
  private ComboBox<String> createComboBoxWith(List<String> srcDatalist) {
    var c = new ComboBox<String>();
    c.setPrefWidth(PREF_WIDTH);
    c.setItems(observableArrayList(srcDatalist));
    c.getSelectionModel().selectFirst();//默认选项
    c.setVisibleRowCount(4);
    new AutoCompleteComboBoxListener<>(c);
    return c;
  }

  /**
   * 得到搜索关键字
   *
   * @param srcText        src文本
   * @param filter         过滤器
   * @param searchKeywords 搜索关键字
   * @return boolean
   */
  public boolean getSearchKeywords(String srcText, String filter, List<String> searchKeywords) {
    //private final List<String> searchKeywords = new ArrayList<>();
    Objects.requireNonNull(filter);

    //筛选出以字母”z“开头的数据，并输出
    searchKeywords = searchKeywords.stream()
        .filter(item -> item.contains(filter))
        .collect(Collectors.toList());
    searchKeywords.forEach(item ->
        System.out.println(item));

    //转换成大写字母
    return contains(srcText, filter)
        || searchKeywords.stream().anyMatch(keyword -> contains(keyword, filter));

  }

  public List<String> getSearchKeywords1(String filter, List<String> searchKeywords) {
    //private final List<String> searchKeywords = new ArrayList<>();
    Objects.requireNonNull(filter);
    String filtertoUpperCase = filter.toUpperCase(Locale.ROOT);
    //筛选出以字母”z“开头的数据，并输出
    searchKeywords = searchKeywords.stream()
        .filter(item -> item.contains(filtertoUpperCase))
        .collect(Collectors.toList());

    return new ArrayList<>(searchKeywords);

  }

  /**
   * 是否包含
   *
   * @param text   文本
   * @param filter 过滤器
   * @return boolean
   */
  private boolean contains(String text, String filter) {
    //转换成大写字母比较
    return text.toUpperCase(Locale.ROOT).contains(filter.toUpperCase(Locale.ROOT));
  }

  private Button createSearchBtn() {

    var searchBtn = new Button("搜索", new FontIcon(Feather.SEARCH));
    searchBtn.setDefaultButton(true);

    return searchBtn;
  }

  private Button createStartBtn() {

    var startBtn = new Button("开始", new FontIcon(Feather.PLAY));
    startBtn.setDefaultButton(true);
    return startBtn;
  }

  /**
   * 切换按钮,开始 or 停止 Start Stop
   */

  private ToggleButton barToggleButton() {

    var barToggle = new ToggleButton();
    barToggle.setStyle(" -fx-background-color:-color-success-emphasis;");
    barToggle.setGraphic(new FontIcon(Feather.PLAY));
    barToggle.setMinWidth(100);
    barToggle.textProperty().bind(Bindings.createStringBinding(
        () -> barToggle.isSelected() ? LanguageResource.getLanguageBinding("ProxyChecker_Btn_Stop")
            .getValue() : LanguageResource.getLanguageBinding("ProxyChecker_Btn_Start").getValue(),
        barToggle.selectedProperty())
    );

    //barToggle.getStyleClass().add(SUCCESS);
    barToggle.setOnAction(event -> {
      if (barToggle.isSelected()) {
        log.debug("切换按钮barToggle：" + "选择状态： " + barToggle.isSelected());
        //这里必须开启一个新线程去启动服务器才不会影响UI主线程
        new Thread(() -> {
          try {
            RunServer.startRun();
          } catch (Exception e) {
            /* TODO 启动异常后弹窗提示 */
            //new DefaultExceptionHandler(MainStage.getMainStage());
            //e.printStackTrace();
          }
        }
        ).start();

      } else {
        //barToggle.setDisable(true);
        //未选择状态
        log.debug("切换按钮barToggle：" + "选择状态： " + barToggle.isSelected());
        RunServer.stopServer();

        //new Thread(() ->
        //ProxyCheckCommand.stop(progressFrom)
        //).start();
      }
    });
    return barToggle;
  }

}
