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
package com.lzhpo.logger.controller;

import cn.hutool.core.util.IdUtil;
import com.lzhpo.logger.Logger;
import com.lzhpo.logger.domain.CreateOrderRequest;
import com.lzhpo.logger.domain.CreateOrderResponse;
import com.lzhpo.logger.domain.ModifyOrderRequest;
import com.lzhpo.logger.domain.ModifyOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author lzhpo
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    // spotless:off
    @PostMapping
    @Logger(
            condition = "#result.getSuccess()",
            category = "'Operation Log'",
            tag = "'Create Order'",
            bizId = "#getBusinessId(#result.orderId)",
            operatorId = "#findUserName(#request.getUserId())",
            message = "#findUserName(#request.getUserId()) + '使用' + #request.getPaymentType() + '下单了' + #findProductName(#request.getProductId()) + '产品'",
            additional = "#findUserName(#request.getUserId()) + '等级是' + #findUserVip(#request.getUserId()) + '，请求日期' + T(java.time.LocalDateTime).now()"
    )
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = new CreateOrderResponse();
        response.setSuccess(true);
        response.setOrderId(IdUtil.fastSimpleUUID());
        response.setUserId(request.getUserId());
        response.setProductId(request.getProductId());
        response.setAddress(request.getAddress());
        response.setPaymentType(request.getPaymentType());
        return response;
    }

    @PutMapping
    @Logger(
            condition = "#result.getSuccess()",
            category = "'Operation Log'",
            tag = "'Modify Order'",
            bizId = "#getBusinessId(#result.orderId)",
            operatorId = "#findUserName(#request.getUserId())",
            message = "#findUserName(#request.getUserId()) + '将地址从' + #findOldAddress(#request.getOrderId()) + '修改为' + #findNewAddress(#request.getAddressId())",
            additional = "#findUserName(#request.getUserId()) + '等级是' + #findUserVip(#request.getUserId()) + '，请求日期' + T(java.time.LocalDateTime).now()"
    )
    public ModifyOrderResponse modifyOrder(@RequestBody ModifyOrderRequest request) {
        ModifyOrderResponse response = new ModifyOrderResponse();
        response.setSuccess(true);
        response.setOrderId(request.getOrderId());
        response.setUserId(request.getUserId());
        response.setAddressId(request.getAddressId());
        return response;
    }
    // spotless:on
}
