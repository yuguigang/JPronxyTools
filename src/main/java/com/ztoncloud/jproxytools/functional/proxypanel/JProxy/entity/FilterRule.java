package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author yugang
 * @create 2023/4/10 2:48
 */
public enum FilterRule {
  ACCEPT,
  REJECT;

  public static List<String> toList() {
    List<String> list = new ArrayList<>();
    list.add(FilterRule.ACCEPT.name());
    list.add(FilterRule.REJECT.name());
    return list;
  }


}
