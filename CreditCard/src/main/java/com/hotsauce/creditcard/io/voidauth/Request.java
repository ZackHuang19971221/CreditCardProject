package com.hotsauce.creditcard.io.voidauth;

import lombok.*;

@Getter
@Setter
public class Request extends com.hotsauce.creditcard.io.base.Request {
    public Request(@NonNull String requestId,@NonNull String refNumber) {
        super(requestId);
        setRefNumber(refNumber);
    }
    private String refNumber;
}
