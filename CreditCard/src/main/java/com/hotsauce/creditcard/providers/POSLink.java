package com.hotsauce.creditcard.providers;

import com.hotsauce.creditcard.CreditCard;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.auth.Request;
import com.hotsauce.creditcard.io.auth.Response;
import com.hotsauce.creditcard.io.manage.PosLinkManageData;
import com.hotsauce.creditcard.io.sale.RequestToken;
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
        return false;
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
        }
        if(request instanceof BatchRequest) {
            posLink.BatchRequest = (BatchRequest) request;
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
        if(request instanceof BatchRequest) {
            return (T2) new APIResponse<>(request,posLink) {

                @Override
                public String getSuccessCode() {
                    return "000000";
                }

                @Override
                public String getProviderCode() {
                    return getResponse().BatchResponse.ResultCode;
                }

                @Override
                public String getProviderMessage() {
                    return getResponse().BatchResponse.ResultTxt;
                }
            };
        }
        throw new Exception();
    }


    @Override
    protected ProviderResult<Response> implementAuth(Request request) {
        TransType transType = TransType.AUTH;
        //create Request
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(transType.getCode());
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
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,transType.getCode());
        Response result = null;
        if(apiResponse.getIsSuccess()) {
            ExtData extData = null;
            try {
                extData = TagConverter.DeserializeObject(ExtData.class, "", "<>" + apiResponse.getResponse().PaymentResponse.ExtData + "</>");
            }catch (Exception ignored) {}
            ExtData finalExtData = extData;
            result = new Response() {
                @Override
                public String getRefNumber() {
                    return apiResponse.getResponse().PaymentResponse.RefNum;
                }
                @Override
                public CreditCardUtil.CardIssuers getCardIssuers() {
                    if(finalExtData == null){return CreditCardUtil.CardIssuers.UNKNOWN;}
                    return CreditCardUtil.getCardIssuers(finalExtData.CARDBIN);
                }
                @Override
                public String getCardNumber() {
                    if(finalExtData == null){return "";}
                    return CreditCardUtil.getPartialCardNumber(finalExtData.CARDBIN,"","");
                }
                @Override
                public String getExpDate() {
                    if(finalExtData == null){return "";}
                    return finalExtData.ExpDate;
                }
            };
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.voidauth.Response> implementVoidAuth(com.hotsauce.creditcard.io.voidauth.Request request) {
        TransType transType = TransType.VOID_AUTH;
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(transType.getCode());
        pay.ECRRefNum = request.getRequestId();
        pay.OrigRefNum = request.getRefNumber();
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,transType.getCode());
        com.hotsauce.creditcard.io.voidauth.Response result = null;
        if(apiResponse.getIsSuccess()) {
            result = new com.hotsauce.creditcard.io.voidauth.Response() {};
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.capture.Response> implementCapture(com.hotsauce.creditcard.io.capture.Request request) {
        TransType transType = TransType.CAPTURE;
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(transType.getCode());
        pay.Amount = convertBigDecimalValue(request.getAmount());
        pay.TipAmt = convertBigDecimalValue(request.getTip());
        pay.TaxAmt = convertBigDecimalValue(request.getTax());
        pay.ECRRefNum = request.getRequestId();
        pay.OrigRefNum = request.getRefNumber();
        pay.ServiceFee = convertBigDecimalValue(request.getServiceFee());
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,transType.getCode());
        com.hotsauce.creditcard.io.capture.Response result = null;
        if(apiResponse.getIsSuccess()) {
            result = () -> apiResponse.getResponse().PaymentResponse.RefNum;
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.sale.Response> implementSale(com.hotsauce.creditcard.io.sale.Request request) {
        TransType transType = TransType.SALE;
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(transType.getCode());
        pay.Amount = convertBigDecimalValue(request.getAmount());
        pay.TaxAmt = convertBigDecimalValue(request.getTax());
        pay.ECRRefNum = request.getRequestId();
        pay.ECRTransID = request.getRequestId();
        pay.ServiceFee = convertBigDecimalValue(request.getServiceFee());
        pay.ExtData = "";
        if(request.getShowEnterTips()) {
            pay.ExtData += "<TipRequest>1</TipRequest>";
        }else {
            pay.TipAmt = convertBigDecimalValue(request.getTip());
        }
        if(request instanceof RequestToken) {
            pay.ExtData += "<Token>" + ((RequestToken) request).getToken() + "</Token>";
            pay.ExtData += "<ExpDate>" + ((RequestToken) request).getExpDate() + "</ExpDate>";
        }
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,transType.getCode());
        com.hotsauce.creditcard.io.sale.Response result = null;
        if(apiResponse.getIsSuccess()) {
            ExtData extData = null;
            try {
                extData = TagConverter.DeserializeObject(ExtData.class, "", "<>" + apiResponse.getResponse().PaymentResponse.ExtData + "</>");
            }catch (Exception ignored) {}
            ExtData finalExtData = extData;
            result = new com.hotsauce.creditcard.io.sale.Response() {
                @Override
                public String getRefNumber() {
                    return apiResponse.getResponse().PaymentResponse.RefNum;
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
    protected ProviderResult<com.hotsauce.creditcard.io.voidsale.Response> implementVoidSale(com.hotsauce.creditcard.io.voidsale.Request request) {
        TransType transType = request.getIsSettled() ? TransType.RETURN : TransType.VOID;
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(transType.getCode());
        pay.ECRRefNum = request.getRequestId();
        pay.OrigRefNum = request.getRefNumber();
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,transType.getCode());
        com.hotsauce.creditcard.io.voidsale.Response result = null;
        if(apiResponse.getIsSuccess()) {
            result = new com.hotsauce.creditcard.io.voidsale.Response() {};
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.entertips.Response> implementEnterTips(com.hotsauce.creditcard.io.entertips.Request request) {
        TransType transType = TransType.ADJUST;
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(transType.getCode());
        pay.Amount = convertBigDecimalValue(request.getTip());
        pay.ECRRefNum = request.getRequestId();
        pay.OrigRefNum = request.getRefNumber();
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,transType.getCode());
        com.hotsauce.creditcard.io.entertips.Response result = null;
        if(apiResponse.getIsSuccess()) {
            result = () -> apiResponse.getResponse().PaymentResponse.RefNum;
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.adjusttips.Response> implementAdjustTips(com.hotsauce.creditcard.io.adjusttips.Request request) {
        TransType transType = TransType.ADJUST;
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(transType.getCode());
        pay.Amount = convertBigDecimalValue(request.getTip());
        pay.ECRRefNum = request.getRequestId();
        pay.OrigRefNum = request.getRefNumber();
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,transType.getCode());
        com.hotsauce.creditcard.io.adjusttips.Response result = null;
        if(apiResponse.getIsSuccess()) {
            result = () -> apiResponse.getResponse().PaymentResponse.RefNum;
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.tokenize.Response> implementTokenize(com.hotsauce.creditcard.io.tokenize.Request request) {
        TransType transType = TransType.TOKENIZE;
        PaymentRequest pay = new PaymentRequest();
        pay.TenderType = pay.ParseTenderType("CREDIT");
        pay.TransType = pay.ParseTransType(transType.getCode());
        pay.ECRRefNum = request.getRequestId();
        APIResponse<PaymentRequest,com.pax.poslink.PosLink> apiResponse = callApi(pay,transType.getCode());
        com.hotsauce.creditcard.io.tokenize.Response result = null;
        if(apiResponse.getIsSuccess()) {
            ExtData extData = null;
            try {
                extData = TagConverter.DeserializeObject(ExtData.class, "", "<>" + apiResponse.getResponse().PaymentResponse.ExtData + "</>");
            }catch (Exception ignored) {}
            ExtData finalExtData = extData;
            result = new com.hotsauce.creditcard.io.tokenize.Response() {
                @Override
                public String getToken() {
                    assert finalExtData != null;
                    return finalExtData.Token;
                }

                @Override
                public String getExpDate() {
                    assert finalExtData != null;
                    return finalExtData.ExpDate;
                }

                @Override
                public CreditCardUtil.CardIssuers getCardIssuer() {
                    if(finalExtData == null){return CreditCardUtil.CardIssuers.UNKNOWN;}
                    return CreditCardUtil.getCardIssuers(finalExtData.CARDBIN);
                }

                @Override
                public String getRefNumber() {return apiResponse.getResponse().PaymentResponse.RefNum;}
            };
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }


    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.batchsettlement.Response> implementBatch(com.hotsauce.creditcard.io.batchsettlement.Request request) {
        TransType transType = TransType.BATCH_CLOSE;
        BatchRequest batchRequest = new BatchRequest();
        batchRequest.TransType = batchRequest.ParseTransType(transType.getCode());
        APIResponse<BatchRequest,com.pax.poslink.PosLink> apiResponse = callApi(batchRequest,transType.getCode());
        com.hotsauce.creditcard.io.batchsettlement.Response result = null;
        if(apiResponse.getIsSuccess()) {
            result = new com.hotsauce.creditcard.io.batchsettlement.Response() {};
        }
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected void implementCancel() {
        posLink.CancelTrans();
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
        return  commSetting;
    }

    private String convertBigDecimalValue(BigDecimal value) {
        BigDecimal multipliedValue = value.multiply(new BigDecimal("100"));
        return String.valueOf(multipliedValue.setScale(0, RoundingMode.HALF_UP));
    }

    enum TransType {
        AUTH("AUTH"),
        CAPTURE("POSTAUTH"),
        SALE("SALE"),
        VOID("VOID"),
        ADJUST("ADJUST"),
        RETURN("RETURN"),
        VOID_AUTH("VOID AUTH"),
        BATCH_CLOSE("BATCHCLOSE"),
        TOKENIZE("TOKENIZE");
        private final String code;
        TransType(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }

    public static class ExtData{
        public String ExpDate;
        public String BatchNum;
        public String Token;
        public String CARDBIN;
    }
}