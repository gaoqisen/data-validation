package com.github.gaoqisen.data.validation.entity;

import java.util.List;

public class UserEntity {

    private AddressEntity addressDetail;


    private String name;

    private Integer age;

    private List<String> nick;

    private List<AddressEntity> address;


    public AddressEntity getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(AddressEntity addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<String> getNick() {
        return nick;
    }

    public void setNick(List<String> nick) {
        this.nick = nick;
    }

    public List<AddressEntity> getAddress() {
        return address;
    }

    public void setAddress(List<AddressEntity> address) {
        this.address = address;
    }

    public static void main(String[] args) {
        int a = 1;
        System.out.println(String.class.getClassLoader());
        System.out.println(Integer.class.getClassLoader());
        System.out.println(AddressEntity.class.getClassLoader());
    }

}
