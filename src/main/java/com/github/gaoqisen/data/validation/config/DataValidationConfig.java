package com.github.gaoqisen.data.validation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gaoqisen.data.validation.bo.LimitValidation;
import com.github.gaoqisen.data.validation.bo.ValidationMeta;
import com.github.gaoqisen.data.validation.core.ValidationUtils;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataValidationConfig {

    @PostConstruct
    public void loadYmlParam() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:validation/**.yml");
        for (Resource resource : resources) {
            YamlMapFactoryBean yamlMapFb = new YamlMapFactoryBean();
            yamlMapFb.setResources(resource);
            String filename = resource.getFilename();
            if(!StringUtils.hasLength(filename)) {
                continue;
            }
            Map<String, Object> object = yamlMapFb.getObject();
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

                ObjectMapper objectMapper = new ObjectMapper();
                LimitValidation validationParam = objectMapper.convertValue(value ,LimitValidation.class);

                validationParamMap.put(stringObjectEntry.getKey(), validationParam);
            }
            ValidationUtils.putVerifyVal(filename, validationParamMap);
        }
    }


    public static void main(String[] args) throws IOException {



        ValidationUtils.validate(null);




    }


}
