package com.ztoncloud.jproxytools.Env;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 语言 Env目录需要在module-info.java授权才能使用，所以统一放Env目录里。
 *
 * @author yugang
 * @date 2023/03/01
 */
public enum Language {

  EN(Locale.US.getDisplayName(Locale.US), Locale.US),
  CN(Locale.SIMPLIFIED_CHINESE.getDisplayName(Locale.SIMPLIFIED_CHINESE), Locale.SIMPLIFIED_CHINESE);


  private final String displayName;
  private final Locale locale;

  private static final Locale defaultLocale = Locale.SIMPLIFIED_CHINESE;

  Language(String displayName, Locale locale) {
    this.displayName = displayName;
    this.locale = locale;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Locale getLocale() {
    return locale;
  }


  /**
   * 获得显示名称列表 将枚举值转化成list集合
   *
   * @return {@link List}<{@link String}>
   */
  public static List<String> getDisplayNameList() {
    List<String> list = new ArrayList<>();
    for (Language language : Language.values()) {
      list.add(language.displayName);
    }
    return list;
  }

  /**
   * 根据选择的语言，返回Locale代码
   *
   * @param displayName language
   * @return 返回Locale代码
   */
  public static Locale getLocale(String displayName) {

    if (displayName == null || displayName.isEmpty()) {
      return defaultLocale;
    }
    for (Language l : Language.values()) {
      if (displayName.equals(l.displayName)) {
        return l.getLocale();
      }

    }
    return defaultLocale;
  }

  /**
   * 根据选择的语言，返回Locale代码
   *
   * @param displayName language
   * @return 返回Language
   */
  public static Language getLanguage (String displayName) {

    if (displayName == null || displayName.isEmpty()) {
      return Language.CN;
    }
    for (Language l : Language.values()) {
      if (displayName.equals(l.displayName)) {
        return l;
      }

    }
    return Language.CN;
  }
}