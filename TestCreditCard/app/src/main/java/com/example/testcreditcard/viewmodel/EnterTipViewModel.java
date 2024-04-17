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
import com.hotsauce.creditcard.io.entertips.Input;
import com.hotsauce.creditcard.io.entertips.Output;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class EnterTipViewModel extends BaseObservable {
    //Get Sale and EnterTip = 0
    private final com.hotsauce.creditcard.io.entertips.Input _enterTipsInput = new Input();
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public EnterTipViewModel(){
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
        setTip("0");
    }


    @Bindable
    public String getTransactionID(){
        return _enterTipsInput.TransactionID ;
    }
    private void setTransactionID(String value){
        _enterTipsInput.TransactionID = value;
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


    private ArrayList<Transaction> TransactionList = new ArrayList<>();
    private String[] _originalTransactionIDList = new String[]{};
    @Bindable
    public String[] getOriginalTransactionIDList()
    {
        return _originalTransactionIDList;
    }
    private void setOriginalTransactionIDList(String[] value){
        _originalTransactionIDList = value;
        notifyPropertyChanged(BR.isEnterTipButtonEnable);
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
        _enterTipsInput.RefNumber =TransactionList.get(value).REF_NUMBER;
        notifyPropertyChanged(BR.selectedOriginalTransactionIDPosition);
    }


    @Bindable
    public String getTip(){
        return df.format(_enterTipsInput.Tip);
    }
    public void setTip(String value){
        double amount = 0.00;
        try {
            amount = Double.parseDouble(value);
        }catch (Exception ignored){}
        _enterTipsInput.Tip = BigDecimal.valueOf(amount);
        setErrorMessage("");
        if(0>=amount){
            setErrorMessage("Tip must be better than 0");
        }
        notifyPropertyChanged(BR.tip);
    }


    @Bindable
    public boolean getIsEnterTipButtonEnable(){
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
        notifyPropertyChanged(BR.isEnterTipButtonEnable);
    }


    public void SendEnterTipRequest(){
        ICreditCard creditCard;
        creditCard = CreditCardFactory.CreateInstance(Util.GetCreditCardType(Adapter.deviceInfo.DEVICE_TYPE));
        _enterTipsInput.deviceInfo = new DeviceInfo();
        _enterTipsInput.deviceInfo.IP = Adapter.deviceInfo.IP;
        _enterTipsInput.deviceInfo.Port = Adapter.deviceInfo.PORT;
        _enterTipsInput.deviceInfo.Timeout = Adapter.deviceInfo.TIMEOUT;
        _enterTipsInput.deviceInfo.RetryTime = Adapter.deviceInfo.RETRY_TIME;
        Output output = creditCard.enterTips(_enterTipsInput);
        Event.Invoke(output.ResultMessage);
        if (Objects.equals(output.ResultCode, ResultCode.Success))
        {
            com.example.testcreditcard.lib.controller.Transaction db = new com.example.testcreditcard.lib.controller.Transaction();
            com.example.testcreditcard.lib.model.Transaction transaction = new Transaction();
            transaction.IS_SETTLED = "N";
            transaction.TIP = _enterTipsInput.Tip;
            transaction.TRANSACTION_ID = _enterTipsInput.TransactionID;
            transaction.REF_NUMBER = output.RefNumber;
            transaction.DEVICE_ID = Adapter.deviceInfo._id;
            transaction.LAST_MODIFY_DATETIME = System.currentTimeMillis();
            transaction.TRANSACTION_STATUS = "SALE";
            db.AddData(transaction);
            transaction.TRANSACTION_STATUS = "ENTER_TIP";
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
                BigDecimal bigDecimal = new BigDecimal(0);
                TransactionList.addAll(Arrays.stream(db.LoadData(Adapter.deviceInfo._id,"SALE")).filter(x->x.TIP.compareTo(bigDecimal) ==0).collect(Collectors.toList()));
                setOriginalTransactionIDList(TransactionList.stream().map(x -> x.TRANSACTION_ID).toArray(String[]::new));
            }
        }
    }
}
