package com.ztoncloud.jproxytools.functional.proxypanel.gui.settings;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.DANGER;
import static atlantafx.base.theme.Styles.SMALL;
import static atlantafx.base.theme.Styles.STATE_SUCCESS;
import static atlantafx.base.theme.Styles.WARNING;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.layout.Priority.ALWAYS;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import com.ztoncloud.jproxytools.Utils.Containers;
import com.ztoncloud.jproxytools.Utils.IpaddressUtils;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.FilterRule;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.IPFilterModel;
import com.ztoncloud.jproxytools.functional.proxypanel.ServerContext;
import inet.ipaddr.AddressStringException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全设置 Tab
 *
 * @Author yugang
 * @create 2023/4/9 5:41
 */
public class securitySetting extends Tab {

  private static final Logger logger = LoggerFactory.getLogger(securitySetting.class);
  private static final int CONTROL_WIDTH = 100;
  private final ServerContext serverContext = ServerContext.getInstance();
  private final HBox infoBox = new HBox();
  private final Label infoLabel = new Label();
  private final List<IPFilterModel> dataList = serverContext.getIpFilterList();
  private final Label warnLabel = new Label();
  private Button addBtn ;
  private ListView<IPFilterModel> lv ;

  securitySetting() {
    super();
    createView();

  }

  private void createView() {

    var securityVBox = new VBox();
    lv = new ListView<>();
    lv.setDisable(!serverContext.isEnableIpFilter());

    var lv = nestedControlsList();
    var scrollPane = new ScrollPane(lv);
    //var scrollPane = new ScrollPane(test_ListView());
    Containers.setScrollConstraints(scrollPane, AS_NEEDED, true, AS_NEEDED, true);
    scrollPane.setMaxHeight(4000);
    VBox.setVgrow(scrollPane, ALWAYS);

    //设置带颜色背景的info提示栏
    ProxySettingsDialog.setColorIcon(infoBox, infoLabel, false);
    infoBox.setAlignment(Pos.CENTER_LEFT);

    addBtn = new Button("新建规则", new FontIcon(Material2MZ.PLUS));

    addBtn.setDisable(!serverContext.isEnableIpFilter());
    addBtn.getStyleClass().add(Styles.ACCENT);
    addBtn.setOnAction(e -> {
      //ServerContext.saveConfig();//保存ServerContext到磁盘
      var model = new IPFilterModel();
      model.setIp("null");
      model.setRule(FilterRule.ACCEPT);
      lv.getItems().add(model);
      dataList.add(model);

    });

    var saveBtn = new Button("保存",new FontIcon(Material2MZ.SAVE));
    saveBtn.setOnAction(e -> {
      //ServerContext.saveConfig();//保存ServerContext到磁盘
      if (validateIP()) {
        warnLabel.setText("");
        warnLabel.setGraphic(null);
        serverContext.setIpFilterList(dataList);
        logger.debug("保存至： " + dataList);
        //将serverContext保存到磁盘
        ServerContext.saveConfig();
      }


    });
    HBox toolsBox = new HBox();
    toolsBox.getChildren().addAll(
        warnLabel,
        new Spacer(10),
        addBtn,
        new Spacer(10),
        saveBtn
    );
    toolsBox.setAlignment(Pos.CENTER_RIGHT);

    securityVBox.setId("theme-repo-manager");
    securityVBox.getChildren().addAll(
        sampleLabel(),
        scrollPane,
        toolsBox
        //infoBox
    );

    //textProperty().bind(LanguageResource.getLanguageBinding("WindowDialog_Title"));
    setText("安全设置");
    //tab.setGraphic(new FontIcon(randomIcon()));
    setContent(securityVBox);

  }


  private ListView<IPFilterModel> nestedControlsList() {
    lv.setCellFactory(l -> new NestedControlsListCell(lv, dataList));
    //lv.setEditable(true);
    lv.getItems().setAll(dataList.stream().limit(10).collect(Collectors.toList()));
    //占位符 当listview没有数据时显示占位符
    lv.setPlaceholder(new Label("没有数据"));
    return lv;
  }

  private HBox sampleLabel() {

    var disableToggle = new ToggleSwitch();
    var enableIpFilter = serverContext.isEnableIpFilter();
    disableToggle.setSelected(enableIpFilter);
    //将选择状态绑定到文本。
    disableToggle.textProperty().bind(Bindings.createStringBinding(()->
      disableToggle.isSelected() ? "允许IP过滤" : "禁用IP过滤",disableToggle.selectedProperty()
    ));

    disableToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {



      if (newValue) {
        lv.setDisable(false);
        addBtn.setDisable(false);
      } else {
        addBtn.setDisable(true);
        lv.setDisable(true);
      }
      //保存到Context
      serverContext.setEnableIpFilter(newValue);
    });

