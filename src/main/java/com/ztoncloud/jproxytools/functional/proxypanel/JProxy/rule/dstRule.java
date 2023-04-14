package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.rule;

/**
 * @Author yugang
 * @create 2023/3/20 0:26
 */
public interface dstRule {

  /**
   * 地址匹配规则
   *
   * @param dstAddr dst addr 目的地址
   * @param dstPort dstPort 目的端口
   * @return boolean
   */
  static Action match(String dstAddr, int dstPort) {
return Action.DIRECT;
  }
}
