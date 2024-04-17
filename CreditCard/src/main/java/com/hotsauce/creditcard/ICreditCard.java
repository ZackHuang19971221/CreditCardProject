package com.hotsauce.creditcard;
public interface ICreditCard {
    com.hotsauce.creditcard.io.auth.Output authCard(com.hotsauce.creditcard.io.auth.Input input);
    com.hotsauce.creditcard.io.capture.Output capture(com.hotsauce.creditcard.io.capture.Input input);
    com.hotsauce.creditcard.io.sale.Output sale(com.hotsauce.creditcard.io.sale.Input input);
    com.hotsauce.creditcard.io.entertips.Output enterTips(com.hotsauce.creditcard.io.entertips.Input input);
    com.hotsauce.creditcard.io.adjusttips.Output adjustTips(com.hotsauce.creditcard.io.adjusttips.Input input);
    com.hotsauce.creditcard.io.voidsale.Output voidSale(com.hotsauce.creditcard.io.voidsale.Input input);
    com.hotsauce.creditcard.io.batchsettlement.Output batchSettlement(com.hotsauce.creditcard.io.batchsettlement.Input input);
    com.hotsauce.creditcard.io.voidauth.Output voidAuth(com.hotsauce.creditcard.io.voidauth.Input input);
}
