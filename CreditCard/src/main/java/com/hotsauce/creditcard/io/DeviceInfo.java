package com.hotsauce.creditcard.io;

import com.hotsauce.creditcard.providers.ProviderType;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Pattern;

@Data
@Builder
public class DeviceInfo {
    public DeviceInfo(String ip,int port, int timeout, int retryTime) {
        if(!isValidIPv4(ip)) {
            throw new IllegalArgumentException("IP Address Is Invalid");
        }
        if(!isValidPort(port)) {
            throw new IllegalArgumentException("Port Is Invalid , Port Range : " + startPort + " - " + endPort);
        }
        if(!isValidTimeOut(timeout)) {
            throw  new IllegalArgumentException("TimeOut Is Invalid , Time Out Range : " + minTimeOut + " - " + maxTimeOut);
        }
        setIp(ip);
        setPort(port);
        setTimeOut(timeout);
        setRetryTime(Math.max(retryTime,0));
    }
    private String ip;
    private int port;
    private int timeOut;
    private int retryTime;

    private boolean isValidIPv4(String ip){
        if(ip == null){return  false;}
        String ipv4Pattern = "^((\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d{2}|2[0-4]\\d|25[0-5])$";
        return Pattern.matches(ipv4Pattern, ip);
    }

    private final int startPort = 0;
    private final int endPort = 65535;
    private boolean isValidPort(int port) {
        return port >= 0 && port <= 65535;
    }

    private final int minTimeOut = 500;
    private final int maxTimeOut = 60000;
    private boolean isValidTimeOut(int timeOut) {
        return timeOut >= minTimeOut && maxTimeOut >= timeOut;
    }

    public static int getDeviceDefaultPort(ProviderType type) {
        switch (type){
            case Developer:
                return 8080;
            case SPIN :
                return 1;
            case POSLink:
                return 10009;
            case Ingenico:
                return 2;
            default:
                break;
        }
        return 0;
    }
}
