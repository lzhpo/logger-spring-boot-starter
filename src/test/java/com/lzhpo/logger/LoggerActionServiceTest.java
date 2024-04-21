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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.util.IdUtil;
import com.lzhpo.logger.domain.OrderRequest;
import com.lzhpo.logger.domain.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @author lzhpo
 */
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest(classes = LoggerTestApplication.class)
@Import({LoggerAutoConfiguration.class, LoggerAction.class, LoggerListenerTest.class})
class LoggerActionServiceTest {

    @Autowired
    private LoggerAction loggerAction;

    @Test
    void updateAddress() {
        loggerAction.updateAddress("朝阳小区1号", "光明小区1号");
        assertTrue(true);
    }

    @Test
    void updateNewAddress() {
        loggerAction.updateNewAddress("幸福小区1号");
        assertTrue(true);
    }

    @Test
    void updateNewSex() {
        loggerAction.updateNewSex("男");
        assertTrue(true);
    }

    @Test
    void findUserAge() {
        Integer userAge = loggerAction.findUserAge("123");
        assertNotNull(userAge);
    }

    @Test
    void systemDate() {
        loggerAction.systemDate();
        assertTrue(true);
    }

    @Test
    void createOrder1() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(IdUtil.fastSimpleUUID());
        orderRequest.setProductId(IdUtil.fastSimpleUUID());

        OrderResponse orderResponse = loggerAction.createOrder1(orderRequest);
        assertNotNull(orderResponse);
    }

    @Test
    void createOrder2() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setUserId(IdUtil.fastSimpleUUID());
        orderRequest.setProductId(IdUtil.fastSimpleUUID());

        OrderResponse orderResponse = loggerAction.createOrder2(orderRequest);
        assertNotNull(orderResponse);
    }
}
