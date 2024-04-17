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
import com.hotsauce.creditcard.io.voidsale.Input;
import com.hotsauce.creditcard.io.voidsale.Output;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class VoidViewModel extends BaseObservable {
    private final com.hotsauce.creditcard.io.voidsale.Input _voidInput = new Input();

    public VoidViewModel(){
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
    }

    @Bindable
    public String getTransactionID(){
        return _voidInput.TransactionID ;
    }
    private void setTransactionID(String value){
        _voidInput.TransactionID = value;
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
        notifyPropertyChanged(BR.isVoidButtonEnable);
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
        _voidInput.RefNumber =TransactionList.get(value).REF_NUMBER;
        _voidInput.IsSettled = TransactionList.get(value).IS_SETTLED.equals("Y");
        notifyPropertyChanged(BR.selectedOriginalTransactionIDPosition);
    }

    @Bindable
    public boolean getIsVoidButtonEnable(){
        return _originalTransactionIDList.length!=0;
    }

    public void SendVoidRequest(){
        ICreditCard creditCard;
        creditCard = CreditCardFactory.CreateInstance(Util.GetCreditCardType(Adapter.deviceInfo.DEVICE_TYPE));
        _voidInput.deviceInfo = new DeviceInfo();
        _voidInput.deviceInfo.IP = Adapter.deviceInfo.IP;
        _voidInput.deviceInfo.Port = Adapter.deviceInfo.PORT;
        _voidInput.deviceInfo.Timeout = Adapter.deviceInfo.TIMEOUT;
        _voidInput.deviceInfo.RetryTime = Adapter.deviceInfo.RETRY_TIME;
        Output output = creditCard.voidSale(_voidInput);
        Event.Invoke(output.ResultMessage);
        if (Objects.equals(output.ResultCode, ResultCode.Success))
        {
            com.example.testcreditcard.lib.controller.Transaction db = new com.example.testcreditcard.lib.controller.Transaction();
            com.example.testcreditcard.lib.model.Transaction transaction = new Transaction();
            transaction.IS_SETTLED = "N";
            transaction.TRANSACTION_ID = _voidInput.TransactionID;
            transaction.DEVICE_ID = Adapter.deviceInfo._id;
            transaction.LAST_MODIFY_DATETIME = System.currentTimeMillis();
            transaction.TRANSACTION_STATUS = "VOID";
            db.AddData(transaction);
            transaction.TRANSACTION_ID = TransactionList.get(_SelectedOriginalTransactionIDPosition).TRANSACTION_ID;
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
                TransactionList.addAll(Arrays.asList(db.LoadData(Adapter.deviceInfo._id,"SALE")));
                setOriginalTransactionIDList(TransactionList.stream().map(x -> x.TRANSACTION_ID).toArray(String[]::new));
            }
        }
    }
}
