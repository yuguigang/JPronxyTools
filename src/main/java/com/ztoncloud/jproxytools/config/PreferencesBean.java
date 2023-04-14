package com.ztoncloud.jproxytools.config;


import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.ztoncloud.jproxytools.Env.BaseEnv;
import com.ztoncloud.jproxytools.Env.Language;
import com.ztoncloud.jproxytools.exception.AppException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PreferencesBean {

    private static final Logger logger = LoggerFactory.getLogger(PreferencesBean.class);
    //config文件
    public static final Path CONFIG_PATH = BaseEnv.getConfigDIR().resolve("preferences.yaml");
    //默认主题
    public static final Theme DEFAULT_THEME = new PrimerLight();
    //供选择的主题列表
    public static final List<Theme> THEMES = List.of(
            DEFAULT_THEME, new PrimerDark(), new NordLight(), new NordDark()
    );

    //默认语言
    private  Language language = Language.CN;
    //主题
    private String theme = DEFAULT_THEME.getName();


    //强调色，可以不设置默认
    private  String accentColor;
    //设置字体
    private String fontFamily = "Default";
    //设置字号
    private String fontSize;


    /** 默认构造函数 */
    public PreferencesBean() { }

    ///////////////////////////////////////////////////////////////////////////
    //              Getter Setter                                            //
    ///////////////////////////////////////////////////////////////////////////

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }


    public void setTheme(@Nullable String theme) {
        this.theme = Objects.requireNonNull(theme, DEFAULT_THEME.getName());
    }
    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Extended API                                                          //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取风格主题，返回Theme
     *
     * @return {@link Theme}
     */
    @JsonIgnore //忽略
    public Theme getStyleTheme() {
        var existingTheme = findThemeByName(theme);
        return existingTheme != null ? existingTheme : DEFAULT_THEME;
    }

    /**
     * 设置风格主题
     *
     * @param theme 主题
     */
    public void setStyleTheme(@Nullable Theme theme) {
        Objects.requireNonNull(theme, "theme");

        var existingTheme = findThemeByName(theme.getName());
        if (existingTheme == null) {
            throw new IllegalArgumentException("Unknown theme: " + theme.getName() + ".");
        }

        this.theme = existingTheme.getName();
    }

    /**
     * 通过主题名称找到主题
     *
     * @param name 名字
     * @return {@link Theme}
     */
    @JsonIgnore
    private @Nullable Theme findThemeByName(String name) {
        return THEMES.stream()
                .filter(theme -> Objects.equals(theme.getName(), name))
                .findFirst()
                .orElse(null);
    }

    @JsonIgnore
    public Locale getLocale() {
        // env variable has priority, but is only needed to simplify app testing,
        // this way can change app language without messing with UI
       // return ObjectUtils.defaultIfNull(Env.LOCALE, language.getLocale());
        return null;
    }


    ///////////////////////////////////////////////////////////////////////////
    // Save / Load                                                           //
    ///////////////////////////////////////////////////////////////////////////

    public static PreferencesBean loadConfig (YAMLMapper mapper) {
        return load(mapper, CONFIG_PATH);
    }

    public static PreferencesBean load(YAMLMapper mapper, Path path) {
        try {
            return mapper.readValue(path.toFile(), PreferencesBean.class);
        } catch (Exception e) {
            logger.error("读取偏好设置时出错: " +path);
            throw new AppException("读取偏好设置时出错！", e);
        }
    }

    public static void saveConfig (PreferencesBean preferences, YAMLMapper mapper) {
        save(preferences, mapper, CONFIG_PATH);
    }

    public static void save(PreferencesBean preferences, YAMLMapper mapper, Path path) {

        File file = path.toFile();
        //先判断文件是否存在，如果不存在再判断父路径是否存在，如果不存在则创建父目录。
        if (!file.exists()&&!file.getParentFile().exists()){
            logger.info("新建目录: " +file.getParentFile());
            if(!file.getParentFile().mkdirs()) {
                throw new RuntimeException(new IOException());
            }
        }

        try {
            mapper.writeValue(path.toFile(), preferences);
        } catch (Exception e) {
            logger.error("保存偏好设置出错！ Path: "+path+" \n"+e);
            throw new AppException("保存偏好设置出错！", e);
        }

    }
}
