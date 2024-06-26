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

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.lzhpo.logger.LoggerConstant;
import com.lzhpo.logger.LoggerFunctionRegistrar;
import com.lzhpo.logger.diff.DiffObjectResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
@UtilityClass
// spotless:off
public class LoggerContextHolder {

    private static final TransmittableThreadLocal<LoggerEvaluationContext> EVALUATION_CONTEXT = new TransmittableThreadLocal<>();
    private static final TransmittableThreadLocal<List<DiffObjectResult>> DIFF_OBJECT_CONTEXT = new TransmittableThreadLocal<>();

    /**
     * Get {@link #EVALUATION_CONTEXT}.
     *
     * @param elementKey the logger element key
     * @return {@link LoggerEvaluationContext}
     */
    public static LoggerEvaluationContext getContext(LoggerElementKey elementKey) {
        return Optional.ofNullable(EVALUATION_CONTEXT.get())
                .map(context -> {
                    initializeIfNecessary(elementKey, context);
                    return context;
                })
                .orElseGet(() -> {
                    LoggerEvaluationContext context = new LoggerEvaluationContext();
                    initializeIfNecessary(elementKey, context);
                    EVALUATION_CONTEXT.set(context);
                    log.debug("Created new context, current thread name: {}", Thread.currentThread().getName());
                    return context;
                });
    }

    /**
     * Get {@link #EVALUATION_CONTEXT}.
     *
     * @return {@link LoggerEvaluationContext}
     */
    public static LoggerEvaluationContext getContext() {
        return Optional.ofNullable(EVALUATION_CONTEXT.get()).orElseGet(LoggerEvaluationContext::new);
    }

    /**
     * Put variable into {@link #EVALUATION_CONTEXT}.
     *
     * @param name  the variable name
     * @param value the variable value
     */
    public static void putVariable(String name, Object value) {
        LoggerEvaluationContext context = getContext();
        context.setVariable(name, value);
        EVALUATION_CONTEXT.set(context);
        log.debug("Put {} variable, current thread name: {}", name, Thread.currentThread().getName());
    }

    /**
     * Set diff object result.
     *
     * @param diffObjectResult the diff object result
     */
    public static void putDiffResult(DiffObjectResult diffObjectResult) {
        List<DiffObjectResult> diffResults = getDiffResults();
        Collections.addAll(diffResults, diffObjectResult);
        DIFF_OBJECT_CONTEXT.set(diffResults);
        log.debug("Put 1 diff result, total has {} diff results, current thread name: {}", diffResults.size(), Thread.currentThread().getName());
    }

    /**
     * Get diff object result.
     *
     * @return the diff object result
     */
    public static List<DiffObjectResult> getDiffResults() {
        return Optional.ofNullable(DIFF_OBJECT_CONTEXT.get()).orElseGet(ArrayList::new);
    }

    /**
     * Lookup variable from {@link #EVALUATION_CONTEXT}.
     *
     * @param name the variable name
     * @return the variable value
     */
    public static Object lookupVariable(String name) {
        return getContext().lookupVariable(name);
    }

    /**
     * Clear {@link #EVALUATION_CONTEXT}.
     */
    public static void clearContext() {
        EVALUATION_CONTEXT.remove();
        DIFF_OBJECT_CONTEXT.remove();
        log.debug("Cleared context, current thread name: {}", Thread.currentThread().getName());
    }

    /**
     * Initialize context's fields if it has empty.
     *
     * @param elementKey {@link LoggerElementKey}
     * @param context    {@link LoggerEvaluationContext}
     */
    private static void initializeIfNecessary(LoggerElementKey elementKey, LoggerEvaluationContext context) {
        if (ObjectUtil.hasEmpty(context.getMethod(), context.getMethod(), context.getDiscoverer())) {
            context.setRootObject(elementKey.getRootObject());
            context.setMethod(elementKey.getMethod());
            context.setArguments(elementKey.getArguments());
            context.setDiscoverer(elementKey.getDiscoverer());
            context.setVariable(LoggerConstant.VARIABLE_RESULT, elementKey.getResult());
            LoggerFunctionRegistrar.registerFunction(context);
            log.debug("The context has null fields, initialized, current thread name: {}", Thread.currentThread().getName());
        }
    }
}
// spotless:on
