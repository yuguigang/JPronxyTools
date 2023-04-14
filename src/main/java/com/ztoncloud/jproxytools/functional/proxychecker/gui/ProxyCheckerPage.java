
package com.ztoncloud.jproxytools.functional.proxychecker.gui;

import atlantafx.base.controls.Spacer;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.layout.page.AbstractPage;
import com.ztoncloud.jproxytools.layout.page.SampleBlock;


import com.ztoncloud.jproxytools.functional.proxychecker.commands.ExportCommand;
import com.ztoncloud.jproxytools.functional.proxychecker.commands.LoadCommand;
import com.ztoncloud.jproxytools.functional.proxychecker.commands.ProxyCheckCommand;
import com.ztoncloud.jproxytools.functional.proxychecker.components.ProxyCheckerSettingsProp;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyAnonymity;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyModel;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyStatus;
import com.ztoncloud.jproxytools.theme.CSSFragment;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;


import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;

import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static atlantafx.base.theme.Styles.*;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_HGAP;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_VGAP;

import static com.ztoncloud.jproxytools.functional.proxychecker.commands.Controls.menuItem;


import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

public class ProxyCheckerPage extends AbstractPage {


    public static final ObservableValue<String> NAME = LanguageResource.getLanguageBinding("ProxyChecker_Name");

    @Override
    public ObservableValue<String> getName() { return NAME; }




    //private static final Logger log = LogManager.getLogger(ProxyCheckerPage.class);
    private  static final Logger log = LoggerFactory.getLogger(ProxyCheckerPage.class);
    //加载代理列表视图
    private final ListView<String> view_loaded_proxies = new ListView<>();
    //检验后的代理视图表格。
    private final TableView<ProxyModel> table_proxy = new TableView<>();
    //高匿代理样式
    private static final String style_ELITE= "-color-success-emphasis";
    //高匿代理样式
    private static final String style_ANONYMOUS= "-color-accent-emphasis";
    //高匿代理样式
    private static final String style_TRANSPARENT= "-color-warning-emphasis";


    //private final Button startBtn = new Button(I18n.getString("ProxyChecker_Btn_Start"));

    private ToggleButton barToggle = new ToggleButton();



    private final Label label_loaded_proxies = new Label();
    private final Text loadedText = new Text();

    private final Label label_checked_proxies = new Label();
    private final Text checkedText = new Text();

    private final Label label_working_proxies  = new Label();
    private final Text workingText = new Text();

    private final Label label_ip_address = new Label();


    private final ProgressBar progressBar = createBar();

    private  ProgressFrom progressFrom ;
    //读取互联网地址
    private final String internetIP = ProxyCheckerSettingsProp.getValue("InternetIP","0.0.0.0");

    private Boolean notifyCompleted;
    //侧边栏控件，在点击检查开始后，需要禁用，完成检查后再开启。不让使用者点击其他页面，不然会丢失整个页面。
    private  StackPane sidebarStackPane ;




    public ProxyCheckerPage() {
        super();

        var sample = new SampleBlock(LanguageResource.getLanguageBinding("ProxyChecker_Name"),createPlayground());



        sample.setFillHeight(true);
        setUserContent(sample);
        //设置本机互联网地址
        //setInternetIP();
        init();
    }

    //初始化Label Text,
    private void init() {

        loadedText.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_label_loaded_proxies"));
        label_loaded_proxies.setText(loadedText.getText()+"0");

        workingText.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_label_working_proxies"));
        label_working_proxies.setText(workingText.getText()+"0");

        checkedText.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_label_checked_proxies"));
        label_checked_proxies.setText(checkedText.getText()+"0");
    }

    /**
     * 创建操场
     *
     * @return {@link VBox}
     */
    private VBox createPlayground() {
        //初始化表格
        createTable();

        VBox.setVgrow(table_proxy, Priority.ALWAYS);
        //加入分割面板组件SplitPane()
        var splitPane = new SplitPane();
        //水平
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.28);
        splitPane.getItems().setAll(loadedProxies(), table_proxy);

        // == 头部 ==
        var header = new HBox(
                //头部，文件菜单
                fileMenu(),
                new Spacer(),
                barToggleButton ()
                //头部开始按钮
                //startBtn()
        );
        header.setAlignment(Pos.CENTER_LEFT);

