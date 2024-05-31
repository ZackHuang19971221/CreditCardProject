package com.hotsauce.creditcard.providers;

import com.hotsauce.creditcard.CreditCard;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.manage.DejavooManageData;
import com.hotsauce.creditcard.util.converter.TagConverter;
import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;

public class Dejavoo extends CreditCard<DejavooManageData> {
    private DejavooManageData manageData;
    public Dejavoo(@NonNull DeviceInfo deviceInfo) {
        super(deviceInfo);
    }

    @Override
    protected void onManagementDataChanged(DejavooManageData data) {
        this.manageData = data;
    }

    @Override
    protected ProviderType getProviderType() {
        return ProviderType.Dejavoo;
    }


    @Override
    protected boolean getNeedManagementData() {
        return true;
    }

    @SneakyThrows
    @Override
    protected <T1, T2 extends APIResponse<T4, T3>, T3, T4> T2 implementCallApi(T1 request,Class<T3> responseType) {
        String requestString = TagConverter.SerializeObject("request",request);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        okhttp3.Request innerRequest = new okhttp3.Request.Builder()
                .url("http://spinpos.net/cgi.html?TerminalTransaction=" + requestString)
                .method("GET",null)
                .build();
        okhttp3.Response innerResponse = client.newCall(innerRequest).execute();
        String respnseString = innerResponse.body().string();
        respnseString = removeXmpTags(respnseString);
        Response response = TagConverter.DeserializeObject(responseType, "response", respnseString);
        System.out.println("");
        return (T2) new APIResponse<>(request,response) {

            @Override
            public String getSuccessCode() {
                return ResultCode.SUCCESS.getCode();
            }

            @Override
            public String getProviderCode() {
                return getResponse().getResultCode();
            }

            @Override
            public String getProviderMessage() {
                return getResponse().getResultMessage();
            }
        };
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.auth.Response> implementAuth(com.hotsauce.creditcard.io.auth.Request request) {
        TransactionRequest req = new TransactionRequest();
        req.AuthKey = manageData.getAuthKey();
        req.RefId = manageData.getRegisterId();
        req.TransType = "Auth";
        req.PaymentType = "Credit";
        APIResponse<TransactionRequest,TransactionResponse> apiResponse =  callApi(req,TransactionResponse.class,req.TransType);
        com.hotsauce.creditcard.io.auth.Response result = new com.hotsauce.creditcard.io.auth.Response() {
            @Override
            public CreditCardUtil.CardIssuers getCardIssuers() {
                return null;
            }
            @Override
            public String getCardNumber() {
                return null;
            }
            @Override
            public String getExpDate() {
                return null;
            }
            @Override
            public String getRefNumber() {
                return null;
            }
        };
        return new ProviderResult<>(apiResponse.getIsSuccess(),apiResponse.getProviderMessage(),result);
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.voidauth.Response> implementVoidAuth(com.hotsauce.creditcard.io.voidauth.Request request) {
        VoidTransactionRequest req = new VoidTransactionRequest();
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.capture.Response> implementCapture(com.hotsauce.creditcard.io.capture.Request request) {
        TransactionRequest req = new TransactionRequest();
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.sale.Response> implementSale(com.hotsauce.creditcard.io.sale.Request request) {
        TransactionRequest req = new TransactionRequest();
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.voidsale.Response> implementVoidSale(com.hotsauce.creditcard.io.voidsale.Request request) {
        VoidTransactionRequest req = new VoidTransactionRequest();
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.entertips.Response> implementEnterTips(com.hotsauce.creditcard.io.entertips.Request request) {
        TipAdjustmentRequest req = new TipAdjustmentRequest();
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.adjusttips.Response> implementAdjustTips(com.hotsauce.creditcard.io.adjusttips.Request request) {
        TipAdjustmentRequest req = new TipAdjustmentRequest();
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.tokenize.Response> implementTokenize(com.hotsauce.creditcard.io.tokenize.Request request) {
        TransactionRequest req = new TransactionRequest();
        return null;
    }

    @Override
    protected ProviderResult<com.hotsauce.creditcard.io.batchsettlement.Response> implementBatch(com.hotsauce.creditcard.io.batchsettlement.Request request) {
        return null;
    }

    @Override
    protected void implementCancel() {

    }


    public String removeXmpTags(String xmlString) {
        // Remove <xmp> and </xmp> tags and their contents
        xmlString = xmlString.replace("<xmp>", "").replace("</xmp>", "");
        // Remove any remaining leading or trailing whitespace
        xmlString = xmlString.trim();
        return xmlString;
    }

    public enum ResultCode {
        SUCCESS("0"),
        TERMINAL_ERROR("1"),
        PROXY_ERROR("2");
        private final String code;
        ResultCode(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
    }
    public static class Request {
        String TransType;
    }
    public interface Response {
        String getResultCode();
        String getResultMessage();
    }


    public static class TransactionRequest extends Request {
        public String AuthKey;
        public String PaymentType;
        public String MerchantId;
        public String Amount;
        public String Tip;
        public String Points;
        public String InvNum;
        public String RefId;
        public String AuthCode;
        public String RegisterId;
        public String TPN;
        public String ClerkId;
        public String TableNum;
        public String TicketNum;
        public String CashbackAmount;
        public String CardData;
        public String PrintReceipt;
        public String Frequency;
        public String Token;
        public String SigCapture;
        public String Cust1;
        public String Cust2;
        public String Cust3;
        public String Alert;
        public String CardType;
        public String TaxAmount;
        public String CustRef;
        public String LocalTaxFlag;
        public String NationalTaxAmount;
        public String DestZipCode;
        public String CustomerVatReg;
        public String SummaryCommodityCode;
        public String FreightAmount;
        public String DutyAmount;
        public String ShipfromZipCode;
        public String DestCountryCode;
        public String FreightVatTaxAmount;
        public String FreightVatTaxRate;
        public String TotalDiscountAmount;
        public String AltTaxAmount;
        public String LineItemCount;
        public String Level3LineItems;
        public String CommodityCode;
        public String Description;
        public String ProductCode;
        public String Quantity;
        public String UnitOfMeasure;
        public String UnitCost;
        public String VatTaxAmount;
        public String VatTaxRate;
        public String DiscountAmount;
        public String TotalAmount;
        public String DiscountRate;
        public String AltTaxID;
        public String TaxTypeApplied;
        public String ExtLineAmount;
        public String DiscountIndicator;
        public String NetGrossIndicator;
        public String DebitCreditIndicator;
        public String QuantityExpIndicator;
        public String DiscountRateExp;
        public String HSA_TotalAmount;
        public String HSA_RxAmount;
        public String HSA_VisionAmount;
        public String HSA_ClinicAmount;
        public String HSA_DentalAmount;
        public String HSA_SKU;
        public String MastercardReturnOffline;
        public String TransactionID;
        public String OrgDate;
        public String OrgTime;
        public String PNRef;
    }
    public static class TransactionResponse implements Response {
        public String Message;
        public String ResultCode;
        public String RefId;
        public String RegisterId;
        public String TransNum;
        public String InvNum;
        public String RespMSG;
        public String AuthCode;
        public String PNRef;
        public String PaymentType;
        public String Voided;
        public String TransType;
        public String SN;
        public String HostSpecific;
        public String MerchantId;
        public String MerchantName;
        public String ExtData;
        public String EMVData;
        public String Sign;
        public String Token;
        public String CVMResult;
        public String Records;

        @Override
        public String getResultCode() {
            return ResultCode;
        }

        @Override
        public String getResultMessage() {
            return Message;
        }
    }


    static class CaptureRequest extends Request {

    }
    static class CaptureResponse implements Response {
        public String Message;
        public String ResultCode;
        @Override
        public String getResultCode() {
            return ResultCode;
        }

        @Override
        public String getResultMessage() {
            return Message;
        }
    }


    static class VoidTransactionRequest extends Request {

    }
    static class VoidTransactionResponse implements Response {
        public String Message;
        public String ResultCode;
        @Override
        public String getResultCode() {
            return ResultCode;
        }

        @Override
        public String getResultMessage() {
            return Message;
        }
    }


    static class TipAdjustmentRequest extends Request {
        public String RegisterId;
        public String AuthKey;
        public String InvNum;
        public String Amount;
        public String Tip;
        public String RefId;
        public String AcntLast4;
        public String ClerkId;
    }
    static class TipAdjustmentResponse implements Response {
        public String Message;
        public String ResultCode;
        public String RefId;
        public String RegisterId;
        public String InvNum;
        public String PaymentType;
        @Override
        public String getResultCode() {
            return ResultCode;
        }

        @Override
        public String getResultMessage() {
            return Message;
        }
    }


    static class TransactionStatusRequest extends Request {

    }
    static class TransactionStatusResponse implements Response {
        public String Message;
        public String ResultCode;
        @Override
        public String getResultCode() {
            return ResultCode;
        }

        @Override
        public String getResultMessage() {
            return Message;
        }
    }
}
