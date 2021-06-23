package com.sda.dialadoc.Models;

import java.io.Serializable;

public class User implements Serializable {
    private String name,age,gender,email,phoneNum,userType,userId;

    public User() {
    }

    public User(String name, String age, String gender, String email, String phoneNum, String userType, String id) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.email = email;
        this.phoneNum = phoneNum;
        this.userType = userType;
        this.userId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
    }
}
