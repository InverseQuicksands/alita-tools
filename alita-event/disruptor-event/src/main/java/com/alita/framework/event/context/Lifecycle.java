/*
 * Copyright 2021 zhang liang<zhangliang0231@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alita.framework.event.context;

/**
 * disruptor生命周期接口.
 * <p>负责disruptor启动，停止.
 *
 * <p>可以放在{@link com.lmax.disruptor.LifecycleAware}接口中管理生命周期.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public interface Lifecycle {

    /**
     * disruptor 启动
     */
    void start();

    /**
     * disruptor 停止
     */
    void stop();

    /**
     * 是否已经启动.
     * @return 是否已经启动.
     */
    boolean isRunning();
}
