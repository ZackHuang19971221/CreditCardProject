package com.hotsauce.creditcard.io.voidsale;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


@Getter
@Setter
public class Request extends com.hotsauce.creditcard.io.base.Request {
   public Request(@NonNull String requestId,@NonNull String refNumber,@NonNull boolean isSettled) {
      super(requestId);
      setRefNumber(refNumber);
      setIsSettled(isSettled);
   }
   private String refNumber;
   private Boolean isSettled;
}
