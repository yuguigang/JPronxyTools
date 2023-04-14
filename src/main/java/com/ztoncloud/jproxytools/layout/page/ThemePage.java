
package com.ztoncloud.jproxytools.layout.page;

import static atlantafx.base.theme.Styles.LEFT_PILL;
import static atlantafx.base.theme.Styles.RIGHT_PILL;
import static atlantafx.base.theme.Styles.TEXT;
import static atlantafx.base.theme.Styles.WARNING;
import static com.ztoncloud.jproxytools.event.ThemeEvent.EventType.COLOR_CHANGE;
import static com.ztoncloud.jproxytools.event.ThemeEvent.EventType.THEME_ADD;
import static com.ztoncloud.jproxytools.event.ThemeEvent.EventType.THEME_CHANGE;
import static com.ztoncloud.jproxytools.event.ThemeEvent.EventType.THEME_REMOVE;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_HGAP;
import static com.ztoncloud.jproxytools.layout.page.SampleBlock.BLOCK_VGAP;
import static com.ztoncloud.jproxytools.theme.ThemeManager.DEFAULT_FONT_FAMILY_NAME;
import static com.ztoncloud.jproxytools.theme.ThemeManager.SUPPORTED_FONT_SIZE;

import atlantafx.base.theme.Styles;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.ztoncloud.jproxytools.Env.Language;
import com.ztoncloud.jproxytools.Utils.io.JacksonMappers;
import com.ztoncloud.jproxytools.config.PreferencesBean;
import com.ztoncloud.jproxytools.config.SettingsConfig;
import com.ztoncloud.jproxytools.event.DefaultEventBus;
import com.ztoncloud.jproxytools.event.ThemeEvent;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.layout.MainStage;
import com.ztoncloud.jproxytools.theme.SamplerTheme;
import com.ztoncloud.jproxytools.theme.ThemeManager;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThemePage extends AbstractPage {

    private static final Logger log = LoggerFactory.getLogger(ThemePage.class.toString());
    //选择框宽度
    private static final int CONTROL_WIDTH = 250;
    //默认字体
    private static final String DEFAULT_FONT_ID = "Default";
    //按钮宽度
    private static final int BUTTON_WIDTH = 120;
    //页面名字
    public static final ObservableValue<String> NAME = LanguageResource.getLanguageBinding("Sidebar_Theme");
    //主题管理区
    private static final ThemeManager TM = ThemeManager.getInstance();
    //

    private final PreferencesBean preferences ;
    private final YAMLMapper yamlMapper = JacksonMappers.createYamlMapper();
    //主题选择框
    private final ChoiceBox<SamplerTheme> themeSelector = createThemeSelector();

    private ThemeRepoManagerDialog themeRepoManagerDialog;

    @Override
    public ObservableValue<String> getName() {
        return NAME;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return false;
    }

    @Override
    public boolean canChangeThemeSettings() {
        return false;
    }

    public ThemePage() {
        super();

        //读取原有的设置
        var preferencesConfig = MainStage.getSettingsConfig();
        this.preferences = preferencesConfig.getPreferences();

        createView();

        DefaultEventBus.getInstance().subscribe(ThemeEvent.class, e -> {
            //加入或者移除主题监听
            if (e.getEventType() == THEME_ADD || e.getEventType() == THEME_REMOVE) {
                themeSelector.getItems().setAll(TM.getRepository().getAll());
                selectCurrentTheme();
            }
            //主题或者颜色改变监听
            if (e.getEventType() == THEME_CHANGE || e.getEventType() == COLOR_CHANGE) {
                //colorPalette.updateColorInfo(Duration.seconds(1));
                //colorScale.updateColorInfo(Duration.seconds(1));
            }
        });
    }

    @Override
    protected void onRendered() {

        super.onRendered();
        //colorPalette.updateColorInfo(Duration.ZERO);
        //colorScale.updateColorInfo(Duration.ZERO);
    }

    private void createView() {
        setUserContent(new VBox(

            Page.PAGE_VGAP,
            createLanguageChooser(),
            createOptionsGrid(),
            fontGrid()

            )
        );
        setBottom(btnSaveAndCancel());

        selectCurrentTheme();
    }

    private SampleBlock createOptionsGrid() {
        //自定义主题按钮
        var themeRepoBtn = new Button("", new FontIcon(Material2OutlinedMZ.SETTINGS));
        themeRepoBtn.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
        var toolTip = new Tooltip();
        toolTip.textProperty().bind(LanguageResource.getLanguageBinding("Settings"));
        themeRepoBtn.setTooltip(toolTip);

        themeRepoBtn.setOnAction(e -> {

            ThemeRepoManagerDialog dialog = getOrCreateThemeRepoManagerDialog();//加入自定义主题Dialog

            overlay.setContent(dialog, HPos.CENTER);
            overlay.setHiddenOutside(true);

            dialog.getContent().update();

            overlay.toFront();
        });
        //强调色选择HBox
        var accentSelector = new AccentColorSelector(preferences);

        var grid = new GridPane();
        grid.setHgap(BLOCK_HGAP);
        grid.setVgap(BLOCK_VGAP);



        var Theme_ColorTheme = new Label();
        Theme_ColorTheme.textProperty().bind(LanguageResource.getLanguageBinding("Theme_ColorTheme"));
        grid.add(Theme_ColorTheme, 0, 0);
        grid.add(themeSelector, 1, 0);
        grid.add(themeRepoBtn, 2, 0);

        var Theme_AccentColor = new Label();
        Theme_AccentColor.textProperty().bind(LanguageResource.getLanguageBinding("Theme_AccentColor"));
        grid.add(Theme_AccentColor, 0, 1);
        grid.add(accentSelector, 1, 1);

        return new SampleBlock(LanguageResource.getLanguageBinding("Theme_ThemeTitle"), grid);
    }

    /**
     * 主题选择Box
     *
     * @return
     */
    private ChoiceBox<SamplerTheme> createThemeSelector() {
        var selector = new ChoiceBox<SamplerTheme>();
        selector.getItems().setAll(TM.getRepository().getAll());//加入所有主题以供选择
        selector.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val != null && getScene() != null) {
                log.debug("暂存到UserSettings.Prop ： key:Theme_Name,value:" + val);
                TM.setTheme(val);
                //保存到UserSettings.properties
                //UserSettings.setValue("Theme_Name", val.getName());
                //切换主题同时移除强调色，不然外观和设置不一致。
                //UserSettings.removeValue("Theme_AccentColor");

                preferences.setTheme(val.getName());
                preferences.setAccentColor(null);


            }
        });
        selector.setPrefWidth(CONTROL_WIDTH);

        selector.setConverter(new StringConverter<>() {
            @Override
            public String toString(SamplerTheme theme) {
                return theme != null ? theme.getName() : "";
            }

            @Override
            public SamplerTheme fromString(String themeName) {
                return TM.getRepository().getAll().stream()
                        .filter(t -> Objects.equals(themeName, t.getName()))
                        .findFirst()
                        .orElse(null);
            }
        });

        return selector;
    }

    /**
     * 自定义主题dialog
     */
    private void selectCurrentTheme() {
        if (TM.getTheme() == null) {
            return;
        }
        themeSelector.getItems().stream()
                .filter(t -> Objects.equals(TM.getTheme().getName(), t.getName()))
                .findFirst()
                .ifPresent(t -> themeSelector.getSelectionModel().select(t));
    }

    private ThemeRepoManagerDialog getOrCreateThemeRepoManagerDialog() {
        if (themeRepoManagerDialog == null) {
            themeRepoManagerDialog = new ThemeRepoManagerDialog();


        }

        themeRepoManagerDialog.setOnCloseRequest(() -> {
            overlay.removeContent();
            overlay.toBack();
        });
        return themeRepoManagerDialog;
    }

    /**
     * 保存按钮
     */
    private HBox btnSaveAndCancel() {
        var twoButtonGroup = new ToggleGroup();

        var btnSave = createToggleButton(LanguageResource.getLanguageBinding("Theme_Save"), twoButtonGroup, true, LEFT_PILL);
        btnSave.setPrefWidth(BUTTON_WIDTH + BLOCK_HGAP / 2.0);
        btnSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //加入警告弹窗
                var alert = new Alert(Alert.AlertType.INFORMATION);
                //默认是显示弹窗头的，如果setHeaderText=null则不显示头。
                alert.setHeaderText(null);
                alert.titleProperty().bind(LanguageResource.getLanguageBinding("Theme_BtnSave_Title"));
                alert.contentTextProperty().bind(LanguageResource.getLanguageBinding("Theme_BtnSave_ContentText"));

                var yesBtnText = new Text();
                yesBtnText.textProperty().bind(LanguageResource.getLanguageBinding("BtnAlert_Yes"));
                ButtonType yesBtn = new ButtonType(yesBtnText.getText(), ButtonBar.ButtonData.YES);

                //ButtonType yesBtn = new ButtonType(I18n.getString("BtnAlert_Yes"), ButtonBar.ButtonData.YES);
                var cancelBtnText = new Text();
                cancelBtnText.textProperty().bind(LanguageResource.getLanguageBinding("BtnAlert_Cancel"));
                ButtonType cancelBtn = new ButtonType(cancelBtnText.getText(), ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(yesBtn, cancelBtn);

                alert.initOwner(getScene().getWindow());
                //关闭按钮样式
                //alert.initStyle(StageStyle.UTILITY);
                //将在对话框消失以前不会执行之后的代码;

                Optional<ButtonType> buttonType = alert.showAndWait();
                //buttonType.isPresent();用于判断对象是否存在，然后用get()方法返回对象。这里需配合使用
                if (buttonType.isPresent()&&buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    //保存YAML
                    PreferencesBean.saveConfig(preferences,yamlMapper);
                }


            }
        });
        //重置按钮
        var btnReset = createToggleButton(LanguageResource.getLanguageBinding("Theme_Reset"), twoButtonGroup, false, RIGHT_PILL);
        btnReset.setPrefWidth(BUTTON_WIDTH + BLOCK_HGAP / 2.0);
        btnReset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //加入警告弹窗
                var alert = new Alert(Alert.AlertType.WARNING);
                //默认是显示弹窗头的，如果setHeaderText=null则不显示头。
                alert.setHeaderText(null);
                alert.titleProperty().bind(LanguageResource.getLanguageBinding("Theme_BtnReset_Title"));
                alert.contentTextProperty().bind(LanguageResource.getLanguageBinding("Theme_BtnReset_ContentText"));

                var yesBtnText = new Text();
                yesBtnText.textProperty().bind(LanguageResource.getLanguageBinding("BtnAlert_Yes"));
                ButtonType yesBtn = new ButtonType(yesBtnText.getText(), ButtonBar.ButtonData.YES);

                var cancelBtnText = new Text();
                cancelBtnText.textProperty().bind(LanguageResource.getLanguageBinding("BtnAlert_Cancel"));
                ButtonType cancelBtn = new ButtonType(cancelBtnText.getText(), ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(yesBtn, cancelBtn);

                alert.initOwner(getScene().getWindow());
                //将设置保存到本地磁盘
                Optional<ButtonType> buttonType = alert.showAndWait();
                //buttonType.isPresent();用于判断对象是否存在，然后用get()方法返回对象。这里需配合使用
                if (buttonType.isPresent()&&buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)) {
                    //保存一个全新的首选项
                    PreferencesBean.saveConfig(new PreferencesBean(),yamlMapper);


                }
            }
        });
        var twoButtonBox = new HBox(btnSave, btnReset);
        //位置，下靠右
        twoButtonBox.setAlignment(Pos.CENTER_RIGHT);
        twoButtonBox.setPadding(new Insets(10, 40, 40, 10));
        return twoButtonBox;

    }

    /**
     * 切换按钮
     * @param text
     * @param group
     * @param selected
     * @param styleClasses
     * @return
     */
    private ToggleButton createToggleButton(ObservableValue<String> text,
                                            ToggleGroup group,
                                            boolean selected,
                                            String... styleClasses) {
        var toggleButton = new ToggleButton();
        toggleButton.textProperty().bind(text);
        if (group != null) {
            toggleButton.setToggleGroup(group);
        }
        toggleButton.setSelected(selected);
        toggleButton.getStyleClass().addAll(styleClasses);

        return toggleButton;
    }

    /**
     * 选择字体和字体大小GridPane
     *
     * @return GridPane
     */
    public SampleBlock fontGrid() {

        var controlsGrid = new GridPane();

        controlsGrid.setHgap(BLOCK_HGAP);
        controlsGrid.setVgap(BLOCK_VGAP);
        var Theme_FontFamily = new Label();
        Theme_FontFamily.textProperty().bind(LanguageResource.getLanguageBinding("Theme_FontFamily"));
        controlsGrid.add(Theme_FontFamily, 0, 0);
        controlsGrid.add(createFontFamilyChooser(), 1, 0);

        var Theme_FontSize = new Label();
        Theme_FontSize.textProperty().bind(LanguageResource.getLanguageBinding("Theme_FontSize"));
        controlsGrid.add(Theme_FontSize, 0, 1);
        controlsGrid.add(crateFontSizeSpinner(), 1, 1);

        return new SampleBlock(LanguageResource.getLanguageBinding("Theme_FontTitle"), controlsGrid);
    }

    /**
     * 字体选择框
     *
     * @return ComboBox
     */
    private ComboBox<String> createFontFamilyChooser() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().add(DEFAULT_FONT_ID); // keyword to reset font family to its default value
        comboBox.getItems().addAll(FXCollections.observableArrayList(Font.getFamilies()));
        comboBox.setPrefWidth(CONTROL_WIDTH);
        comboBox.getSelectionModel().select(TM.getFontFamily()); // select active font family value on page load
        comboBox.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                TM.setFontFamily(DEFAULT_FONT_ID.equals(val) ? DEFAULT_FONT_FAMILY_NAME : val);
                preferences.setFontFamily(val);
            }
        });

        return comboBox;
    }

    /**
     * 字体大小
     *
     * @return
     */
    private Spinner<Integer> crateFontSizeSpinner() {
        var spinner = new Spinner<Integer>(
                SUPPORTED_FONT_SIZE.get(0),
                SUPPORTED_FONT_SIZE.get(SUPPORTED_FONT_SIZE.size() - 1),
                TM.getFontSize()
        );
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        spinner.setPrefWidth(CONTROL_WIDTH);

        // Instead of this we should obtain font size from a rendered node.
        // But since it's not trivial (thanks to JavaFX doesn't expose relevant API)
        // we just keep current font size inside ThemeManager singleton.
        // It works fine if ThemeManager default font size value matches
        // default theme font size value.
        spinner.getValueFactory().setValue(TM.getFontSize());

        spinner.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                TM.setFontSize(val);
                //暂存到设置
                preferences.setFontSize(String.valueOf(val));
            }
        });

        return spinner;
    }

    /**
     * 语言选择框
     *
     * @return ComboBox
     */
    private SampleBlock createLanguageChooser() {
        //语言选择框
        ComboBox<String> comboBox = new ComboBox<>();
        //加入候选列表
        comboBox.getItems().addAll(Language.getDisplayNameList());
        comboBox.setPrefWidth(CONTROL_WIDTH);
        //读取设置语言，默认显示
        comboBox.getSelectionModel().select(TM.getLanguage().getDisplayName());
        log.debug("默认语言： "+TM.getLanguage().getDisplayName());
        comboBox.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {

                //改变语言设置
                LanguageResource.setLanguage( Language.getLocale(val) );
                //更新TM
                TM.setLanguage(Language.getLanguage(val));
                //暂存到设置里面
                preferences.setLanguage(Language.getLanguage(val));

            }
        });

        var grid = new GridPane();
        grid.setHgap(BLOCK_HGAP);
        grid.setVgap(BLOCK_VGAP);

        var Theme_SelectLanguage = new Label();
        Theme_SelectLanguage.textProperty().bind(LanguageResource.getLanguageBinding("Theme_SelectLanguage"));
        grid.add(Theme_SelectLanguage, 0, 0);
        grid.add(comboBox, 1, 0);

        Text tipText = new Text();
        tipText.textProperty().bind(LanguageResource.getLanguageBinding("Theme_SelectLanguage_Tips"));
        tipText.getStyleClass().setAll(TEXT, WARNING);
        TextFlow textFlow = new TextFlow(tipText);
        //textFlow.setMaxWidth(CONTROL_WIDTH);
        grid.add((textFlow),1,1);


        return new SampleBlock(LanguageResource.getLanguageBinding("Theme_LanguageTitle"), grid);
    }
}
