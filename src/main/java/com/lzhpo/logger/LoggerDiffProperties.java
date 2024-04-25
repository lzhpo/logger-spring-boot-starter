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

import com.lzhpo.logger.diff.DiffState;
import java.util.EnumMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The diff configurations.
 *
 * @author lzhpo
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "logger.diff")
public class LoggerDiffProperties implements InitializingBean {

    /**
     * The diff format message template.
     * <p>Allowed placeholders: {filedName}, {oldValue}, {newValue}
     */
    private Map<DiffState, String> template = new EnumMap<>(DiffState.class);

    /**
     * The diff format message delimiter if it has multiple diff results.
     */
    private String delimiter = LoggerConstant.DIFF_MESSAGE_DELIMITER;

    @Override
    public void afterPropertiesSet() {
        if (!template.containsKey(DiffState.ADDED)) {
            template.put(DiffState.ADDED, LoggerConstant.DIFF_MESSAGE_TEMPLATE_ADDED);
            log.debug("The diff added template not configure, use default initialized.");
        }

        if (!template.containsKey(DiffState.DELETED)) {
            template.put(DiffState.DELETED, LoggerConstant.DIFF_MESSAGE_TEMPLATE_DELETED);
            log.debug("The diff deleted template not configure, use default initialized.");
        }

        if (!template.containsKey(DiffState.UPDATED)) {
            template.put(DiffState.UPDATED, LoggerConstant.DIFF_MESSAGE_TEMPLATE_UPDATED);
            log.debug("The diff updated template not configure, use default initialized.");
        }
    }
}
