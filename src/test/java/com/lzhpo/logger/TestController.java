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

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lzhpo
 */
@Slf4j
@RestController
public class TestController {

    @GetMapping("/say")
    @Logger(condition = "#isHello(#content)", message = "'I want say ' + #print(#content)")
    public ResponseEntity<String> say(@RequestParam String content) {
        return ResponseEntity.ok(content);
    }

    @GetMapping("/editAge")
    @Logger(message = "#user.getName() + ' edit age from ' + #findUserOldAge(#user.getName()) + ' to ' + #user.getAge()")
    public ResponseEntity<User> editAge(@RequestBody User user) {
        return ResponseEntity.ok(user);
    }

    @GetMapping("/editAgeWhenError")
    @Logger(message = "#user.getName() + ' edit age from ' + #findUserOldAgeWhenError(#user.getName()) + ' to ' + #user.getAge()")
    public ResponseEntity<User> editAgeWhenError(@RequestBody User user) {
        return ResponseEntity.ok(user);
    }
}
