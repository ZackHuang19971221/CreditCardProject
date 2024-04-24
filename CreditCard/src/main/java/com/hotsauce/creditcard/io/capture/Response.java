package com.hotsauce.creditcard.io.capture;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    public String refNumber;
}
