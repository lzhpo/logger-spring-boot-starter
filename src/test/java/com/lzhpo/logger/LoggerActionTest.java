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

import com.lzhpo.logger.domain.*;
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

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertTrue(message.contains("将地址从朝阳小区1号修改为光明小区1号"));
    }

    @Test
    void updateNewAddress() {
        loggerAction.updateNewAddress("幸福小区1号");

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertTrue(message.contains("将地址从光明小区1号修改为幸福小区1号"));
    }

    @Test
    void updateNewSex() {
        loggerAction.updateNewSex("男");

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertTrue(message.contains("性别已修改为男"));
    }

    @Test
    void findUserAge() {
        Integer userAge = loggerAction.findUserAge("123");

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertTrue(message.contains("用户123年龄为23"));
    }

    @Test
    void systemDate() {
        loggerAction.systemDate();

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertTrue(message.contains("当前时间为"));
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
        User oldUser = User.builder()
                .username("Jack")
                .age(22)
                .phone("123456")
                .status("P")
                .build();

        User newUser = User.builder()
                .username("Jack")
                .age(23)
                .email("jack@gmail.com")
                .status("P")
                .build();

        loggerAction.userDiff(oldUser, newUser);

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertFalse(message.contains("[username] has been updated from [Jack] to [Rose]"));
        assertTrue(message.contains("[age] has been updated from [22] to [23]"));
        assertTrue(message.contains("[email: jack@gmail.com] has been added"));
        assertTrue(message.contains("[phone: 123456] has been deleted"));
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
        assertTrue(message.contains("[role: admin] has been deleted"));
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
        assertTrue(message.contains("[email: jack@gmail.com] has been added"));
        assertTrue(message.contains("[phone: 123456] has been deleted"));
    }

    @Test
    void userWithDisabledObjectDiff() {
        UserWithDisabledObject oldUser = UserWithDisabledObject.builder()
                .username("Jack")
                .age(22)
                .email("jack@gmail.com")
                .phone("123456")
                .build();

        UserWithDisabledObject newUser = UserWithDisabledObject.builder()
                .username("Rose")
                .age(23)
                .email("rose@gmail.com")
                .phone("456789")
                .build();

        loggerAction.userWithDisabledObjectDiff(oldUser, newUser);

        String message = LoggerTestSupport.getMessage();
        assertFalse(StringUtils.hasText(message));
        assertFalse(message.contains("[username] has been updated from [Jack] to [Rose]"));
        assertFalse(message.contains("[age] has been updated from [22] to [23]"));
        assertFalse(message.contains("[email] has been updated from [jack@gmail.com] to [rose@gmail.com]"));
        assertFalse(message.contains("[phone] has been updated from [123456] to [456789]"));
    }

    @Test
    void userWithTitleDiff() {
        UserWithTitle oldUser = UserWithTitle.builder()
                .username("Jack")
                .age(22)
                .email("jack@gmail.com")
                .phone("123456")
                .build();

        UserWithTitle newUser = UserWithTitle.builder()
                .username("Rose")
                .age(23)
                .email("rose@gmail.com")
                .phone("456789")
                .build();

        loggerAction.userWithTitleDiff(oldUser, newUser);

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertTrue(message.contains("[用户名称] has been updated from [Jack] to [Rose]"));
        assertTrue(message.contains("[用户年龄] has been updated from [22] to [23]"));
        assertTrue(message.contains("[用户邮箱] has been updated from [jack@gmail.com] to [rose@gmail.com]"));
        assertTrue(message.contains("[用户号码] has been updated from [123456] to [456789]"));
    }

    @Test
    void userToAdmin() {
        User user = User.builder()
                .username("Rose")
                .age(23)
                .email("rose@gmail.com")
                .phone("456789")
                .build();

        Admin admin = loggerAction.userToAdmin(user);

        String message = LoggerTestSupport.getMessage();
        assertTrue(StringUtils.hasText(message));
        assertTrue(message.contains("[status: ] has been deleted"));
        assertTrue(message.contains("[role: admin] has been added"));
    }
}
