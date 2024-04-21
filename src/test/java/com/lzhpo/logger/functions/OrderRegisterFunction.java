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

import com.lzhpo.logger.LoggerComponent;
import com.lzhpo.logger.LoggerFunction;

/**
 * @author lzhpo
 */
@LoggerComponent
public class OrderRegisterFunction {

    @LoggerFunction
    public static String getBusinessId(String orderId) {
        return "BusinessId_" + orderId;
    }

    @LoggerFunction("findUserName")
    public static String findUserName(String userId) {
        return "UserName_" + userId;
    }

    @LoggerFunction
    public static String findUserVip(String userId) {
        return "UserVip_" + userId.length();
    }

    @LoggerFunction
    public static String findProductName(String productId) {
        return "ProductName_" + productId;
    }

    @LoggerFunction
    public static String findOldAddress(String orderId) {
        return "OldAddress_" + orderId;
    }

    @LoggerFunction
    public static String findNewAddress(String addressId) {
        return "NewAddress_" + addressId;
    }

    public static void test() {
        System.out.println("Hello World!");
    }
}
