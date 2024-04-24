package com.hotsauce.creditcard.io.base;

import com.hotsauce.creditcard.providers.ProviderType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Response<T> {
    private String requestId;
    private ProviderType providerType;
    private String resultCode;
    private String resultMessage;
    private List<Log<?,?>> logList;
    private T data;
    @Data
    @Builder
    public static class Log<T1,T2> {
        private String requestId;
        private int sequence;
        private String action;
        private String providerCode;
        private String providerMessage;
        private T1 request;
        private T2 response;
    }
}
