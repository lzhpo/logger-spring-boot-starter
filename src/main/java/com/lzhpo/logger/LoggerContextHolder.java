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
package com.lzhpo.logger;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.lang.reflect.Method;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * @author lzhpo
 */
// spotless:off
@Slf4j
@UtilityClass
public class LoggerContextHolder {

    private static final TransmittableThreadLocal<LoggerEvaluationContext> EVALUATION_CONTEXT = new TransmittableThreadLocal<>();

    /**
     * Get {@link #EVALUATION_CONTEXT}.
     *
     * @param rootObject the root object
     * @param method     the method
     * @param result     the result
     * @param args       the arguments
     * @param discoverer the parameter name discoverer
     * @return {@link LoggerEvaluationContext}
     */
    public static LoggerEvaluationContext getContext(Object rootObject, Method method, Object result, Object[] args, ParameterNameDiscoverer discoverer) {
        return Optional.ofNullable(EVALUATION_CONTEXT.get()).orElseGet(() -> {
            LoggerEvaluationContext context = new LoggerEvaluationContext(rootObject, method, args, discoverer);
            context.setVariable(LoggerConstant.VARIABLE_RESULT, result);
            LoggerFunctionRegistrar.registerFunction(context);
            EVALUATION_CONTEXT.set(context);
            log.debug("The evaluation context is null, created new context, current thread name: {}", Thread.currentThread().getName());
            return context;
        });
    }

    /**
     * Put variable into {@link #EVALUATION_CONTEXT}.
     *
     * @param name  the variable name
     * @param value the variable value
     */
    public static void putVariable(String name, Object value) {
        StandardEvaluationContext context = EVALUATION_CONTEXT.get();
        Assert.notNull(context, "The evaluation context is null");
        context.setVariable(name, value);
    }

    /**
     * Lookup variable from {@link #EVALUATION_CONTEXT}.
     *
     * @param name the variable name
     * @return the variable value
     */
    public static Object lookupVariable(String name) {
        StandardEvaluationContext context = EVALUATION_CONTEXT.get();
        Assert.notNull(context, "The evaluation context is null");
        return context.lookupVariable(name);
    }

    /**
     * Clear {@link #EVALUATION_CONTEXT}.
     */
    public static void clearContext() {
        EVALUATION_CONTEXT.remove();
        log.debug("Cleared evaluation context, current thread name: {}", Thread.currentThread().getName());
    }
}
// spotless:on
