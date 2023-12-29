package com.luv2code.doan.service;


public interface OTPService {
    public static String KEY_FORGOT = "_forgot";
    public static String KEY_FORGOT_VERIFY = "_forgot_verify";

    public int generateOTP(String key);

    public int getOtp(String key);

    public void clearOTP(String key);

}