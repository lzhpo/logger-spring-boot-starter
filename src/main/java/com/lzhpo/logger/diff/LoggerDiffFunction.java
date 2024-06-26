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

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.logger.LoggerConstant;
import com.lzhpo.logger.LoggerDiffProperties;
import com.lzhpo.logger.annotation.LoggerComponent;
import com.lzhpo.logger.annotation.LoggerDiffField;
import com.lzhpo.logger.annotation.LoggerDiffObject;
import com.lzhpo.logger.annotation.LoggerFunction;
import com.lzhpo.logger.context.LoggerContextHolder;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * @author lzhpo
 */
@Slf4j
@LoggerComponent
public class LoggerDiffFunction implements InitializingBean {

    private static LoggerDiffProperties loggerDiffProperties;
    private static final String DIFF_MESSAGE_TEMPLATE_FILED_NAME = "filedName";
    private static final String DIFF_MESSAGE_TEMPLATE_OLD_FIELD_VALUE = "oldValue";
    private static final String DIFF_MESSAGE_TEMPLATE_NEW_FIELD_VALUE = "newValue";

    /**
     * Diff two object difference function.
     *
     * <ul>
     *     <li><b>Updated field</b>: field exists in old object and new object, but value not same.</li>
     *     <li><b>Deleted field</b>: field not exists in new object, but exist in old object.</li>
     *     <li><b>Added field</b>: field not exists in old object, but exist in new object.</li>
     * </ul>
     *
     * @param oldObject the old object
     * @param newObject the new object
     * @return the formated diff message
     */
    @LoggerFunction(LoggerConstant.FUNCTION_DIFF)
    public static String diff(Object oldObject, Object newObject) {
        log.debug("DIFF oldObject={}, newObject={}", oldObject, newObject);
        if (Objects.isNull(oldObject) || Objects.isNull(newObject)) {
            return StrUtil.EMPTY;
        }

        if (isDisabledObjectDiff(oldObject, newObject)) {
            log.debug("The oldObject or newObject has been disabled diff.");
            return StrUtil.EMPTY;
        }

        final DiffObjectResult diffObjectResult;
        if (isSimpleValueType(oldObject, newObject)) {
            diffObjectResult = getDiffSimpleResult(oldObject, newObject);
        } else {
            diffObjectResult = getDiffObjectResult(oldObject, newObject);
        }

        LoggerContextHolder.putDiffResult(diffObjectResult);
        return formatDiffResultsMessage(diffObjectResult.getFieldResults());
    }

    /**
     * Get diff object result.
     *
     * @param oldObject the old object
     * @param newObject the new object
     * @return the diff result
     */
    // spotless:off
    public static DiffObjectResult getDiffObjectResult(Object oldObject, Object newObject) {
        Class<?> oldObjectClass = oldObject.getClass();
        Class<?> newObjectClass = newObject.getClass();

        DiffObjectResult diffObjectResult = createDiffObjectResult(oldObjectClass.getName(), newObjectClass.getName());
        List<DiffFieldResult> diffFieldResults = diffObjectResult.getFieldResults();

        Map<String, Field> oldFieldMap = getFieldsMap(oldObjectClass);
        Map<String, Field> newFieldMap = getFieldsMap(newObjectClass);
        log.debug("Found {} field in old object, found {} field in new object", oldFieldMap.size(), newFieldMap.size());

        oldFieldMap.forEach((oldFieldName, oldField) -> {
            Object oldValue = ReflectUtil.getFieldValue(oldObject, oldFieldName);
            Field newField = newFieldMap.get(oldFieldName);

            if (Objects.nonNull(newField)) {
                Object newValue = ReflectUtil.getFieldValue(newObject, oldFieldName);

                final DiffState state;
                if (!Objects.equals(oldValue, newValue) && ObjectUtil.isAllNotEmpty(oldValue, newValue)) {
                    state = DiffState.UPDATED;
                } else if (Objects.nonNull(oldValue) && Objects.isNull(newValue)) {
                    state = DiffState.DELETED;
                } else if (Objects.isNull(oldValue) && Objects.nonNull(newValue)) {
                    state = DiffState.ADDED;
                } else {
                    return;
                }

                DiffFieldResult diffFieldResult = createDiffFieldResult(oldFieldName, oldField, oldValue, newField, newValue);
                diffFieldResult.setState(state);
                diffFieldResults.add(diffFieldResult);
            } else {
                DiffFieldResult diffFieldResult = createDiffFieldResult(oldFieldName, oldField, oldValue, null, null);
                diffFieldResult.setState(DiffState.DELETED);
                diffFieldResults.add(diffFieldResult);
            }
        });

        newFieldMap.forEach((newFieldName, newField) -> {
            if (!oldFieldMap.containsKey(newFieldName)) {
                Object newValue = ReflectUtil.getFieldValue(newObject, newFieldName);
                DiffFieldResult diffFieldResult = createDiffFieldResult(newFieldName, null, null, newField, newValue);
                diffFieldResult.setState(DiffState.ADDED);
                diffFieldResults.add(diffFieldResult);
            }
        });

        return diffObjectResult;
    }
    // spotless:on

