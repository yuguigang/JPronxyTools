
package com.ztoncloud.jproxytools.functional.proxypanel.gui;


import com.ztoncloud.jproxytools.functional.proxypanel.gui.settings.ProxySettingsDialog;
import com.ztoncloud.jproxytools.layout.page.AbstractPage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ProxyTools 面板页面
 *
 * @author yugang
 * @date 2023/04/09
 */
public class ProxyPanelPage extends AbstractPage {

  private static final Logger logger = LoggerFactory.getLogger(ProxyPanelPage.class);
  public static final ObservableValue<String> NAME = new SimpleStringProperty("JProxyTools");
  private ProxySettingsDialog proxySettingsDialog;

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

  public ProxyPanelPage() {
    super();
    createView();

  }

  @Override
  protected void onRendered() {

    super.onRendered();

  }

  private void createView() {
    //overlay属性不能传递到InfoGrid对象，所以这里反过来，拿取InfoGrid的按钮并监听。
    var infoGrid = new InfoGrid();//顶部4个卡片
    infoGrid.getSettingsBtn().setOnAction(event -> {

      ProxySettingsDialog dialog = getOrCreateProxySettingsDialog();//加入自定义主题Dialog
      overlay.setContent(dialog, HPos.CENTER);
      overlay.setHiddenOutside(false);//设置在区域外点击鼠标不会关闭弹窗。
      overlay.toFront();

    });

    var vbox = new VBox(
        30,
        infoGrid,//顶部4个卡片
        new SearchBox(),//搜索BOX
        new ProxyPoolTable()//ip表格

    );
    setUserContent(vbox);
  }

  /**
   * 获取或创建代理设置对话框
   *
   * @return {@link ProxySettingsDialog}
   */
  private ProxySettingsDialog getOrCreateProxySettingsDialog() {
    if (proxySettingsDialog == null) {
      proxySettingsDialog = new ProxySettingsDialog();
    }

    proxySettingsDialog.setOnCloseRequest(() -> {
      overlay.removeContent();
      overlay.toBack();
    });

    return proxySettingsDialog;
  }
}
