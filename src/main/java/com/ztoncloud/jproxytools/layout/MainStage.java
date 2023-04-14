package com.ztoncloud.jproxytools.layout;


import static com.ztoncloud.jproxytools.Env.BaseEnv.APP_NAME;
import static com.ztoncloud.jproxytools.theme.ThemeManager.DEFAULT_FONT_FAMILY_NAME;

import atlantafx.base.theme.Theme;
import com.ztoncloud.jproxytools.Env.BaseEnv;
import com.ztoncloud.jproxytools.Utils.Resources;
import com.ztoncloud.jproxytools.config.PreferencesBean;
import com.ztoncloud.jproxytools.config.SettingsConfig;
import com.ztoncloud.jproxytools.i18n.LanguageResource;
import com.ztoncloud.jproxytools.layout.CustomStage.CustomStage;
import com.ztoncloud.jproxytools.theme.AccentColor;
import com.ztoncloud.jproxytools.theme.SamplerTheme;
import com.ztoncloud.jproxytools.theme.ThemeManager;
import com.ztoncloud.jproxytools.theme.ThemeNameConverter;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 初始化Stage
 *
 * @author yugang
 * @date 2023/03/01
 */
public final class MainStage {

  private static final Logger log = LoggerFactory.getLogger(MainStage.class);
  private static Stage mainStage ;

  public static SettingsConfig getSettingsConfig() {
    return settingsConfig;
  }



  /**
   * 创建 Stage 因为要自定义stage，所以要特别注意设置步骤， 1、设置语言，CustomStage需要传入设置 2、构建CustomStage 3、此时才能设置主题相关参数
   * 4、最后new一个MainStage。
   *
   * @param primaryStage 初级阶段
   * @param scene        场景
   * @return {@link MainStage}
   *///初始化
  public static Stage create(Stage primaryStage, Scene scene) {
    mainStage = primaryStage;
    //读取首选项,加载所有初始化配置
    settingsConfig = new SettingsConfig();
    var preferences = settingsConfig.getPreferences();
    var language = preferences.getLanguage();
    // 步骤1：
    // 设置语言
    LanguageResource.setLanguage(language.getLocale());

        /*
          TODO 设置ThemeManager。这里有个问题，需要设置两遍，第一遍设置后显示正常，但是没有实际改变。
               如果只设置第二遍，确实改变了设置，但是显示又不正确。暂时用设置两遍让其正常；

         */
    var tm = ThemeManager.getInstance();
    tm.setScene(scene);
    //设置语言
    //tm.setLanguage(language);
    // 第一遍设置
    settingPreferences(tm, preferences);

    var root = new ApplicationWindow();
    //自定义Stage
    new CustomStage.Builder(primaryStage, scene)
        .setContent(root)
        //.setEnableCloseBtn(true)
        //.setEnableMaxBtn(false)
        //.setEnableMinBtn(false)
        //.setWindowRound(false)
        //.setBackgroundStyle("-fx-background-color: -color-accent-emphasis;")
        //.setBackgroundStyle("-fx-background-color: yellow;")
        .setLogoImage(new ImageView(new Image(Resources.getResourceAsStream(BaseEnv.APP_LOGO))))
        //.setTitle(BaseEnv.APP_NAME)//固定字符串
        .setTitle(LanguageResource.getLanguageBinding("Sidebar_GENERAL"))//绑定字符串，实现动态切换语言
        .builder();

    //第二遍设置
    settingPreferences(tm, preferences);
    //scene.getStylesheets().addAll("index.css");
    return primaryStage;

  }

  /**
   * 设置首选项
   *
   * @param tm          tm
   * @param preferences 首选项
   */
  private static void settingPreferences(ThemeManager tm, PreferencesBean preferences) {

    tm.setLanguage(preferences.getLanguage());

    var themeName = preferences.getTheme();
    //主题名字转换成主题，如果没有查到则返回null。
    SamplerTheme theme = ThemeNameConverter.NameToThemeConverter(themeName);
    tm.setTheme(theme == null ? tm.getDefaultTheme() : theme);//如果读取错误

    //设置强调色
    String accentColor = preferences.getAccentColor();
    AccentColor settingsAccentColor = AccentColor.getAccentColor(accentColor);
    if (settingsAccentColor == null) {
      tm.resetAccentColor();//如果查询不到，则设置为默认。
    } else {
      tm.setAccentColor(settingsAccentColor);
    }

    //设置字体
    String fontFamily = preferences.getFontFamily();
    tm.setFontFamily("Default".equals(fontFamily) ? DEFAULT_FONT_FAMILY_NAME : fontFamily);

    //设置字号
    String fontSize = preferences.getFontSize();
    tm.setFontSize(fontFamily.equals("") || fontSize == null ? 14 : Integer.parseInt(fontSize));

  }

  private static SettingsConfig settingsConfig = new SettingsConfig();

  public static Stage getMainStage (){
    return mainStage;
  };

}
