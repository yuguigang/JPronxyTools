package com.ztoncloud.jproxytools.Utils;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;

/**
 * @Author yugang
 * @create 2023/4/12 3:33
 */
public class IpaddressUtils {


  /**
   * ipaddress字符串验证,如果不是ip字符串，抛出AddressStringException异常
   *
   * @param address 地址 需要校验的ip字符串
   * @return {@link IPAddress}
   * @throws AddressStringException 地址字符串异常，不是有效的ip字符串。
   */
  public static IPAddress IPAddressStringValidate(String address) throws AddressStringException {

    try {
      IPAddressString str = new IPAddressString(address);
      // use address
      return str.toAddress();
    } catch(AddressStringException e) {
      // e.getMessage has validation error
      throw new AddressStringException(e.getMessage());
    }

  }

}
