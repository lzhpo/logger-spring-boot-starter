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
package com.lzhpo.logger.diff;

import cn.hutool.core.util.StrUtil;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzhpo
 */
@Getter
@AllArgsConstructor
public enum DiffState {

    /**
     * The field not exists in old object, but exist in new object.
     */
    ADDED {
        @Override
        public String format(Map<DiffState, String> templateMap, Map<String, Object> variableMap) {
            return StrUtil.format(templateMap.get(ADDED), variableMap, false);
        }
    },

    /**
     * The field exists in old object and new object, but value not same.
     */
    UPDATED {
        @Override
        public String format(Map<DiffState, String> templateMap, Map<String, Object> variableMap) {
            return StrUtil.format(templateMap.get(UPDATED), variableMap, false);
        }
    },

    /**
     * The field not exists in new object, but exist in old object.
     */
    DELETED {
        @Override
        public String format(Map<DiffState, String> templateMap, Map<String, Object> variableMap) {
            return StrUtil.format(templateMap.get(DELETED), variableMap, false);
        }
    };

    /**
     * Use template and variable to format message.
     *
     * @param templateMap the template map
     * @param variableMap the variable map
     * @return the formated message
     */
    public abstract String format(Map<DiffState, String> templateMap, Map<String, Object> variableMap);
}
