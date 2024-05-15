package com.hotsauce.creditcard;

import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.ResultCode;
import com.hotsauce.creditcard.io.base.Request;
import com.hotsauce.creditcard.io.base.Response;
import com.hotsauce.creditcard.providers.ProviderType;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.concurrent.*;

public abstract class CreditCard<M> {
    protected abstract ProviderType getProviderType();

    public CreditCard(@NonNull DeviceInfo deviceInfo) {
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
        if(getNeedManagementData() && getManagementData() == null) {
            setResponseData(ResultCode.INPUT_ERROR,"Management Data can not be null");
            return (Res) getResponse();
        }
        //time out handle
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
                case TOKENIZE:
                    providerResult = (ProviderResult<ResT>) implementTokenize((com.hotsauce.creditcard.io.tokenize.Request) request);
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
        }finally {
            setFirstInRequest(null,true);
        }
        return (Res) getResponse();
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
    protected <Req, Res extends APIResponse<Req1, Res1>, Res1, Req1> Res callApi(Req request, String action) {
        Res response = null;
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
            if(response != null) {
                getResponse().getLogList().add(Response.Log
                        .builder()
                        .requestId(getResponse().getRequestId())
                        .sequence(seq)
                        .action(action)
                        .providerCode(response.getProviderCode())
                        .providerMessage(response.getProviderMessage())
                        .request(response.getRequest())
                        .response(response.getResponse())
                        .build());
            }
        }
    }

    protected abstract <T1,T2 extends APIResponse<T4,T3>,T3,T4> T2 implementCallApi(T1 request);
    //endregion


    //region "function"
    public Response<com.hotsauce.creditcard.io.auth.Response> auth(com.hotsauce.creditcard.io.auth.Request request) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = executor.schedule(() -> {
            implementCancel();
            setResponseData(ResultCode.TIME_OUT);
        }, getDeviceInfo().getTimeOut(), TimeUnit.MILLISECONDS);

        try {
            Response<com.hotsauce.creditcard.io.auth.Response> response = runFunction(request, Action.AUTH);
            future.cancel(true);
            return response;
        } finally {
            executor.shutdown();
        }
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
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> future = executor.schedule(() -> {
            implementCancel();
            setResponseData(ResultCode.TIME_OUT);
        }, getDeviceInfo().getTimeOut(), TimeUnit.MILLISECONDS);

        try {
            Response<com.hotsauce.creditcard.io.sale.Response> response = runFunction(request,Action.SALE);
            future.cancel(true);
            return response;
        } finally {
            executor.shutdown();
        }
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


    public Response<com.hotsauce.creditcard.io.tokenize.Response> tokenize(com.hotsauce.creditcard.io.tokenize.Request request) {
        return runFunction(request,Action.TOKENIZE);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.tokenize.Response> implementTokenize(com.hotsauce.creditcard.io.tokenize.Request request);


    public Response<com.hotsauce.creditcard.io.batchsettlement.Response> batch(com.hotsauce.creditcard.io.batchsettlement.Request request) {
        return runFunction(request,Action.BATCH);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.batchsettlement.Response> implementBatch(com.hotsauce.creditcard.io.batchsettlement.Request request);


    protected abstract void implementCancel();

    //endregion


    public enum Action {
        AUTH,
        VOID_AUTH,
        CAPTURE,
        SALE,
        VOID_SALE,
        ENTER_TIP,
        ADJUST_TIP,
        BATCH,
        TOKENIZE
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

    protected abstract static class APIResponse<Req, Res> {
        private final Req request;
        private final Res response;
        public APIResponse(Req request, Res response) {
            this.request = request;
            this.response = response;
        }
       public abstract String getSuccessCode();
       public abstract String getProviderCode();
       public abstract String getProviderMessage();
       public Req getRequest() {
            return request;
        }
       public Res getResponse() {
            return response;
        }
       public boolean getIsSuccess() {
           return getSuccessCode().equals(getProviderCode());
       }
    }
}
