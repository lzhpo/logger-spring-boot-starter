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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
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
     * Get {@link Expression} of {@code expression}.
     *
     * @param expression the condition expression
     * @param object     the target object
     * @param method     the target method
     * @return {@link Expression}
     */
    public Expression getExpression(String expression, Object object, Method method) {
        AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, object.getClass());
        return getExpression(expressionCache, annotatedElementKey, expression);
    }

    /**
     * Evaluate {@code expression} and get result value.
     *
     * @param expression the condition expression
     * @param object     the target object
     * @param method     the target method
     * @param result     the result
     * @param args       the args
     * @return the evaluated result
     */
    public String evalExpression(String expression, Object object, Method method, Object result, Object[] args) {
        if (!StringUtils.hasText(expression)) {
            return expression;
        }

        EvaluationContext evaluationContext = createEvalContext(object, method, result, args);
        return getExpression(expression, object, method).getValue(evaluationContext, String.class);
    }

    /**
     * Create registered {@link LoggerFunction}'s {@link EvaluationContext}.
     *
     * @param object the target object
     * @param method the target method
     * @param result the result
     * @param args   the args
     * @return {@link EvaluationContext}
     */
    public EvaluationContext createEvalContext(Object object, Method method, Object result, Object[] args) {
        ParameterNameDiscoverer discoverer = getParameterNameDiscoverer();
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(object, method, args, discoverer);
        context.setVariable(LoggerConstant.VARIABLE_RESULT, result);
        LoggerFunctionRegistrar.registerFunction(context);
        return context;
    }
}
