package com.ztoncloud.jproxytools.layout.CustomStage;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;

import java.awt.Dimension;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;


/**
 * 自定义 Stage
 *
 * @author yugang
 * @date 2023/02/12
 */
public class CustomStage {

  public static final Dimension MIN_SIZE = new Dimension(200, 31);
  int sceneWidth;//scene宽
  int sceneHeight;//scene高
  private final Stage primaryStage;
  Scene scene;

  private double x = 0.00;
  private double y = 0.00;
  private double width = 0.00;
  private double height = 0.00;
  private boolean isMax = false;

  private double xOffset = 0, yOffset = 0;//自定义dialog移动横纵坐标


  //部件控制
  private ImageView logoImage;
  private FontIcon logoIcon;
  private String title;
  private ObservableValue<String> observableTitle;
  private boolean enableMinBtn = true;//是否需要最小化按钮
  private boolean enableMaxBtn = true;//是否需要最大化按钮
  private boolean enableClose = true;//是否需要最关闭按钮
  private boolean windowRound = true;//是否需要四角圆角
  //内容
  private  Node content;
  //背景样式
  private String backgroundStyle = "";

  public CustomStage(final Stage primaryStage ,final Scene scene ) {
    this.primaryStage = primaryStage;
    this.scene = scene;
    //createView();
  }


  private Stage createView() {

    primaryStage.initStyle(StageStyle.TRANSPARENT);


    BorderPane root = new BorderPane();
    root.setMinSize(MIN_SIZE.getWidth(),MIN_SIZE.getHeight());

    GridPane gpTitle = new GridPane();

    gpTitle.setAlignment(Pos.CENTER_LEFT);
    //这里位置要调整，不然有白边
    gpTitle.setPadding(new Insets(-1, -1, 0, 10));
    gpTitle.setPrefHeight(15);

    //设置logo图片，Image优先，字体图标次之
    Label lbTitle = new Label();
    if (observableTitle != null) {
      lbTitle.textProperty().bind(observableTitle);
    } else {
      lbTitle.setText(title);
    }
    if (logoImage != null) {
      // 示例： logoImage = new ImageView(new Image("/assets/images/T3.png"));
      logoImage.setFitWidth(15);
      logoImage.setFitHeight(15);
      lbTitle.setGraphic(logoImage);
    } else if (logoIcon != null) {
      //字体图标型logo，随强调色变化。
      // 示例： logoIcon = new FontIcon(Material2AL.GAVEL);
      logoIcon.setIconSize(20);
      logoIcon.getStyleClass().addAll(ACCENT);
      lbTitle.setGraphic(logoIcon);
    }

    Button btnMin = new Button("", new FontIcon(Material2MZ.MINUS));
    btnMin.setVisible(enableMinBtn);
    btnMin.getStyleClass().addAll(BUTTON_ICON, FLAT, "title-button");

    Button btnMax = new Button("", new FontIcon(Material2AL.CROP_SQUARE));
    btnMax.setVisible(enableMaxBtn);
    btnMax.getStyleClass().addAll(BUTTON_ICON, FLAT, "title-button");

    Button btnClose = new Button("", new FontIcon(Material2AL.CLOSE));
    btnClose.setVisible(enableClose);
    btnClose.getStyleClass().addAll(BUTTON_ICON, FLAT, "title-close-button");





    gpTitle.add(lbTitle, 0, 0);
    gpTitle.add(btnMin, 1, 0);
    gpTitle.add(btnMax, 2, 0);
    gpTitle.add(btnClose, 3, 0);
    GridPane.setHgrow(lbTitle, Priority.ALWAYS);
    //如果要圆角需要加入一些新的样式
    //如果要圆角需要加入一些新的样式
    GridPane gpBottom = new GridPane();
    gpBottom.setPadding(new Insets(0, 0, 5, 0));
    gpBottom.setStyle("-fx-background-radius: 0 0 5 5px;");
    if (isWindowRound()) {
      root.setBottom(gpBottom);//圆角需要加入Bottom，不然控件会撑成直角，gpBottom会抬高底线5px。如果要解决这个问题只能每个接触底线的控件都圆边，特别麻烦。
      btnClose.setStyle("-fx-background-radius: 0 5 0 0px;");
      root.setStyle("-fx-background-radius: 5px;");
      gpTitle.setStyle("-fx-border-color: transparent transparent -color-border-default transparent;-fx-background-radius: 5 5 0 0px;"+backgroundStyle);
    } else {
      //下划线+标题栏背景样式
      gpTitle.setStyle("-fx-border-color: transparent transparent -color-border-default transparent;"+backgroundStyle);
    }


    root.setTop(gpTitle);
    //监听按钮
    btnMin.setOnAction(event -> primaryStage.setIconified(true));
    btnMax.setOnAction(event -> {
      Rectangle2D rectangle2d = Screen.getPrimary().getVisualBounds();
      isMax = !isMax;
      if (isMax) {
        // 最大化
        btnMax.setGraphic(new FontIcon(Material2AL.FILTER_NONE));
        primaryStage.setX(rectangle2d.getMinX());
        primaryStage.setY(rectangle2d.getMinY());
        primaryStage.setWidth(rectangle2d.getWidth());
        primaryStage.setHeight(rectangle2d.getHeight());
      } else {
        // 缩放回原来的大小
        btnMax.setGraphic(new FontIcon(Material2AL.CROP_SQUARE));
        primaryStage.setX(x);
        primaryStage.setY(y);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
      }
    });
    btnClose.setOnAction(event -> primaryStage.close());
    //Stage X,Y,Width,Height监听
    primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && !isMax) {
        x = newValue.doubleValue();
      }
    });
    primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && !isMax) {
        y = newValue.doubleValue();
      }
    });
    primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && !isMax) {
        width = newValue.doubleValue();
      }
    });
    primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && !isMax) {
        height = newValue.doubleValue();
      }
    });

    root.setOnMouseDragged((MouseEvent event) -> {
      //根据鼠标的横纵坐标移动dialog位置
      event.consume();
      if (yOffset != 0) {
        primaryStage.setX(event.getScreenX() - xOffset);
        if (event.getScreenY() - yOffset < 0) {
          primaryStage.setY(0);
        } else {
          primaryStage.setY(event.getScreenY() - yOffset);
        }
      }
    });
    //鼠标点击获取横纵坐标
    root.setOnMousePressed(event -> {
      event.consume();
      xOffset = event.getSceneX();
      if (event.getSceneY() > 46) {
        yOffset = 0;
      } else {
        yOffset = event.getSceneY();
      }
    });
    scene.setRoot(root);
    //设置scene透明，不然四角显示白色底
    scene.setFill(Color.TRANSPARENT);

    primaryStage.setScene(scene);

   root.setCenter(content);
