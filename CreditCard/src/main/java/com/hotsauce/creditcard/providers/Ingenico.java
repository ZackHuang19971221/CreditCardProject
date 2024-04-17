package com.hotsauce.creditcard.providers;

import com.hotsauce.creditcard.ICreditCard;
import com.hotsauce.creditcard.io.auth.Input;
import com.hotsauce.creditcard.io.auth.Output;
import com.hotsauce.creditcard.io.ResultCode;

public class Ingenico implements ICreditCard {

    @Override
    public Output authCard(Input input) {
        Output output = new Output();
        try
        {
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


    class TransType {
        public static final String Sale = "CCR1";
        public static final String Auth = "CCR2";
        public static final String Capture = "CCR4";
        public static final String AuthCancel = "CCR7";
        public static final String Return = "CCR9";
        public static final String Void = "CCRX";
        public static final String Batch = "CCRZ";
    }
}
