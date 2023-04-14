package com.ztoncloud.jproxytools.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.ztoncloud.jproxytools.Utils.io.JacksonMappers;
import com.ztoncloud.jproxytools.functional.proxychecker.components.ProxyCheckerSettings;
import com.ztoncloud.jproxytools.functional.proxychecker.components.ProxyCheckerSettingsProp;
import com.ztoncloud.jproxytools.functional.proxychecker.components.RequestAPI;
import com.ztoncloud.jproxytools.functional.proxypanel.ServerContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设置配置，所有需要预读的设置信息在APP启动时读取一次。避免重复读取。
 *
 * @author yugang
 * @date 2023/03/01
 */
public final class SettingsConfig {

    private static final Logger logger = LoggerFactory.getLogger(SettingsConfig.class);
    //属性文件
    private static final String APP_PROPS_FILE_NAME = "/application.properties";

    private final YAMLMapper yamlMapper;
    private PreferencesBean preferences;//主题、界面相关首选项
    //改为不预读，启动服务端才读取服务端设置。
    //private ServerContext serverContext;//代理服务端相关首选项

    public SettingsConfig() {
        this.yamlMapper = JacksonMappers.createYamlMapper();
        init();
    }

    private void init() {
        //加载属性文件到System.Property
        loadApplicationProperties();
        //加载代理检查config，代理检查使用独立的配置文件，方便模块解耦。
        createProxyCheckerConfig();

        createUserResources();

        // 设置无限加密策略
        java.security.Security.setProperty("crypto.policy", "unlimited");

        // 加载（或创建）应用程序首选项
        // 程序不能在缺失首选项的情况下运行，必须要加载或者新建一个。
        var firstRun = false;
        if (Files.exists(PreferencesBean.CONFIG_PATH)) {
            preferences = PreferencesBean.loadConfig(yamlMapper);
        } else {
            preferences = new PreferencesBean();
            firstRun = true;
        }

        // 第一次运行，也是检测某些默认值
        if (firstRun) {
            // pick app language from system if supported
            // Arrays.stream(Language.values())
            //        .filter(lang -> lang.getLocale().getLanguage().equalsIgnoreCase(Locale.getDefault().getLanguage()))
            //        .findFirst()
            //        .ifPresent(language -> preferences.setLanguage(language));

            PreferencesBean.saveConfig(preferences, yamlMapper);
        }
    }

    /**
     * 加载应用程序属性文件
     */
    private void loadApplicationProperties() {
        try {
            var properties = new Properties();
            properties.load(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(APP_PROPS_FILE_NAME)), UTF_8));

            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                System.setProperty(
                        String.valueOf(entry.getKey()),
                        String.valueOf(entry.getValue())
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createUserResources() {
        // Env.APP_DIR may not exist, so use 'mkdir -p'
        //FileSystemUtils.createDirTree(Env.USER_DIR);

        //FileSystemUtils.createDir(Env.AUTOCOMPLETE_DIR);

    }

    private void loadI18nResources() {
        // set default locale and load resource bundles
        // after that any component can just call Locale.getDefault()
        Locale.setDefault(preferences.getLocale());
        //I18n.getInstance().register(M.getLoader());
        //I18n.getInstance().register(DM.getLoader());
        //I18n.getInstance().reload();
    }

    /**
     * 获取首选项设置
     *
     * @return {@link PreferencesBean}
     */
    public PreferencesBean getPreferences() {
        return preferences;
    }

    /**
     * 创建代理检查配置
     */
    private void createProxyCheckerConfig() {
        ProxyCheckerSettingsProp.loadSettingsProp();//初始化proxyCheckerSettingsProperties
        ProxyCheckerSettings proxyCheckerSettings = new ProxyCheckerSettings();
        RequestAPI requestAPI = new RequestAPI(proxyCheckerSettings);
        Pair<HttpURLConnection, Long> pair = requestAPI.connect(null);

        //RequestAPI.Response response = requestAPI.getResponse(requestAPI.connect(null).getKey());
        try {
            //本机到API服务器延时
            Long networkDelay = pair.getValue();//pair有可能为Null
            RequestAPI.Response response ;
            response = requestAPI.getResponse(pair.getKey());
            String ip = response.ip;
            logger.info("本机互联网ip： "+ip);
            logger.info("网络延时:: "+networkDelay+ " ms");
            ProxyCheckerSettingsProp.setValue("InternetIP",ip);
            ProxyCheckerSettingsProp.setValue("networkDelay",String.valueOf(networkDelay));
            if(!proxyCheckerSettings.getIp().equals(ip)) {
                proxyCheckerSettings.setIp(ip);
            }
        } catch (IOException | NullPointerException e) {
            logger.error("API服务器请求错误,请检查网络连接和服务器是否运行正常！");
        }
    }

}
