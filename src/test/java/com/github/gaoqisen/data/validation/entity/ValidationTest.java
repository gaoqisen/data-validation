package com.github.gaoqisen.data.validation.entity;

import com.github.gaoqisen.data.validation.bo.ValidationResult;
import com.github.gaoqisen.data.validation.config.DataValidationConfig;
import com.github.gaoqisen.data.validation.core.ValidationUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;


public class ValidationTest {

    @Before
    public void init() throws IOException {
        DataValidationConfig dataValidationConfig = new DataValidationConfig();
        dataValidationConfig.loadYmlParam();
    }

    @Test
    public void test() {
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setProvince("广东省");
        addressEntity.setCity("深圳市");
        addressEntity.setArea("固戍");

        UserEntity userEntity = new UserEntity();
    //    userEntity.setName("test");
        userEntity.setAge(12);
        userEntity.setNick(Collections.singletonList("test"));
        userEntity.setAddress(Collections.singletonList(addressEntity));

        ValidationResult validate = ValidationUtils.validate(userEntity);
        assert validate != null;
        Assert.assertTrue(validate.getSuccess());
        System.out.println(validate.toString());
    }

}
