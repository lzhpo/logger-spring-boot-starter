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
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author lzhpo
 */
@Slf4j
public class LoggerExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    /**
     * Get {@link Expression} of {@code expression}.
     *
     * @param expression        the condition expression
     * @param evaluationContext the evaluation context
     * @return {@link Expression}
     */
    public Expression getExpression(String expression, LoggerEvaluationContext evaluationContext) {
        Method method = evaluationContext.getMethod();
        TypedValue rootObject = evaluationContext.getRootObject();
        if (ObjectUtils.isEmpty(method) || ObjectUtils.isEmpty(rootObject)) {
            return super.parseExpression(expression);
        }

        AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, rootObject.getClass());
        return super.getExpression(expressionCache, annotatedElementKey, expression);
    }

    /**
     * Evaluate {@code expression} and get result value.
     *
     * @param expression        the condition expression
     * @param evaluationContext the evaluation context
     * @return the evaluated result
     */
    public String evalExpression(String expression, LoggerEvaluationContext evaluationContext) {
        if (!StringUtils.hasText(expression)) {
            return expression;
        }
        return getExpression(expression, evaluationContext).getValue(evaluationContext, String.class);
    }

    /**
     * Return a shared parameter name discoverer which caches data internally.
     *
     * @return the parameter name discoverer
     */
    @Override
    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return super.getParameterNameDiscoverer();
    }
}
