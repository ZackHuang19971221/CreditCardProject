package com.hotsauce.creditcard.io.manage;

import com.hotsauce.creditcard.providers.POSLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PosLink {
    public POSLink() {

    }
    private String userName;
    private String userPassword;
    private String merchantId;
    private String deviceId;
}
