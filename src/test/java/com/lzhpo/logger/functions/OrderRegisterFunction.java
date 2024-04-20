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
package com.lzhpo.logger.functions;

import cn.hutool.crypto.digest.DigestUtil;
import com.lzhpo.logger.LoggerComponent;
import com.lzhpo.logger.LoggerFunction;

/**
 * @author lzhpo
 */
@LoggerComponent
public class OrderRegisterFunction {

    @LoggerFunction
    public static String getBusinessId(String orderId) {
        return DigestUtil.sha256Hex(orderId);
    }

    @LoggerFunction
    public static String queryUserName(String userId) {
        return "Jack";
    }

    @LoggerFunction
    public static String queryProductName(String productId) {
        return "ABC";
    }

    @LoggerFunction
    public static String queryOldAddress(String orderId) {
        return "Jiangxi";
    }

    @LoggerFunction
    public static String queryNewAddress(String addressId) {
        return "Guangzhou";
    }

    public static void test() {
        System.out.println("Hello World!");
    }
}
