package com.hotsauce.creditcard.providers;

import com.hotsauce.creditcard.ICreditCard;
import com.hotsauce.creditcard.io.auth.Input;
import com.hotsauce.creditcard.io.auth.Output;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.ResultCode;
import com.hotsauce.creditcard.util.converter.TagConverter;
import com.hotsauce.creditcard.util.creditcard.CreditCardUtil;
import com.pax.poslink.*;
import com.pax.poslink.constant.EDCType;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class POSLink implements ICreditCard {
    private String _errorMessage;
    private String providerCode;
    private boolean isDeviceInfoInvalid(DeviceInfo deviceInfo)
    {
        if(deviceInfo == null)
        {
            _errorMessage = "Device Info Cannot be null";
            return true;
        }
        if(!DeviceInfo.isValidIPv4(deviceInfo.IP)) {
            _errorMessage = "IP Address Is Invalid";
            return true;
        }
        return false;
    }
    private PosLink createPosLink(DeviceInfo deviceInfo)
    {
        PosLink posLink = new PosLink();
        posLink.SetCommSetting(createCommSetting(deviceInfo));
        return posLink;
    }
    private CommSetting createCommSetting(DeviceInfo deviceInfo)
    {
        CommSetting commSetting = new CommSetting();
        commSetting.setType(CommSetting.TCP);
        commSetting.setDestIP(deviceInfo.IP);
        commSetting.setDestPort(String.valueOf(deviceInfo.Port));
        commSetting.setEnableProxy(false);
        commSetting.setTimeOut(String.valueOf(deviceInfo.Timeout));
        return  commSetting;
    }

    private boolean isExecuteTransactionError(PosLink posLink, PaymentRequest paymentRequest)
    {
        ManageRequest manageRequest = new ManageRequest();
        manageRequest.EDCType = manageRequest.ParseEDCType(EDCType.CREDIT);
        manageRequest.TransType = manageRequest.ParseTransType("SETVAR");
        //UserName
        manageRequest.VarName = "UserName";
        //TaLogin
        manageRequest.VarValue = "TA5714099";
        //UserPassword
        manageRequest.VarName1 = "UserPassword";
        //TaLogin Password
        manageRequest.VarValue1 = "Hotsauce9!";
        //MID
        manageRequest.VarName2 = "MID";
        //Merchant Number
        manageRequest.VarValue2 = "887000001519";
        //DeviceID
        manageRequest.VarName3 = "DeviceID";
        //MID + DeviceID
        manageRequest.VarValue3 = "88700000151901";
        posLink.ManageRequest = manageRequest;
        posLink.ProcessTrans();

        posLink.PaymentRequest = paymentRequest;
        if(paymentRequest == null)
        {
            throw  new RuntimeException("Request Is null");
        }
        ProcessTransResult response = posLink.ProcessTrans();
        _errorMessage = "";
        providerCode = String.valueOf(response.Code);
        if(response.Code !=ProcessTransResult.ProcessTransResultCode.OK)
        {
            _errorMessage = response.Msg;
            return true;
        }
        PaymentResponse paymentResponse = posLink.PaymentResponse;
        if(paymentResponse == null)
        {
            _errorMessage = "ChipReader did not send Response Back";
            return true;
        }
        if(paymentResponse.ResultCode.equals("000000"))
        {
            return false;
        }
        _errorMessage = paymentResponse.ResultTxt;
        return true;
    }
    private String convertBigDecimalValue(BigDecimal value)
    {
        BigDecimal multipliedValue = value.multiply(new BigDecimal("100"));
        return String.valueOf(multipliedValue.setScale(0, RoundingMode.HALF_UP));
    }

    @Override
    public Output authCard(Input input) {
        Output output = new Output();
        try
        {
            if(input == null)
            {
                output.ResultMessage = "Input Cannot be null";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(isDeviceInfoInvalid(input.deviceInfo))
            {
                output.ResultMessage = "Device Info Value Error:" + _errorMessage;
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            PosLink posLink = createPosLink(input.deviceInfo);

            //Start Generate Auth Request
            PaymentRequest pay = new PaymentRequest();
            pay.TenderType = pay.ParseTenderType("CREDIT");
            pay.TransType = pay.ParseTransType(TransType.AUTH);
            pay.Amount = convertBigDecimalValue(input.AuthAmount);
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
            pay.ECRRefNum = input.TransactionID;
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
            if(isExecuteTransactionError(posLink, pay))
            {
                output.ProviderCode = providerCode;
                output.ResultMessage = _errorMessage;
                output.ResultCode = ResultCode.ProviderError;
                return output;
            }
            try {
                posLink.PaymentResponse.ExtData = "<>" + posLink.PaymentResponse.ExtData + "</>";
                ExtData extData = TagConverter.DeserializeObject(ExtData.class,"",posLink.PaymentResponse.ExtData);
                assert extData != null;
                output.CardIssuers = CreditCardUtil.getCardIssuers(extData.CARDBIN);
                output.CardNumber = CreditCardUtil.getPartialCardNumber(extData.CARDBIN,"","");
                output.ExpDate = extData.ExpDate;
            }catch (Exception ignored) {

            }
            output.ProviderCode = providerCode;
            output.RefNumber = posLink.PaymentResponse.RefNum;
            output.ResultMessage = "Success!";
            output.ResultCode = ResultCode.Success;
        }
        catch(RuntimeException exception)
        {
            output.ResultMessage = exception.getMessage();
            output.ResultCode = ResultCode.SystemError;
        }
        return  output;
    }

    @Override
    public com.hotsauce.creditcard.io.capture.Output capture(com.hotsauce.creditcard.io.capture.Input input) {
        com.hotsauce.creditcard.io.capture.Output output = new com.hotsauce.creditcard.io.capture.Output();
        try
        {
            if(input == null)
            {
                output.ResultMessage = "Input Cannot be null";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(isDeviceInfoInvalid(input.deviceInfo))
            {
                output.ResultMessage = "Device Info Value Error:" + _errorMessage;
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(BigDecimal.ZERO.compareTo(input.AuthAmount) > -1) {
                output.ResultMessage = "InputValue Error: Auth amount must better than 0";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            PosLink posLink = createPosLink(input.deviceInfo);
            PaymentRequest pay;
            //if capture amount better than auth amount try to increase auth amount
            if(input.Amount.compareTo(input.AuthAmount) == 1) {
                pay = new PaymentRequest();
                pay.TenderType = pay.ParseTenderType("CREDIT");
                pay.TransType = 41;
                pay.Amount = convertBigDecimalValue(input.Amount);
                pay.CashBackAmt="";
                pay.FuelAmt = "";
                pay.ClerkID = "";
                pay.Zip="";
                pay.Street = "";
                pay.Street2 = "";
                pay.SurchargeAmt = "";
                pay.InvNum = "";
                pay.ECRRefNum = input.TransactionID;
                pay.AuthCode = "";
                pay.ECRTransID ="";
                pay.OrigRefNum = input.RefNumber;
                pay.OrigECRRefNum = "";
                pay.ContinuousScreen = "";
                pay.GiftCardType = "";
                pay.CVVBypassReason = "";
                pay.GiftTenderType = "";
                pay.OrigTraceNum = "";
                pay.ExtData = "";
                if(isExecuteTransactionError(posLink, pay))
                {
                    output.ProviderCode = providerCode;
                    output.ResultMessage = "Increase auth amount failed " + _errorMessage;
                    output.ResultCode = ResultCode.ProviderError;
                    return output;
                }
            }
            //Start Generate capture Request
            pay = new PaymentRequest();
            pay.TenderType = pay.ParseTenderType("CREDIT");
            pay.TransType = pay.ParseTransType(TransType.CAPTURE);
            pay.Amount = convertBigDecimalValue(input.Amount);
            pay.CashBackAmt="";
            pay.FuelAmt = "";
            pay.ClerkID = "";
            pay.Zip="";
            pay.TipAmt = convertBigDecimalValue(input.Tip);
            pay.TaxAmt = convertBigDecimalValue(input.Tax);
            pay.Street = "";
            pay.Street2 = "";
            pay.SurchargeAmt = "";
            pay.InvNum = "";
            pay.ECRRefNum = input.TransactionID;
            pay.AuthCode = "";
            pay.ECRTransID ="";
            pay.OrigRefNum = input.RefNumber;
            pay.OrigECRRefNum = "";
            pay.ContinuousScreen = "";
            pay.ServiceFee = convertBigDecimalValue(input.ServiceFee);
            pay.GiftCardType = "";
            pay.CVVBypassReason = "";
            pay.GiftTenderType = "";
            pay.OrigTraceNum = "";
            pay.ExtData = "";
            if(isExecuteTransactionError(posLink, pay))
            {
                output.ProviderCode = providerCode;
                output.ResultMessage = _errorMessage;
                output.ResultCode = ResultCode.ProviderError;
                return output;
            }
            output.ProviderCode = providerCode;
            output.RefNumber = posLink.PaymentResponse.RefNum;
            output.ResultMessage = "Success!";
            output.ResultCode = ResultCode.Success;
        }
        catch(RuntimeException exception)
        {
            output.ResultMessage = exception.getMessage();
            output.ResultCode = ResultCode.SystemError;
        }
        return  output;
    }

    @Override
    public com.hotsauce.creditcard.io.sale.Output sale(com.hotsauce.creditcard.io.sale.Input input) {
        com.hotsauce.creditcard.io.sale.Output output = new com.hotsauce.creditcard.io.sale.Output();
        try
        {
            if(input == null)
            {
                output.ResultMessage = "Input Cannot be null";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(isDeviceInfoInvalid(input.deviceInfo))
            {
                output.ResultMessage = "Device Info Value Error:" + _errorMessage;
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            PosLink posLink = createPosLink(input.deviceInfo);

            //Start Generate Pay Request
            PaymentRequest pay = new PaymentRequest();
            pay.TenderType = pay.ParseTenderType("CREDIT");
            pay.TransType = pay.ParseTransType(TransType.SALE);
            pay.Amount = convertBigDecimalValue(input.Amount);
            pay.CashBackAmt="";
            pay.FuelAmt = "";
            pay.ClerkID = "";
            pay.Zip="";
            pay.TipAmt = convertBigDecimalValue(input.Tip);
            pay.TaxAmt = convertBigDecimalValue(input.Tax);
            pay.Street = "";
            pay.Street2 = "";
            pay.SurchargeAmt = "";
            pay.InvNum = "";
            pay.ECRRefNum = input.TransactionID;
            pay.AuthCode = "";
            pay.ECRTransID =input.TransactionID;
            pay.OrigECRRefNum = "";
            //pay.CommercialCard = null;
            pay.ContinuousScreen = "";
            pay.ServiceFee = convertBigDecimalValue(input.ServiceFee);
            pay.GiftCardType = "";
            pay.CVVBypassReason = "";
            pay.GiftTenderType = "";
            pay.OrigTraceNum = "";
            pay.ExtData = "";
            if(isExecuteTransactionError(posLink, pay))
            {
                output.ProviderCode = providerCode;
                output.ResultMessage = _errorMessage;
                output.ResultCode = ResultCode.ProviderError;
                return output;
            }
            try {
                posLink.PaymentResponse.ExtData = "<>" + posLink.PaymentResponse.ExtData + "</>";
                ExtData extData = TagConverter.DeserializeObject(ExtData.class,"",posLink.PaymentResponse.ExtData);
                assert extData != null;
                output.CardIssuers = CreditCardUtil.getCardIssuers(extData.CARDBIN);
                output.CardNumber = CreditCardUtil.getPartialCardNumber(extData.CARDBIN,"","");
                output.ExpDate = extData.ExpDate;
            }catch (Exception ignored) {

            }
            output.ProviderCode = providerCode;
            output.RefNumber = posLink.PaymentResponse.RefNum;
            output.ResultMessage = "Success!";
            output.ResultCode = ResultCode.Success;
        }
        catch(RuntimeException exception)
        {
            output.ResultMessage = exception.getMessage();
            output.ResultCode = ResultCode.SystemError;
        }
        return  output;
    }

    @Override
    public com.hotsauce.creditcard.io.entertips.Output enterTips(com.hotsauce.creditcard.io.entertips.Input input) {
        com.hotsauce.creditcard.io.entertips.Output output = new com.hotsauce.creditcard.io.entertips.Output();
        try
        {
            if(input == null)
            {
                output.ResultMessage = "Input Cannot be null";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(isDeviceInfoInvalid(input.deviceInfo))
            {
                output.ResultMessage = "Device Info Value Error:" + _errorMessage;
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            PosLink posLink = createPosLink(input.deviceInfo);

            //Start Generate EnterTip Request
            PaymentRequest pay = new PaymentRequest();
            pay.TenderType = pay.ParseTenderType("CREDIT");
            pay.TransType = pay.ParseTransType(TransType.ADJUST);
            pay.Amount = convertBigDecimalValue(input.Tip);
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
            pay.ECRRefNum = input.TransactionID;
            pay.AuthCode = "";
            pay.ECRTransID ="";
            pay.OrigRefNum = input.RefNumber;
            pay.OrigECRRefNum = "";
            //pay.CommercialCard = null;
            pay.ContinuousScreen = "";
            pay.ServiceFee =  "";
            pay.GiftCardType = "";
            pay.CVVBypassReason = "";
            pay.GiftTenderType = "";
            pay.OrigTraceNum = "";
            pay.ExtData = "";
            if(isExecuteTransactionError(posLink, pay))
            {
                output.ProviderCode = providerCode;
                output.ResultMessage = _errorMessage;
                output.ResultCode = ResultCode.ProviderError;
                return output;
            }
            output.ProviderCode = providerCode;
            output.RefNumber = posLink.PaymentResponse.RefNum;
            output.ResultMessage = "Success!";
            output.ResultCode = ResultCode.Success;
        }
        catch(RuntimeException exception)
        {
            output.ResultMessage = exception.getMessage();
            output.ResultCode = ResultCode.SystemError;
        }
        return  output;
    }

    @Override
    public com.hotsauce.creditcard.io.adjusttips.Output adjustTips(com.hotsauce.creditcard.io.adjusttips.Input input) {
        com.hotsauce.creditcard.io.adjusttips.Output output = new com.hotsauce.creditcard.io.adjusttips.Output();
        try
        {
            if(input == null)
            {
                output.ResultMessage = "Input Cannot be null";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(isDeviceInfoInvalid(input.deviceInfo))
            {
                output.ResultMessage = "Device Info Value Error:" + _errorMessage;
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            PosLink posLink = createPosLink(input.deviceInfo);

            //Start Generate AdjustTip Request
            PaymentRequest pay = new PaymentRequest();
            pay.TenderType = pay.ParseTenderType("CREDIT");
            pay.TransType = pay.ParseTransType(TransType.ADJUST);
            pay.Amount = convertBigDecimalValue(input.Tip);
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
            pay.ECRRefNum = input.TransactionID;
            pay.AuthCode = "";
            pay.ECRTransID ="";
            pay.OrigRefNum = input.RefNumber;
            pay.OrigECRRefNum = "";
            pay.ContinuousScreen = "";
            pay.ServiceFee =  "";
            pay.GiftCardType = "";
            pay.CVVBypassReason = "";
            pay.GiftTenderType = "";
            pay.OrigTraceNum = "";
            pay.ExtData = "";
            if(isExecuteTransactionError(posLink, pay))
            {
                output.ResultMessage = _errorMessage;
                output.ResultCode = ResultCode.ProviderError;
                return output;
            }
            output.RefNumber = posLink.PaymentResponse.RefNum;
            output.ResultMessage = "Success!";
            output.ResultCode = ResultCode.Success;
        }
        catch(RuntimeException exception)
        {
            output.ResultMessage = exception.getMessage();
            output.ResultCode = ResultCode.SystemError;
        }
        return  output;
    }

    @Override
    public com.hotsauce.creditcard.io.voidsale.Output voidSale(com.hotsauce.creditcard.io.voidsale.Input input) {
        com.hotsauce.creditcard.io.voidsale.Output output = new com.hotsauce.creditcard.io.voidsale.Output();
        try
        {
            if(input == null)
            {
                output.ResultMessage = "Input Cannot be null";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(isDeviceInfoInvalid(input.deviceInfo))
            {
                output.ResultMessage = "Device Info Value Error:" + _errorMessage;
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            PosLink posLink = createPosLink(input.deviceInfo);

            //Start Generate AdjustTip Request
            PaymentRequest pay = new PaymentRequest();
            pay.TenderType = pay.ParseTenderType("CREDIT");
            pay.TransType = pay.ParseTransType(TransType.VOID);
            if (input.IsSettled)
            {
                pay.TransType = pay.ParseTransType(TransType.RETURN);
            }
            pay.ECRRefNum = input.TransactionID;
            pay.OrigRefNum = input.RefNumber;
            if(isExecuteTransactionError(posLink, pay))
            {
                output.ProviderCode = providerCode;
                output.ResultMessage = _errorMessage;
                output.ResultCode = ResultCode.ProviderError;
                return output;
            }
            output.ProviderCode = providerCode;
            output.ResultMessage = "Success!";
            output.ResultCode = ResultCode.Success;
        }
        catch(RuntimeException exception)
        {
            output.ResultMessage = exception.getMessage();
            output.ResultCode = ResultCode.SystemError;
        }
        return  output;
    }

    @Override
    public com.hotsauce.creditcard.io.batchsettlement.Output batchSettlement(com.hotsauce.creditcard.io.batchsettlement.Input input) {
        com.hotsauce.creditcard.io.batchsettlement.Output output = new com.hotsauce.creditcard.io.batchsettlement.Output();
        try
        {
            if(input == null)
            {
                output.ResultMessage = "Input Cannot be null";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(isDeviceInfoInvalid(input.deviceInfo))
            {
                output.ResultMessage = "Device Info Value Error:" + _errorMessage;
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            PosLink posLink = createPosLink(input.deviceInfo);
            BatchRequest batchRequest = new BatchRequest();
            batchRequest.TransType = batchRequest.ParseTransType(TransType.BATCH_CLOSE);
            posLink.BatchRequest = batchRequest;
            ProcessTransResult response = posLink.ProcessTrans();
            if(response.Code !=ProcessTransResult.ProcessTransResultCode.OK)
            {
                output.ProviderCode = providerCode;
                output.ResultMessage = response.Msg;
                output.ResultCode = ResultCode.ProviderError;
                return output;
            }
            output.ProviderCode = providerCode;
            output.ResultMessage = "Success!";
            output.ResultCode = ResultCode.Success;
        }
        catch(RuntimeException exception)
        {
            output.ResultMessage = exception.getMessage();
            output.ResultCode = ResultCode.SystemError;
        }
        return  output;
    }

    @Override
    public com.hotsauce.creditcard.io.voidauth.Output voidAuth(com.hotsauce.creditcard.io.voidauth.Input input) {
        com.hotsauce.creditcard.io.voidauth.Output output = new com.hotsauce.creditcard.io.voidauth.Output();
        try
        {
            if(input == null)
            {
                output.ResultMessage = "Input Cannot be null";
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            if(isDeviceInfoInvalid(input.deviceInfo))
            {
                output.ResultMessage = "Device Info Value Error:" + _errorMessage;
                output.ResultCode = ResultCode.InputError;
                return output;
            }
            PosLink posLink = createPosLink(input.deviceInfo);

            //Start Generate AdjustTip Request
            PaymentRequest pay = new PaymentRequest();
            pay.TenderType = pay.ParseTenderType("CREDIT");
            pay.TransType = pay.ParseTransType(TransType.VOID_AUTH);
            pay.ECRRefNum = input.TransactionID;
            pay.OrigRefNum = input.RefNumber;
            if(isExecuteTransactionError(posLink, pay))
            {
                output.ProviderCode = providerCode;
                output.ResultMessage = _errorMessage;
                output.ResultCode = ResultCode.ProviderError;
                return output;
            }
            output.ProviderCode = providerCode;
            output.ResultMessage = "Success!";
            output.ResultCode = ResultCode.Success;
        }
        catch(RuntimeException exception)
        {
            output.ResultMessage = exception.getMessage();
            output.ResultCode = ResultCode.SystemError;
        }
        return  output;
    }

    private static class TransType{
        public static final String AUTH="AUTH";

        public static final String CAPTURE="POSTAUTH";

        public static final String SALE="SALE";

        //voidSale Is for UnSettled Transaction
        public static final String VOID="VOID";

        public static final String ADJUST = "ADJUST";

        //Return Is for Settled Transaction
        public static final String RETURN="RETURN";

        public static final String VOID_AUTH = "VOID AUTH";

        public static final String INCREMENTAL_AUTH = "INCREMENTALAUTH";
        public static final String BATCH_CLOSE = "BATCHCLOSE";
    }
    public static class ExtData{
        public String ExpDate;
        public String BatchNum;
        public String CARDBIN;
    }
}