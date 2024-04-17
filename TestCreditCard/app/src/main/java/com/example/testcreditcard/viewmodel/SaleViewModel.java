package com.example.testcreditcard.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.testcreditcard.BR;
import com.example.testcreditcard.Event;
import com.example.testcreditcard.lib.Adapter;
import com.example.testcreditcard.lib.model.Transaction;
import com.hotsauce.creditcard.CreditCardFactory;
import com.hotsauce.creditcard.ICreditCard;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.ResultCode;
import com.hotsauce.creditcard.io.sale.Input;
import com.hotsauce.creditcard.io.sale.Output;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SaleViewModel extends BaseObservable {

    private final com.hotsauce.creditcard.io.sale.Input _saleInput = new Input();
    private static final DecimalFormat df = new DecimalFormat("0.00");
    public SaleViewModel(){

        Thread thread = new Thread(() -> {
            try {
                updateTransactionID();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        Thread checkThread = new Thread(this::checkDevice);
        checkThread.start();
    }

    private void checkDevice(){
        BigDecimal bigDecimal = BigDecimal.ZERO;
        while (true) {
            if (Adapter.deviceInfo._id == -1) {
                setErrorMessage("Device Info did not set");
            } else if (_saleInput.Amount.compareTo(bigDecimal)!=1) {
                setErrorMessage("Amount should better than 0");
            } else
            {
                setErrorMessage("");
            }
        }
    }

    @Bindable
    public String getTransactionID(){
        return _saleInput.TransactionID ;
    }
    private void setTransactionID(String value){
        _saleInput.TransactionID = value;
        notifyPropertyChanged(BR.transactionID);
    }
    private void updateTransactionID() throws InterruptedException {
        while (true){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMddhhmmss");
            LocalDateTime now = LocalDateTime.now();
            setTransactionID(dtf.format(now));
            Thread.sleep(1000);
        }
    }


    @Bindable
    public String getAmount(){
        return df.format(_saleInput.Amount);
    }
    public void  setAmount(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _saleInput.Amount = BigDecimal.valueOf(amount);
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    public String getTip(){
        return df.format(_saleInput.Tip);
    }
    public void setTip(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _saleInput.Tip = BigDecimal.valueOf(amount);
        notifyPropertyChanged(BR.tip);
    }

    @Bindable
    public String getTax(){
        return df.format(_saleInput.Tax);
    }
    public void setTax(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _saleInput.Tax = BigDecimal.valueOf(amount);
        notifyPropertyChanged(BR.tax);
    }

    @Bindable
    public String getServiceFee(){
        return df.format(_saleInput.ServiceFee);
    }
    public void setServiceFee(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _saleInput.ServiceFee = BigDecimal.valueOf(amount);
        notifyPropertyChanged(BR.serviceFee);
    }

    @Bindable
    public boolean getIsSendButtonEnable(){
        return Objects.equals(getErrorMessage(), "");
    }

    private String _ErrorMessage;
    @Bindable
    public String getErrorMessage(){
        return _ErrorMessage;
    }
    private void setErrorMessage(String value){
        _ErrorMessage = value;
        notifyPropertyChanged(BR.errorMessage);
        notifyPropertyChanged(BR.isSendButtonEnable);
    }

    public void SendSaleRequest()
    {
        ICreditCard creditCard;
        creditCard = CreditCardFactory.CreateInstance(Util.GetCreditCardType(Adapter.deviceInfo.DEVICE_TYPE));
        _saleInput.deviceInfo = new DeviceInfo();
        _saleInput.deviceInfo.IP = Adapter.deviceInfo.IP;
        _saleInput.deviceInfo.Port = Adapter.deviceInfo.PORT;
        _saleInput.deviceInfo.Timeout = Adapter.deviceInfo.TIMEOUT;
        _saleInput.deviceInfo.RetryTime = Adapter.deviceInfo.RETRY_TIME;
        Output output= creditCard.sale(_saleInput);
        Event.Invoke(output.ResultMessage);
        if (Objects.equals(output.ResultCode, ResultCode.Success))
        {
            com.example.testcreditcard.lib.controller.Transaction db = new com.example.testcreditcard.lib.controller.Transaction();
            com.example.testcreditcard.lib.model.Transaction transaction = new Transaction();
            transaction.IS_SETTLED = "N";
            transaction.TRANSACTION_ID = _saleInput.TransactionID;
            transaction.DEVICE_ID = Adapter.deviceInfo._id;
            transaction.LAST_MODIFY_DATETIME = System.currentTimeMillis();
            transaction.TRANSACTION_STATUS = "SALE";
            transaction.AMOUNT = _saleInput.Amount;
            transaction.TAX = _saleInput.Tax;
            transaction.SERVICE_FEE = _saleInput.ServiceFee;
            transaction.TIP = _saleInput.Tip;
            transaction.REF_NUMBER = output.RefNumber;
            db.AddData(transaction);
        }
    }
}
