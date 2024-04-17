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
import com.hotsauce.creditcard.io.capture.Input;
import com.hotsauce.creditcard.io.capture.Output;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class CaptureViewModel extends BaseObservable {
    private final com.hotsauce.creditcard.io.capture.Input _captureInput = new Input();
    private static final DecimalFormat df = new DecimalFormat("0.00");
    public CaptureViewModel(){
        Thread thread = new Thread(() -> {
            try {
                updateTransactionID();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        Thread thread2 = new Thread(() -> {
            try {
                updateOriginalTransactionIDList();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread2.start();
        setAmount("0");
    }


    @Bindable
    public String getTransactionID(){
        return _captureInput.TransactionID ;
    }
    private void setTransactionID(String value){
        _captureInput.TransactionID = value;
        notifyPropertyChanged(BR.transactionID);
    }
    private void updateTransactionID() throws InterruptedException {
        while (true){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");
            LocalDateTime now = LocalDateTime.now();
            setTransactionID(dtf.format(now));
            Thread.sleep(1000);
        }
    }


    @Bindable
    public String getAmount(){
        return df.format(_captureInput.Amount);
    }
    public void  setAmount(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _captureInput.Amount = BigDecimal.valueOf(amount);
        setErrorMessage("");
        if (0>=amount)
        {
            setErrorMessage("Amount should better than 0");
        }
        notifyPropertyChanged(BR.amount);
    }


    @Bindable
    public String getTip(){
        return df.format(_captureInput.Tip);
    }
    public void setTip(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _captureInput.Tip = BigDecimal.valueOf(amount);
        notifyPropertyChanged(BR.tip);
    }


    @Bindable
    public String getTax(){
        return df.format(_captureInput.Tax);
    }
    public void setTax(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _captureInput.Tax = BigDecimal.valueOf(amount);
        notifyPropertyChanged(BR.tax);
    }


    @Bindable
    public String getServiceFee(){
        return df.format(_captureInput.ServiceFee);
    }
    public void setServiceFee(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _captureInput.ServiceFee = BigDecimal.valueOf(amount);
        notifyPropertyChanged(BR.serviceFee);
    }


    private ArrayList<Transaction> TransactionList = new ArrayList<>();
    private String[] _originalTransactionIDList = new String[]{};
    @Bindable
    public String[] getOriginalTransactionIDList()
    {
        return _originalTransactionIDList;
    }
    private void setOriginalTransactionIDList(String[] value){
        _originalTransactionIDList = value;
        notifyPropertyChanged(BR.isCaptureButtonEnable);
        notifyPropertyChanged(BR.originalTransactionIDList);
    }


    private int _SelectedOriginalTransactionIDPosition =0;
    @Bindable
    public int getSelectedOriginalTransactionIDPosition(){
        return _SelectedOriginalTransactionIDPosition;
    }
    @Bindable
    public void setSelectedOriginalTransactionIDPosition(int value){
        _SelectedOriginalTransactionIDPosition = value;
        _captureInput.RefNumber =TransactionList.get(value).REF_NUMBER;
        _captureInput.AuthAmount = TransactionList.get(value).AMOUNT;
        notifyPropertyChanged(BR.selectedOriginalTransactionIDPosition);
    }


    @Bindable
    public boolean getIsCaptureButtonEnable(){
        return _originalTransactionIDList.length!=0 & Objects.equals(_ErrorMessage, "");
    }


    private String _ErrorMessage;
    @Bindable
    public String getErrorMessage(){
        return _ErrorMessage;
    }
    private void setErrorMessage(String value){
        _ErrorMessage = value;
        notifyPropertyChanged(BR.errorMessage);
        notifyPropertyChanged(BR.isCaptureButtonEnable);
    }


    public void SendCaptureRequest(){
        ICreditCard creditCard;
        creditCard = CreditCardFactory.CreateInstance(Util.GetCreditCardType(Adapter.deviceInfo.DEVICE_TYPE));
        _captureInput.deviceInfo = new DeviceInfo();
        _captureInput.deviceInfo.IP = Adapter.deviceInfo.IP;
        _captureInput.deviceInfo.Port = Adapter.deviceInfo.PORT;
        _captureInput.deviceInfo.Timeout = Adapter.deviceInfo.TIMEOUT;
        _captureInput.deviceInfo.RetryTime = Adapter.deviceInfo.RETRY_TIME;
        Output output = creditCard.capture(_captureInput);
        Event.Invoke(output.ResultMessage);
        if (Objects.equals(output.ResultCode, ResultCode.Success))
        {
            com.example.testcreditcard.lib.controller.Transaction db = new com.example.testcreditcard.lib.controller.Transaction();
            com.example.testcreditcard.lib.model.Transaction transaction = new Transaction();
            transaction.IS_SETTLED = "N";
            transaction.TRANSACTION_ID = _captureInput.TransactionID;
            transaction.DEVICE_ID = Adapter.deviceInfo._id;
            transaction.LAST_MODIFY_DATETIME = System.currentTimeMillis();
            transaction.TRANSACTION_STATUS = "SALE";
            transaction.REF_NUMBER = output.RefNumber;
            transaction.AMOUNT = _captureInput.Amount;
            transaction.TAX = _captureInput.Tax;
            transaction.SERVICE_FEE = _captureInput.ServiceFee;
            transaction.TIP = _captureInput.Tip;
            db.AddData(transaction);
            transaction.TRANSACTION_ID =TransactionList.get(_SelectedOriginalTransactionIDPosition).TRANSACTION_ID;
            transaction.TRANSACTION_STATUS = "CAPTURE";
            db.SaveData(transaction);
        }
    }

    private long _LastModifyDateTime;
    private int _LastDeviceID = -1;
    private void updateOriginalTransactionIDList() throws InterruptedException {
        com.example.testcreditcard.lib.controller.Transaction db = new com.example.testcreditcard.lib.controller.Transaction();
        long TempLastModifyDateTime;
        while (true){
            if(Adapter.deviceInfo._id != _LastDeviceID){
                _LastModifyDateTime =0;
                _LastDeviceID = Adapter.deviceInfo._id;
                TransactionList = new ArrayList<>();
                setOriginalTransactionIDList(new String[]{});
            }
            TempLastModifyDateTime=db.LoadNewestData(Adapter.deviceInfo._id).LAST_MODIFY_DATETIME;
            if(TempLastModifyDateTime > _LastModifyDateTime){
                _LastModifyDateTime=TempLastModifyDateTime;
                TransactionList = new ArrayList<>();
                TransactionList.addAll(Arrays.asList(db.LoadData(Adapter.deviceInfo._id,"AUTH")));
                setOriginalTransactionIDList(TransactionList.stream().map(x -> x.TRANSACTION_ID).toArray(String[]::new));
            }
        }
    }
}
