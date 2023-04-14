package com.ztoncloud.jproxytools.theme;

import java.util.Objects;

/**
 * @Author yugang
 * @create 2022/10/21 23:55
 */
public class ThemeNameConverter {
    /**
     * 主题转换成主题名字，同SamplerTheme.toString
     * @param theme 主题
     * @return String 主题 name
     */
    public static String ThemeToNameConverter(SamplerTheme theme){

        return theme != null ? theme.getName() : "";
    }

    /**
     * 主题名字转换成主题，如果没有查找到则返回null
     * @param name 主题名字
     * @return 主题
     */
    public static SamplerTheme NameToThemeConverter(String name) {
        ThemeManager tm = ThemeManager.getInstance();
        return tm.getRepository().getAll().stream()
                .filter(t -> Objects.equals(name, t.getName()))
                .findFirst()
                .orElse(null);
    }
}
