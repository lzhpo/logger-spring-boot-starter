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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lzhpo.logger.annotation.LoggerFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.util.StringUtils;

/**
 * @author lzhpo
 */
@Slf4j
public class LoggerEvaluationContext extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    /**
     * Get {@link Expression} of {@code conditionExpression}.
     *
     * @param conditionExpression the condition expression
     * @param targetObject        the target object
     * @param targetMethod        the target method
     * @return {@link Expression}
     */
    public Expression getExpression(String conditionExpression, Object targetObject, Method targetMethod) {
        AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(targetMethod, targetObject.getClass());
        return getExpression(expressionCache, annotatedElementKey, conditionExpression);
    }

    /**
     * Evaluate {@code conditionExpression} and get result value.
     *
     * @param conditionExpression the condition expression
     * @param targetObject        the target object
     * @param targetMethod        the target method
     * @param args                the args
     * @return the evaluated result
     */
    public String evaluateExpression(String conditionExpression, Object targetObject, Method targetMethod, Object[] args) {
        if (!StringUtils.hasText(conditionExpression)) {
            return conditionExpression;
        }

        EvaluationContext evaluationContext = createEvaluationContext(targetObject, targetMethod, args);
        return getExpression(conditionExpression, targetObject, targetMethod).getValue(evaluationContext, String.class);
    }

    /**
     * Create registered {@link LoggerFunction}'s {@link EvaluationContext}.
     *
     * @param targetObject the target object
     * @param targetMethod the target method
     * @param args         the args
     * @return {@link EvaluationContext}
     */
    public EvaluationContext createEvaluationContext(Object targetObject, Method targetMethod, Object[] args) {
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(targetObject, targetMethod, args, getParameterNameDiscoverer());
        LoggerFunctionRegistrar.registerFunction(evaluationContext);
        return evaluationContext;
    }
}
