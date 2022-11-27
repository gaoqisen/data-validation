package com.github.gaoqisen.data.validation.core;

import com.github.gaoqisen.data.validation.bo.LimitValidation;
import com.github.gaoqisen.data.validation.bo.ValidationMeta;
import com.github.gaoqisen.data.validation.bo.ValidationResult;

import java.util.Map;

public class TypeHandlerContext {

    private Object args;

    private LimitValidation limitValidation;

    private ValidationResult validationResult;

    private Map<String, ValidationHandler> validationHandlerMap;

    private ValidationMeta validationMeta;

    public static TypeHandlerContext build(ValidationResult validationResult, Map<String, ValidationHandler> validationHandlerMap, ValidationMeta meta) {
        TypeHandlerContext context = new TypeHandlerContext();
        context.setValidationResult(validationResult);
        context.setValidationHandlerMap(validationHandlerMap);
        context.setValidationMeta(meta);
        return context;
    }

    public TypeHandlerContext() {
    }

    @Override
    public String toString() {
        return "TypeHandlerContext{" +
                "args=" + args +
                ", limitValidation=" + limitValidation +
                ", validationResult=" + validationResult +
                ", validationHandlerMap=" + validationHandlerMap +
                ", validationMeta=" + validationMeta +
                '}';
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }

    public LimitValidation getLimitValidation() {
        return limitValidation;
    }

    public void setLimitValidation(LimitValidation limitValidation) {
        this.limitValidation = limitValidation;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public Map<String, ValidationHandler> getValidationHandlerMap() {
        return validationHandlerMap;
    }

    public void setValidationHandlerMap(Map<String, ValidationHandler> validationHandlerMap) {
        this.validationHandlerMap = validationHandlerMap;
    }

    public ValidationMeta getValidationMeta() {
        return validationMeta;
    }

    public void setValidationMeta(ValidationMeta validationMeta) {
        this.validationMeta = validationMeta;
    }

}
