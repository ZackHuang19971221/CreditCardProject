package com.hotsauce.creditcard.io.auth;

import com.hotsauce.creditcard.io.IReferNumber;
import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;

public interface Response extends IReferNumber {
    CreditCardUtil.CardIssuers getCardIssuers();
    String getCardNumber();
    String getExpDate();
}
