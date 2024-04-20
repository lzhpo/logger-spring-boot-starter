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

    @LoggerFunction("findUserName")
    public static String findUserName(String userId) {
        return "小刘";
    }

    @LoggerFunction
    public static String findUserVip(String userId) {
        return "VIP5";
    }

    @LoggerFunction
    public static String findProductName(String productId) {
        return "ABC";
    }

    @LoggerFunction
    public static String findOldAddress(String orderId) {
        return "Jiangxi";
    }

    @LoggerFunction
    public static String findNewAddress(String addressId) {
        return "Guangzhou";
    }

    public static void test() {
        System.out.println("Hello World!");
    }
}
