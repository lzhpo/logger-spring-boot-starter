package com.lzhpo.logger;

import com.lzhpo.logger.annotation.LoggerFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lzhpo
 */
@Slf4j
@Component
public class RegisterFunctions {

    private static final Map<String, User> usersMap = new HashMap<>();

    static {
        usersMap.put("jack", User.builder().name("jack").age(25).build());
        usersMap.put("lewis", User.builder().name("lewis").age(20).build());
    }

    @LoggerFunction
    public static String print(String content) {
        log.info("I'm say, content: {}", content);
        return content;
    }

    @LoggerFunction
    public static boolean isHello(String content) {
        return "Hello".equals(content);
    }

    @LoggerFunction
    public static Integer findUserOldAge(String username) {
        log.info("I'm findUserOldAge, username: {}", username);
        User user = usersMap.get(username);
        return user.getAge();
    }

    @LoggerFunction
    public static Integer findUserOldAgeWhenError(String username) {
        throw new RuntimeException("I'm findUserOldAgeWhenError, username: " + username);
    }
}
