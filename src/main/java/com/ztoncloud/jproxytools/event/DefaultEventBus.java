/* SPDX-License-Identifier: MIT */
package com.ztoncloud.jproxytools.event;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Simple event bus implementation.
 * 简单的事件总线实现。
 * Subscribe and publish events. Events are published in channels distinguished by event type.
 * Channels can be grouped using an event type hierarchy.
 * 订阅和发布事件。事件发布在按事件类型区分的通道中。
 * 可以使用事件类型层次结构对通道进行分组。
 * You can use the default event bus instance {@link #getInstance}, which is a singleton,
 * or you can create one or multiple instances of {@link DefaultEventBus}.
 * 您可以使用默认的事件总线实例{@link#getInstance}，它是一个单例，
 * 或者您可以创建一个或多个{@link-DefaultEventBus}实例。
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class DefaultEventBus implements EventBus {

    public DefaultEventBus() {}

    private final Map<Class<?>, Set<Consumer>> subscribers = new ConcurrentHashMap<>();

    @Override
    public <E extends Event> void subscribe(Class<? extends E> eventType, Consumer<E> subscriber) {
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(subscriber);

        Set<Consumer> eventSubscribers = getOrCreateSubscribers(eventType);
        eventSubscribers.add(subscriber);
    }

    private <E> Set<Consumer> getOrCreateSubscribers(Class<E> eventType) {
        Set<Consumer> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers == null) {
            eventSubscribers = new CopyOnWriteArraySet<>();
            subscribers.put(eventType, eventSubscribers);
        }
        return eventSubscribers;
    }

    @Override
    public <E extends Event> void unsubscribe(Consumer<E> subscriber) {
        Objects.requireNonNull(subscriber);

        subscribers.values().forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }

    @Override
    public <E extends Event> void unsubscribe(Class<? extends E> eventType, Consumer<E> subscriber) {
        Objects.requireNonNull(eventType);
        Objects.requireNonNull(subscriber);

        subscribers.keySet().stream()
                .filter(eventType::isAssignableFrom)
                .map(subscribers::get)
                .forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }

    @Override
    public <E extends Event> void publish(E event) {
        Objects.requireNonNull(event);

        Class<?> eventType = event.getClass();
        subscribers.keySet().stream()
                .filter(type -> type.isAssignableFrom(eventType))
                .flatMap(type -> subscribers.get(type).stream())
                .forEach(subscriber -> publish(event, subscriber));
    }

    private <E extends Event> void publish(E event, Consumer<E> subscriber) {
        try {
            subscriber.accept(event);
        } catch (Exception e) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class InstanceHolder {

        private static final DefaultEventBus INSTANCE = new DefaultEventBus();
    }

    public static DefaultEventBus getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
