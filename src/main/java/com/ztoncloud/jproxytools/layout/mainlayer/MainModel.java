
package com.ztoncloud.jproxytools.layout.mainlayer;



import com.ztoncloud.jproxytools.layout.page.Page;
import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class MainModel {

    public enum SubLayer {
        PAGE,
        SOURCE_CODE
    }
    //title 只读
    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper();
    //搜索文本
    private final StringProperty searchText = new SimpleStringProperty();
    //选择页面
    private final ReadOnlyObjectWrapper<Class<? extends Page>> selectedPage = new ReadOnlyObjectWrapper<>();
    //主题切换
    private final ReadOnlyBooleanWrapper themeChangeToggle = new ReadOnlyBooleanWrapper();
    //源代码切换
    private final ReadOnlyBooleanWrapper sourceCodeToggle = new ReadOnlyBooleanWrapper();
    //当前Sub层
    private final ReadOnlyObjectWrapper<SubLayer> currentSubLayer = new ReadOnlyObjectWrapper<>(
        SubLayer.PAGE);

    ///////////////////////////////////////////////////////////////////////////
    // Properties  属性                                                          //
    ///////////////////////////////////////////////////////////////////////////

    public StringProperty searchTextProperty() {
        return searchText;
    }

    public ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty themeChangeToggleProperty() {
        return themeChangeToggle.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty sourceCodeToggleProperty() {
        return sourceCodeToggle.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Class<? extends Page>> selectedPageProperty() {
        return selectedPage.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<SubLayer> currentSubLayerProperty() {
        return currentSubLayer.getReadOnlyProperty();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands     命令                                                         //
    ///////////////////////////////////////////////////////////////////////////

    public void setPageData(ObservableValue<String> text, boolean canChangeTheme, boolean canDisplaySource) {
        //title.set(Objects.requireNonNull(text));
        title.bind(text);
        themeChangeToggle.set(canChangeTheme);
        sourceCodeToggle.set(canDisplaySource);
    }
    /*
    public void setPageData(String text, boolean canChangeTheme, boolean canDisplaySource) {
        //title.set(Objects.requireNonNull(text));
        title.set(text);
        themeChangeToggle.set(canChangeTheme);
        sourceCodeToggle.set(canDisplaySource);
    }

     */

    public void navigate(Class<? extends Page> page) {
        selectedPage.set(Objects.requireNonNull(page));
        currentSubLayer.set(SubLayer.PAGE);
    }

    public void nextSubLayer() {
        var old = currentSubLayer.get();
        currentSubLayer.set(old == SubLayer.PAGE ? SubLayer.SOURCE_CODE : SubLayer.PAGE);
    }
}
