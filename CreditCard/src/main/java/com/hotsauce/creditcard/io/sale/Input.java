package com.hotsauce.creditcard.io.sale;

import com.hotsauce.creditcard.io.DeviceInfo;

import java.math.BigDecimal;

public class Input {
   public BigDecimal Amount = BigDecimal.ZERO;
   public BigDecimal Tip = BigDecimal.ZERO;
   public BigDecimal Tax = BigDecimal.ZERO;
   public BigDecimal ServiceFee = BigDecimal.ZERO;
   public String TransactionID;
   public DeviceInfo deviceInfo;
}
