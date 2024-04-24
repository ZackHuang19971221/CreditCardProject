package com.hotsauce.creditcard.io.auth;

import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;

public interface Response {
    String getRefNumber();
    CreditCardUtil.CardIssuers getCardIssuers();
    String getCardNumber();
    String getExpDate();
}
