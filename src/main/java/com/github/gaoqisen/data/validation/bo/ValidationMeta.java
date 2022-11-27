package com.github.gaoqisen.data.validation.bo;

public class ValidationMeta {

    private String maxLength;

    private String minLength;

    private String reqex;

    private String type;

    private String required;

    private Boolean skip = false;

    @Override
    public String toString() {
        return "ValidationMeta{" +
                "maxLength='" + maxLength + '\'' +
                ", minLength='" + minLength + '\'' +
                ", reqex='" + reqex + '\'' +
                ", type='" + type + '\'' +
                ", required='" + required + '\'' +
                ", skip=" + skip +
                '}';
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getReqex() {
        return reqex;
    }

    public void setReqex(String reqex) {
        this.reqex = reqex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        this.skip = skip;
    }
}
