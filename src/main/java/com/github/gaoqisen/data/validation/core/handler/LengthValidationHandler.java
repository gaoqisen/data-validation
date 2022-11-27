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
        if(limitValidation == null) {
            return true;
        }
        Object args = context.getArgs();
        if(args == null) {
            return true;
        }
        ValidationMeta validationMeta = context.getValidationMeta();
        List<String> failMsgList = new ArrayList<>();
        ValidationResult validationResult = context.getValidationResult();
        if(args instanceof String) {
            String str = (String) args;
            if (lengthHandler(limitValidation, validationMeta, failMsgList, validationResult, str.length())){
                return false;
            }
        }

        if(args instanceof Integer) {
            int num = (int) args;
            if (lengthHandler(limitValidation, validationMeta, failMsgList, validationResult, num)){
                return false;
            }
        }

        if(args instanceof List) {
            List<?> list = (List<?>) args;
            if (lengthHandler(limitValidation, validationMeta, failMsgList, validationResult, list.size())){
                return false;
            }
        }
        validationResult.setFieldCases(failMsgList);
        return true;
    }

    private boolean lengthHandler(LimitValidation limitValidation, ValidationMeta validationMeta, List<String> failMsgList, ValidationResult validationResult, int num) {
        if (num > limitValidation.getMaxLength()) {
            String failMsg = String.format(validationMeta.getMaxLength(), limitValidation.getDesc());
            if (validationMeta.getSkip()) {
                validationResult.setSuccess(false);
                validationResult.setFailMessage(failMsg);
                return true;
            }
            failMsgList.add(failMsg);
        }

        if (num < limitValidation.getMinLength()) {
            String failMsg = String.format(validationMeta.getMinLength(), limitValidation.getDesc());
            if (validationMeta.getSkip()) {
                validationResult.setSuccess(false);
                validationResult.setFailMessage(failMsg);
                return true;
            }
            failMsgList.add(failMsg);
        }
        return false;
    }
}
