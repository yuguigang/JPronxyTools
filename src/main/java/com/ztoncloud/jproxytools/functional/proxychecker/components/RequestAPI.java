package com.ztoncloud.jproxytools.functional.proxychecker.components;



import com.google.gson.Gson;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyAnonymity;
import javafx.util.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Manages and makes a request to the API server
 * 管理API服务器并向其发出请求
 */
public class RequestAPI {
    private  static final Logger logger = LoggerFactory.getLogger(RequestAPI.class);
    //private static final Logger logger = LogManager.getLogger(RequestAPI.class);
    ProxyCheckerSettings settings;
    /**
     * API链接
     */
    private String api_url = "http://api.proxychecker.co/";

    /**
     *
     * API服务器响应模型
     */
    public class Response {

        public String ip;
        public String country;
        public ProxyAnonymity anonymity;
        public String connected_ip;

    }

    /**
     *
     * 新的RequestAPI对象，以发出API请求
     *
     */
    public RequestAPI(ProxyCheckerSettings settings) {
        this.settings = settings;
    }

    /**
     * 将一个json解析到Response.class模型
     * @param json - String to parse
     * @return Response
     */
    public Response getResponse(String json) {
        return new Gson().fromJson(json, Response.class);
    }

    /**
     * 将HttpURLConnection对象获取的web响应，转换成String,再解析成Response.class模型
     * 注意：响应可能是很大的网页。
     * @param connection - HttpURLConnection that has been connected
     * @return Response
     * @throws IOException
     */
    public Response getResponse(HttpURLConnection connection) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        return new Gson().fromJson(sb.toString(), Response.class);
    }

    /**
     * 给定的代理尝试连接到API服务器，
     * 将连接作为key，响应延时作为value存储在javafx.util.Pair中。
     * @param proxy - 用于连接API服务器的代理
     * @return 如果超时或者无法连接，value为null
     * Pair和 Map的区别，Map的key、v是一对一的，而Pair一个key可以对多个值。。
     */
    public Pair<HttpURLConnection, Long> connect(Proxy proxy) {
        try {
            HttpURLConnection connection;
            //如果没有提供proxy，则使用默认url，api_url = "http://api.proxychecker.co/"
            if(proxy == null) {

                connection = (HttpURLConnection) new URL(this.api_url).openConnection();
            } else {
                logger.debug("开始使用代理连接API服务器: Address: {}  Type: {} ",proxy.address(),proxy.type());
                //反之则通过proxy连接http://api.proxychecker.co/?ip=150.25.223.369，
                URL url = new URL(this.get_query_url(settings.getIp()));
                //URL url = new URL("http://api.proxychecker.co/?ip=150.25.223.369");
                logger.debug("URL: "+url);
                connection = (HttpURLConnection) url.openConnection(proxy);
                //connection = (HttpURLConnection) new URL(this.get_query_url(settings.getIp())).openConnection(proxy);
            }
            connection.setRequestProperty("User-Agent", "Proxy Checker v." + /*Settings.getApplicationVersion()*/"1.2" +
                    " - (proxychecker.co) : " + System.getProperty("os.name") +
                    " v." + System.getProperty("os.version"));
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(settings.getTimeout());
            connection.setReadTimeout(settings.getTimeout());

            long startTime = System.currentTimeMillis();
            connection.connect();
            long endTime = System.currentTimeMillis();
            //将管道和延时信息返回Pair。连接connection作为key，延时作为value
            return new Pair<>(connection, (endTime - startTime));
        } catch (IOException e) {
            if (proxy != null) {
                logger.debug("代理 {} Type: {} 连接异常！", proxy.address(), proxy.type());
            }
            return null;
        }
    }

    /**
     * Get the API url to Query based on option ip parameter.
     * 根据选项ip参数获取要查询的API url。
     * @param ip - The current users IP address
     * @return String - API url to query
     */
    private String get_query_url(String ip) {

        if (ip == null) {
            return api_url;
        } else {
            return api_url + "?ip=" + ip;
        }
    }

}
