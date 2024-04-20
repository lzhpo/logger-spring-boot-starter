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
package com.lzhpo.logger.service;

import com.lzhpo.logger.domain.CreateOrderRequest;
import com.lzhpo.logger.domain.CreateOrderResponse;
import com.lzhpo.logger.domain.ModifyOrderRequest;
import com.lzhpo.logger.domain.ModifyOrderResponse;

/**
 * @author lzhpo
 */
public interface OrderService {

    CreateOrderResponse createOrder(CreateOrderRequest request);

    ModifyOrderResponse modifyOrder(ModifyOrderRequest request);
}
