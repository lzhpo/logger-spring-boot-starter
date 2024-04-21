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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.event.EventListener;

/**
 * <pre>
 * {@code
 *     @Component
 *     public class LoggerListener implements ApplicationListener<LoggerEvent> {
 *
 *         @Override
 *         public void onApplicationEvent(LoggerEvent event) {
 *             log.info("Received LoggerEvent: {}", event);
 *             log.info(event.getMessage());
 *         }
 *     }
 * }
 * </pre>
 *
 * @author lzhpo
 * @see org.springframework.context.ApplicationListener
 */
@Slf4j
@TestConfiguration
public class LoggerListenerTest {

    @EventListener
    public void process(LoggerEvent event) {
        log.info("Received LoggerEvent: {}", event);
        log.info(event.getMessage());
    }
}
