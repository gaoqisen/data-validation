package com.github.gaoqisen.data.validation.bo;

import java.util.List;

public class ValidationResult {

    private Boolean success;

    private String failMessage;

    private String code;

    private List<String> fieldCases;

    public static ValidationResult buildSuccess() {
        ValidationResult result = new ValidationResult();
        result.setSuccess(true);
        result.setFailMessage("success");
        return result;
    }

    public static ValidationResult buildFail(String fail) {
        ValidationResult result = new ValidationResult();
        result.setSuccess(false);
        result.setFailMessage(fail);
        return result;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "success=" + success +
                ", failMessage='" + failMessage + '\'' +
                ", code='" + code + '\'' +
                ", fieldCases=" + fieldCases +
                '}';
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getFieldCases() {
        return fieldCases;
    }

    public void setFieldCases(List<String> fieldCases) {
        this.fieldCases = fieldCases;
    }
}
