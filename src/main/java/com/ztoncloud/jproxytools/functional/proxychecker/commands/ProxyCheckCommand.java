package com.ztoncloud.jproxytools.functional.proxychecker.commands;



import com.ztoncloud.jproxytools.functional.proxychecker.components.ProxyCheckerSettings;
import com.ztoncloud.jproxytools.functional.proxychecker.components.RequestAPI;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyModel;
import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyStatus;
import com.ztoncloud.jproxytools.functional.proxychecker.gui.ProgressFrom;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * 代理检查核心，获取ListView中的每个代理值，执行检查并相应更新TableView。
 */
public class ProxyCheckCommand {
    private static final Logger logger = LoggerFactory.getLogger(ProxyCheckCommand.class);

    //返回当前线程的线程组及其子组中活动线程的估计数。
    private static final int thCount_start = Thread.activeCount();
    private static final ProxyCheckerSettings settings = new ProxyCheckerSettings();//这里会一直读取磁盘上config

    //创建一个固定线程池,默认100，newFixedThreadPool是一个固定线程池
    private static ExecutorService executorService ;

    /**
     * Setups the thread pool and launches asynchronous checks on the list of proxies
     * 设置线程池并对代理列表启动异步检查
     * @param listView - The ListView containing the proxies that will be checked 包含要检查的代理的ListView
     * @param tableView - The TableView that will be updated with the status of the proxies 将使用代理的状态更新的TableView
     */
    public static void check(ListView<String> listView, TableView<ProxyModel> tableView) {
        logger.info(settings.toString());
         executorService = Executors.newFixedThreadPool(settings.getThreads());
        //读取设的Type,默认HTTP。
        //Proxy.Type type = Proxy.Type.valueOf(ProxyCheckerSettings.getValue("ProxyType","HTTP"));
        for (String proxy : listView.getItems()) {
            //提交Runnable任务以执行，并返回表示该任务的Future。Future的get方法在成功完成后将返回null。
            executorService.submit(
                    new Checker(
                            new ProxyModel(   //从ListView首次创建Proxy对象,这里会将ip:port格式解析扯ProxyModel
                                    proxy,
                                    settings.getProxyType()
                                    //settings.getProxyType()
                            ),
                            tableView
                    )
            );
        }
        //关闭线程池，执行提交的任务，但不接受新任务。并且不会等待线程执行完毕。如需等待完成后关闭，需awaitTermination方法。
        executorService.shutdown();
    }

    /**
     * @return Boolean - 检查线程是否在运行中。。。
     */
    public static boolean isRunning() {
        return (Thread.activeCount() - thCount_start) != 0;
    }

    /**
     *
     * 异步检查每个代理并更新TableView的任务
     */
    private static class Checker implements Runnable {

        private final ProxyModel proxyModel;
        private final TableView<ProxyModel> tableView;

        /**
         *
         * @param proxyModel - 需要检查的代理对象
         * @param tableView table控件
         */
        public Checker(ProxyModel proxyModel, TableView<ProxyModel> tableView) {

            this.proxyModel = proxyModel;
            this.tableView = tableView;
        }

        @Override
        public void run() {
            RequestAPI requestAPI = new RequestAPI(settings);
            java.net.Proxy proxy = new java.net.Proxy(
                    this.proxyModel.getProxyType(),
                    new InetSocketAddress(
                            this.proxyModel.getIp(),
                            this.proxyModel.getPort()
                    )
            );

            Pair<HttpURLConnection, Long> pair = requestAPI.connect(proxy);
            //logger.info("获取返回值 key："+pair.getKey()+" value: "+pair.getValue());
            if(pair != null) {

                try {
                    this.proxyModel.setProxyStatus(ProxyStatus.ALIVE);

                    RequestAPI.Response response = requestAPI.getResponse(pair.getKey());
                    logger.info("connected ip: "+response.connected_ip+
                            " country: "+response.country+
                            " anonymity: "+response.anonymity
                            );
                    this.proxyModel.setProxyAnonymity(response.anonymity);
                    this.proxyModel.setCountry(response.country);
                    this.proxyModel.setResponseTime(pair.getValue() + " (ms)");


                } catch (Exception e) {
                    //logger.error("返回错误： "+e);
                    this.proxyModel.setProxyStatus(ProxyStatus.DEAD);
                    this.proxyModel.setProxyAnonymity(null);
                }
            } else {

                this.proxyModel.setProxyStatus(ProxyStatus.DEAD);
                this.proxyModel.setProxyAnonymity(null);
            }


            // 这必须在另一个线程上完成
            Platform.runLater(()-> tableView.getItems().add(this.proxyModel));
        }
    }
    //关闭线程池
    private static void shutdownAndAwaitTermination(ExecutorService pool , ProgressFrom progressFrom ) {

        pool.shutdown(); // 禁止新任务
        try {
            boolean b = pool.awaitTermination(30, TimeUnit.SECONDS);
            // 等待现有任务终止
            if (!b) {
                pool.shutdownNow(); //取消当前正在执行的任务
                boolean bc = pool.awaitTermination(30, TimeUnit.SECONDS);
                logger.info("线程池状态bc3： "+bc);
                // 等待任务响应取消
                if (!bc)
                    logger.error("线程池没有关闭！");
            }
        } catch (InterruptedException ie) {
            // 如果当前线程也中断，则取消
            pool.shutdownNow();
            // 保留中断状态
            Thread.currentThread().interrupt();

        }
        if (progressFrom!=null){
            //ui线程关闭进度条
            Platform.runLater(progressFrom::ProgressBarClose);

        }
    }
    public static void stop(ProgressFrom progressFrom) {
        if (isRunning()) {
            shutdownAndAwaitTermination(executorService,progressFrom);
        }
    }

}
