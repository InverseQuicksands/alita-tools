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

import org.springframework.beans.factory.Aware;

/**
 * DisruptorContext 上下文扩展接口.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang liang</a>
 * @version v1.0
 */
public interface DisruptorContextAware extends Aware {

    void setDisruptorContext(DisruptorContext disruptorContext);
}
