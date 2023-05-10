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

package com.alita.framework.event.autoconfigure;

import com.alita.framework.event.context.EventHandlerDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "spring.disruptor")
public class DisruptorProperties {

    /** Enable Disruptor. */
    private boolean enabled = false;

    /** RingBuffer缓冲区大小, 默认 1024 */
    private int ringBufferSize = 1024;

    /** 消息出来责任链 */
    private List<EventHandlerDefinition> handlerDefinitions = new ArrayList<EventHandlerDefinition>();


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getRingBufferSize() {
        return ringBufferSize;
    }

    public void setRingBufferSize(int ringBufferSize) {
        this.ringBufferSize = ringBufferSize;
    }

    public List<EventHandlerDefinition> getHandlerDefinitions() {
        return handlerDefinitions;
    }

    public void setHandlerDefinitions(List<EventHandlerDefinition> handlerDefinitions) {
        this.handlerDefinitions = handlerDefinitions;
    }

}
