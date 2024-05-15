package com.hotsauce.creditcard.io.sale;

import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RequestToken extends Request{
    public RequestToken(@NonNull String requestId, @NonNull BigDecimal amount, @NonNull BigDecimal tip, @NonNull BigDecimal tax, @NonNull BigDecimal serviceFee, boolean showEnterTips ,@NonNull String refNumber, @NonNull String token, @NonNull String expDate,@NonNull CreditCardUtil.CardIssuers cardIssuer) {
        super(requestId, amount, tip, tax, serviceFee, showEnterTips);
        this.refNumber = refNumber;
        this.token = token;
        this.expDate = expDate;
        this.cardIssuer = cardIssuer;
    }
    private CreditCardUtil.CardIssuers cardIssuer;
    private String refNumber;
    private String token;
    private String expDate;
}
