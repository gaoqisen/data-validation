package com.github.gaoqisen.data.validation.entity;

import com.github.gaoqisen.data.validation.core.ValidationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;


public class ValidationTest {

    @Before
    public void init() throws IOException {
        ValidationUtils.loadYmlParam();
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

        AddressEntity addressDetail = new AddressEntity();
        addressDetail.setProvince("广东省");
        addressDetail.setCity("深圳市");
        addressDetail.setArea("固戍");
        addressDetail.setTest(12);
        userEntity.setAddressDetail(addressDetail);

        ValidationUtils.ValidationResult validate = ValidationUtils.validate(userEntity);
        System.out.println(validate.toString());

        Assert.assertNotNull(validate.getFieldCases());
    }

}
