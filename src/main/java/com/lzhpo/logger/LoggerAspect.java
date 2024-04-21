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
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author lzhpo
 */
@Slf4j
@Aspect
@Component
public class LoggerAspect {

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
        LoggerEvent event = new LoggerEvent(this);
        event.setCreateTime(createTime);
        event.setErrors(new ArrayList<>());

        final Object result;

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
        try {
            Object object = point.getThis();
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            Object[] args = point.getArgs();

            LoggerEvaluationContext context = new LoggerEvaluationContext();
            if (!Boolean.parseBoolean(evalExpression(logger.condition(), object, method, args, event, context))) {
                log.debug("The resolved condition is false in @Logger.");
                return;
            }

            event.setLogId(IdUtil.fastSimpleUUID());
            event.setTag(evalExpression(logger.tag(), object, method, args, event, context));
            event.setBusinessId(evalExpression(logger.businessId(), object, method, args, event, context));
            event.setMessage(evalExpression(logger.message(), object, method, args, event, context));
            event.setCategory(evalExpression(logger.category(), object, method, args, event, context));
            event.setOperatorId(evalExpression(logger.operatorId(), object, method, args, event, context));
            event.setAdditional(evalExpression(logger.additional(), object, method, args, event, context));
            event.setSuccess(CollectionUtils.isEmpty(event.getErrors()));
            event.setTakeTime(System.currentTimeMillis() - event.getCreateTime().getTime());
            SpringUtil.publishEvent(event);
            log.debug("Published LoggerEvent, {} method takes {} ms.", method.getName(), event.getTakeTime());
        } catch (Exception e) {
            log.error("Resolve @Logger error: {}", e.getMessage(), e);
        }
    }

    /**
     * Evaluate condition expression to get result.
     *
     * @param expression the condition expression
     * @param object     the target object
     * @param method     the target method
     * @param args       the args
     * @param event      the logger event
     * @param context    the logger evaluation context
     * @return the evaluated result
     */
    // spotless:off
    private String evalExpression(String expression,
                                  Object object,
                                  Method method,
                                  Object[] args,
                                  LoggerEvent event,
                                  LoggerEvaluationContext context) {
        try {
            return context.evalExpression(expression, object, method, event.getResult(), args);
        } catch (Exception exception) {
            log.error("Evaluate expression error: {}", exception.getMessage(), exception);
            event.getErrors().add(exception);
            return expression;
        }
    }
    // spotless:on
}
