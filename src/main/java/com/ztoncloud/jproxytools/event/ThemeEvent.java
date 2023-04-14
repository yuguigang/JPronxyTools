package com.ztoncloud.jproxytools.event;

public class ThemeEvent extends Event {

    private final EventType eventType;

    public ThemeEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public enum EventType {
        // 主题可以同时更改基本字体大小和颜色
        THEME_CHANGE,
        // 仅更改字体大小或系列
        FONT_CHANGE,
        // 颜色更改
        COLOR_CHANGE,
        // 新主题加入或者移除
        THEME_ADD,
        THEME_REMOVE
    }

    @Override
    public String toString() {
        return "ThemeEvent{" +
                "eventType=" + eventType +
                "} " + super.toString();
    }
}
