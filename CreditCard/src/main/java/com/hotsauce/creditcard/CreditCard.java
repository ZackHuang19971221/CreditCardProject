package com.hotsauce.creditcard;

import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.ResultCode;
import com.hotsauce.creditcard.io.base.Request;
import com.hotsauce.creditcard.io.base.Response;
import com.hotsauce.creditcard.providers.ProviderType;
import com.hotsauce.creditcard.util.IDataReadCallBack;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CreditCard<M> {
    protected abstract ProviderType getProviderType();

    public CreditCard(@NonNull DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @SuppressWarnings("unchecked")
    private <Req extends Request, ResT, Res extends com.hotsauce.creditcard.io.base.Response<ResT>> void runFunction(Req request, Action action, IDataReadCallBack<Res> callBack) {
        //set the first in request
        AtomicBoolean isCancel = new AtomicBoolean(false);
        this.<Res>setFirstInRequest(request,false);
        if(request == null) {
            setResponseData(ResultCode.INPUT_ERROR,"Request can not be null");
            callBack.onDataRead((Res) getResponse());
        }
        if(getNeedManagementData() && getManagementData() == null) {
            setResponseData(ResultCode.INPUT_ERROR,"Management Data can not be null");
            callBack.onDataRead((Res) getResponse());
        }
        //time out handle
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
                    isCancel.set(true);
                    implementCancel();
                    setResponseData(ResultCode.TIME_OUT);
                    callBack.onDataRead((Res) getResponse());
                }
                , getDeviceInfo().getTimeOut(), TimeUnit.MILLISECONDS);
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
            if(isCancel.get()){return;}
            callBack.onDataRead((Res) getResponse());
        }catch (Exception exception) {
            if(!(exception instanceof TimeoutException)) {
                getResponse().setResultCode(ResultCode.SYSTEM_ERROR.getCode());
                getResponse().setResultMessage(exception.getMessage());
            }else {
                getResponse().setResultMessage("Connect Fail");
                getResponse().setResultCode(ResultCode.NETWORK_ERROR.getCode());
            }
            if(isCancel.get()){return;}
            callBack.onDataRead((Res) getResponse());
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
    public void auth(com.hotsauce.creditcard.io.auth.Request request,@NonNull IDataReadCallBack<Response<com.hotsauce.creditcard.io.auth.Response>> callBack) {
        runFunction(request,Action.AUTH,callBack);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.auth.Response> implementAuth(com.hotsauce.creditcard.io.auth.Request request);


    public void voidAuth(com.hotsauce.creditcard.io.voidauth.Request request,@NonNull IDataReadCallBack<Response<com.hotsauce.creditcard.io.voidauth.Response>> callBack) {
        runFunction(request,Action.VOID_AUTH,callBack);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.voidauth.Response> implementVoidAuth(com.hotsauce.creditcard.io.voidauth.Request request);


    public void capture(com.hotsauce.creditcard.io.capture.Request request,@NonNull IDataReadCallBack<Response<com.hotsauce.creditcard.io.capture.Response>> callBack) {
        runFunction(request,Action.CAPTURE,callBack);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.capture.Response> implementCapture(com.hotsauce.creditcard.io.capture.Request request);


    public void sale(com.hotsauce.creditcard.io.sale.Request request,@NonNull IDataReadCallBack<Response<com.hotsauce.creditcard.io.sale.Response>> callBack) {
        runFunction(request,Action.SALE,callBack);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.sale.Response> implementSale(com.hotsauce.creditcard.io.sale.Request request);


    public void voidSale(com.hotsauce.creditcard.io.voidsale.Request request,@NonNull IDataReadCallBack<Response<com.hotsauce.creditcard.io.voidsale.Response>> callBack) {
        runFunction(request,Action.VOID_SALE,callBack);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.voidsale.Response> implementVoidSale(com.hotsauce.creditcard.io.voidsale.Request request);


    public void enterTips(com.hotsauce.creditcard.io.entertips.Request request,@NonNull IDataReadCallBack<Response<com.hotsauce.creditcard.io.entertips.Response>> callBack) {
        runFunction(request,Action.ENTER_TIP,callBack);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.entertips.Response> implementEnterTips(com.hotsauce.creditcard.io.entertips.Request request);


    public void adjustTips(com.hotsauce.creditcard.io.adjusttips.Request request,@NonNull IDataReadCallBack<Response<com.hotsauce.creditcard.io.adjusttips.Response>> callBack) {
        runFunction(request,Action.ADJUST_TIP,callBack);
    }
    protected abstract ProviderResult<com.hotsauce.creditcard.io.adjusttips.Response> implementAdjustTips(com.hotsauce.creditcard.io.adjusttips.Request request);


    public void batch(com.hotsauce.creditcard.io.batchsettlement.Request request,@NonNull IDataReadCallBack<Response<com.hotsauce.creditcard.io.batchsettlement.Response>> callBack) {
        runFunction(request,Action.BATCH,callBack);
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
