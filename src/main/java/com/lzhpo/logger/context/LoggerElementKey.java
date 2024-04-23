/*
 * Copyright lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzhpo.logger.context;

import java.lang.reflect.Method;
import java.util.Optional;
import lombok.Getter;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * @author lzhpo
 */
// spotless:off
@Getter
public class LoggerElementKey {

    private final Object rootObject;
    private final Method method;
    private final Object result;
    private final Object[] arguments;
    private final ParameterNameDiscoverer discoverer;

    public LoggerElementKey(Object rootObject, Method method, Object result, Object[] arguments, ParameterNameDiscoverer discoverer) {
        this.arguments = arguments;
        this.result = result;
        this.method = Optional.ofNullable(method).orElseThrow(() -> new IllegalArgumentException("method is null"));
        this.rootObject = Optional.ofNullable(rootObject).orElseThrow(() -> new IllegalArgumentException("rootObject is null"));
        this.discoverer = Optional.ofNullable(discoverer).orElseThrow(() -> new IllegalArgumentException("discoverer is null"));
    }
}
// spotless:on
