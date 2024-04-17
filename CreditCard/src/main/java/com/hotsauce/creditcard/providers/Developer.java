package com.hotsauce.creditcard.providers;
import com.hotsauce.creditcard.ICreditCard;
import com.hotsauce.creditcard.io.ResultCode;
import com.hotsauce.creditcard.io.voidauth.Input;
import com.hotsauce.creditcard.io.voidauth.Output;

public class Developer implements ICreditCard {
    @Override
    public com.hotsauce.creditcard.io.auth.Output authCard(com.hotsauce.creditcard.io.auth.Input input) {
        com.hotsauce.creditcard.io.auth.Output output = new com.hotsauce.creditcard.io.auth.Output();
        try
        {
            output.RefNumber = input.TransactionID;
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
            output.RefNumber = input.TransactionID;
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
            output.RefNumber = input.TransactionID;
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
            output.RefNumber = input.TransactionID;
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
            output.RefNumber = input.TransactionID;
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
    public Output voidAuth(Input input) {
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
}
