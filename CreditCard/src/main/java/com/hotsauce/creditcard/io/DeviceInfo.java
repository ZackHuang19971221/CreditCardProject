package com.hotsauce.creditcard.io;

import java.util.regex.Pattern;

public class DeviceInfo {
    public String IP;
    public int Port;
    public  int Timeout;
    public  int RetryTime;
    public static boolean isValidIPv4(String ip){
        if(ip == null){return  false;}
        String ipv4Pattern = "^((\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])$";
        return Pattern.matches(ipv4Pattern, ip);
    }
}
