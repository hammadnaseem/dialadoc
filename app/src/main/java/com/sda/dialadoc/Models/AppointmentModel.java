package com.sda.dialadoc.Models;

import java.io.Serializable;

public class AppointmentModel extends User implements Serializable {
    private String time,date,details,status;

    public AppointmentModel() {
    }

    public AppointmentModel(String name, String age, String gender, String email, String phoneNum, String userType, String id, String time, String date, String details,String status) {
        super(name, age, gender, email, phoneNum, userType, id);
        this.time = time;
        this.date = date;
        this.details = details;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    public void setUser(User user){

       setName(user.getName());
       setAge(user.getAge());
       setGender(user.getGender());
       setUserId(user.getUserId());
       setPhoneNum(user.getPhoneNum());
       setEmail(user.getEmail());
       setUserType(user.getUserType());



    }

}
