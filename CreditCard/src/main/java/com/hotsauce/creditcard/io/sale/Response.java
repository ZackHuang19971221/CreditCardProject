package com.hotsauce.creditcard.io.sale;

import com.hotsauce.creditcard.io.IReferNumber;
import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;

public interface Response extends IReferNumber {
    CreditCardUtil.CardIssuers getCardIssuers();
    String getCardNumber();
    String getExpDate();
}
