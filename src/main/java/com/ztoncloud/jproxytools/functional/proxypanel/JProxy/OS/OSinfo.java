package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.OS;

/**
 * @Author yugang
 * @create 2022/12/24 23:51
 */
public class OSinfo {


        private static String OS = System.getProperty("os.name").toLowerCase();

        private static final OSinfo _instance = new OSinfo();


        private OSinfo(){}

        public static boolean isLinux(){
            return OS.contains("linux");
        }

        public static boolean isMacOS(){
            return OS.contains("mac") &&OS.indexOf("os")>0&& !OS.contains("x");
        }

        public static boolean isMacOSX(){
            return OS.contains("mac") &&OS.indexOf("os")>0&&OS.indexOf("x")>0;
        }

        public static boolean isWindows(){
            return OS.contains("windows");
        }

    }



