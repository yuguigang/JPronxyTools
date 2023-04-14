
package com.ztoncloud.jproxytools.layout.mainlayer;

import static javafx.scene.layout.Priority.ALWAYS;

import atlantafx.base.controls.Popover;
import com.ztoncloud.jproxytools.layout.page.Page;
import com.ztoncloud.jproxytools.layout.page.ThemePage;
import java.util.Objects;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * 主要层，配置所有页面
 */

public class MainLayer extends BorderPane {

    static final int SIDEBAR_WIDTH = 220;
    static final int PAGE_TRANSITION_DURATION = 500; // ms

    private final MainModel model = new MainModel();
    //配置头部Bar
    private final HeaderBar headerBar = new HeaderBar(model);
    //配置边栏
    private final Sidebar sidebar = new Sidebar(model);
    private final StackPane subLayerPane = new StackPane();
    //快速配置页
    private Popover quickConfigPopover;
    //显示代码页
    //private CodeViewer codeViewer;
    private StackPane codeViewerWrapper;

    public MainLayer() {
        super();

        createView();
        initListeners();
        //起始页，启动后默认显示页
        model.navigate(ThemePage.class);
        // keyboard navigation won't work without focus
        Platform.runLater(sidebar::begForFocus);
    }

    private void createView() {
        sidebar.setMinWidth(SIDEBAR_WIDTH);

        codeViewerWrapper = new StackPane();
        codeViewerWrapper.getStyleClass().add("source-code");
        //codeViewerWrapper.getChildren().setAll(codeViewer);

        subLayerPane.getChildren().setAll(codeViewerWrapper);
        HBox.setHgrow(subLayerPane, ALWAYS);
        VBox.setVgrow(subLayerPane,ALWAYS);

        setId("main");
        setTop(headerBar);
        //setTop(menuBar);
        //setTop(contentPage);
        //setTop(toolBarPage);//菜单和toolbar
        setLeft(sidebar);
        setCenter(subLayerPane);
    }

    private void initListeners() {


        model.selectedPageProperty().addListener((obs, old, val) -> {
            if (val != null) { loadPage(val); }
        });
    }

    private void loadPage(Class<? extends Page> pageClass) {
        try {
            final Page prevPage = (Page) subLayerPane.getChildren().stream()
                    .filter(c -> c instanceof Page)
                    .findFirst()
                    .orElse(null);
            final Page nextPage = pageClass.getDeclaredConstructor().newInstance();

            model.setPageData(
                    nextPage.getName(),
                    nextPage.canChangeThemeSettings(),
                    nextPage.canDisplaySourceCode()
            );

            // startup, no prev page, no animation
            if (getScene() == null) {
                subLayerPane.getChildren().add(nextPage.getView());
                return;
            }

            Objects.requireNonNull(prevPage);

            // reset previous page, e.g. to free resources
            prevPage.reset();

            // animate switching between pages
            subLayerPane.getChildren().add(nextPage.getView());
            subLayerPane.getChildren().remove(prevPage.getView());
            //过渡设置，duration–淡入淡出过渡的持续时间 node–将为不透明度设置动画的节点
            var transition = new FadeTransition(Duration.millis(PAGE_TRANSITION_DURATION), nextPage.getView());
            transition.setFromValue(0.0);
            transition.setToValue(1.0);
            transition.setOnFinished(t -> {
                if (nextPage instanceof Pane nextPane) { nextPane.toFront(); }
            });
            transition.play();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
