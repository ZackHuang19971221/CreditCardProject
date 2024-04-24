package com.hotsauce.creditcard.io.base;

import lombok.*;

@Getter
@Setter
public class Request {
    public Request(@NonNull String requestId) {
        setRequestId(requestId);
    }
    private String requestId;
}
