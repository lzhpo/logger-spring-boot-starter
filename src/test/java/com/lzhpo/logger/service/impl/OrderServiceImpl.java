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
package com.lzhpo.logger.service.impl;

import cn.hutool.core.util.IdUtil;
import com.lzhpo.logger.Logger;
import com.lzhpo.logger.domain.CreateOrderRequest;
import com.lzhpo.logger.domain.CreateOrderResponse;
import com.lzhpo.logger.domain.ModifyOrderRequest;
import com.lzhpo.logger.domain.ModifyOrderResponse;
import com.lzhpo.logger.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author lzhpo
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Override
    @Logger(message = "'I am create order: ' + #findUserName(#request.getUserId()) ")
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        CreateOrderResponse response = new CreateOrderResponse();
        response.setSuccess(true);
        response.setOrderId(IdUtil.fastSimpleUUID());
        response.setUserId(request.getUserId());
        response.setProductId(request.getProductId());
        response.setAddress(request.getAddress());
        response.setPaymentType(request.getPaymentType());
        return response;
    }

    @Override
    @Logger(message = "'I am modify order: ' + #findUserName(#request.getUserId()) ")
    public ModifyOrderResponse modifyOrder(ModifyOrderRequest request) {
        ModifyOrderResponse response = new ModifyOrderResponse();
        response.setSuccess(true);
        response.setOrderId(request.getOrderId());
        response.setUserId(request.getUserId());
        response.setAddressId(request.getAddressId());
        return response;
    }
}
