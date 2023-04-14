/*
 * Copyright 2022 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ztoncloud.jproxytools.functional.proxypanel.JProxy.rule;

/**
 * 要采取的行动
 */
public enum Action {

    /**
     * 接受请求并将其传递给下一个处理程序。
     *
     * <p>
     *支持的法：通道和数据
     */
    ACCEPT,

    /**
     * 在不关闭连接的情况下删除请求。
     *
     * <p>
     * 支持的用法：数据
     */
    DROP,

    /**
     * 删除请求并关闭连接。
     *
     * <p>
     * 支持的用途：通道和数据
     */
    REJECT,
    /**
     * 直接连接，不通过前置代理和代理
     */
    DIRECT,
    /**
     * 通过代理连接，包含前置代理
     */
    PROXY,

    /**
     * 仅仅通过前置代理连接，此时不同代理
     */
    PRE_PROXIES,

    /**
     * 默认
     */
    DEFAULT,

}
