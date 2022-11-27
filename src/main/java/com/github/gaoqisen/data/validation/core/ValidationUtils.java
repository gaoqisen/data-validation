package com.github.gaoqisen.data.validation.core;

import com.github.gaoqisen.data.validation.bo.LimitValidation;
import com.github.gaoqisen.data.validation.bo.ValidationResult;
import com.github.gaoqisen.data.validation.bo.ValidationMeta;
import com.github.gaoqisen.data.validation.core.handler.LengthValidationHandler;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

public class ValidationUtils {

    private static final Map<String, Map<String, LimitValidation>> verifyMap = new HashMap<>();

    private static ValidationMeta validationMeta = null;

    private static List<ValidationHandler> handlerList = new ArrayList<>();

    private static Map<String, ValidationHandler> validationHandlerMap = new HashMap<>();

    public static void register(ValidationHandler handler) {
        handlerList.add(handler);
    }

    public static void register(List<ValidationHandler> handlers) {
        handlerList.addAll(handlers);
    }

    static {
        handlerList.add(new LengthValidationHandler());
    }

    public static void setValidationTranslate(ValidationMeta validationTranslate) {
        validationMeta = validationTranslate;
    }

    public static void putVerifyVal(String classPath, Map<String, LimitValidation> verifyVal) {
        verifyMap.put(classPath, verifyVal);
    }

    public static ValidationResult validate(Object obj) {
        Class<?> aClass = obj.getClass();
        String name = aClass.getName();
        Map<String, LimitValidation> stringLimitValidationMap = verifyMap.get(name + ".yml");
        if(stringLimitValidationMap == null || stringLimitValidationMap.size() < 1) {
            return ValidationResult.buildSuccess();
        }

        // 必填逻辑处理
        List<String> fieldRequiredErr = new ArrayList<>();
        for (Map.Entry<String, LimitValidation> entry : stringLimitValidationMap.entrySet()) {
            LimitValidation value = entry.getValue();
            if(value == null) {
                continue;
            }
            if(value.getRequired()) {
                Field field = null;
                try {
                     field = aClass.getField(entry.getKey());
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("validation get object field error");
                }
                if(!StringUtils.hasLength(field.getName())) {
                    String requiredErr = String.format(validationMeta.getRequired(), entry.getKey());
                    if(validationMeta.getSkip()) {
                        return ValidationResult.buildFail(requiredErr);
                    }
                    fieldRequiredErr.add(requiredErr);
                }
            }
        }

        ValidationResult validationResult = new ValidationResult();
        validationResult.setFieldCases(fieldRequiredErr);
        Field[] declaredFields = aClass.getDeclaredFields();
        TypeHandlerContext context = TypeHandlerContext.build(validationResult, validationHandlerMap, validationMeta);
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            field.setAccessible(true);

            LimitValidation limitValidation = stringLimitValidationMap.get(fieldName);
            buildArgs(context, field, obj);
            context.setLimitValidation(limitValidation);
            for (ValidationHandler validationHandler : handlerList) {
                boolean handler = validationHandler.validationHandler(context);
                if(!handler) {
                    break;
                }
            }
        }
        return context.getValidationResult();
    }

    private static void buildArgs(TypeHandlerContext context, Field field, Object obj) {
        try {
            context.setArgs(field.get(obj));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("validation get object value error");
        }
    }

}
