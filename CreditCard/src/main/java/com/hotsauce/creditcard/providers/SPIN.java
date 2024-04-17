package com.hotsauce.creditcard.providers;

import com.hotsauce.creditcard.ICreditCard;
import com.hotsauce.creditcard.io.auth.Input;
import com.hotsauce.creditcard.io.auth.Output;

import java.io.IOException;

public class SPIN implements ICreditCard {
    private int _retryTime = 10;
    private String CallAPI(String requestString,String url,int retryTime) throws IOException {
       return "";
    }
    @Override
    public Output authCard(Input input) {

        try {
            String responseString = CallAPI("","",_retryTime);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public com.hotsauce.creditcard.io.capture.Output capture(com.hotsauce.creditcard.io.capture.Input input) {
        return null;
    }

    @Override
    public com.hotsauce.creditcard.io.sale.Output sale(com.hotsauce.creditcard.io.sale.Input input) {
        return null;
    }

    @Override
    public com.hotsauce.creditcard.io.entertips.Output enterTips(com.hotsauce.creditcard.io.entertips.Input input) {
        return null;
    }

    @Override
    public com.hotsauce.creditcard.io.adjusttips.Output adjustTips(com.hotsauce.creditcard.io.adjusttips.Input input) {
        return null;
    }

    @Override
    public com.hotsauce.creditcard.io.voidsale.Output voidSale(com.hotsauce.creditcard.io.voidsale.Input input) {
        return null;
    }

    @Override
    public com.hotsauce.creditcard.io.batchsettlement.Output batchSettlement(com.hotsauce.creditcard.io.batchsettlement.Input input) {
        return null;
    }

    @Override
    public com.hotsauce.creditcard.io.voidauth.Output voidAuth(com.hotsauce.creditcard.io.voidauth.Input input) {
        return null;
    }

    class TransactionInput {
        public String AuthKey;
        public String PaymentType;
        public String TransType;
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
    class TransactionOutPut{
        Response response;
        class Response{
            public String RefId;
            public String RegisterId;
            public String TransNum;
            public String InvNum;
            public String ResultCode;
            public String RespMSG;
            public String Message;
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
        }
        class ExtData{
            public String InvNum;
            public String CardType;
            public String BatchNum;
            public double Tip;
            public String CashBack;
            public double Fee;
            public String AcntLast4;
            public String Name;
            public String SVC;
            public double TotalAmt;
            public double DISC;
            public double SHFee;
            public double RwdPoints;
            public double RwdBalance;
            public String RwdIssued;
            public double EBTFSLedgerBalance;
            public double EBTCashLedgerBalance;
            public String AcqReference;
            public String ProcessData;
            public String RefNo;
            public String RewardQR;
            public String RewardCode;
            public String Language;
            public String EntryType;
            public String table_num;
            public String clerk_id;
            public String ticket_num;
            public String Cust1;
            public String Cust1Value;
            public String Cust2;
            public String Cust2Value;
            public String Cust3;
            public String Cust3Value;
            public String AVSRsp;
            public String CVVRsp;
            public String TransactionID;
            public String ExtraHostData;
        }
        class EMVData{
            public String AID;
            public String AppName;
            public String TVR;
            public String TSI;
            public String IAD;
            public String ARC;
        }
    }

    class AdjustTipInput{
        public String RegisterId;
        public String AuthKey;
        public String TransType;
        public String InvNum;
        public String Amount;
        public String Tip;
        public String RefId;
        public String AcntLast4;
        public String ClerkId;
    }
    class AdjustTipOutput{
        Response response;
        class Response{
            public String RefId;
            public String RegisterId;
            public String InvNum;
            public String ResultCode;
            public String Message;
            public String PaymentType;
        }
    }
}
