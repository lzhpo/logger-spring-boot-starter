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
package com.lzhpo.logger.aspect;

import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.logger.LoggerEvaluationContext;
import com.lzhpo.logger.annotation.Logger;
import com.lzhpo.logger.event.LoggerEvent;

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
        LoggerEvent loggerEvent = new LoggerEvent(this);
        loggerEvent.setCreateTime(createTime);
        loggerEvent.setErrors(new ArrayList<>());

        final Object result;

        try {
            result = point.proceed();
        } catch (Exception exception) {
            loggerEvent.getErrors().add(exception.getMessage());
            throw exception;
        } finally {
            resolveLogger(point, logger, loggerEvent);
        }
        return result;
    }

    /**
     * Resolve {@link Logger} annotation and publish {@link LoggerEvent}.
     *
     * @param point       {@link ProceedingJoinPoint}
     * @param logger      {@link Logger}
     * @param loggerEvent {@link LoggerEvent}
     */
    private void resolveLogger(ProceedingJoinPoint point, Logger logger, LoggerEvent loggerEvent) {
        try {
            Object targetObject = point.getThis();
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method targetMethod = signature.getMethod();
            Object[] args = point.getArgs();

            LoggerEvaluationContext evaluationContext = new LoggerEvaluationContext();
            loggerEvent.setLogId(IdUtil.fastSimpleUUID());
            loggerEvent.setTag(evaluateExpression(logger.tag(), targetObject, targetMethod, args, evaluationContext, loggerEvent));
            loggerEvent.setBizId(evaluateExpression(logger.bizId(), targetObject, targetMethod, args, evaluationContext, loggerEvent));
            loggerEvent.setMessage(evaluateExpression(logger.message(), targetObject, targetMethod, args, evaluationContext, loggerEvent));
            loggerEvent.setCategory(evaluateExpression(logger.category(), targetObject, targetMethod, args, evaluationContext, loggerEvent));
            loggerEvent.setOperatorId(evaluateExpression(logger.operatorId(), targetObject, targetMethod, args, evaluationContext, loggerEvent));
            loggerEvent.setAdditional(evaluateExpression(logger.additional(), targetObject, targetMethod, args, evaluationContext, loggerEvent));
            loggerEvent.setSuccess(CollectionUtils.isEmpty(loggerEvent.getErrors()));
            loggerEvent.setTakeTime(System.currentTimeMillis() - loggerEvent.getCreateTime().getTime());
            SpringUtil.publishEvent(loggerEvent);
        } catch (Exception e) {
            log.error("Resolve @Logger error: {}", e.getMessage(), e);
        }
    }

    /**
     * Evaluate condition expression to get result.
     *
     * @param expression   the condition expression
     * @param targetObject the target object
     * @param targetMethod the target method
     * @param args         the args
     * @param context      {@link LoggerEvaluationContext}
     * @param event        {@link LoggerEvent}
     * @return the evaluated result
     */
    private String evaluateExpression(String expression, Object targetObject, Method targetMethod, Object[] args, LoggerEvaluationContext context, LoggerEvent event) {
        try {
            return context.evaluateExpression(expression, targetObject, targetMethod, args);
        } catch (Exception e) {
            log.error("Evaluate expression error: {}", e.getMessage(), e);
            event.getErrors().add(e.getMessage());
            return expression;
        }
    }
}
