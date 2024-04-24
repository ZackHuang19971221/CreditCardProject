package com.hotsauce.creditcard.io.voidauth;

import lombok.*;

@Getter
@Setter
public class Request extends com.hotsauce.creditcard.io.base.Request {
    public Request(@NonNull String requestId) {
        super(requestId);
    }
    private String refNumber;
}
