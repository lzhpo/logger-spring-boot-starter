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

import static org.springframework.expression.spel.SpelMessage.FUNCTION_MUST_BE_STATIC;

import cn.hutool.core.util.StrUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author lzhpo
 */
@Slf4j
@Component
public class LoggerFunctionRegistrar implements SmartInitializingSingleton, BeanFactoryPostProcessor {

    private ConfigurableListableBeanFactory beanFactory;
    private static final Map<String, Method> REGISTERED_FUNCTIONS = new ConcurrentHashMap<>();

    /**
     * Register the specified {@link Method} as a SpEL function.
     *
     * @param context {@link StandardEvaluationContext}
     */
    public static void registerFunction(StandardEvaluationContext context) {
        REGISTERED_FUNCTIONS.forEach(context::registerFunction);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    // spotless:off
    @Override
    public void afterSingletonsInstantiated() {
        Assert.notNull(beanFactory, "The beanFactory not initialized.");
        Map<String, Object> loggerComponentBeanMap = beanFactory.getBeansWithAnnotation(LoggerComponent.class);
        log.debug("Found {} class has @LoggerComponent.", loggerComponentBeanMap.size());

        loggerComponentBeanMap.forEach((beanName, beanInstance) -> {
            log.debug("Processing bean=[{}] for @LoggerFunction.", beanName);
            Class<?> clazz = beanInstance.getClass();
            Method[] methods = ReflectionUtils.getDeclaredMethods(clazz);

            for (Method method : methods) {
                String methodName = method.getName();
                LoggerFunction loggerFunction = AnnotationUtils.findAnnotation(method, LoggerFunction.class);
                if (Objects.isNull(loggerFunction)) {
                    log.debug("The method=[{}] not exists @LoggerFunction, skipped.", methodName);
                    continue;
                }

                String functionName = StrUtil.blankToDefault(loggerFunction.value(), methodName);
                log.debug("Found @LoggerFunction with functionName=[{}] in beanName=[{}]", functionName, beanName);
                Assert.isTrue(Modifier.isStatic(method.getModifiers()), FUNCTION_MUST_BE_STATIC.formatMessage(ClassUtils.getQualifiedMethodName(method), functionName));
                Assert.isTrue(!REGISTERED_FUNCTIONS.containsKey(functionName), StrUtil.format("The function name already exists for @LoggerFunction", functionName));
                REGISTERED_FUNCTIONS.put(functionName, method);
            }
        });
    }
    // spotless:on
}
