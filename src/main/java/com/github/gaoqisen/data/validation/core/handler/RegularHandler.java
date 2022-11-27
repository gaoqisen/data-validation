package com.github.gaoqisen.data.validation.core.handler;

import com.github.gaoqisen.data.validation.bo.LimitValidation;
import com.github.gaoqisen.data.validation.bo.ValidationMeta;
import com.github.gaoqisen.data.validation.bo.ValidationResult;
import com.github.gaoqisen.data.validation.core.TypeHandlerContext;
import com.github.gaoqisen.data.validation.core.ValidationHandler;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

public class RegularHandler implements ValidationHandler {

    @Override
    public boolean validationHandler(TypeHandlerContext context) {
        ValidationMeta validationMeta = context.getValidationMeta();
        ValidationResult validationResult = context.getValidationResult();
        LimitValidation limitValidation = context.getLimitValidation();
        if(!StringUtils.hasLength(limitValidation.getReqex())) {
            return true;
        }
        Pattern compile = Pattern.compile(limitValidation.getReqex());
        boolean pattern = compile.matcher(String.valueOf(context.getArgs())).find();
        if(pattern) {
            return true;
        }
        String failMsg = String.format(validationMeta.getReqex(), limitValidation.getDesc());
        if(validationMeta.getSkip()) {
            validationResult.setFailMessage(failMsg);
            return false;
        }
        validationResult.getFieldCases().add(failMsg);
        return true;
    }
}
