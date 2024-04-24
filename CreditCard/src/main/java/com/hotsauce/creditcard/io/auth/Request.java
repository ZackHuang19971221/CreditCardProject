package com.hotsauce.creditcard.io.auth;

import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
public class Request extends com.hotsauce.creditcard.io.base.Request {
    public Request(@NonNull String requestId,@NonNull BigDecimal authAmount) {
        super(requestId);
        setAuthAmount(authAmount);
    }
    private BigDecimal authAmount;
}
