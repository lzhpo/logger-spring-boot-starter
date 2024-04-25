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

import com.lzhpo.logger.annotation.Logger;
import com.lzhpo.logger.annotation.LoggerComponent;
import com.lzhpo.logger.annotation.LoggerFunction;
import com.lzhpo.logger.context.LoggerContextHolder;
import com.lzhpo.logger.domain.*;
import org.springframework.stereotype.Component;

/**
 * @author lzhpo
 */
// spotless:off
@Component
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

    @Logger(message = "#DIFF(#oldAddress, #newAddress)")
    public void updateAddressDiff(String oldAddress, String newAddress) {
        // NOP
    }

    @Logger(message = "#DIFF(#oldUser, #newUser)")
    public void userDiff(User oldUser, User newUser) {
        // NOP
    }

    @Logger(message = "#DIFF(#admin, #user)")
    public void adminUserDiff(Admin admin, User user) {
        // NOP
    }

    @Logger(message = "#DIFF(#oldUser, #newUser)")
    public void userWithDisabledFieldDiff(UserWithDisabledField oldUser, UserWithDisabledField newUser) {
        // NOP
    }

    @Logger(message = "#DIFF(#oldUser, #newUser)")
    public void userWithDisabledObjectDiff(UserWithDisabledObject oldUser, UserWithDisabledObject newUser) {
        // NOP
    }

    // @el(DIFF: com.lzhpo.logger.diff.LoggerDiffFunction)
    // @el(oldUser: com.lzhpo.logger.domain.UserWithTitle)
    // @el(newUser: com.lzhpo.logger.domain.UserWithTitle)
    @Logger(message = "#DIFF(#oldUser, #newUser)")
    public void userWithTitleDiff(UserWithTitle oldUser, UserWithTitle newUser) {
        // NOP
    }

    // @el(DIFF: com.lzhpo.logger.diff.LoggerDiffFunction)
    // @el(user: com.lzhpo.logger.domain.User)
    // @el(result: com.lzhpo.logger.domain.Admin)
    @Logger(
            condition = "#result.getUsername() != null",
            message = "#DIFF(#user, #result)",
            operatorId = "#user.getUsername()",
            businessId = "T(java.util.UUID).randomUUID()",
            category = "T(java.util.UUID).randomUUID()",
            tag = "T(java.util.UUID).randomUUID()",
            prelude = false,
            returning = true,
            additional = "T(cn.hutool.json.JSONUtil).toJsonStr(#user)"
    )
    public Admin userToAdmin(User user) {
        return Admin.builder()
                .username(user.getUsername())
                .age(user.getAge())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role("admin")
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
