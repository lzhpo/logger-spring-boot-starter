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
package com.lzhpo.logger.event;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * @author lzhpo
 */
@Setter
@Getter
@ToString
public class LoggerEvent extends ApplicationEvent {

    private String logId;
    private String message;
    private Boolean success;
    private String operatorId;
    private String bizId;
    private String category;
    private String tag;
    private String additional;
    private Date createTime;
    private Long takeTime;
    private List<String> errors;

    public LoggerEvent(Object source) {
        super(source);
    }
}
