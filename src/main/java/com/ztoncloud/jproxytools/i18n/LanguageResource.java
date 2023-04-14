package com.ztoncloud.jproxytools.i18n;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class LanguageResource {

    /**
     * 默认语言文件 Base Name
     */
    private static final String LANGUAGE_RESOURCE_NAME = "/i18n/Language";

    /**
     * 语言资源工厂
     */
    private static final ObservableResourceBundleFactory LANGUAGE_RESOURCE_FACTORY = new ObservableResourceBundleFactory();

    /**
     * 支持的语言集合，应与语言资源文件同步手动更新
     */
    public static final List<Locale> SUPPORT_LANGUAGE_LIST = Arrays.asList(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH);

    /**
     * 记录当前所选时区
     */
    private static final ObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>();

    public static ObjectProperty<Locale> currentLocaleProperty() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocaleProperty().set(locale);
    }

    /**
     * 更换语言的组件使用此方法初始化自己的值，调用 {@link LanguageResource#setLanguage(Locale)} 来更新界面语言
     *
     * @return 当前界面语言
     */
    public static Locale getCurrentLocale() {
        return currentLocaleProperty().get();
    }

    /**
     * 更新界面语言
     *
     * @param locale 区域
     */
    public static void setLanguage(Locale locale) {
        setCurrentLocale(locale);
        LANGUAGE_RESOURCE_FACTORY.setResourceBundle(ResourceBundle.getBundle(LANGUAGE_RESOURCE_NAME, locale));
    }

    /**
     * 更新界面语言
     *
     * @param locale 区域
     */
    public static void setLanguage(String path ,Locale locale) {
        setCurrentLocale(locale);
        LANGUAGE_RESOURCE_FACTORY.setResourceBundle(ResourceBundle.getBundle(path, locale));
    }

    /**
     * 获取指定标识的字符串绑定
     *
     * @param key 标识
     * @return 对应该标识的字符串属性绑定
     */
    public static StringBinding getLanguageBinding(String key) {
        return LANGUAGE_RESOURCE_FACTORY.getStringBinding(key);
    }

    /**
     * 有此类所在路径决定相对路径基于/com/icuxika
     *
     * @param path 资源文件相对路径
     * @return 资源文件路径
     */
    public static URL load(String path) {
        return LanguageResource.class.getResource(path);
    }
}
