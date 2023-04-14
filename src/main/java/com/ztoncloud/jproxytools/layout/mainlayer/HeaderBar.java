
package com.ztoncloud.jproxytools.layout.mainlayer;


import static atlantafx.base.theme.Styles.TEXT_SMALL;
import static atlantafx.base.theme.Styles.TITLE_3;
import static atlantafx.base.theme.Styles.TITLE_4;
import static com.ztoncloud.jproxytools.layout.mainlayer.MainLayer.SIDEBAR_WIDTH;
import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Spacer;
import com.ztoncloud.jproxytools.Env.BaseEnv;
import com.ztoncloud.jproxytools.Utils.Resources;
import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

class HeaderBar extends HBox {

    private static final int HEADER_HEIGHT = 50;
    private static final Ikon ICON_CODE = Feather.CODE;
    private static final Ikon ICON_SAMPLE = Feather.LAYOUT;

    private final MainModel model;
    private Consumer<Node> quickConfigActionHandler;

    public HeaderBar(MainModel model) {
        super();
        this.model = model;
        createView();
    }

    private void createView() {
        var logoImage = new ImageView(new Image(Resources.getResourceAsStream(BaseEnv.APP_LOGO)));
        logoImage.setFitWidth(32);
        logoImage.setFitHeight(32);


        var logoImageBorder = new Insets(1);//上下左右偏移量
        var logoImageBox = new StackPane(logoImage);
        logoImageBox.getStyleClass().add("image");
        logoImageBox.setPadding(logoImageBorder);
        logoImageBox.setPrefWidth(logoImage.getFitWidth() + logoImageBorder.getRight() * 2);
        logoImageBox.setMaxWidth(logoImage.getFitHeight() + logoImageBorder.getTop() * 2);
        logoImageBox.setPrefHeight(logoImage.getFitWidth() + logoImageBorder.getTop() * 2);
        logoImageBox.setMaxHeight(logoImage.getFitHeight() + logoImageBorder.getRight() * 2);

        //var logoLabel = new Label("AtlantaFX");
        var logoLabel = new Label(System.getProperty("app.name"));
        logoLabel.getStyleClass().addAll(TITLE_4);

        var versionLabel = new Label(System.getProperty("app.version"));
        versionLabel.getStyleClass().addAll("version", TEXT_SMALL);

        var logoBox = new HBox(10, logoImageBox, logoLabel, versionLabel);
        logoBox.getStyleClass().add("logo");
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setMinWidth(SIDEBAR_WIDTH);
        logoBox.setPrefWidth(SIDEBAR_WIDTH);
        logoBox.setMaxWidth(SIDEBAR_WIDTH);

        var titleLabel = new Label();
        titleLabel.getStyleClass().addAll("page-title", TITLE_4);
        titleLabel.textProperty().bind(model.titleProperty());

        var searchField = new CustomTextField();
        searchField.setLeft(new FontIcon(Material2MZ.SEARCH));
        searchField.setPromptText("Search");
        model.searchTextProperty().bind(searchField.textProperty());
/*
        DefaultEventBus.getInstance().subscribe(HotkeyEvent.class, e -> {
            if (e.getKeys().getControl() == ModifierValue.DOWN && e.getKeys().getCode() == KeyCode.F) {
                searchField.requestFocus();
            }
        });

 */

        // this dummy anchor prevents popover from inheriting
        // unwanted styles from the owner node
        //这个伪锚点防止popover继承
        // 所有者节点中不需要的样式
        var popoverAnchor = new Region();

        var quickConfigBtn = new FontIcon(Material2OutlinedMZ.STYLE);
        quickConfigBtn.mouseTransparentProperty().bind(model.themeChangeToggleProperty().not());
        quickConfigBtn.opacityProperty().bind(Bindings.createDoubleBinding(
                () -> model.themeChangeToggleProperty().get() ? 1.0 : 0.5, model.themeChangeToggleProperty()
        ));
        quickConfigBtn.setOnMouseClicked(e -> {
            if (quickConfigActionHandler != null) { quickConfigActionHandler.accept(popoverAnchor); }
        });

        var sourceCodeBtn = new FontIcon(ICON_CODE);
        sourceCodeBtn.mouseTransparentProperty().bind(model.sourceCodeToggleProperty().not());
        sourceCodeBtn.opacityProperty().bind(Bindings.createDoubleBinding(
                () -> model.sourceCodeToggleProperty().get() ? 1.0 : 0.5, model.sourceCodeToggleProperty()
        ));
        sourceCodeBtn.setOnMouseClicked(e -> model.nextSubLayer());
/*
        var githubLink = new FontIcon(Feather.GITHUB);
        githubLink.getStyleClass().addAll("github");
        githubLink.setOnMouseClicked(e -> {
            var homepage = System.getProperty("app.homepage");
            if (homepage != null) {
                DefaultEventBus.getInstance().publish(new BrowseEvent(URI.create(homepage)));
            }
        });

 */

        // ~

        model.currentSubLayerProperty().addListener((obs, old, val) -> {
            switch (val) {
                case PAGE -> sourceCodeBtn.setIconCode(ICON_CODE);
                case SOURCE_CODE -> sourceCodeBtn.setIconCode(ICON_SAMPLE);
            }
        });

        setId("header-bar");
        setMinHeight(HEADER_HEIGHT);
        setPrefHeight(HEADER_HEIGHT);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().setAll(
                logoBox,
                titleLabel,
                new Spacer(),
                searchField,
                popoverAnchor,
                quickConfigBtn,
                sourceCodeBtn
                //githubLink
        );
/*
        if (IS_DEV_MODE) {
            var devModeLabel = new Label("app is running in development mode");
            devModeLabel.getStyleClass().addAll(TEXT_SMALL, "dev-mode-indicator");
            getChildren().add(2, devModeLabel);
        }

 */
    }

    void setQuickConfigActionHandler(Consumer<Node> handler) {
        this.quickConfigActionHandler = handler;
    }
}
