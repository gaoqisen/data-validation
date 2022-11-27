package com.github.gaoqisen.data.validation.core.handler;

import com.github.gaoqisen.data.validation.bo.LimitValidation;
import com.github.gaoqisen.data.validation.bo.ValidationMeta;
import com.github.gaoqisen.data.validation.bo.ValidationResult;
import com.github.gaoqisen.data.validation.core.TypeHandlerContext;
import com.github.gaoqisen.data.validation.core.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class LengthValidationHandler implements ValidationHandler {

    @Override
    public boolean validationHandler(TypeHandlerContext context) {
        LimitValidation limitValidation = context.getLimitValidation();
        Object args = context.getArgs();
        if(args == null) {
            return true;
        }
        ValidationMeta validationMeta = context.getValidationMeta();
        List<String> failMsgList = new ArrayList<>();
        ValidationResult validationResult = context.getValidationResult();
        if(args instanceof String) {
            String str = (String) args;
            if(str.length() > limitValidation.getMaxLength()) {
                String failMsg = String.format(validationMeta.getMaxLength(), limitValidation.getDesc());
                if(validationMeta.getSkip()) {
                    validationResult.setSuccess(false);
                    validationResult.setFailMessage(failMsg);
                    return false;
                }
                failMsgList.add(failMsg);
            }

            if(str.length() < limitValidation.getMinLength()) {
                String failMsg = String.format(validationMeta.getMinLength(), limitValidation.getDesc());
                if(validationMeta.getSkip()) {
                    validationResult.setSuccess(false);
                    validationResult.setFailMessage(failMsg);
                    return false;
                }
                failMsgList.add(failMsg);
            }
        }
        validationResult.setFieldCases(failMsgList);
        return true;
    }
}
