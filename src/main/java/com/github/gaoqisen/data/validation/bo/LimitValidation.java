package com.github.gaoqisen.data.validation.bo;

import java.io.Serializable;

public class LimitValidation implements Serializable {

    private Integer maxLength;

    private Integer minLength;

    private String reqex;

    private String desc;

    private Boolean required;

    private LimitValidation children;

    public LimitValidation() {

    }

    @Override
    public String toString() {
        return "LimitValidation{" +
                "maxLength=" + maxLength +
                ", minLength=" + minLength +
                ", reqex='" + reqex + '\'' +
                ", desc='" + desc + '\'' +
                ", required=" + required +
                ", children=" + children +
                '}';
    }

    public LimitValidation(Integer maxLength, Integer minLength, String reqex, String desc, Boolean required, LimitValidation children) {
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.reqex = reqex;
        this.desc = desc;
        this.required = required;
        this.children = children;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public String getReqex() {
        return reqex;
    }

    public void setReqex(String reqex) {
        this.reqex = reqex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public LimitValidation getChildren() {
        return children;
    }

    public void setChildren(LimitValidation children) {
        this.children = children;
    }
}
