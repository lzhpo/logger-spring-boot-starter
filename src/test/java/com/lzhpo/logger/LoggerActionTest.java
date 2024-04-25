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

import static org.junit.jupiter.api.Assertions.*;

import com.lzhpo.logger.domain.Admin;
import com.lzhpo.logger.domain.User;
import com.lzhpo.logger.domain.UserWithDisabledField;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

/**
 * @author lzhpo
 */
@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootTest(classes = LoggerTestApplication.class)
@Import({LoggerAutoConfiguration.class, LoggerAction.class, LoggerListenerTest.class})
class LoggerActionTest {

    @Autowired
    private LoggerAction loggerAction;

    @BeforeEach
    public void before() {
        LoggerTestSupport.setLoggerEvent(new LoggerEvent(this));
    }

    @AfterEach
    public void after() {
        LoggerTestSupport.setLoggerEvent(null);
    }

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
    void updateAddressDiff() {
        loggerAction.updateAddressDiff("朝阳小区1号", "光明小区1号");

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertEquals("[] has been updated from [朝阳小区1号] to [光明小区1号]", message);
    }

    @Test
    void userDiff() {
        User oldUser = User.builder().username("Jack").age(22).phone("123456").build();

        User newUser =
                User.builder().username("Jack").age(23).email("jack@gmail.com").build();

        loggerAction.userDiff(oldUser, newUser);

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertFalse(message.contains("[username] has been updated from [Jack] to [Rose]"));
        assertTrue(message.contains("[age] has been updated from [22] to [23]"));
        assertTrue(message.contains("[email] has been updated from [] to [jack@gmail.com]"));
        assertTrue(message.contains("[phone] has been updated from [123456] to []"));
    }

    @Test
    void adminUserDiff() {
        Admin admin = Admin.builder()
                .username("Jack")
                .age(22)
                .email("jack@gmail.com")
                .phone("123456")
                .role("admin")
                .build();

        User user = User.builder()
                .username("Rose")
                .age(23)
                .email("rose@gmail.com")
                .phone("456789")
                .build();

        loggerAction.adminUserDiff(admin, user);

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertTrue(message.contains("[username] has been updated from [Jack] to [Rose]"));
        assertTrue(message.contains("[age] has been updated from [22] to [23]"));
        assertTrue(message.contains("[email] has been updated from [jack@gmail.com] to [rose@gmail.com]"));
        assertTrue(message.contains("[phone] has been updated from [123456] to [456789]"));
        assertTrue(message.contains("[role] has been updated from [admin] to []"));
    }

    @Test
    void userWithDisabledFieldDiff() {
        UserWithDisabledField oldUser = UserWithDisabledField.builder()
                .username("Jack")
                .age(22)
                .phone("123456")
                .build();

        UserWithDisabledField newUser = UserWithDisabledField.builder()
                .username("Jack")
                .age(23)
                .email("jack@gmail.com")
                .build();

        loggerAction.userWithDisabledFieldDiff(oldUser, newUser);

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertFalse(message.contains("[username] has been updated from [Jack] to [Rose]"));
        assertTrue(message.contains("[age] has been updated from [22] to [23]"));
        assertTrue(message.contains("[email] has been updated from [] to [jack@gmail.com]"));
        assertTrue(message.contains("[phone] has been updated from [123456] to []"));
    }
}