return primaryStage;
  }
  /*
    #####################################################################################
    ################################## Getter | Setter ##################################
    #####################################################################################
     */

  /**
   * 获取内容
   *
   * @return {@link Node}
   */
  private Node getContent() {
    return content;
  }

  /**
   * 设置内容
   *
   * @param content 内容
   */
  private void setContent(Node content) {
    this.content = content;
  }

  private ImageView getLogoImage() {
    return logoImage;
  }

  /**
   * 设置标志图片Image ，如果同时设置字体图标，Image优先
   *
   * @param logoImage 标志形象
   */
  private void setLogoImage(ImageView logoImage) {
    this.logoImage = logoImage;
  }

  /**
   * 得到标志图标FontIcon
   *
   * @return {@link FontIcon}
   */
  private FontIcon getLogoIcon() {
    return logoIcon;
  }

  private void setLogoIcon(FontIcon logoIcon) {
    this.logoIcon = logoIcon;
  }

  /**
   * 获得标题文件
   *
   * @return {@link String}
   */
  private String getTitle() {
    return title;
  }

  /**
   * 设置标题文字
   *
   * @param title 标题
   */
  private void setTitle(String title) {
    this.title = title;
  }
  /**
   * 设置标题文字
   * 设置 ObservableValue<String> 类型title，动态调整文字，实现动态切换语言。
   * @param observableTitle 标题
   */
  private void setTitle(ObservableValue<String> observableTitle) {
    this.observableTitle = observableTitle;
  }


  private boolean isEnableMinBtn() {
    return enableMinBtn;
  }

  /**
   * 设置启用最小化按钮 默认true
   *
   * @param enableMinBtn 布尔
   */
  private void setEnableMinBtn(boolean enableMinBtn) {
    this.enableMinBtn = enableMinBtn;
  }

  private boolean isEnableMaxBtn() {
    return enableMaxBtn;
  }

  /**
   * 设置启用最大化按钮 默认true
   *
   * @param enableMaxBtn 布尔
   */
  private void setEnableMaxBtn(boolean enableMaxBtn) {
    this.enableMaxBtn = enableMaxBtn;
  }

  private boolean isEnableClose() {
    return enableClose;
  }

  /**
   * 设置启用关闭按钮 默认true
   *
   * @param enableClose 布尔
   */
  private void setEnableClose(boolean enableClose) {
    this.enableClose = enableClose;
  }


  /**
   * 窗口是圆
   *
   * @return boolean
   */
  private boolean isWindowRound() {
    return windowRound;
  }

  /**
   * 设置窗口轮
   *
   * @param windowRound 窗轮
   */
  private void setWindowRound(boolean windowRound) {
    this.windowRound = windowRound;
  }

  /**
   * 获取背景风格
   *
   * @return {@link String}
   */
  private String getBackgroundStyle() {
    return backgroundStyle;
  }

  /**
   * 设置背景风格
   *
   * @param backgroundStyle 背景风格
   */
  private void setBackgroundStyle(String backgroundStyle) {
    this.backgroundStyle = backgroundStyle;
  }


  /**
   * 构建自定义Stage的静态类
   *
   * @author yugang
   * @date 2023/02/17
   */
  public static class Builder  {

    CustomStage customStage;

    public Builder (Stage stage,Scene scene) {
      this.customStage = new CustomStage(stage,scene);

    }

    public Stage builder () {
      return customStage.createView();
    }

    /**
     * 设置logo图片,图片和图标同时设置时，图片优先
     *
     * @param logoImage 标志形象
     * @return {@link Builder}
     */
    public Builder setLogoImage (ImageView logoImage) {
      customStage.setLogoImage(logoImage);
      return this;
    }

    /**
     * 设置logo图标，图片和图标同时设置时，图片优先
     *
     * @param logoIcon 标志图标
     * @return {@link Builder}
     */
    public Builder setLogoIcon (FontIcon logoIcon) {
      customStage.setLogoIcon(logoIcon);
      return this;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     * @return {@link Builder}
     */
    public Builder setTitle (String title) {
      customStage.setTitle(title);
      return this;
    }
    /**
     * 设置标题
     *
     * @param  observableTitle ObservableValue<String>类型标题
     * @return {@link Builder}
     */
    public Builder setTitle (ObservableValue<String> observableTitle) {
      customStage.setTitle(observableTitle);
      return this;
    }

    /**
     * 设置最小化按钮，默认true
     *
     * @param enableMinBtn bool
     * @return {@link Builder}
     */
    public Builder setEnableMinBtn (boolean enableMinBtn) {
      customStage.setEnableMinBtn(enableMinBtn);
      return this;
    }

    /**
     * 设置启用最大化按钮，默认true
     *
     * @param enableMaxBtn bool 默认true
     * @return {@link Builder}
     */
    public Builder setEnableMaxBtn (boolean enableMaxBtn) {
      customStage.setEnableMaxBtn(enableMaxBtn);
      return this;
    }

    /**
     * 设置启用关闭btn
     *
     * @param enableClose bool 默认true
     * @return {@link Builder}
     */
    public Builder setEnableCloseBtn (boolean enableClose) {
      customStage.setEnableClose(enableClose);
      return this;
    }

    /**
     * 设置窗口圆角
     *
     * @param windowRound bool 默认true
     * @return {@link Builder}
     */
    public Builder setWindowRound (boolean windowRound) {
      customStage.setWindowRound(windowRound);
      return this;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     * @return {@link Builder}
     */
    public Builder setContent (Node content) {
      customStage.setContent(content);
      return this;
    }

    public Builder setBackgroundStyle (String style) {
      customStage.setBackgroundStyle(style);
      return this;
    }





  }




}






