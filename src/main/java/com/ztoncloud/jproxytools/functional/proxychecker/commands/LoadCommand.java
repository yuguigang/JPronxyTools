package com.ztoncloud.jproxytools.functional.proxychecker.commands;



import com.ztoncloud.jproxytools.functional.proxychecker.components.entities.ProxyModel;
import com.ztoncloud.jproxytools.functional.proxychecker.gui.AlertBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.util.List;

/**
 * Loads a file or string onto a ListView in the form ip:port
 * 加载文件或者字符串到ListView
 */
public class LoadCommand {
    private  static final Logger log = LoggerFactory.getLogger(LoadCommand.class);

    /**
     * Manages the addition of one or more files onto the ListView
     * 管理向ListView添加一个或多个文件
     * @param view - Listview 显示添加文件后的显示视图控件
     * @param list - 要添加到视图中的文件列表（如果为null，则显示对话框）
     */
    public static void file(ListView<String> view, List<File> list) {
        //打开
        list = list == null ? FileCommand.getFilesToOpen() : list;
        log.info("读取文件list： "+list);
        if (list != null) {
            for (File file : list) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        addItem(line, view); // 校验格式和检查重复，仅在有效行时才添加到list
                    }
                    /*
                } catch (FileNotFoundException e) {
                    AlertBox.show(Alert.AlertType.INFORMATION, "文件没找到",
                            "文件: " + file.getName() + ", 不存在！");
                } catch (IOException e) {
                    AlertBox.show(Alert.AlertType.ERROR, "文件异常",
                            "无法读取文件 " + file.getName() + ". Error: " + e.getMessage());
                }

                     */
                } catch (FileNotFoundException e) {
                    AlertBox.show(Alert.AlertType.INFORMATION, null,
                        null);
                } catch (IOException e) {
                    AlertBox.show(Alert.AlertType.ERROR, null,
                        null);
                }
            }
        }

    }

    /**
     * Manages the addition of a single string onto the ListView
     * 管理向ListView添加单个字符串
     * @param string - The String to add to the ListView
     * @param view - The ListView to add the string onto.
     */
    public static void string(String string, ListView<String> view) {
        /*
        if(view.getItems().contains(string)) {
            AlertBox.show(Alert.AlertType.INFORMATION,"已加载",
                    "您输入的代理已加载，可以开始检查了！");
        } else if(!addItem(string, view)) { // make sure it was added
            AlertBox.show(Alert.AlertType.ERROR,"代理格式无效",
                    "必须以ip:port格式输入有效代理！");
        }

         */
        if(view.getItems().contains(string)) {
            AlertBox.show(Alert.AlertType.INFORMATION,null,
                null);
        } else if(!addItem(string, view)) { // make sure it was added
            AlertBox.show(Alert.AlertType.ERROR,null,
                null);
        }
    }

    /**
     *
     * 先校验字符串是否有效，再判断是否重复。格式为 ip:port
     * 通过校验后，将字符串添加到列表视图中，
     * @param string - 添加的字符串
     * @param view - ListView控件
     * @return Boolean - 添加成功 true ,失败 false
     */
    private static boolean addItem(String string, ListView<String> view) {
        //检验格式、检查重复
        if( (ProxyModel.isValidFormat(string)) && (!view.getItems().contains(string))) {
            return view.getItems().add(string);
        }
        return false;
    }
}
