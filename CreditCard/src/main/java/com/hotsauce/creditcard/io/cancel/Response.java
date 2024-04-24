package com.hotsauce.creditcard.io.cancel;

import lombok.NonNull;

public class Response extends com.hotsauce.creditcard.io.base.Request{
    public Response(@NonNull String requestId) {
        super(requestId);
    }
}
