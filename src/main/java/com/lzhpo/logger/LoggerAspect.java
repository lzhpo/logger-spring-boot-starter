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

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author lzhpo
 */
// spotless:off
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggerAspect {

    private final ObjectProvider<OperatorAware> operatorAwareObjectProvider;

    /**
     * Around aspect for {@link Logger} annotation.
     *
     * @param joinPoint {@link ProceedingJoinPoint}
     * @param logger    {@link Logger}
     * @return the result
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("@annotation(logger)")
    public Object doAround(ProceedingJoinPoint joinPoint, Logger logger) throws Throwable {
        LoggerEvent event = new LoggerEvent(this);
        event.setCreateTime(new Date(event.getTimestamp()));
        event.setErrors(new ArrayList<>());

        if (logger.prelude()) {
            resolveLogger(joinPoint, logger, event);
        }

        try {
            Object result = joinPoint.proceed();
            event.setResult(logger.returning() && !logger.prelude() ? result : null);
            return result;
        } catch (Throwable e) {
            event.getErrors().add(new Exception(e.getMessage(), e));
            throw e;
        } finally {
            if (!logger.prelude()) {
                resolveLogger(joinPoint, logger, event);
            }

            LoggerContextHolder.clearContext();
            CompletableFuture.runAsync(() -> SpringUtil.publishEvent(event))
                    .thenRunAsync(() -> log.debug("Published LoggerEvent."))
                    .exceptionally(e -> {
                        log.error("Publishing LoggerEvent error: {}", e.getMessage(), e);
                        return null;
                    });
        }
    }

    /**
     * Resolve {@link Logger} annotation and publish {@link LoggerEvent}.
     *
     * @param joinPoint {@link ProceedingJoinPoint}
     * @param logger    {@link Logger}
     * @param event     {@link LoggerEvent}
     */
    private void resolveLogger(ProceedingJoinPoint joinPoint, Logger logger, LoggerEvent event) {
        try {
            Object object = joinPoint.getThis();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();

            LoggerExpressionEvaluator evaluator = new LoggerExpressionEvaluator();
            ParameterNameDiscoverer discoverer = evaluator.getParameterNameDiscoverer();
            LoggerEvaluationContext context = LoggerContextHolder.getContext(object, method, event.getResult(), args, discoverer);
            if (!Boolean.parseBoolean(evalExpression(logger.condition(), event, context, evaluator))) {
                log.debug("The resolved condition is false in @Logger.");
                return;
            }

            event.setLogId(IdUtil.fastSimpleUUID());
            event.setSuccess(CollectionUtils.isEmpty(event.getErrors()));
            event.setOperatorId(getOperatorId(logger.operatorId(), event, context, evaluator));
            event.setTag(evalExpression(logger.tag(), event, context, evaluator));
            event.setMessage(evalExpression(logger.message(), event, context, evaluator));
            event.setCategory(evalExpression(logger.category(), event, context, evaluator));
            event.setBusinessId(evalExpression(logger.businessId(), event, context, evaluator));
            event.setAdditional(evalExpression(logger.additional(), event, context, evaluator));
        } catch (Exception e) {
            log.error("Resolve @Logger error: {}", e.getMessage(), e);
            event.getErrors().add(e);
        } finally {
            event.setTakeTime(System.currentTimeMillis() - event.getCreateTime().getTime());
        }
    }

    /**
     * Get operatorId.
     *
     * @param operatorId the operatorId
     * @param event      the logger event
     * @param context    the logger evaluation context
     * @param evaluator  the logger expression evaluator
     * @return the operatorId
     */
    private String getOperatorId(String operatorId, LoggerEvent event, LoggerEvaluationContext context, LoggerExpressionEvaluator evaluator) {
        return Optional.ofNullable(operatorId)
                .filter(StringUtils::hasText)
                .map(operator -> evalExpression(operator, event, context, evaluator))
                .orElseGet(() -> Optional.ofNullable(operatorAwareObjectProvider.getIfAvailable())
                        .map(OperatorAware::getCurrentOperatorId)
                        .orElseGet(() -> {
                            log.debug("No operatorId was entered, also cannot be obtained using OperatorAware.");
                            return null;
                        }));
    }

    /**
     * Evaluate condition expression to get result.
     *
     * @param expression the condition expression
     * @param event      the logger event
     * @param context    the logger evaluation context
     * @param evaluator  the logger expression evaluator
     * @return the evaluated result
     */
    private String evalExpression(String expression, LoggerEvent event, LoggerEvaluationContext context, LoggerExpressionEvaluator evaluator) {
        try {
            return evaluator.evalExpression(expression, context);
        } catch (Exception exception) {
            log.error("Evaluate expression error: {}", exception.getMessage(), exception);
            event.getErrors().add(exception);
            return expression;
        }
    }
}
// spotless:on
