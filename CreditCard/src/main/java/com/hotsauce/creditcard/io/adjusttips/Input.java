package com.hotsauce.creditcard.io.adjusttips;

import com.hotsauce.creditcard.io.DeviceInfo;

import java.math.BigDecimal;

public class Input {
    public String TransactionID;
    public BigDecimal Tip = BigDecimal.ZERO;

    public String RefNumber;
    public DeviceInfo deviceInfo;
}
