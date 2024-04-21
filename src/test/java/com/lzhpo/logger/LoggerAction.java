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

import org.springframework.boot.test.context.TestComponent;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lzhpo
 */
@TestComponent
public class LoggerAction {

    @Logger(message = "'将地址从' + #oldAddress + '修改为' + #newAddress")
    public void updateAddress(@RequestParam String oldAddress, @RequestParam String newAddress) {}

    @Logger(message = "'将地址从' + #oldAddress + '修改为' + #newAddress")
    public void updateNewAddress(@RequestParam String newAddress) {
        LoggerContextHolder.putVariable("oldAddress", "光明小区1号");
    }
}
