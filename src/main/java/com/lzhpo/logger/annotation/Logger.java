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
package com.lzhpo.logger.annotation;

import com.lzhpo.logger.LoggerConstant;
import com.lzhpo.logger.LoggerEvent;
import java.lang.annotation.*;
import org.intellij.lang.annotations.Language;

/**
 * @author lzhpo
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logger {

    /**
     * The logger condition.
     *
     * @return condition
     */
    @Language(LoggerConstant.SpEL)
    String condition() default LoggerConstant.BOOLEAN_TRUE;

    /**
     * The logger message, support SpringEL expression and function.
     *
     * @return message
     */
    @Language(LoggerConstant.SpEL)
    String message();

    /**
     * The operator id.
     *
     * @return operatorId
     */
    @Language(LoggerConstant.SpEL)
    String operatorId() default LoggerConstant.EMPTY;

    /**
     * The business id.
     *
     * @return businessId
     */
    @Language(LoggerConstant.SpEL)
    String businessId() default LoggerConstant.EMPTY;

    /**
     * The logger category.
     *
     * @return category
     */
    @Language(LoggerConstant.SpEL)
    String category() default LoggerConstant.EMPTY;

    /**
     * The logger tag.
     *
     * @return tag
     */
    @Language(LoggerConstant.SpEL)
    String tag() default LoggerConstant.EMPTY;

    /**
     * The prelude means logger will resolve before execute business logic.
     *
     * <p>If true, will cannot use result in SpringEL expression. <br/>
     * e.g: {@code @Logger(condition = "#result.getSuccess()", message = "xxx")}
     *
     * @return execution pointcut
     */
    boolean prelude() default false;

    /**
     * Return the result of the business method execution.
     *
     * <p>If not use result, can set false, the {@link LoggerEvent} will not set result.
     *
     * @return returning
     */
    boolean returning() default true;

    /**
     * The logger additional information.
     *
     * @return additional
     */
    @Language(LoggerConstant.SpEL)
    String additional() default LoggerConstant.EMPTY;
}
