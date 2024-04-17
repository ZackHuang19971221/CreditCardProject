package com.hotsauce.creditcard.io.auth;
import com.hotsauce.creditcard.io.DeviceInfo;

import java.math.BigDecimal;

public class Input {
    public String TransactionID;
    public BigDecimal AuthAmount= BigDecimal.ZERO;
    public DeviceInfo deviceInfo;
}
