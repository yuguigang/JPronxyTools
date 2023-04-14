package com.ztoncloud.jproxytools.functional.proxypanel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.ztoncloud.jproxytools.Env.BaseEnv;
import com.ztoncloud.jproxytools.Utils.io.JacksonMappers;
import com.ztoncloud.jproxytools.exception.AppException;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.FilterRule;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.UpstreamProxy;
import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.IPFilterModel;
import com.ztoncloud.jproxytools.Utils.io.FileSystemUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Context 和 APP设置
 * 这个Context是单例的。
 * @Author yugang
 * @create 2023/1/13 9:29
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerContext {

    private static final Logger logger = LoggerFactory.getLogger(ServerContext.class);

    private volatile static  ServerContext instance ;

    private static final YAMLMapper yamlMapper = JacksonMappers.createYamlMapper() ;
    //避免其它类使用，构建函数是私有的private
    private ServerContext() {
        //this.ipFilterList = ipFilterListTEST();
        //setIpFilterList(testIPFilterModel());
    }

    //config文件
    public static final Path PROXY_SERVER_CONFIG = BaseEnv.getConfigDIR().resolve("ProxyServerConfig.yaml");



    private  Integer serverBeginPort = 5001 ;

    private  Integer serverEndPort = 5010 ;

    private  int timeout = 800 ;

    //IP提供商
    private String provider;



    //是否开启IP过滤
    private boolean enableIpFilter = false ;




    private List<IPFilterModel> ipFilterList;

    // ip过滤List，格式如：[{ip:127.0.0.1/24,accept:REJECT},{192.168.1.1/32,accept:ACCEPT}],并且，跟进规则优先级，List应当有下标
    //private List<Map<String, String>> ipFilterList = null; // = new ArrayList<>();
    // 上级代理链
    private  Queue<UpstreamProxy> upstreamProxies = null ;



    //返回一个异步锁单例
    public static ServerContext getInstance() {

        if (instance == null) {
            synchronized (ServerContext.class) {
                if (instance == null) {
                    instance = new ServerContext();
                    //创建新单例时，读取配置文件。
                    instance = loadConfig();
                }
            }
        }
        return instance;
    }


    ///////////////////////////////////////////////////////////////////////////
    //              Getter Setter                                            //
    ///////////////////////////////////////////////////////////////////////////

    public Integer getServerBeginPort() {
        return serverBeginPort;
    }

    public void setServerBeginPort(Integer serverBeginPort) {
        this.serverBeginPort = serverBeginPort;
    }

    public Integer getServerEndPort() {
        return serverEndPort;
    }

    public void setServerEndPort(Integer serverEndPort) {
        this.serverEndPort = serverEndPort;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    //public List<Map<String, String>> getIpFilterList() {
    //    return ipFilterList;
    //}



    //public void setIpFilterList(
    //    List<Map<String, String>> ipFilterList) {
     //   this.ipFilterList = ipFilterList;
    //}

    public Queue<UpstreamProxy> getUpstreamProxies() {
        return upstreamProxies;
    }

    public void setUpstreamProxies(
        Queue<UpstreamProxy> upstreamProxies) {
        this.upstreamProxies = upstreamProxies;
    }
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isEnableIpFilter() {
        return enableIpFilter;
    }

    public void setEnableIpFilter(boolean enableIpFilter) {
        this.enableIpFilter = enableIpFilter;
    }

    public List<IPFilterModel> getIpFilterList () {
        return ipFilterList;
    }

    public void setIpFilterList (List<IPFilterModel> ipFilterModels) {
        this.ipFilterList = ipFilterModels;
    }

    private List<IPFilterModel> testIPFilterModel (){
        List<IPFilterModel> list = new ArrayList<>();
        IPFilterModel ipFilterModel = new IPFilterModel();
        ipFilterModel.setIp("192.168.31.1/24");
        ipFilterModel.setRule(FilterRule.ACCEPT);
        list.add(ipFilterModel);
        IPFilterModel ipFilterModel2 = new IPFilterModel();
        ipFilterModel2.setIp("127.0.0.1/0");
        ipFilterModel2.setRule(FilterRule.REJECT);
        list.add(ipFilterModel2);
        return list;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Extended API                                                          //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获得服务器端口设置
     *
     * @return {@link List}<{@link Integer}>
     */
    @JsonIgnore
    public List<Integer> getServerPorts() {
        logger.debug("开始端口： {}，结束端口：{}", serverBeginPort, serverEndPort);
        if (checkPort(serverBeginPort, serverEndPort)) {
            List<Integer> ports = new ArrayList<>();
            for (int i = serverBeginPort; i <= serverEndPort; i++) {
                ports.add(i);
            }
            return ports;
        }
        return null;
    }

    /**
     * 校验端口设置， 端口最多能设置30个，范围在1025-65535之间
     *
     * @param begin 开始端口号
     * @param end 结束端口号
     * @return boolean true 校验成功 false 校验是吧
     */
    @JsonIgnore
    public static boolean checkPort(int begin, int end) {
        return 1025 < begin && begin < 65535 && 1025 < end && end < 65535 && end - begin < 30 && end -begin >= 0;
    }
    @JsonIgnore
    public void addUpstreamProxies(UpstreamProxy upstreamProxy) {
        upstreamProxies.offer(upstreamProxy);
    }

    // ip过滤List，格式如：[{ip:127.0.0.1/24,accept:REJECT},{192.168.1.1/32,accept:ACCEPT}],并且，跟进规则优先级，List应当有下标
    @JsonIgnore
    public static List<Map<String, String>> ipFilterListTEST() {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map1 = new HashMap<>();
        map1.put("ip", "192.168.31.1/24");//放行192.168.31.1-255局域网
        map1.put("rule", "ACCEPT");
        Map<String, String> map2 = new HashMap<>();
        map2.put("ip", "127.0.0.1/0");//本地拦截连接
        map2.put("rule", "REJECT");
        list.add(0, map1);
        list.add(1, map2);
        logger.debug("list: " + list.toString());
        return list;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Save / Load                                                           //
    ///////////////////////////////////////////////////////////////////////////

    //私有的，仅限创建时读取配置
    private static ServerContext loadConfig () {

        return load(yamlMapper, PROXY_SERVER_CONFIG);
    }

    public static ServerContext loadConfig (YAMLMapper mapper) {
        return load(mapper, PROXY_SERVER_CONFIG);
    }

    public static ServerContext load(YAMLMapper mapper, Path path) {
        //FileSystemUtils.requireFile(path.toFile());
        try {
            return mapper.readValue(path.toFile(), ServerContext.class);
        } catch (Exception e) {
            logger.info("读取服务器Context时出错,尝试新建默认配置文件: " +path);
            //新建一个默认的配置文件
            saveConfig ();
        }
        return getInstance();
    }

    /**
     * 保存配置到系统磁盘
     */
    public static void saveConfig () {
        save(getInstance(), yamlMapper, PROXY_SERVER_CONFIG);
    }

    //私有的
    private static void saveConfig (ServerContext serverContext, YAMLMapper mapper) {
        save(serverContext, mapper, PROXY_SERVER_CONFIG);
    }

    //私有的
    private static void save(ServerContext serverContext, YAMLMapper mapper, Path path) {

        File file = path.toFile();
        //先判断文件是否存在，如果不存在再判断父路径是否存在，如果不存在则创建父目录。
        if (!file.exists()&&!file.getParentFile().exists()){
            logger.info("新建目录: " +file.getParentFile());
            if(!file.getParentFile().mkdirs()) {
                throw new RuntimeException(new IOException());
            }
        }

        try {
            mapper.writeValue(path.toFile(), serverContext);
        } catch (Exception e) {
            logger.error("保存服务器Context出错！ Path: "+path+" \n"+e);
            throw new AppException("保存服务器Context出错！", e);
        }

    }

    @Override
    public String toString() {
        return "ServerContext{" +
            "serverBeginPort=" + serverBeginPort +
            ", serverEndPort=" + serverEndPort +
            ", timeout=" + timeout +
            ", provider='" + provider + '\'' +
            ", enableIpFilter=" + enableIpFilter +
            ", ipFilterList=" + ipFilterList +
            ", upstreamProxies=" + upstreamProxies +
            '}';
    }

}

