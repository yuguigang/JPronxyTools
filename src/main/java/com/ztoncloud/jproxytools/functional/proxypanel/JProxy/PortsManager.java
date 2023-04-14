package com.ztoncloud.jproxytools.functional.proxypanel.JProxy;

import com.ztoncloud.jproxytools.functional.proxypanel.JProxy.entity.PortModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author yugang
 * @create 2023/1/5 7:37
 */
public class PortsManager {

    private  static final Logger log = LoggerFactory.getLogger(PortsManager.class);

  private static   PortsManager instance ;

    private static List<PortModel> portModels ;
  private PortsManager () {};

  public static PortsManager getInstance()  {
      if (instance == null) {
        instance = new PortsManager();
        portModels = new  ArrayList<>();
      }
      return instance;
    }



    /**
     * 创建端口模型
     *
     * @return {@link List}<{@link PortModel}>
     */
    public void createPortModels () {
        //this.portModels = new ArrayList<>();


    }

    /**
     * 添加端口模式
     *
     * @param portModel 端口模式
     */
    public void addToPortModels(PortModel portModel) {
        portModels.add(portModel);
    }

    /**
     * 得到所有端口模式
     *
     * @return {@link List}<{@link PortModel}>
     */
    public List<PortModel> getAllPortModels() {
        return portModels;
    }

    /**
     * 根据端口 port 获取端口模型存储的数据
     *
     * @param port 端口
     * @return {@link PortModel}
     */
    public PortModel getPortModel(Integer port) {
        PortModel portModel = new PortModel();

        portModels.stream().filter(o -> o.getLocalPort().equals(port)).collect(Collectors.toList())
                .forEach(s -> {
                            portModel.setLocalPort(s.getLocalPort());
                            portModel.setRemotePort(s.getRemotePort());
                            portModel.setLatency(s.getLatency());
                            portModel.setRemoteProtocol(s.getRemoteProtocol());
                            portModel.setRemoteIP(s.getRemoteIP());
                            portModel.setRemoteUserName(s.getRemoteUserName());
                            portModel.setRemoteUserPassword(s.getRemoteUserPassword());


                        });
        return portModel;
    }

    /**
     * 设置指定端口到模型列表
     *
     * @param port      端口
     * @param model 端口模式
     */
    public void setPortModels (Integer port ,PortModel model) {
        portModels.stream().filter(o -> o.getLocalPort().equals(port))
                .forEach(f -> {
                    f.setLocalPort(model.getLocalPort());
                    f.setRemotePort(model.getRemotePort());
                    f.setLatency(model.getLatency());
                    f.setRemoteProtocol(model.getRemoteProtocol());
                    f.setRemoteIP(model.getRemoteIP());
                    f.setRemoteUserName(model.getRemoteUserName());
                    f.setRemoteUserPassword(model.getRemoteUserPassword());
                });

    }
}
