package com.ajithvgiri.libphoneval.model;

import com.ajithvgiri.libphoneval.Phonenumber;

public class PhoneModel {
    long id;
    String name;
    String phone;
    Phonenumber.PhoneNumber phoneNumber;

    public PhoneModel() {

    }

    public PhoneModel(long id, String name, String phone, Phonenumber.PhoneNumber phoneNumber) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.phoneNumber = phoneNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Phonenumber.PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Phonenumber.PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
