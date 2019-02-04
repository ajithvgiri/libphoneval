package com.ajithvgiri.libphoneval.model;

import com.ajithvgiri.libphoneval.Phonenumber;

public class PhoneModel {
    long rawId;
    long contact_id;
    String name;
    String phone;
    Phonenumber.PhoneNumber phoneNumber;

    public PhoneModel() {

    }

    public PhoneModel(long rawId, long contact_id, String name, String phone, Phonenumber.PhoneNumber phoneNumber) {
        this.rawId = rawId;
        this.contact_id = contact_id;
        this.name = name;
        this.phone = phone;
        this.phoneNumber = phoneNumber;
    }

    public long getRawId() {
        return rawId;
    }

    public void setRawId(long rawId) {
        this.rawId = rawId;
    }

    public long getContact_id() {
        return contact_id;
    }

    public void setContact_id(long contact_id) {
        this.contact_id = contact_id;
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
