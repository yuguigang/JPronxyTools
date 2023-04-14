package com.ztoncloud.jproxytools.functional.proxychecker.components;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * 管理用户可配置的系统设置
 */
public class ProxyCheckerSettingsProp {


    /**
     * 保存设置的文件目录
     */
    private static final String APPLICATION_NAME = ".JProxyTools";
    /**
     * 保存设置的文件名
     */
    public static final String PROPERTIES_FILE_NAME = "ProxyCheckerSettings.properties";
    /**
     * 保存设置文件、logo保存的路径
     * 如：windows 10 C:\Users\用户名\.ProxyTools\
     */
    public static final String PROPERTIES_DIR_LOCATION = System.getProperty("user.home") + "/" + APPLICATION_NAME + "/";
    /**
     * 本地保存properties的文件的位置,包含文件名
     */
    private static String propertiesFileLocation;

    private static Properties proxyCheckerSettingsProperties;

    //private static final Logger logger = LogManager.getLogger(ProxyCheckerSettingsProp.class);
    private  static final Logger logger = LoggerFactory.getLogger(ProxyCheckerSettingsProp.class);

    /**
     * 设置属性文件properties位置
     *
     * @param propertiesFileLocation 属性文件properties位置
     */
    public static void setPropertiesFileLocation(final String propertiesFileLocation) {
        if (propertiesFileLocation == null || propertiesFileLocation.isEmpty())
            throw new IllegalArgumentException("Properties属性文件位置不能为空！");

        ProxyCheckerSettingsProp.propertiesFileLocation = propertiesFileLocation;
    }
    /**
     * 获取配置文件properties的储存位置,包含文件名
     *
     * @return 配置文件在本地磁盘的位置
     */
    public static String getPropertiesFileLocation() {
        return propertiesFileLocation;
    }
    /**
     * 设置 Properties ，将其他prop文件保存到proxyCheckerProperties.
     *
     * @param properties The properties object
     */
    public void setProperties(final Properties properties) {
        proxyCheckerSettingsProperties = properties;
    }

    /**
     *
     * 将设置保存到UserSettings.properties
     * 无法存储Properties对象时的异常
     */
    public static Boolean saveSettingsFile()  {
        logger.info("保存配置Properties");
        //注意：
        //1、OutputStream out：字节输出流，不能写入中文
        //2、Wreiter wreiter ：字符输出流，可以写入中文
        //3、comments ：用来解释保存说明 用来干什么用的（不能使用中文，会产生乱码）
/*
        try (final FileOutputStream fos = new FileOutputStream(getPropertiesFileLocation())) {
            properties.store(fos, null);
        }catch (IOException e){
            logger.info("保存配置文件失败！ "+e);
        }

 */

        try (final FileWriter writer = new FileWriter(getPropertiesFileLocation())) {
            proxyCheckerSettingsProperties.store(writer, null);
            return true;
        }catch (IOException e){
            logger.info("保存配置文件失败！ "+e);
            return false;
        }

    }

    /**
     * 加载设置文件，如果没有则新建一个
     * 注意，必须先加载到内存后，才能使用proxyCheckerSettingsProperties。
     */
    public static void loadSettingsProp() {
        //logger.info("读取配置文件proxyCheckerSettingsProperties中。。。");
        //final Path propertiesPath = Paths.get(getPropertiesFileLocation());
        setPropertiesFileLocation(PROPERTIES_DIR_LOCATION + PROPERTIES_FILE_NAME);
        if (proxyCheckerSettingsProperties == null) {
            proxyCheckerSettingsProperties = new Properties();
        }
        File file = new File(getPropertiesFileLocation());
        //创建baseFilePath文件所在的所有文件夹，使用file.getParentFile().mkdirs()
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            logger.info("创建默认属性文件");
            try {
                file.createNewFile();
                //Files.createFile(propertiesPath);
            } catch (IOException e) {
                logger.info("创建文件失败！ " + e);
            }

        }
        try {
            //这里用字节输入流FileInputStream，中文会乱码，得用字符输入流FileReader
            //final FileInputStream fis = new FileInputStream(getPropertiesFileLocation());
            //userSettingsProperties.load(fis);
            final FileReader reader = new FileReader(getPropertiesFileLocation());
            proxyCheckerSettingsProperties.load(reader);


        } catch (IOException e) {
            logger.info("读取prop文件失败！ " + e);
        }


    }

    /**
     * 根据key查询value值
     * @param key key
     * @return 默认值，如果读取key值为null或者为空，则返回默认值
     */
    public static String getValue(String key ,String defaultValue){

        String value = proxyCheckerSettingsProperties.getProperty(key);
        if (value == null || value.isEmpty()){
            return defaultValue;
        }
        return value;
    }

    /**
     * 新增/修改数据
     * @param key key
     * @param value value
     */
    public static void setValue(String key, String value  ){
        proxyCheckerSettingsProperties.setProperty(key, value);

    }

    /**
     * 移除指定设置
     * @param key key
     */
    public static void removeValue(String key){
        proxyCheckerSettingsProperties.remove(key);
    }

    /**
     * 移除所有设置
     *
     */
    public static void removeAllValue(){

        proxyCheckerSettingsProperties.clear();
    }
}
