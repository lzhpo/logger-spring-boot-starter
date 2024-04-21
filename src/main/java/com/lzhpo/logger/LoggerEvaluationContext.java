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
import java.util.Arrays;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

/**
 * A method-based {@link org.springframework.expression.EvaluationContext} that
 * provides explicit support for method-based invocations.
 *
 * <p>Expose the actual method arguments using the following aliases:
 * <ol>
 * <li>pX where X is the index of the argument (p0 for the first argument)</li>
 * <li>aX where X is the index of the argument (a1 for the second argument)</li>
 * <li>the name of the parameter as discovered by a configurable {@link ParameterNameDiscoverer}</li>
 * </ol>
 *
 * @author lzhpo
 * @see org.springframework.context.expression.MethodBasedEvaluationContext
 */
@Slf4j
@Getter
@NoArgsConstructor
public class LoggerEvaluationContext extends StandardEvaluationContext {

    private Method method;
    private Object[] arguments;
    private ParameterNameDiscoverer discoverer;
    private boolean argumentsLoaded = false;

    public LoggerEvaluationContext(Object rootObject, Method method, Object[] arg, ParameterNameDiscoverer discoverer) {
        super(rootObject);
        this.method = method;
        this.arguments = arg;
        this.discoverer = discoverer;
    }

    @Override
    public Object lookupVariable(String name) {
        Object variable = super.lookupVariable(name);
        if (variable != null) {
            return variable;
        }
        if (!this.argumentsLoaded) {
            lazyLoadArguments();
            this.argumentsLoaded = true;
            variable = super.lookupVariable(name);
        }
        return variable;
    }

    /**
     * Load the param information only when needed.
     */
    public void lazyLoadArguments() {
        if (ObjectUtils.isEmpty(this.arguments)
                || ObjectUtils.isEmpty(this.method)
                || ObjectUtils.isEmpty(this.discoverer)) {
            return;
        }

        // Expose indexed variables as well as parameter names (if discoverable)
        String[] paramNames = this.discoverer.getParameterNames(this.method);
        int paramCount = (paramNames != null ? paramNames.length : this.method.getParameterCount());
        int argsCount = this.arguments.length;

        for (int i = 0; i < paramCount; i++) {
            Object value = null;
            if (argsCount > paramCount && i == paramCount - 1) {
                // Expose remaining arguments as vararg array for last parameter
                value = Arrays.copyOfRange(this.arguments, i, argsCount);
            } else if (argsCount > i) {
                // Actual argument found - otherwise left as null
                value = this.arguments[i];
            }

            setVariable(LoggerConstant.VARIABLE_ARG_A + i, value);
            setVariable(LoggerConstant.VARIABLE_ARG_P + i, value);
            if (paramNames != null && paramNames[i] != null) {
                setVariable(paramNames[i], value);
            }
        }
    }
}
