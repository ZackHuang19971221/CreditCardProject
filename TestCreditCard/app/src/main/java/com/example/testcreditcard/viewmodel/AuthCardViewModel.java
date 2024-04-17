package com.example.testcreditcard.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.testcreditcard.BR;
import com.example.testcreditcard.Event;
import com.example.testcreditcard.lib.Adapter;
import com.example.testcreditcard.lib.model.Transaction;
import com.hotsauce.creditcard.CreditCardFactory;
import com.hotsauce.creditcard.ICreditCard;
import com.hotsauce.creditcard.io.auth.Input;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.ResultCode;
import com.hotsauce.creditcard.io.auth.Output;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AuthCardViewModel extends BaseObservable {
    private final com.hotsauce.creditcard.io.auth.Input _authInput = new Input();
    private static final DecimalFormat df = new DecimalFormat("0.00");
    public AuthCardViewModel(){
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
        setAmount("0");
    }

    private void checkDevice(){
        BigDecimal bigDecimal = BigDecimal.ZERO;
        while (true) {
            if (Adapter.deviceInfo._id == -1) {
                setErrorMessage("Device Info did not set");
            } else if ( _authInput.AuthAmount.compareTo(bigDecimal)!=1) {
                setErrorMessage("Amount should better than 0");
            } else
            {
                setErrorMessage("");
            }
        }
    }

    @Bindable
    public String getTransactionID(){
        return _authInput.TransactionID ;
    }
    private void setTransactionID(String value){
        _authInput.TransactionID = value;
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
        return df.format(_authInput.AuthAmount);
    }
    public void  setAmount(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _authInput.AuthAmount = BigDecimal.valueOf(amount);
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    public boolean getIsAuthButtonEnable(){
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
        notifyPropertyChanged(BR.isAuthButtonEnable);
    }

    public void SendAuthRequest()
    {
        ICreditCard creditCard;
        creditCard = CreditCardFactory.CreateInstance(Util.GetCreditCardType(Adapter.deviceInfo.DEVICE_TYPE));
        _authInput.deviceInfo = new DeviceInfo();
        _authInput.deviceInfo.IP = Adapter.deviceInfo.IP;
        _authInput.deviceInfo.Port = Adapter.deviceInfo.PORT;
        _authInput.deviceInfo.Timeout = Adapter.deviceInfo.TIMEOUT;
        _authInput.deviceInfo.RetryTime = Adapter.deviceInfo.RETRY_TIME;
        Output output= creditCard.authCard(_authInput);
        Event.Invoke(output.ResultMessage);
        if (Objects.equals(output.ResultCode, ResultCode.Success))
        {
            com.example.testcreditcard.lib.controller.Transaction db = new com.example.testcreditcard.lib.controller.Transaction();
            com.example.testcreditcard.lib.model.Transaction transaction = new Transaction();
            transaction.IS_SETTLED = "N";
            transaction.TRANSACTION_ID = _authInput.TransactionID;
            transaction.DEVICE_ID = Adapter.deviceInfo._id;
            transaction.AMOUNT = _authInput.AuthAmount;
            transaction.LAST_MODIFY_DATETIME = System.currentTimeMillis();
            transaction.TRANSACTION_STATUS = "AUTH";
            transaction.REF_NUMBER = output.RefNumber;
            db.AddData(transaction);
        }
    }
}