        // 用VBox包裹所有页面控件
        var playground = new VBox(
                BLOCK_VGAP,
                //头部
                header,
                //中部拆分面板，列表+表格
                splitPane,
                //页脚
                createFooter());
        playground.setMinHeight(100);
        return playground;
    }

    /**
     * 创建表格
     */
    @SuppressWarnings("unchecked")
    private void createTable() {
        notifyCompleted = false;
        //internetIP = ProxyCheckerSettingsProp.getValue("InternetIP","0.0.0.0");
        // 设置显示使用者IP
        var ipTip = new Tooltip();
        ipTip.setHideDelay(Duration.seconds(3));//3秒后显示
        ipTip.setWrapText(true);//自动换行

        if (internetIP.equals("0.0.0.0")){
            ipTip.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_label_ip_address_Tip_internetIP"));

        }else {
            ipTip.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_label_ip_address_Tip_noIP"));
            //ipTip.setText(I18n.getString("ProxyChecker_label_ip_address_Tip_noIP")+internetIP);
        }
        label_ip_address.setText(internetIP);
        label_ip_address.setTooltip(ipTip);
        // setup key press event handler 设置按键事件处理程序
        //view_loaded_proxies.setOnKeyPressed(new ProxyCheckerKeyEvent());
        //table.setOnKeyPressed(new ProxyCheckerKeyEvent());
        // 设置表格工厂
        TableColumn<ProxyModel, String> column_ip = new TableColumn<>();
        column_ip.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_ip"));
        column_ip.setMinWidth(130);
        //禁止排序
        column_ip.setSortable(false);
        column_ip.setCellValueFactory(new PropertyValueFactory<>("Ip"));

        TableColumn<ProxyModel, String> column_port = new TableColumn<>();
        column_port.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_Port"));
        column_port.setSortable(false);
        column_port.setCellValueFactory(new PropertyValueFactory<>("Port"));//Port

        TableColumn<ProxyModel, String> column_status = new TableColumn<>();
        column_status.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_Status"));
        column_status.setCellValueFactory(new PropertyValueFactory<>("ProxyStatus"));//ProxyStatus

        TableColumn<ProxyModel, String> column_anonymity = new TableColumn<>();
        column_anonymity.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_Anonymity"));
        column_anonymity.setMinWidth(100);
        column_anonymity.setCellValueFactory(new PropertyValueFactory<>("ProxyAnonymity"));//ProxyAnonymity

        TableColumn<ProxyModel, String> column_country = new TableColumn<>();
        column_country.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_Country"));
        column_country.setMinWidth(120);
        column_country.setCellValueFactory(new PropertyValueFactory<>("Country"));//Country

        TableColumn<ProxyModel, String> column_response_time = new TableColumn<>();
        column_response_time.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_table_ResponseTime"));
        column_response_time.setMinWidth(100);
        column_response_time.setCellValueFactory(new PropertyValueFactory<>("ResponseTime"));//ResponseTime
        //按响应排序
        table_proxy.getSortOrder().add(column_response_time);
        //加入样式切换，实现表格中列，条纹效果。
        toggleStyleClass(table_proxy, STRIPED);
        // 管理工作代理和已检查代理的进度条和计数
        table_proxy.getItems().addListener((ListChangeListener<ProxyModel>) c -> {
            if (!c.getList().isEmpty()) {
                int in =c.getList().size();

                ProxyModel proxyModel = c.getList().get(in - 1); // 最新添加的代理
                //String str = I18n.getString("ProxyChecker_label_checked_proxies") + in;

                //var text = new Text();
                //text.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_label_checked_proxies"));
               // String str = text.getText() + in;
                label_checked_proxies.setText(checkedText.getText()+in);
                if ((in == view_loaded_proxies.getItems().size())) {

                    progressBar.setProgress(1f);
                    progressBar.setDisable(false);
                    //在检测完成后重置开关按钮 barToggle
                    barToggle.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_Btn_Start"));
                    barToggle.setStyle(" -fx-background-color:-color-success-emphasis;");
                    barToggle.setGraphic(new FontIcon(Feather.PLAY));
                    barToggle.setSelected(false);//开启play按钮
                    sidebarStackPane.setDisable(false);//开启侧边栏
                    if (!notifyCompleted) {
                        AlertBox.show(Alert.AlertType.INFORMATION, LanguageResource.getLanguageBinding("ProxyChecker_AlertBox_completed_header"),
                                LanguageResource.getLanguageBinding("ProxyChecker_AlertBox_completed_content")
                                );
                        notifyCompleted = true;
                    }
                } else {
                    progressBar.setProgress((float) c.getList().size() / view_loaded_proxies.getItems().size());
                }
                if (proxyModel.getProxyStatus() == ProxyStatus.ALIVE) {
                    int current_working = Integer.parseInt(label_working_proxies.getText().split(":")[1].trim());
                    label_working_proxies.setText(workingText.getText() + (current_working + 1));//注意：字符串里面的“：”只能使用英文字符
                }
            }
        });

        table_proxy.getColumns().setAll(column_ip,column_port,column_status,column_anonymity,column_country,column_response_time);

        //行工厂，为每行着色。
        table_proxy.setRowFactory(tp -> new TableRow<>() {
            @Override
            protected void updateItem(ProxyModel proxyModel, boolean empty) {
                super.updateItem(proxyModel, empty);
                if ((proxyModel == null) || (proxyModel.getProxyStatus() == ProxyStatus.DEAD)) {
                    setStyle("");
                } else {
                    ProxyAnonymity anonymity = proxyModel.getProxyAnonymity();
                    if (anonymity == ProxyAnonymity.ELITE) {
                        setStyle("-fx-background-color:" + style_ELITE);
                    }
                    if (anonymity == ProxyAnonymity.ANONYMOUS) {
                        setStyle("-fx-background-color:" + style_ANONYMOUS);
                    }
                    if (anonymity == ProxyAnonymity.TRANSPARENT) {
                        setStyle("-fx-background-color:" + style_TRANSPARENT);
                    }
                }
            }
        });
    }

    /**
     * 头部菜单
     * @return FlowPane
     */
    private FlowPane fileMenu() {
       var openFileBtn =  new Button("",new FontIcon(Feather.PLUS));
       openFileBtn.getStyleClass().addAll(BUTTON_ICON,ACCENT);
       openFileBtn.setOnAction(event -> {
           if (!ProxyCheckCommand.isRunning()) {
               LoadCommand.file(view_loaded_proxies, null);
           }
       });
       //设置按钮
       var settingsBtn = new Button("",new FontIcon(Feather.SETTINGS));
       settingsBtn.getStyleClass().addAll(BUTTON_ICON,FLAT);
       settingsBtn.setOnAction(event -> {
           new WindowDialog().show();//设置弹窗
       });
       //颜色示例按钮
       var  sampleColorELITE = colorButton(null,style_ELITE);
       sampleColorELITE.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_Btn_ELITE"));
       sampleColorELITE.setMouseTransparent(true);//禁止点击
       var  sampleColorANONYMOUS = colorButton(null,style_ANONYMOUS);
       sampleColorANONYMOUS.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_Btn_ANONYMOUS"));
       sampleColorANONYMOUS.setMouseTransparent(true);//禁止点击
       var  sampleColorTRANSPARENT = colorButton(null,style_TRANSPARENT);
       sampleColorTRANSPARENT.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_Btn_TRANSPARENT"));
       sampleColorTRANSPARENT.setMouseTransparent(true);//禁止点击

       var content = new FlowPane(BLOCK_HGAP,BLOCK_VGAP);
       content.setMinWidth(750);
       content.getChildren().addAll(
               openFileBtn,//打开文件
               exportBtn(),//导出文件
               settingsBtn,//设置
               sampleColorELITE,//高匿示例
               sampleColorANONYMOUS,//匿名示例
               sampleColorTRANSPARENT//透明示例
       );
       return content;
    }

    /**
     * 切换按钮,开始 or 停止 Start Stop
     */

    private ToggleButton barToggleButton () {

        barToggle = new ToggleButton();
        barToggle.setStyle(" -fx-background-color:-color-success-emphasis;");
        barToggle.setGraphic(new FontIcon(Feather.PLAY));
        barToggle.setMinWidth(100);
        barToggle.textProperty().bind(Bindings.createStringBinding(
                () -> barToggle.isSelected() ? LanguageResource.getLanguageBinding("ProxyChecker_Btn_Stop").getValue() : LanguageResource.getLanguageBinding("ProxyChecker_Btn_Start").getValue(), barToggle.selectedProperty())
        );


        //barToggle.getStyleClass().add(SUCCESS);
        barToggle.setOnAction(event -> {
            if (barToggle.isSelected()) {
                log.info("切换按钮：" + "选择状态");
                if (view_loaded_proxies.getItems().size() > 0) { //确保加载了代理列表再进行检查

                    //选择状态
                    barToggle.setStyle(" -fx-background-color:-color-danger-emphasis;");
                    barToggle.setGraphic(new FontIcon(Feather.STOP_CIRCLE));
                    //var text = new Text();
                    //text.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_label_working_proxies"));
                    label_working_proxies.setText(workingText.getText() + "0"); // reset working proxy count 重置工作代理计数
                    table_proxy.getItems().clear(); // reset table 重置表格
                    //开始检查
                    ProxyCheckCommand.check(view_loaded_proxies, table_proxy);

                    Scene scene = barToggle.getScene();
                    //拿侧边栏控件到ID为sidebar，注意#
                    sidebarStackPane = (StackPane) scene.lookup("#sidebar");
                    //设置样式，让侧边栏不可操作但是不丢失亮度和颜色
                    //stackPane.getStyleClass().add("pane_opacity");
                    //禁止操作
                    sidebarStackPane.setDisable(true);

                } else {
                    AlertBox.show(Alert.AlertType.INFORMATION, LanguageResource.getLanguageBinding("ProxyChecker_AlertBox_not_loaded_header"),
                            LanguageResource.getLanguageBinding("ProxyChecker_AlertBox_not_loaded_content")
                            );
                    barToggle.setSelected(false);

                }
            } else {
                //barToggle.setDisable(true);
                //未选择状态
                barToggle.setGraphic(new FontIcon(Feather.PLAY));
                barToggle.setStyle(" -fx-background-color:-color-success-emphasis;");
                log.info("切换按钮：" + "未选择状态");
                //Stage stage = (Stage) barToggle.getScene().getWindow();
                sidebarStackPane.setDisable(false);//开启侧边栏
                progressFrom = new ProgressFrom();
                progressFrom.ProgressBarShow();
                //开启一个新的线程去关闭，不然会卡UI
                new Thread(() ->
                        ProxyCheckCommand.stop(progressFrom)
                ).start();
            }
        });
        return barToggle;
    }
    /**
     * 加载代理的列表框
     */
    private ListView<String> loadedProxies(){
        view_loaded_proxies.setMinWidth(200);


        // 允许将文件拖放到加载的代理视图中
        view_loaded_proxies.setOnDragOver(event -> {
            if (!ProxyCheckCommand.isRunning()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            } else {
                event.acceptTransferModes(TransferMode.NONE);
            }
        });

        // 将丢弃的文件路由到正确的load命令
        view_loaded_proxies.setOnDragDropped(event -> {
            if (event.getDragboard().hasFiles()) {
                LoadCommand.file(view_loaded_proxies, event.getDragboard().getFiles());
            }
        });

        // 更新加载的代理计数
        view_loaded_proxies.getItems().addListener((ListChangeListener<String>) c ->

                label_loaded_proxies.setText(loadedText.getText() + view_loaded_proxies.getItems().size()));
        return view_loaded_proxies;
    }

    /**
     * 创建页脚
     *
     * @return {@link HBox}
     */
    private HBox createFooter() {
        var footer = new HBox(
                BLOCK_HGAP,
                label_loaded_proxies,
                new Label("|"),
                label_checked_proxies,
                new Label("|"),
                label_working_proxies,
                new Spacer(),
                label_ip_address,
                internetIPTip(),
                progressBar

        );
        footer.setAlignment(Pos.CENTER_LEFT);
        return footer;

    }
    /**
     * 转换颜色为颜色按钮
     * @param colorString 样式字符串
     * @return btn
     */
    private Button colorButton(String title , String colorString) {

        var iconBtn = new Button(title, new FontIcon(Material2MZ.STOP));
        iconBtn.getStyleClass().addAll("favorite-button",  FLAT);
        String sb = " .favorite-button.button >.ikonli-font-icon {                \n" +
                "-fx-icon-color:" +
                colorString +
                """
                        ;
                        -fx-font-size:  22px;
                        -fx-icon-size:  22px;
                        }""";
        new CSSFragment(sb).addTo(iconBtn);
        return iconBtn;
    }
    private ProgressBar createBar(String... styleClasses) {
        var bar = new ProgressBar(0);
        bar.getStyleClass().addAll(styleClasses);
        bar.setDisable(false);
        return bar;
    }

    /**
     *
     * 导出按钮
     *
     * @return {@link MenuButton}
     */
    private MenuButton exportBtn() {
        //导出所有代理
        var exAllProxies = menuItem(null,new KeyCodeCombination(KeyCode.A, CONTROL_DOWN));
        exAllProxies.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_ExportBtn_AllProxies"));
        exAllProxies.setMnemonicParsing(true);
        exAllProxies.setOnAction(event -> ExportCommand.save(view_loaded_proxies));
        //导出所有可用代理
        var exAllAliveProxies = menuItem(null,new KeyCodeCombination(KeyCode.A, CONTROL_DOWN));
        exAllAliveProxies.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_ExportBtn_AllAliveProxies"));
        exAllAliveProxies.setMnemonicParsing(true);
        exAllAliveProxies.setOnAction(event -> ExportCommand.save(table_proxy, ProxyStatus.ALIVE, null));
        //导出所有可用的高匿名代理
        var exAliveEliteProxies = menuItem(null,new KeyCodeCombination(KeyCode.E, CONTROL_DOWN));
        exAliveEliteProxies.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_ExportBtn_EliteProxies"));
        exAliveEliteProxies.setMnemonicParsing(true);
        exAliveEliteProxies.setOnAction(event -> ExportCommand.save(table_proxy, ProxyStatus.ALIVE, ProxyAnonymity.ELITE));
        //导出所有可用的匿名代理
        var exAliveAnonymousProxies = menuItem(null,new KeyCodeCombination(KeyCode.A, CONTROL_DOWN));
        exAliveAnonymousProxies.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_ExportBtn_AnonymousProxies"));
        exAliveAnonymousProxies.setMnemonicParsing(true);
        exAliveAnonymousProxies.setOnAction(event -> ExportCommand.save(table_proxy, ProxyStatus.ALIVE, ProxyAnonymity.ANONYMOUS));
        //导出所有可用的透明代理
        var exAliveTransparent = menuItem(null,new KeyCodeCombination(KeyCode.T, CONTROL_DOWN));
        exAliveTransparent.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_ExportBtn_TransparentProxies"));
        exAliveTransparent.setMnemonicParsing(true);
        exAliveTransparent.setOnAction(event -> ExportCommand.save(table_proxy, ProxyStatus.ALIVE, ProxyAnonymity.TRANSPARENT));
        //可用代理
        var AliveProxies = new Menu();
        AliveProxies.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_ExportBtn_AliveProxies"));
        AliveProxies.setMnemonicParsing(true);
        AliveProxies.getItems().addAll(
                //IntStream.range(0, 10).mapToObj(x -> new MenuItem(faker.file().fileName())).toList()
                exAllAliveProxies,
                new SeparatorMenuItem(),
                exAliveEliteProxies,
                exAliveAnonymousProxies,
                exAliveTransparent
        );
        //不可用代理
        var exDeadProxies = menuItem(null,null,new KeyCodeCombination(KeyCode.D, CONTROL_DOWN));
        exDeadProxies.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_ExportBtn_DeadProxies"));
        exDeadProxies.setMnemonicParsing(true);
        exDeadProxies.setOnAction(event -> ExportCommand.save(table_proxy, ProxyStatus.DEAD, null));
       //导出为表格
        var exLabel = menuItem(null,null,new KeyCodeCombination(KeyCode.C, CONTROL_DOWN));
        exLabel.textProperty().bind(LanguageResource.getLanguageBinding("ProxyChecker_ExportBtn_exLabel"));
        exLabel.setMnemonicParsing(true);
        exLabel.setOnAction(event -> ExportCommand.save(table_proxy));
        //导出按钮
        var exportBtn = new MenuButton();
        exportBtn.getItems().addAll(
                exAllProxies,
                new SeparatorMenuItem(),
                AliveProxies,
                exDeadProxies,
                new SeparatorMenuItem(),
                exLabel

        );
        exportBtn.setGraphic(new FontIcon(Feather.EXTERNAL_LINK));
        exportBtn.getStyleClass().addAll(BUTTON_ICON,WARNING);
        return exportBtn;

    }

    /**
     * 本机互联网ip地址没有读取到时，显示不同图标。
     */
    private Button internetIPTip(){

        if (!internetIP.equals("0.0.0.0")) {
            var successBtn = new Button("", new FontIcon(Feather.CHECK_CIRCLE));
            successBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT, SUCCESS);
            successBtn.setMouseTransparent(true);
            return successBtn;
        }else {
            var noIPBtn = new Button("", new FontIcon(Feather.ALERT_CIRCLE));
            noIPBtn.getStyleClass().addAll(BUTTON_CIRCLE, FLAT, DANGER);
            noIPBtn.setMouseTransparent(true);
            return noIPBtn;
        }
    }



}