    var label = new Label();
    label.setText("IP/CIDR格式; 如： 127.0.0.1 ; 192.168.5.0/24 ; 2001:db8:abcd:0012::/64");
    label.getStyleClass().add(ACCENT);
    var hBox = new HBox();
    hBox.getChildren().addAll(
        label,
        new Spacer(),
        disableToggle
    );
    return hBox;

  }

  /**
   * 嵌套控件列表，listview嵌套其他控件，貌似ComboBox<String>会失去边框。
   *
   * @author yugang
   * @date 2023/04/11
   */
  private static class NestedControlsListCell extends ListCell<IPFilterModel> {

    private final HBox root;
    private final TextField titleLabel;
    private final ComboBox<String> ruleComboBox;


    public NestedControlsListCell(ListView<IPFilterModel> lv, List<IPFilterModel> dataList) {
      titleLabel = new TextField();
      titleLabel.setEditable(false);
      titleLabel.setMinWidth(CONTROL_WIDTH*2);

      ruleComboBox = new ComboBox<>(observableArrayList(FilterRule.toList()));
      ruleComboBox.setPrefWidth(CONTROL_WIDTH);
      ruleComboBox.pseudoClassStateChanged(STATE_SUCCESS, true);
      ruleComboBox.getSelectionModel().selectFirst();
      //禁用而不使选择框变灰。
      ruleComboBox.setDisable(true);
      //ruleComboBox.setStyle("-fx-opacity:1;");

      var saveBtn = new Button("编辑");
      saveBtn.setGraphic(new FontIcon(Feather.EDIT));
      saveBtn.getStyleClass().addAll(SMALL);
      saveBtn.setOnAction(event -> {
        //使titleLabel可以编辑，并加入监听
        titleLabel.setEditable(true);
        titleLabel.textProperty().addListener((observable, oldValue, newValue) -> {
          logger.debug("textField值改变监听： " + newValue);
          //dataList同步新值
          dataList.get(getIndex()).setIp(newValue);
        });
        //使ruleComboBox可以编辑，并加入监听
        ruleComboBox.setDisable(false);
        ruleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
          logger.debug("值改变监听： " + newValue);
          //dataList同步新值
          dataList.get(getIndex()).setRule(FilterRule.valueOf(newValue));
        });

      });

      var deleteBtn = new Button("删除");
      deleteBtn.setGraphic(new FontIcon(Material2OutlinedAL.DELETE));
      deleteBtn.getStyleClass().addAll(DANGER, SMALL);
      deleteBtn.setOnAction(event -> {
        logger.debug("titleLabel值： " + titleLabel.getText());
        getItem().setIp(titleLabel.getText());
        getItem().setRule(FilterRule.valueOf(ruleComboBox.getValue()));
        logger.debug("id " + itemProperty().get() + " item: " + getItem());
        lv.getItems().remove(getIndex());
        dataList.remove(getIndex());


      });

      root = new HBox(5,
          titleLabel,

          new Spacer(),
          ruleComboBox,
          new Spacer(20),
          saveBtn,
          deleteBtn
      );
      root.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    public void updateItem(IPFilterModel filterModel, boolean empty) {
      super.updateItem(filterModel, empty);

      if (filterModel == null || empty) {
        titleLabel.textProperty().set(null);
        ruleComboBox.setValue(null);
        setGraphic(null);
        return;
      }
      //对模型的ip和rule进行判空。
      var ip = filterModel.getIp();
      var rule = filterModel.getRule().name();
      if (ip.isBlank()) {
        titleLabel.textProperty().set("null");
      } else {
        titleLabel.textProperty().set(filterModel.getIp());
      }
      if (rule.isBlank()) {
        ruleComboBox.setValue(FilterRule.ACCEPT.name());
      } else {
        ruleComboBox.setValue(filterModel.getRule().name());
      }
      setGraphic(root);

    }
  }


  /**
   * 验证ip格式
   *
   * @return boolean
   */
  private boolean validateIP() {

    if (dataList != null && dataList.size() > 0) {
      for (int i = 0; i < dataList.size(); i++) {
        var ip = dataList.get(i).getIp();
        logger.debug("ip: {} id: {}", ip, i);
        try {
          IpaddressUtils.IPAddressStringValidate(ip);
        } catch (AddressStringException e) {
          warnLabel.setGraphic(new FontIcon(Material2MZ.WARNING));
          warnLabel.getStyleClass().addAll(WARNING);
          warnLabel.setText("输入规则[ " + ip + " ]校验失败，请检查IP格式！ ");
          logger.error("ip校验失败！ " + ip);
          return false;
        }
      }
    }
    return true;
  }

}
