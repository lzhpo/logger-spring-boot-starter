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
import com.lzhpo.logger.domain.OrderRequest;
import com.lzhpo.logger.domain.OrderResponse;
import org.springframework.boot.test.context.TestComponent;

/**
 * @author lzhpo
 */
// spotless:off
@TestComponent
public class LoggerAction {

    @Logger(message = "'将地址从' + #oldAddress + '修改为' + #newAddress")
    public void updateAddress(String oldAddress, String newAddress) {
        // NOP
    }

    @Logger(message = "'将地址从' + #oldAddress + '修改为' + #newAddress")
    public void updateNewAddress(String newAddress) {
        LoggerContextHolder.putVariable("oldAddress", "光明小区1号");
    }

    @Logger(message = "#getCurrentUserName() + '性别已修改为' + #newSex")
    public void updateNewSex(String newSex) {
    }

    @Logger(condition = "#userId != null", message = "'用户' + #userId + '年龄为' + #result")
    public Integer findUserAge(String userId) {
        return 23;
    }

    @Logger(message = "'当前时间为' + T(java.time.LocalDateTime).now()")
    public void systemDate() {
        // NOP
    }

    @Logger(condition = "#result.isSuccess()", message = "'用户' + #findUserName(#request.getUserId()) + '下单了' + #findProductName(#request.getProductId())")
    public OrderResponse createOrder1(OrderRequest request) {
        return OrderResponse.builder()
                .success(true)
                .orderId(IdUtil.fastSimpleUUID())
                .userId(request.getUserId())
                .productId(request.getProductId())
                .build();
    }

    @Logger(condition = "#result.isSuccess()", message = "'用户' + #findUserName(#request.getUserId()) + '下单了' + #findProductName(#request.getProductId())")
    public OrderResponse createOrder2(OrderRequest request) {
        return OrderResponse.builder()
                .success(false)
                .orderId(IdUtil.fastSimpleUUID())
                .userId(request.getUserId())
                .productId(request.getProductId())
                .build();
    }

    @LoggerComponent
    public static class LoggerFunctions {

        @LoggerFunction
        public static String getCurrentUserName() {
            return "小明";
        }

        @LoggerFunction
        public static String findUserName(String userId) {
            return "小张";
        }

        @LoggerFunction
        public static String findProductName(String productId) {
            return "ABC";
        }
    }
}
// spotless:on
