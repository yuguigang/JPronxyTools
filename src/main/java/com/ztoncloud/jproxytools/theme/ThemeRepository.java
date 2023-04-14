/* SPDX-License-Identifier: MIT */
package com.ztoncloud.jproxytools.theme;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import com.ztoncloud.jproxytools.event.DefaultEventBus;
import com.ztoncloud.jproxytools.event.ThemeEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ThemeRepository {

    private static final Comparator<SamplerTheme> THEME_COMPARATOR = Comparator.comparing(SamplerTheme::getName);
    //内部主题
    private final List<SamplerTheme> internalThemes = Arrays.asList(
            new SamplerTheme(new PrimerLight()),
            new SamplerTheme(new PrimerDark()),
            new SamplerTheme(new NordLight()),
            new SamplerTheme(new NordDark())
    );
    //外部主题
    private final List<SamplerTheme> externalThemes = new ArrayList<>();
    /**
     * 所有主题，包括内部主题和自定义外部主题
     * @return
     */
    public List<SamplerTheme> getAll() {
        var list = new ArrayList<>(internalThemes);
        list.addAll(externalThemes);//加入外部主题
        return list;
    }

    public SamplerTheme addFromFile(File file) {
        Objects.requireNonNull(file);

        if (!isFileValid(file.toPath())) {
            throw new RuntimeException("Invalid CSS file \"" + file.getAbsolutePath() + "\".");
        }

        // creating GUI dialogs is hard, so we just obtain theme name from the file name :)
        String filename = file.getName();
        String themeName = Arrays.stream(filename.replace(".css", "").split("[-_]"))
                .map(s -> !s.isEmpty() ? s.substring(0, 1).toUpperCase() + s.substring(1) : "")
                .collect(Collectors.joining(" "));

        var theme = new SamplerTheme(Theme.of(themeName, file.toString(), filename.contains("dark")));

        if (!isUnique(theme)) {
            throw new RuntimeException("A theme with the same name or user agent stylesheet already exists in the repository.");
        }


        externalThemes.add(theme);
        externalThemes.sort(THEME_COMPARATOR);
        DefaultEventBus.getInstance().publish(new ThemeEvent(ThemeEvent.EventType.THEME_ADD));

        return theme;
    }

    public void remove(SamplerTheme theme) {
        Objects.requireNonNull(theme);
        externalThemes.removeIf(t -> Objects.equals(t.getName(), theme.getName()));
        DefaultEventBus.getInstance().publish(new ThemeEvent(ThemeEvent.EventType.THEME_REMOVE));
    }

    public boolean isFileValid(Path path) {
        Objects.requireNonNull(path);
        return !Files.isDirectory(path, NOFOLLOW_LINKS) &&
                Files.isRegularFile(path, NOFOLLOW_LINKS) &&
                Files.isReadable(path) &&
                path.getFileName().toString().endsWith(".css");
    }

    public boolean isUnique(SamplerTheme theme) {
        Objects.requireNonNull(theme);
        for (SamplerTheme t : getAll()) {
            if (Objects.equals(t.getName(), theme.getName()) || Objects.equals(t.getPath(), theme.getPath())) {
                return false;
            }
        }
        return true;
    }
}
