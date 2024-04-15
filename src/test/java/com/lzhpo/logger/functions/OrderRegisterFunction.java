package com.lzhpo.logger.functions;

import cn.hutool.crypto.digest.DigestUtil;
import com.lzhpo.logger.LoggerFunction;
import org.springframework.stereotype.Component;

/**
 * @author lzhpo
 */
@Component
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
}
