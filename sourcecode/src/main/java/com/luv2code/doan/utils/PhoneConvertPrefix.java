package com.luv2code.doan.utils;

public class PhoneConvertPrefix {
    public String convertPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            return "+84" + phoneNumber.substring(1);
        } else {
            return phoneNumber;
        }
    }
}
