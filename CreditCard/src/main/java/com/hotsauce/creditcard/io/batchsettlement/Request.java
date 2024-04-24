package com.hotsauce.creditcard.io.batchsettlement;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class Request extends com.hotsauce.creditcard.io.base.Request {

   public Request(@NonNull String requestId,String refNumber) {
      super(requestId);
      setRefNumber(refNumber);
   }
   private String refNumber;
}
