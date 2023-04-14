/* SPDX-License-Identifier: MIT */
package com.ztoncloud.jproxytools.event;

import java.util.function.Consumer;

public interface EventBus {

    /**
     * Subscribe to an event type
     * 订阅事件类型
     *
     * @param eventType the event type, can be a super class of all events to subscribe.
     *                  事件类型，可以是要订阅的所有事件的超类。
     * @param subscriber the subscriber which will consume the events.
     *                   将使用事件的订户。
     * @param <T> 事件类型class。
     */
    <T extends Event> void subscribe(Class<? extends T> eventType, Consumer<T> subscriber);

    /**
     * Unsubscribe from all event types.
     * 取消订阅所有事件类型。
     *
     * @param subscriber the subscriber to unsubscribe.要取消订阅的订户。
     */
    <T extends Event> void unsubscribe(Consumer<T> subscriber);

    /**
     * Unsubscribe from an event type.
     * 取消订阅事件类型。
     *
     * @param eventType the event type, can be a super class of all events to unsubscribe.
     *                  事件类型，可以是要取消订阅的所有事件的超类。
     * @param subscriber the subscriber to unsubscribe.要取消订阅的订户。
     * @param <T> 事件类型 class.
     */
    <T extends Event> void unsubscribe(Class<? extends T> eventType, Consumer<T> subscriber);

    /**
     * Publish an event to all subscribers.
     * 向所有订阅方发布事件。
     * <p>
     * The event type is the class of <code>event</code>. The event is published to all consumers which subscribed to
     * this event type or any super class.
     * 事件类型是Class的<code>event</code>。事件将发布给订阅的所有消费者
     * 此事件类型或任何超级类。
     *
     * @param event the event.
     */
    <T extends Event> void publish(T event);

}