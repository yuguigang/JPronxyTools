package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.handler.IpFilterRuleHandler;


import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.IPFilterModel;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yugang
 * IP过滤规则
 * @create 2023/2/5 3:00
 */
public class IpFilterRuleHandler  {
  private  static final Logger logger = LoggerFactory.getLogger(IpFilterRuleHandler.class);
  private static  RuleBasedIpFilter ruleBasedIpFilter ;

  /**
   * IP转换成IpSubnetFilterRule
   * 只支持IP4转换
   *
   * @param ip ip字符串
   * @param ruleType 规则类型
   * @return {@link IpSubnetFilterRule}
   */
  public static IpSubnetFilterRule convertIp(final String ip,
      final IpFilterRuleType ruleType) {
    //如果ruleType为null则返回null，表示此规则有错。
    if (ruleType == null) {
      return null;
    }
    //判断是否是合格IP格式的正则表达式
    String pattern = "^(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$";
    if (!ip.contains("/") && ip.matches (pattern) ) {

      return new IpSubnetFilterRule(ip, 32, ruleType);
    }

    final String[] parts = ip.split("/", 2);
    if (parts[0].matches(pattern)) {
      if (parts.length < 2 ) {
        return new IpSubnetFilterRule(parts[0], 32, ruleType);
      }

      final int cidrPrefix = Integer.parseInt(parts[1]);
      if (cidrPrefix < 32) {
        return new IpSubnetFilterRule(parts[0], cidrPrefix, ruleType);
      } else {
        return new IpSubnetFilterRule(parts[0], 32, ruleType);
      }
      //如果IP字符串不是合法ip则返回null
    } else {
      return null;
    }
  }

  /**
   * List<Map<String, String>>转换成RuleBasedIpFilter规则
   *
   * @param ipFilterList ip筛选器列表
   */
  public static void convertMapToRules (final List<Map<String, String>> ipFilterList) {
    if (ipFilterList != null) {
      int Int = ipFilterList.size();
      logger.info("Int: "+Int);
      IpFilterRule[] rules = new IpFilterRule [Int+1];
      for (int i = 0 ; i < Int  ; i ++ ) {
         String ipFilter = ipFilterList.get(i).get("ip");
         String rule = ipFilterList.get(i).get("rule");
         //转换成IpSubnetFilterRule，如果格式错误则返回null
        logger.info("ip： "+ipFilter+" rule: "+rule);
        IpSubnetFilterRule ipSubnetFilterRule = convertIp(ipFilter,getRuleType(rule));
         if ( ipSubnetFilterRule !=null ) {
           rules[i] = ipSubnetFilterRule;

         }
      }

      //拦截所有请求，根据优先级，这个规则一定要加最后。
      IpFilterRule rejectAll = new IpFilterRule() {
        @Override
        public boolean matches(InetSocketAddress remoteAddress) {
          return true;
          //如果rules为空，则允许所有连接
        }

        @Override
        public IpFilterRuleType ruleType() {
          return IpFilterRuleType.REJECT;
        }
      };
      rules[Int] = rejectAll;
      setRuleBasedIpFilter(new RuleBasedIpFilter(rules));
    }else {
      //如果没有规则设置，则允许所有链接。
    setRuleBasedIpFilter(null);
    }
  }

  public static void convertRules (final List<IPFilterModel> ipFilterList) {
    if (ipFilterList != null) {
      int Int = ipFilterList.size();
      logger.info("Int: "+Int);
      IpFilterRule[] rules = new IpFilterRule [Int+1];
      for (int i = 0 ; i < Int  ; i ++ ) {
        String ipFilter = ipFilterList.get(i).getIp();
        String rule = ipFilterList.get(i).getRule().toString();
        //转换成IpSubnetFilterRule，如果格式错误则返回null
        logger.info("ip： "+ipFilter+" rule: "+rule);
        IpSubnetFilterRule ipSubnetFilterRule = convertIp(ipFilter,getRuleType(rule));
        if ( ipSubnetFilterRule !=null ) {
          rules[i] = ipSubnetFilterRule;

        }
      }

      //拦截所有请求，根据优先级，这个规则一定要加最后。
      IpFilterRule rejectAll = new IpFilterRule() {
        @Override
        public boolean matches(InetSocketAddress remoteAddress) {
          return true;
          //如果rules为空，则允许所有连接
        }

        @Override
        public IpFilterRuleType ruleType() {
          return IpFilterRuleType.REJECT;
        }
      };
      rules[Int] = rejectAll;
      setRuleBasedIpFilter(new RuleBasedIpFilter(rules));
    }else {
      //如果没有规则设置，则允许所有链接。
      setRuleBasedIpFilter(null);
    }
  }


  /**
   * 字符串转换成规则类型
   *
   * @param accept 接受
   * @return {@link IpFilterRuleType}
   */public static  IpFilterRuleType getRuleType(String accept) {

    if (accept == null || accept.isEmpty()){
      return null;
    }

    return switch (accept) {
      case "ACCEPT" -> IpFilterRuleType.ACCEPT;
      case "REJECT" -> IpFilterRuleType.REJECT;
      default -> null;
    };
  }

  public static RuleBasedIpFilter getRuleBasedIpFilter() {
    return ruleBasedIpFilter;
  }

  public static void setRuleBasedIpFilter(RuleBasedIpFilter ruleBasedIpFilter) {
    IpFilterRuleHandler.ruleBasedIpFilter = ruleBasedIpFilter;
  }

}
