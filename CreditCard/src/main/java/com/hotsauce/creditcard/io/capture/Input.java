package com.hotsauce.creditcard.io.capture;

import com.hotsauce.creditcard.io.DeviceInfo;

import java.math.BigDecimal;

public class Input {
    public DeviceInfo deviceInfo;
    public String TransactionID;
    public BigDecimal AuthAmount = BigDecimal.ZERO;
    public BigDecimal Amount = BigDecimal.ZERO;
    public BigDecimal Tip = BigDecimal.ZERO;
    public BigDecimal Tax = BigDecimal.ZERO;
    public BigDecimal ServiceFee = BigDecimal.ZERO;
    public String RefNumber;
}
