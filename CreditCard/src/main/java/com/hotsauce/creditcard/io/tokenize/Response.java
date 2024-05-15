package com.hotsauce.creditcard.io.tokenize;

import com.hotsauce.creditcard.io.IReferNumber;
import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;

public interface Response extends IReferNumber {
    String getToken();
    String getExpDate();
    CreditCardUtil.CardIssuers getCardIssuer();
}
