package com.ztoncloud.jproxytools.functional.proxychecker.components;


import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Manages Application settings and configuration files and directory
 * 管理应用程序设置、配置文件和目录
 */
public class Settings {

    //private static final Logger log =LogManager.getLogger(Settings.class.getName());
    private  static final Logger log = LoggerFactory.getLogger(Settings.class);

    private static final String APPLICATION_VERSION = "1.2";//版本
    private static final String APPLICATION_NAME = "Proxy Tools";
    private static final String APPLICATION_URL = "https://ztoncloud.com";
    private static final String APPLICATION_REPO = "https://github.com/faiqsohail/ProxyChecker/";

    public static String getApplicationName() {
        return APPLICATION_NAME;
    }

    public static String getApplicationUrl() {
        return APPLICATION_URL;
    }

    /**
     * Gets the configuration file and parses it using Gson
     * 获取配置文件并使用Gson对其进行解析
     * @return UserSettings
     */
    public static ProxyCheckerSettings getConfig()  {

        try {

            return new Gson().fromJson(new JsonReader(new FileReader(getConfigFile())), ProxyCheckerSettings.class);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            log.error("读取配置文件错误：");
            return null;
        }

    }

    /**
     * Saves an updated UserSettings  to disk
     * 将更新的UserSettings保存到磁盘
     * @param userSettings - UserSettings
     * @return boolean - Whether or not the save was successful
     */
    //public static boolean saveConfig(UserSettings userSettings) {
    public static boolean saveConfig(ProxyCheckerSettings userSettings) {
        File file = new File(getSettingsFolder().getAbsolutePath() + File.separator + "config.json");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(new Gson().toJson(userSettings));
            //log.info(userSettings.getColorScheme());
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get the configuration File object from the settings folder
     * 从设置文件夹获取配置文件对象
     * @return File
     */
    public static File getConfigFile() {
        File file = new File(getSettingsFolder().getAbsolutePath() + File.separator + "config.json");
        log.info("配置文件路径： "+file.getAbsolutePath());
        //如果文件不存在
        if(!file.exists()) {
            log.info("新建config.json");
            //testGson userSettings = new testGson();
            //savetestGson(userSettings);

            if(!saveConfig(new ProxyCheckerSettings())) {

                throw new RuntimeException("无法保存默认config.json！");
            }
        }
        return file;
    }


    /**
     * Gets the settings folder and creates it if it doesn't exist in the home directory
     * 获取设置文件夹，并在主目录中不存在时创建它
     * @return File
     */
    public static File getSettingsFolder()  {
        String safe_name = APPLICATION_NAME.replaceAll(" ", "_");//空格替换成_
        File file = new File(
                System.getProperty("user.home") + File.separator + "." + safe_name + File.separator
        );

        if(!file.exists()) {
            log.info("新建目录");
            if(!file.mkdirs()) {
                throw new RuntimeException("无法创建应用程序目录！");
            }
        }
        return file;
    }

    /**
     * Gets the current released version of this application
     * 获取此应用程序的当前发布版本
     * @return version
     */
    public static String getApplicationVersion() {
        return APPLICATION_VERSION;
    }

    /**
     * Gets the main repo of this project
     * 获取此项目的地址
     * @return
     */
    public static String getApplicationRepo() {
        return APPLICATION_REPO;
    }




}
