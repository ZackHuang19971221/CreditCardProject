package com.hotsauce.creditcard.io.manage;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class PosLinkManageData {
    public PosLinkManageData(@NonNull String userName,@NonNull String userPassword,@NonNull String merchantId,@NonNull String deviceId) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.merchantId = merchantId;
        this.deviceId = deviceId;
    }
    private String userName;
    private String userPassword;
    private String merchantId;
    private String deviceId;
}
