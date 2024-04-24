package com.hotsauce.creditcard;

import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.ResultCode;
import com.hotsauce.creditcard.io.base.Request;
import com.hotsauce.creditcard.io.base.Response;
import com.hotsauce.creditcard.providers.ProviderType;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public abstract class ICreditCard<M> {
    protected abstract ProviderType getProviderType();

    public ICreditCard(@NonNull DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @SuppressWarnings("unchecked")
    private <Req extends Request, ResT, Res extends com.hotsauce.creditcard.io.base.Response<ResT>> Res runFunction(Req request, Action action) {
        //set the first in request
        this.<Res>setFirstInRequest(request,false);
        if(request == null) {
            setResponseData(ResultCode.INPUT_ERROR,"Request can not be null");
            return (Res) getResponse();
        }
        if(getNeedManagementData()) {
            setResponseData(ResultCode.INPUT_ERROR,"Management Data can not be null");
            return (Res) getResponse();
        }
        try {
            //Default Result
            ProviderResult<ResT> providerResult = new ProviderResult<>(false,"Fail",null);
            switch (action) {
                case AUTH:
                    providerResult = (ProviderResult<ResT>) implementAuth((com.hotsauce.creditcard.io.auth.Request) request);
                    break;
                case VOID_AUTH:
                    providerResult = (ProviderResult<ResT>) implementVoidAuth((com.hotsauce.creditcard.io.voidauth.Request) request);
                    break;
                case CAPTURE:
                    providerResult = (ProviderResult<ResT>) implementCapture((com.hotsauce.creditcard.io.capture.Request) request);
                    break;
                case SALE:
                    providerResult = (ProviderResult<ResT>) implementSale((com.hotsauce.creditcard.io.sale.Request) request);
                    break;
                case VOID_SALE:
                    providerResult = (ProviderResult<ResT>) implementVoidSale((com.hotsauce.creditcard.io.voidsale.Request) request);
                    break;
                case ENTER_TIP:
                    providerResult = (ProviderResult<ResT>) implementEnterTips((com.hotsauce.creditcard.io.entertips.Request) request);
                    break;
                case ADJUST_TIP:
                    providerResult = (ProviderResult<ResT>) implementAdjustTips((com.hotsauce.creditcard.io.adjusttips.Request) request);
                    break;
                case BATCH:
                    providerResult = (ProviderResult<ResT>) implementBatch((com.hotsauce.creditcard.io.batchsettlement.Request) request);
                    break;
            }
            if(providerResult.getIsSuccess()) {
                setResponseData(ResultCode.SUCCESS);
                ((com.hotsauce.creditcard.io.base.Response<ResT>) getResponse()).setData(providerResult.getData());
            }else {
                setResponseData(ResultCode.PROVIDER_ERROR,providerResult.getMessage());
            }
            return (Res) getResponse();
        }catch (Exception exception) {
            if(!(exception instanceof TimeoutException)) {
                getResponse().setResultCode(ResultCode.SYSTEM_ERROR.getCode());
                getResponse().setResultMessage(exception.getMessage());
            }else {
                getResponse().setResultMessage("Connect Fail");
                getResponse().setResultCode(ResultCode.NETWORK_ERROR.getCode());
            }
            return (Res) getResponse();
        }finally {
            setFirstInRequest(null,true);
        }
    }

    //region "Device Info"
    private final DeviceInfo deviceInfo;
    protected DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }
    //endregion


    //region "ManagementData"
    private M managementData;
    public void setManagementData(M value) {
        managementData = value;
        onManagementDataChanged(getManagementData());
    }
    public M getManagementData() {
        return managementData;
    }
    protected abstract void onManagementDataChanged(M data);
    protected abstract boolean getNeedManagementData();
    //endregion


    //region "Response"
    private Response<?> response;
    protected Response<?> getResponse() {
        return response;
    }
    private <T extends Response<?>> void setResponse(T response) {
        this.response = response;
    }
    private void setResponseData(ResultCode code, String message) {
        Response<?> resp = getResponse();
        resp.setResultCode(code.getCode());
        resp.setResultMessage(message);
    }
    private void setResponseData(ResultCode code) {
        Response<?> resp = getResponse();
        resp.setResultCode(code.getCode());
        resp.setResultMessage(code.getDescription());
    }
    //endregion


    //region "FirstRequest"
    private Request firstRequest;
    @SuppressWarnings("unchecked")
    private <T extends com.hotsauce.creditcard.io.base.Response<?>> void setFirstInRequest(Request value,boolean resetValue) {
        firstRequest = value;
        // Check if the request is null and set response to null
        if (getFirstRequest() == null && resetValue) {
            setResponse(null);
            return;
        }
        // If not null, create a new response of type T
        T response = (T) Response.builder().build();  // Cast needed with assumed builder pattern
        response.setProviderType(getProviderType());
        if(getFirstRequest() != null) {
            response.setRequestId(getFirstRequest().getRequestId());
        }
        response.setLogList(new ArrayList<>());
        setResponse(response);
    }
    protected Request getFirstRequest() {
        return firstRequest;
    }
    //endregion


    //region "Call API"
    @SneakyThrows
    protected <T1,T2 extends APIResponse<T4,T3>,T3,T4> T2 callApi(T1 request) {
        T2 response = null;
        try {
            while (true) {
                try {
                  response = implementCallApi(request);
                  return response;
                }catch (Exception exception) {
                    if(!(exception instanceof TimeoutException)) {throw exception;}
                    int retryTime = getDeviceInfo().getRetryTime();
                    retryTime -=1;
                    if(0 > retryTime) {
                        throw new TimeoutException();
                    }
                }
            }
        } finally {
            int seq = getResponse().getLogList().size();
            getResponse().getLogList().add(Response.Log
                    .builder()
                    .requestId(getResponse().getRequestId())
                    .sequence(seq)
                    .providerCode(response == null ? null : response.getProviderCode())
                    .providerMessage(response == null ? null : response.getProviderMessage())
                    .request(response.getRequest())
                    .response(response.getResponse())
                    .build());
            }
    }
    @SneakyThrows
    protected abstract <T1,T2 extends APIResponse<T4,T3>,T3,T4> T2 implementCallApi(T1 request);
    //endregion


    //region "function"
    public Response<com.hotsauce.creditcard.io.auth.Response> auth(com.hotsauce.creditcard.io.auth.Request request) {
        return runFunction(request,Action.AUTH);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.auth.Response> implementAuth(com.hotsauce.creditcard.io.auth.Request request);


    public Response<com.hotsauce.creditcard.io.voidauth.Response> voidAuth(com.hotsauce.creditcard.io.voidauth.Request request) {
        return runFunction(request,Action.VOID_AUTH);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.voidauth.Response> implementVoidAuth(com.hotsauce.creditcard.io.voidauth.Request request);


    public Response<com.hotsauce.creditcard.io.capture.Response> capture(com.hotsauce.creditcard.io.capture.Request request) {
        return runFunction(request,Action.CAPTURE);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.capture.Response> implementCapture(com.hotsauce.creditcard.io.capture.Request request);


    public Response<com.hotsauce.creditcard.io.sale.Response> sale(com.hotsauce.creditcard.io.sale.Request request) {
        return runFunction(request,Action.SALE);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.sale.Response> implementSale(com.hotsauce.creditcard.io.sale.Request request);


    public Response<com.hotsauce.creditcard.io.voidsale.Response> voidSale(com.hotsauce.creditcard.io.voidsale.Request request) {
        return runFunction(request,Action.VOID_SALE);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.voidsale.Response> implementVoidSale(com.hotsauce.creditcard.io.voidsale.Request request);


    public Response<com.hotsauce.creditcard.io.entertips.Response> enterTips(com.hotsauce.creditcard.io.entertips.Request request) {
        return runFunction(request,Action.ENTER_TIP);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.entertips.Response> implementEnterTips(com.hotsauce.creditcard.io.entertips.Request request);


    public Response<com.hotsauce.creditcard.io.adjusttips.Response> adjustTips(com.hotsauce.creditcard.io.adjusttips.Request request) {
        return runFunction(request,Action.ADJUST_TIP);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.adjusttips.Response> implementAdjustTips(com.hotsauce.creditcard.io.adjusttips.Request request);


    public Response<com.hotsauce.creditcard.io.batchsettlement.Response> batch(com.hotsauce.creditcard.io.batchsettlement.Request request) {
        return runFunction(request,Action.BATCH);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.batchsettlement.Response> implementBatch(com.hotsauce.creditcard.io.batchsettlement.Request request);

    //endregion


    public enum Action {
        AUTH,
        VOID_AUTH,
        CAPTURE,
        SALE,
        VOID_SALE,
        ENTER_TIP,
        ADJUST_TIP,
        BATCH
    }

    protected static class ProviderResult<T> {
        private final boolean isSuccess;
        private final T data;
        private final String message;
        public ProviderResult(boolean isSuccess,String message,T data) {
            this.isSuccess = isSuccess;
            this.data = data;
            this.message = message;
        }
        public boolean getIsSuccess() {
            return isSuccess;
        }
        public T getData() {
            return data;
        }
        public String getMessage() {
            return message;
        }
    }

    protected abstract static class APIResponse<T1,T2> {
        private final T1 request;
        private final T2 response;
        public APIResponse(T1 request, T2 response) {
            this.request = request;
            this.response = response;
        }
       public abstract String getSuccessCode();
       public abstract String getProviderCode();
       public abstract String getProviderMessage();
       public T1 getRequest() {
            return request;
        }
       public T2 getResponse() {
            return response;
        }
       public boolean getIsSuccess() {
           return getSuccessCode().equals(getProviderCode());
       }
    }
}
