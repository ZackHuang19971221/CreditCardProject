package com.hotsauce.creditcard.io.manage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DejavooManageData {
    public DejavooManageData(String authKey,String registerId) {
        setAuthKey(authKey);
        setRegisterId(registerId);
    }
    private String authKey;
    private String registerId;
}
