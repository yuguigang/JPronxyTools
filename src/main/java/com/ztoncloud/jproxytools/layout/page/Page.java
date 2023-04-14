
package com.ztoncloud.jproxytools.layout.page;

import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;

public interface Page {

    int PAGE_HGAP = 30;
    int PAGE_VGAP = 30;

    //这里使用有感知类型ObservableValue<String>，在更改语言后不用重启app。用String类型需要重启app。
    ObservableValue<String> getName();


    Parent getView();

    boolean canDisplaySourceCode();

    boolean canChangeThemeSettings();

    void reset();
}
