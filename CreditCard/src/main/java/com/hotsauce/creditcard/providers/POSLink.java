package com.hotsauce.creditcard.providers;

import com.hotsauce.creditcard.CreditCard;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.auth.Request;
import com.hotsauce.creditcard.io.auth.Response;
import com.hotsauce.creditcard.io.manage.PosLinkManageData;
import com.hotsauce.creditcard.util.converter.TagConverter;
import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;
import com.pax.poslink.*;
import com.pax.poslink.constant.EDCType;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class POSLink extends CreditCard<PosLinkManageData> {
    com.pax.poslink.PosLink posLink;

    @Override
    protected ProviderType getProviderType() {
        return ProviderType.POSLink;
    }

    public POSLink(@NonNull DeviceInfo deviceInfo) {
        super(deviceInfo);
        posLink = createPosLink(getDeviceInfo());
    }

    @Override
    protected void onManagementDataChanged(PosLinkManageData data) {
        createManageRequest();
    }

    @Override
    protected boolean getNeedManagementData() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    protected <T1, T2 extends APIResponse<T4, T3>, T3, T4> T2 implementCallApi(T1 request) {
        if(request instanceof PaymentRequest) {
            posLink.PaymentRequest = (PaymentRequest) request;
        }
        if(request instanceof ManageRequest) {
            posLink.ManageRequest = (ManageRequest) request;
            posLink.ProcessTrans();
        }

        ProcessTransResult response = posLink.ProcessTrans();
        //check transResult first
        if(response.Code !=ProcessTransResult.ProcessTransResultCode.OK) {
            return (T2) new APIResponse<>(request,posLink) {

                @Override
                public String getSuccessCode() {
                    return ProcessTransResult.ProcessTransResultCode.OK.toString();
                }

                @Override
                public String getProviderCode() {
                    return response.Code.toString();
                }

                @Override
                public String getProviderMessage() {
                    return response.Msg;
                }
            };
        }

        if(request instanceof PaymentRequest) {
            return (T2) new APIResponse<>(request,posLink) {
                @Override
                public String getSuccessCode() {
                    return "000000";
                }

                @Override
                public String getProviderCode() {
                    return getResponse().PaymentResponse.ResultCode;
                }

                @Override
                public String getProviderMessage() {
                    return getResponse().PaymentResponse.ResultTxt;
                }
            };
        }
        if(request instanceof ManageRequest) {
            return (T2) new APIResponse<>(request,posLink) {

                @Override
                public String getSuccessCode() {
                    return "000000";
                }

                @Override
                public String getProviderCode() {
                    return getResponse().ManageResponse.ResultCode;
                }

                @Override
                public String getProviderMessage() {
                    return getResponse().ManageResponse.ResultTxt;
                }
            };
        }
        throw new Exception();
    }


    @Override
    protected ProviderResult<Response> implementAuth(Request request) {
        ProviderResult<Response> response = this.processManageData();
        if(!response.getIsSuccess()) {
            return response;
        }
        //create Request
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(TransType.AUTH);
        pay.Amount = convertBigDecimalValue(request.getAuthAmount());
        pay.CashBackAmt="";
        pay.FuelAmt = "";
        pay.ClerkID = "";
        pay.Zip="";
        pay.TipAmt = "";
        pay.TaxAmt = "";
        pay.Street = "";
        pay.Street2 = "";
        pay.SurchargeAmt = "";
        pay.InvNum = "";
        pay.ECRRefNum = request.getRequestId();
        pay.AuthCode = "";
        pay.ECRTransID ="";
        pay.OrigECRRefNum = "";
        pay.ContinuousScreen = "";
        pay.ServiceFee = "";
        pay.GiftCardType = "";
        pay.CVVBypassReason = "";
        pay.GiftTenderType = "";
        pay.OrigTraceNum = "";
        pay.ExtData = "";
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,TransType.AUTH);
        Response result = null;
        if(apiResponse.getIsSuccess()) {
            ExtData extData = null;
            try {
                extData = TagConverter.DeserializeObject(ExtData.class, "", posLink.PaymentResponse.ExtData);
            }catch (Exception ignored) {}
            ExtData finalExtData = extData;
            result = new Response() {
                @Override
                public String getRefNumber() {
                    return request.getRequestId();
                }
                @Override
                public CreditCardUtil.CardIssuers getCardIssuers() {
                    assert finalExtData != null;
                    return CreditCardUtil.getCardIssuers(finalExtData.CARDBIN);
                }
                @Override
                public String getCardNumber() {
                    assert finalExtData != null;
                    return CreditCardUtil.getPartialCardNumber(finalExtData.CARDBIN,"","");
                }
                @Override
                public String getExpDate() {
                    assert finalExtData != null;
                    return finalExtData.ExpDate;
                }
            };
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.voidauth.Response> implementVoidAuth(com.hotsauce.creditcard.io.voidauth.Request request) {
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.capture.Response> implementCapture(com.hotsauce.creditcard.io.capture.Request request) {
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.sale.Response> implementSale(com.hotsauce.creditcard.io.sale.Request request) {
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.voidsale.Response> implementVoidSale(com.hotsauce.creditcard.io.voidsale.Request request) {
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.entertips.Response> implementEnterTips(com.hotsauce.creditcard.io.entertips.Request request) {
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.adjusttips.Response> implementAdjustTips(com.hotsauce.creditcard.io.adjusttips.Request request) {
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.batchsettlement.Response> implementBatch(com.hotsauce.creditcard.io.batchsettlement.Request request) {
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.cancel.Response> implementCancel(com.hotsauce.creditcard.io.cancel.Request request) {
        posLink.CancelTrans();
        return new ProviderResult<>(true,"Success",null);
    }


    //region "Manage Request"
    private <T> ProviderResult<T> processManageData() {
        APIResponse<ManageRequest,com.pax.poslink.PosLink> apiResponse = callApi(getManageRequest(),"MANAGEMENT");
        return new ProviderResult<>(apiResponse.getIsSuccess(), apiResponse.getProviderMessage(),null);
    }
    private ManageRequest manageRequest;
    private ManageRequest getManageRequest() {
        return manageRequest;
    }
    private void createManageRequest() {
        manageRequest = new ManageRequest();
        manageRequest.EDCType = manageRequest.ParseEDCType(EDCType.CREDIT);
        manageRequest.TransType = manageRequest.ParseTransType("SETVAR");
        //UserName
        manageRequest.VarName = "UserName";
        //TaLogin
        manageRequest.VarValue = getManagementData().getUserName();
        //UserPassword
        manageRequest.VarName1 = "UserPassword";
        //TaLogin Password
        manageRequest.VarValue1 = getManagementData().getUserPassword();
        //MID
        manageRequest.VarName2 = "MID";
        //Merchant Number
        manageRequest.VarValue2 = getManagementData().getMerchantId();
        //DeviceID
        manageRequest.VarName3 = "DeviceID";
        //MID + DeviceID
        manageRequest.VarValue3 = getManagementData().getDeviceId();
    }
    //endregion

    private com.pax.poslink.PosLink createPosLink(DeviceInfo deviceInfo) {
        com.pax.poslink.PosLink posLink = new com.pax.poslink.PosLink();
        posLink.SetCommSetting(createCommSetting(deviceInfo));
        return posLink;
    }

    private CommSetting createCommSetting(DeviceInfo deviceInfo) {
        CommSetting commSetting = new CommSetting();
        commSetting.setType(CommSetting.TCP);
        commSetting.setDestIP(deviceInfo.getIp());
        commSetting.setDestPort(String.valueOf(deviceInfo.getPort()));
        commSetting.setEnableProxy(false);
        commSetting.setTimeOut(String.valueOf(deviceInfo.getTimeOut()));
        return  commSetting;
    }

    private String convertBigDecimalValue(BigDecimal value) {
        BigDecimal multipliedValue = value.multiply(new BigDecimal("100"));
        return String.valueOf(multipliedValue.setScale(0, RoundingMode.HALF_UP));
    }

    static class TransType{
        public static final String AUTH="AUTH";

        public static final String CAPTURE="POSTAUTH";

        public static final String SALE="SALE";

        //voidSale Is for UnSettled Transaction
        public static final String VOID="VOID";

        public static final String ADJUST = "ADJUST";

        //Return Is for Settled Transaction
        public static final String RETURN="RETURN";

        public static final String VOID_AUTH = "VOID AUTH";

        public static final String BATCH_CLOSE = "BATCHCLOSE";
    }

    static class ExtData{
        public String ExpDate;
        public String BatchNum;
        public String CARDBIN;
    }
}