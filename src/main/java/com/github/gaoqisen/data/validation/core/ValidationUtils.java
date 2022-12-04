package com.github.gaoqisen.data.validation.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class ValidationUtils {

    /**
     * 配置文件配置的数据
     */
    private static final Map<String, Map<String, LimitValidation>> verifyMap = new HashMap<>();

    /**
     * 检验元数据
     */
    private static ValidationMeta validationMeta = null;

    /**
     * 存放校验处理器
     */
    private static List<ValidationHandler> handlerList = new ArrayList<>();

    /**
     * 扩展校验处理器
     */
    public static void register(ValidationHandler handler) {
        handlerList.add(handler);
    }

    public static void register(List<ValidationHandler> handlers) {
        handlerList.addAll(handlers);
    }

    static {
        handlerList.add(new LengthValidationHandler());
        handlerList.add(new RegularHandler());
    }

    /**
     * 校验
     */
    public static ValidationUtils.ValidationResult validate(Object obj) {
        Class<?> aClass = obj.getClass();
        String name = aClass.getName();
        Map<String, LimitValidation> stringLimitValidationMap = verifyMap.get(name + ".yml");
        if(stringLimitValidationMap == null || stringLimitValidationMap.size() < 1) {
            return ValidationUtils.ValidationResult.buildSuccess();
        }
        ValidationUtils.ValidationResult validationResult = new ValidationUtils.ValidationResult();
        validationResult.setSuccess(true);
        doValidationResult(obj, stringLimitValidationMap, validationResult);
        return validationResult;
    }

    public static void setValidationTranslate(ValidationMeta validationTranslate) {
        validationMeta = validationTranslate;
    }

    public static void putVerifyVal(String classPath, Map<String, LimitValidation> verifyVal) {
        verifyMap.put(classPath, verifyVal);
    }

    private static void doValidationResult(Object obj, Map<String, LimitValidation> stringLimitValidationMap, ValidationUtils.ValidationResult validationResult) {
        // 必填逻辑处理
        Class<?> aClass = obj.getClass();
        List<String> fieldRequiredErr = new ArrayList<>();
        for (Map.Entry<String, LimitValidation> entry : stringLimitValidationMap.entrySet()) {
            LimitValidation value = entry.getValue();
            if(value == null) {
                continue;
            }
            if(value.getRequired()) {
                Object invoke = invokeGetMethods(obj, aClass, entry);
                if(invoke == null) {
                    String requiredErr = String.format(validationMeta.getRequired(), entry.getValue().getDesc(), entry.getKey());
                    validationResult.setSuccess(false);
                    if(validationMeta.getSkip()) {
                        return;
                    }
                    fieldRequiredErr.add(requiredErr);
                }
            }
        }


        if(fieldRequiredErr.size() > 0) {
            validationResult.setFieldCases(fieldRequiredErr);
        }
        Field[] declaredFields = aClass.getDeclaredFields();
        TypeHandlerContext context = TypeHandlerContext.build(validationResult, validationMeta);
        context.setHandlerList(handlerList);
        for (Field field : declaredFields) {
            // 准备数据
            String fieldName = field.getName();
            LimitValidation limitValidation = stringLimitValidationMap.get(fieldName);
            if(limitValidation == null) {
                continue;
            }
            buildArgs(context, field, obj);
            if(context.getArgs() == null) {
                continue;
            }
            limitValidation.setFieldName(fieldName);
            context.setLimitValidation(limitValidation);

            // 遍历处理器
            for (ValidationHandler validationHandler : handlerList) {
                boolean handler = validationHandler.validationHandler(context);
                if(!handler) {
                    break;
                }
            }

            // is list
            if(field.getType().equals(List.class)) {
                List<?> list = (List<?>)context.getArgs();
                for (Object o : list) {
                    if(o instanceof String) {
                        // List<String>

                        continue;
                    }
                    doValidationResult(o,limitValidation.getChildren(), validationResult);
                }
                break;
            }

            // is object
            if(field.getType().getClassLoader() != null) {
                doValidationResult(context.getArgs(),limitValidation.getChildren(), validationResult);
            }
        }
    }

    /**
     * 加载参数
     */
    public static void loadYmlParam() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:validation/**.yml");
        if(resources.length < 1) {
            return;
        }
        for (Resource resource : resources) {
            YamlMapFactoryBean yamlMapFb = new YamlMapFactoryBean();
            yamlMapFb.setResources(resource);
            String filename = resource.getFilename();
            if(!StringUtils.hasLength(filename)) {
                continue;
            }
            Map<String, Object> object = yamlMapFb.getObject();
            if(object == null) {
                continue;
            }
            if(filename.contains("validation-rule")) {
                ValidationMeta validationTranslate = new ValidationMeta();
                BeanMap beanMap = BeanMap.create(validationTranslate);
                beanMap.putAll(object);
                ValidationUtils.setValidationTranslate(validationTranslate);
                continue;
            }

            Map<String, LimitValidation> validationParamMap = new HashMap<>();
            for (Map.Entry<String, Object> stringObjectEntry : object.entrySet()) {

                Object value = stringObjectEntry.getValue();

                LimitValidation validationParam = JSONObject.toJavaObject((JSON) JSON.toJSON(value), LimitValidation.class);

                validationParamMap.put(stringObjectEntry.getKey(), validationParam);
            }
            ValidationUtils.putVerifyVal(filename, validationParamMap);
        }
    }


    private static Object invokeGetMethods(Object obj, Class<?> aClass, Map.Entry<String, LimitValidation> entry) {
        Object invoke = null;
        try {
            PropertyDescriptor propertyDescriptor = new PropertyDescriptor(entry.getKey(), aClass);
            Method readMethod = propertyDescriptor.getReadMethod();
            invoke = readMethod.invoke(obj);
        } catch (IntrospectionException  | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(String.format("调用Get失败，请检查字段[%s]是否配置正确，以及[%s]是否存在get方法", entry.getKey(), aClass.getCanonicalName()));
        }
        return invoke;
    }

    private static void buildArgs(TypeHandlerContext context, Field field, Object obj) {
        try {
            field.setAccessible(true);
            context.setArgs(field.get(obj));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("validation get object value error");
        }
    }

    public static class ValidationResult {

        private Boolean success;

        private String failMessage;

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

        public List<String> getFieldCases() {
            return fieldCases;
        }

        public void setFieldCases(List<String> fieldCases) {
            this.fieldCases = fieldCases;
        }
    }
    
}

class ValidationMeta {

    private String maximun;

    private String minimun;

    private String reqex;

    private String type;

    private String required;

    private Boolean skip = false;

    @Override
    public String toString() {
        return "ValidationMeta{" +
                "maxLength='" + maximun + '\'' +
                ", minLength='" + minimun + '\'' +
                ", reqex='" + reqex + '\'' +
                ", type='" + type + '\'' +
                ", required='" + required + '\'' +
                ", skip=" + skip +
                '}';
    }

    public String getMaximun() {
        return maximun;
    }

    public void setMaximun(String maximun) {
        this.maximun = maximun;
    }

    public String getMinimun() {
        return minimun;
    }

    public void setMinimun(String minimun) {
        this.minimun = minimun;
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



class TypeHandlerContext {

    private Object args;

    private LimitValidation limitValidation;

    private ValidationUtils.ValidationResult validationResult;

    private ValidationMeta validationMeta;

    private List<ValidationHandler> handlerList;


    public static TypeHandlerContext build(ValidationUtils.ValidationResult validationResult, ValidationMeta meta) {
        TypeHandlerContext context = new TypeHandlerContext();
        context.setValidationResult(validationResult);
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
                ", validationMeta=" + validationMeta +
                '}';
    }

    public Object getArgs() {
        return args;
    }

    public List<ValidationHandler> getHandlerList() {
        return handlerList;
    }

    public void setHandlerList(List<ValidationHandler> handlerList) {
        this.handlerList = handlerList;
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

    public ValidationUtils.ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationUtils.ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationMeta getValidationMeta() {
        return validationMeta;
    }

    public void setValidationMeta(ValidationMeta validationMeta) {
        this.validationMeta = validationMeta;
    }

}


class LimitValidation implements Serializable {

    private Integer maximun;

    private Integer minimun;

    private String reqex;

    private String desc;

    private Boolean required;

    private String fieldName;

    private Map<String, LimitValidation> children;

    public LimitValidation() {

    }

    @Override
    public String toString() {
        return "LimitValidation{" +
                "maximun=" + maximun +
                ", minimun=" + minimun +
                ", reqex='" + reqex + '\'' +
                ", desc='" + desc + '\'' +
                ", required=" + required +
                ", fieldName='" + fieldName + '\'' +
                ", children=" + children +
                '}';
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setChildren(Map<String, LimitValidation> children) {
        this.children = children;
    }

    public LimitValidation(Integer maximun, Integer minimun, String reqex, String desc, Boolean required, Map<String, LimitValidation> children) {
        this.maximun = maximun;
        this.minimun = minimun;
        this.reqex = reqex;
        this.desc = desc;
        this.required = required;
        this.children = children;
    }

    public Integer getMaximun() {
        return maximun;
    }

    public void setMaximun(Integer maximun) {
        this.maximun = maximun;
    }

    public Integer getMinimun() {
        return minimun;
    }

    public void setMinimun(Integer minimun) {
        this.minimun = minimun;
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

    public Map<String, LimitValidation> getChildren() {
        return children;
    }
}

interface ValidationHandler {

    boolean validationHandler(TypeHandlerContext context);

}

class RegularHandler implements ValidationHandler {

    @Override
    public boolean validationHandler(TypeHandlerContext context) {
        ValidationMeta validationMeta = context.getValidationMeta();
        ValidationUtils.ValidationResult validationResult = context.getValidationResult();
        LimitValidation limitValidation = context.getLimitValidation();
        if(context.getArgs() == null) {
            return false;
        }
        if(limitValidation == null || !StringUtils.hasLength(limitValidation.getReqex())) {
            return true;
        }
        Pattern compile = Pattern.compile(limitValidation.getReqex());
        boolean pattern = compile.matcher(String.valueOf(context.getArgs())).find();
        if(pattern) {
            return true;
        }
        String failMsg = String.format(validationMeta.getReqex(), limitValidation.getDesc(), limitValidation.getFieldName());
        if(validationMeta.getSkip()) {
            validationResult.setFailMessage(failMsg);
            return false;
        }
        validationResult.getFieldCases().add(failMsg);
        return true;
    }
}

class LengthValidationHandler implements ValidationHandler {

    @Override
    public boolean validationHandler(TypeHandlerContext context) {
        LimitValidation limitValidation = context.getLimitValidation();
        if(limitValidation == null) {
            return true;
        }
        Object args = context.getArgs();
        if(args == null) {
            return false;
        }
        ValidationMeta validationMeta = context.getValidationMeta();
        List<String> failMsgList = new ArrayList<>();
        ValidationUtils.ValidationResult validationResult = context.getValidationResult();
        if(args instanceof String) {
            String str = (String) args;
            if (lengthHandler(limitValidation, validationMeta, failMsgList, validationResult, str.length())){
                return false;
            }
            validationResult.getFieldCases().addAll(failMsgList);
            return true;
        }

        if(args instanceof Number) {
            int num = (int) args;
            if (lengthHandler(limitValidation, validationMeta, failMsgList, validationResult, num)){
                return false;
            }
            validationResult.getFieldCases().addAll(failMsgList);
            return true;
        }

        if(args instanceof List) {
            List<?> list = (List<?>) args;
            if (lengthHandler(limitValidation, validationMeta, failMsgList, validationResult, list.size())){
                return false;
            }
            if(CollectionUtils.isEmpty(list)) {
                return true;
            }
        }
        validationResult.getFieldCases().addAll(failMsgList);
        return true;
    }

    private boolean lengthHandler(LimitValidation limitValidation, ValidationMeta validationMeta, List<String> failMsgList, ValidationUtils.ValidationResult validationResult, int num) {
        if (limitValidation.getMaximun() != null && num > limitValidation.getMaximun()) {
            String failMsg = String.format(validationMeta.getMaximun(), limitValidation.getDesc(),limitValidation.getFieldName(), num);
            validationResult.setSuccess(false);
            if (validationMeta.getSkip()) {
                validationResult.setFailMessage(failMsg);
                return true;
            }
            failMsgList.add(failMsg);
        }

        if (limitValidation.getMinimun() != null && num < limitValidation.getMinimun()) {
            String failMsg = String.format(validationMeta.getMinimun(), limitValidation.getDesc(), limitValidation.getFieldName(), num);
            validationResult.setFailMessage(failMsg);
            if (validationMeta.getSkip()) {
                validationResult.setSuccess(false);
                return true;
            }
            failMsgList.add(failMsg);
        }
        return false;
    }
}


