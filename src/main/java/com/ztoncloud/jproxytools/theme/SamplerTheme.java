/* SPDX-License-Identifier: MIT */
package com.ztoncloud.jproxytools.theme;


import static com.ztoncloud.jproxytools.theme.ThemeManager.APP_STYLESHEETS;
import static com.ztoncloud.jproxytools.theme.ThemeManager.DUMMY_STYLESHEET;
import static com.ztoncloud.jproxytools.theme.ThemeManager.PROJECT_THEMES;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import atlantafx.base.theme.Theme;
import com.ztoncloud.jproxytools.Utils.FileResource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Application;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * *{@link主题}装饰器可以绕过一些JavaFX CSS限制。
 * *JavaFX不提供主题API。此外，没有这样的概念，它被称为
 * “用户代理样式表”。有两种方法可以更改平台样式表：
 * <ul>
 * <li>{@link Scene#setUserAgentStylesheet(String)}</li>
 * <li>{@link Application#setUserAgentStylesheet(String)}</li>
 * </ul>
 *
 * *第一种是可观察的，而第二种则不是。两者都无法提供热重新加载
 * {@link 'CSSFX'}。这意味着如果我们设置一些CSS文件作为平台样式表
 * 然后更改（重新编译）此文件，不会发生任何事情。
 * 为了解决这个问题，我们使用开发模式技巧。在这种模式下，我们设置虚拟样式表
 * 将真实主题CSS添加到{@link Scene#getStylesheets（）}
 * *以及应用程序自己的样式表。当CSSFX检测到任何CSS文件更改时，它会强制
 * 可观察列表更新，从而用更改重新加载CSS。
 * 另外，请注意，在使用{@link Application#setUserAgentStylesheet（String）}时，某些样式不会应用。
 * 例如，JavaFX忽略Ikonli-fx图标颜色和-fx图标大小属性，但未知
 * 原因是当通过{@link Scene#getStylesheets（）}设置完全相同的样式表时，它们不会被忽略。
 */
public final class SamplerTheme implements Theme {
    private  final Logger log = LoggerFactory.getLogger(this.toString());

    private final boolean IS_DEV_MODE = false;

    private static final int PARSE_LIMIT = 250;
    private static final Pattern COLOR_PATTERN =
            Pattern.compile("\s*?(-color-(fg|bg|accent|success|danger)-.+?):\s*?(.+?);");

    private final Theme theme;

    private FileTime lastModified;
    private Map<String, String> colors;

    public SamplerTheme(Theme theme) {
        Objects.requireNonNull(theme);

        if (theme instanceof SamplerTheme) {
            throw new IllegalArgumentException("Sampler theme must not be wrapped into itself.");
        }

        this.theme = theme;
    }

    @Override
    public String getName() {
        return theme.getName();
    }

    //Application.setUserAgentStylesheet()方法 仅接受URL（或URL字符串表示），
    //任何外部文件路径都必须有“file://”前缀
    @Override
    public String getUserAgentStylesheet() {
        //不需要IS_DEV_MODE，IS_DEV_MODE直接设置flash；
        return IS_DEV_MODE ? DUMMY_STYLESHEET : getThemeFile().toURI().toString();
    }

    @Override
    public boolean isDarkMode() {
        return theme.isDarkMode();
    }

    public Set<String> getAllStylesheets() {
        return IS_DEV_MODE ? merge(getThemeFile().toURI().toString(), APP_STYLESHEETS) : Set.of(APP_STYLESHEETS);
    }

    // Checks whether wrapped theme is a project theme or user external theme.
    // 检查 主题是项目主题还是使用的外部主题。
    public boolean isProjectTheme() {
        return PROJECT_THEMES.contains(theme.getClass());
    }

    // Tries to parse theme CSS and extract conventional looked-up colors. There are few limitations:
    // - minified CSS files are not supported
    // - only first PARSE_LIMIT lines will be read
    // 尝试解析主题CSS并提取传统的查找颜色。有一些限制：
    // -不支持缩小的CSS文件
    // -仅读取第一行PARSE_LIMIT
    public Map<String, String> parseColors() throws IOException {
        FileResource file = getThemeFile();
        return file.internal() ? parseColorsForClasspath(file) : parseColorsForFilesystem(file);
    }

    private Map<String, String> parseColorsForClasspath(FileResource file) throws IOException {
        // classpath resources are static, no need to parse project theme more than once
        // 类路径资源是静态的，不需要多次解析项目主题
        if (colors != null) { return colors; }

        try (var br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            colors = parseColors(br);
        }

        return colors;
    }

    private Map<String, String> parseColorsForFilesystem(FileResource file) throws IOException {
        // return cached colors if file wasn't changed since the last read
        // 如果文件自上次读取后未更改，则返回缓存的颜色
        FileTime fileTime = Files.getLastModifiedTime(file.toPath(), NOFOLLOW_LINKS);
        if (Objects.equals(fileTime, lastModified)) {
            return colors;
        }

        try (var br = new BufferedReader(new InputStreamReader(file.getInputStream(), UTF_8))) {
            colors = parseColors(br);
        }

        // don't save time before parsing is finished to avoid
        // remembering operation that might end up with an error
        //不要在解析完成之前节省时间以避免
        // 记住可能导致错误的操作
        lastModified = fileTime;

        return colors;
    }

    private Map<String, String> parseColors(BufferedReader br) throws IOException {
        Map<String, String> colors = new HashMap<>();

        String line;
        int lineCount = 0;

        while ((line = br.readLine()) != null) {
            Matcher matcher = COLOR_PATTERN.matcher(line);
            if (matcher.matches()) {
                colors.put(matcher.group(1), matcher.group(3));
            }

            lineCount++;
            if (lineCount > PARSE_LIMIT) { break; }
        }

        return colors;
    }

    public String getPath() {
        return getThemeFile().toPath().toString();
    }

    private FileResource getThemeFile() {
        //是否是项目里面的主题文件，如果依赖了io.github.mkpaz:atlantafx-base，那么主题文件在依赖包里面。
        //如果不是项目主题而是外部主题，那么这里返回扩展目录
        if (!isProjectTheme()) {
            return FileResource.external(theme.getUserAgentStylesheet());
        }

        FileResource classpathTheme = FileResource.internal(theme.getUserAgentStylesheet(), Theme.class);


        /*
        if (!IS_DEV_MODE) { return classpathTheme; }

         */
        log.debug("主题文件路径："+classpathTheme.toPath());
        return classpathTheme;
    }

    public Theme unwrap() {
        return theme;
    }

    @SafeVarargs
    private <T> Set<T> merge(T first, T... arr) {
        var set = new LinkedHashSet<T>();
        set.add(first);
        Collections.addAll(set, arr);
        return set;
    }
}
