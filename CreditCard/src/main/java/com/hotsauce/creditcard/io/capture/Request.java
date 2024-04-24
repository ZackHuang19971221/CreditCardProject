package com.hotsauce.creditcard.io.capture;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class Request extends com.hotsauce.creditcard.io.base.Request {
    public Request(@NonNull String requestId,@NonNull BigDecimal authAmount,@NonNull BigDecimal amount,@NonNull BigDecimal tip,@NonNull BigDecimal tax,@NonNull BigDecimal serviceFee,@NonNull String refNumber) {
        super(requestId);
        setAuthAmount(authAmount);
        setAmount(amount);
        setTip(tip);
        setTax(tax);
        setServiceFee(serviceFee);
        setRefNumber(refNumber);
    }
    private BigDecimal authAmount;
    private BigDecimal amount;
    private BigDecimal tip;
    private BigDecimal tax;
    private BigDecimal serviceFee;
    private String refNumber;
}