    /**
     * Get diff simple value type result.
     *
     * @param oldObject the old object
     * @param newObject the new object
     * @return the diff result
     */
    public static DiffObjectResult getDiffSimpleResult(Object oldObject, Object newObject) {
        Class<?> oldObjectClass = oldObject.getClass();
        Class<?> newObjectClass = newObject.getClass();
        DiffObjectResult diffObjectResult = createDiffObjectResult(oldObjectClass.getName(), newObjectClass.getName());
        if (!Objects.equals(oldObject, newObject)) {
            DiffFieldResult diffFieldResult = createDiffFieldResult(null, null, oldObject, null, newObject);
            diffFieldResult.setState(DiffState.UPDATED);
            diffObjectResult.getFieldResults().add(diffFieldResult);
        }
        return diffObjectResult;
    }

    /**
     * Format diff message by configured template.
     *
     * @param diffFieldResults the diff field results
     * @return the formated diff message
     */
    // spotless:off
    public static String formatDiffResultsMessage(List<DiffFieldResult> diffFieldResults) {
        Map<DiffState, String> templateMap = loggerDiffProperties.getTemplate();
        StringJoiner messageJoiner = new StringJoiner(loggerDiffProperties.getDelimiter());

        diffFieldResults.stream().filter(fieldResult -> Objects.nonNull(fieldResult.getState())).forEach(fieldResult -> {
            DiffState diffState = fieldResult.getState();
            Map<String, Object> variablesMap = createMessageTemplateMap(fieldResult);
            String message = diffState.format(templateMap, variablesMap);
            messageJoiner.add(message);
        });

        return messageJoiner.toString();
    }
    // spotless:on

    /**
     * Whether the old object or new object is disabled diff.
     *
     * @param oldObject the old object
     * @param newObject the new object
     * @return the result of whether disabled diff object
     */
    public static boolean isDisabledObjectDiff(Object oldObject, Object newObject) {
        return Optional.of(oldObject.getClass())
                        .map(clazz -> clazz.getAnnotation(LoggerDiffObject.class))
                        .map(LoggerDiffObject::disabled)
                        .isPresent()
                || Optional.of(newObject.getClass())
                        .map(clazz -> clazz.getAnnotation(LoggerDiffObject.class))
                        .map(LoggerDiffObject::disabled)
                        .isPresent();
    }

    /**
     * Get object class all fields and convert to map.
     *
     * @param objectClass the object class
     * @return the fields map
     */
    public static Map<String, Field> getFieldsMap(Class<?> objectClass) {
        return Arrays.stream(ReflectUtil.getFields(objectClass, field -> {
                    LoggerDiffField loggerDiffField = field.getAnnotation(LoggerDiffField.class);
                    return Objects.isNull(loggerDiffField) || !loggerDiffField.disabled();
                }))
                .collect(Collectors.toMap(Field::getName, Function.identity()));
    }

    /**
     * Whether is simple value type.
     *
     * @param oldObject the old object
     * @param newObject the new object
     * @return the result of whether is simple value type
     */
    public static boolean isSimpleValueType(Object oldObject, Object newObject) {
        return ClassUtil.isSimpleValueType(oldObject.getClass()) || ClassUtil.isSimpleValueType(newObject.getClass());
    }

    /**
     * Create diff object result.
     *
     * @param oldObjectClassName the old object class name
     * @param newObjectClassName the new object class name
     * @return the diff object result
     */
    public static DiffObjectResult createDiffObjectResult(String oldObjectClassName, String newObjectClassName) {
        DiffObjectResult diffObjectResult = new DiffObjectResult();
        diffObjectResult.setOldObjectName(oldObjectClassName);
        diffObjectResult.setNewObjectName(newObjectClassName);
        diffObjectResult.setFieldResults(new ArrayList<>());
        return diffObjectResult;
    }

    /**
     * Create diff field result.
     *
     * @param fieldName the old object field name
     * @param oldValue  the old object field value
     * @param newValue  the new object field value
     * @return the diff field result
     */
    public static DiffFieldResult createDiffFieldResult(
            String fieldName, Field oldField, Object oldValue, Field newField, Object newValue) {
        DiffFieldResult diffResult = new DiffFieldResult();
        diffResult.setFieldName(fieldName);
        diffResult.setNewTitle(getFieldTitle(newField));
        diffResult.setNewValue(newValue);
        diffResult.setOldTitle(getFieldTitle(oldField));
        diffResult.setOldValue(oldValue);
        return diffResult;
    }

    /**
     * Get field title.
     *
     * @param field the field
     * @return field title
     */
    public static String getFieldTitle(Field field) {
        return Optional.ofNullable(field)
                .map(x -> x.getAnnotation(LoggerDiffField.class))
                .map(LoggerDiffField::title)
                .filter(StringUtils::hasText)
                .orElse(null);
    }

    /**
     * Create diff format message template map.
     *
     * @param diffResult the diff result
     * @return the message template map
     */
    public static Map<String, Object> createMessageTemplateMap(DiffFieldResult diffResult) {
        String title = Optional.ofNullable(diffResult.getOldTitle()).orElseGet(diffResult::getNewTitle);
        String fieldName = StrUtil.blankToDefault(title, diffResult.getFieldName());

        Map<String, Object> messageTemplateMap = new HashMap<>();
        messageTemplateMap.put(DIFF_MESSAGE_TEMPLATE_FILED_NAME, fieldName);
        messageTemplateMap.put(DIFF_MESSAGE_TEMPLATE_OLD_FIELD_VALUE, diffResult.getOldValue());
        messageTemplateMap.put(DIFF_MESSAGE_TEMPLATE_NEW_FIELD_VALUE, diffResult.getNewValue());
        return messageTemplateMap;
    }

    @Override
    public void afterPropertiesSet() {
        loggerDiffProperties = SpringUtil.getBean(LoggerDiffProperties.class);
    }
}
