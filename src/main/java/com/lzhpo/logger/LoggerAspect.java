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
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggerAspect {

    private final ObjectProvider<OperatorAware> operatorAwareObjectProvider;

    /**
     * Around aspect for {@link Logger} annotation.
     *
     * @param point  {@link ProceedingJoinPoint}
     * @param logger {@link Logger}
     * @return the result
     * @throws Throwable if the invoked proceed throws anything
     */
    @Around("@annotation(logger)")
    public Object doAround(ProceedingJoinPoint point, Logger logger) throws Throwable {
        Date createTime = new Date();
        Object result;

        LoggerEvent event = new LoggerEvent(this);
        event.setCreateTime(createTime);
        event.setErrors(new ArrayList<>());

        try {
            result = point.proceed();
            event.setResult(result);
        } catch (Exception exception) {
            event.getErrors().add(exception);
            throw exception;
        } finally {
            resolveLogger(point, logger, event);
        }
        return result;
    }

    /**
     * Resolve {@link Logger} annotation and publish {@link LoggerEvent}.
     *
     * @param point  {@link ProceedingJoinPoint}
     * @param logger {@link Logger}
     * @param event  {@link LoggerEvent}
     */
    private void resolveLogger(ProceedingJoinPoint point, Logger logger, LoggerEvent event) {
        Object object = point.getThis();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();

        LoggerExpressionEvaluator evaluator = new LoggerExpressionEvaluator();
        ParameterNameDiscoverer discoverer = evaluator.getParameterNameDiscoverer();
        LoggerContextHolder.initialize(object, method, event.getResult(), args, discoverer);

        try {
            LoggerEvaluationContext context = LoggerContextHolder.getContext();
            if (!Boolean.parseBoolean(evalExpression(logger.condition(), event, context, evaluator))) {
                log.debug("The resolved condition is false in @Logger.");
                return;
            }

            String operatorId = Optional.ofNullable(logger.operatorId())
                    .filter(StringUtils::hasText)
                    .map(operator -> evalExpression(operator, event, context, evaluator))
                    .orElseGet(() -> Optional.ofNullable(operatorAwareObjectProvider.getIfAvailable())
                            .map(OperatorAware::getCurrentOperatorId)
                            .orElseGet(() -> {
                                log.debug("No operatorId was entered, also cannot be obtained using OperatorAware.");
                                return null;
                            }));
            event.setOperatorId(operatorId);

            event.setLogId(IdUtil.fastSimpleUUID());
            event.setSuccess(CollectionUtils.isEmpty(event.getErrors()));
            event.setTag(evalExpression(logger.tag(), event, context, evaluator));
            event.setMessage(evalExpression(logger.message(), event, context, evaluator));
            event.setCategory(evalExpression(logger.category(), event, context, evaluator));
            event.setBusinessId(evalExpression(logger.businessId(), event, context, evaluator));
            event.setAdditional(evalExpression(logger.additional(), event, context, evaluator));
            event.setTakeTime(System.currentTimeMillis() - event.getCreateTime().getTime());

            CompletableFuture.runAsync(() -> SpringUtil.publishEvent(event))
                    .thenRunAsync(() -> log.debug("Published LoggerEvent."))
                    .exceptionally(e -> {
                        log.error("Publishing LoggerEvent error: {}", e.getMessage(), e);
                        return null;
                    });
        } catch (Exception e) {
            log.error("Resolve @Logger error: {}", e.getMessage(), e);
        } finally {
            LoggerContextHolder.clearContext();
        }
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
    private String evalExpression(
            String expression,
            LoggerEvent event,
            LoggerEvaluationContext context,
            LoggerExpressionEvaluator evaluator) {

        try {
            return evaluator.evalExpression(expression, context);
        } catch (Exception exception) {
            log.error("Evaluate expression error: {}", exception.getMessage(), exception);
            event.getErrors().add(exception);
            return expression;
        }
    }
}
