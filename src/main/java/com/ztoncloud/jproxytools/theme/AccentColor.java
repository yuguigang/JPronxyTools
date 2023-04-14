package com.ztoncloud.jproxytools.theme;

import javafx.css.PseudoClass;
import javafx.scene.paint.Color;


/**
 * 强调色
 * @param primaryColor 颜色，可自定义也可动态获取样式表CSS的颜色
 * @param pseudoClass 伪类虚拟样式
 */
public record AccentColor(Color primaryColor, PseudoClass pseudoClass) {

    public static AccentColor primerPurple() {
        return new AccentColor(Color.web("#8250df"), PseudoClass.getPseudoClass("accent-primer-purple"));
    }

    public static AccentColor primerPink() {
        return new AccentColor(Color.web("#bf3989"), PseudoClass.getPseudoClass("accent-primer-pink"));
    }

    public static AccentColor primerCoral() {
        return new AccentColor(Color.web("#c4432b"), PseudoClass.getPseudoClass("accent-primer-coral"));
    }

    /**
     * 根据保存的pseudoClass值，返回强调色。
     * @param pseudoClass pseudoClass
     * @return 强调色AccentColor的类
     */
    public static AccentColor getAccentColor(String pseudoClass) {

        if (pseudoClass == null || pseudoClass.isEmpty()){
            return null;
        }

        return switch (pseudoClass) {
                case "accent-primer-purple" -> AccentColor.primerPurple();
                case "accent-primer-pink" -> AccentColor.primerPink();
                case "accent-primer-coral" -> AccentColor.primerCoral();
                default -> null;
        };
    }
}
