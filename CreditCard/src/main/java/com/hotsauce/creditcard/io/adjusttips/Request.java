package com.hotsauce.creditcard.io.adjusttips;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
public class Request extends com.hotsauce.creditcard.io.base.Request {
    public Request(@NonNull String requestId,@NonNull BigDecimal tip,@NonNull String refNumber) {
        super(requestId);
        setTip(tip);
        setRefNumber(refNumber);
    }
    private BigDecimal tip;
    private String refNumber;
}
