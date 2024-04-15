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
package com.lzhpo.logger.context;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class StandardEvaluationContextTest {

    @Test
    public void test() {
        final ExpressionParser expressionParser = new SpelExpressionParser();
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

        final String functionNamePrint = "print";
        Method printMethod = ReflectUtil.getMethodByName(StandardEvaluationContextTest.class, functionNamePrint);
        evaluationContext.registerFunction(functionNamePrint, printMethod);

        final String expression = "#print('Hello World')";
        String result = expressionParser.parseExpression(expression).getValue(evaluationContext, String.class);
        log.info(result);
        assertNotNull(result);
    }

    public static String print(String content) {
        log.info("I'm print, content: {}", content);
        return content;
    }
}
