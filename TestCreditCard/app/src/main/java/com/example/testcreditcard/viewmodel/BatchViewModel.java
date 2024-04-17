package com.example.testcreditcard.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.testcreditcard.BR;
import com.example.testcreditcard.Event;
import com.example.testcreditcard.lib.Adapter;
import com.example.testcreditcard.lib.model.Transaction;
import com.hotsauce.creditcard.CreditCardFactory;
import com.hotsauce.creditcard.ICreditCard;
import com.hotsauce.creditcard.io.batchsettlement.Output;
import com.hotsauce.creditcard.io.batchsettlement.Input;
import com.hotsauce.creditcard.io.DeviceInfo;
import com.hotsauce.creditcard.io.ResultCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class BatchViewModel extends BaseObservable {
    private final com.hotsauce.creditcard.io.batchsettlement.Input _batchInput = new Input();

    public BatchViewModel(){
        Thread thread = new Thread(() -> {
            try {
                updateOriginalTransactionIDList();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        Thread checkThread = new Thread(this::checkDevice);
        checkThread.start();
    }

    private void checkDevice(){
        while (true) {
            if (Adapter.deviceInfo._id == -1) {
                setErrorMessage("Device Info did not set");
            }
            else
            {
                setErrorMessage("");
            }
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
        notifyPropertyChanged(BR.isBatchButtonEnable);
        notifyPropertyChanged(BR.originalTransactionIDList);
    }

    @Bindable
    public boolean getIsBatchButtonEnable(){
        return _originalTransactionIDList.length!=0 & Objects.equals(getErrorMessage(), "");
    }

    private String _ErrorMessage;
    @Bindable
    public String getErrorMessage(){
        return _ErrorMessage;
    }
    private void setErrorMessage(String value){
        _ErrorMessage = value;
        notifyPropertyChanged(BR.errorMessage);
        notifyPropertyChanged(BR.isBatchButtonEnable);
    }


    public void SendBatchRequest(){
        ICreditCard creditCard;
        creditCard = CreditCardFactory.CreateInstance(Util.GetCreditCardType(Adapter.deviceInfo.DEVICE_TYPE));
        _batchInput.deviceInfo = new DeviceInfo();
        _batchInput.deviceInfo.IP = Adapter.deviceInfo.IP;
        _batchInput.deviceInfo.Port = Adapter.deviceInfo.PORT;
        _batchInput.deviceInfo.Timeout = Adapter.deviceInfo.TIMEOUT;
        _batchInput.deviceInfo.RetryTime = Adapter.deviceInfo.RETRY_TIME;
        Output output = creditCard.batchSettlement(_batchInput);
        Event.Invoke(output.ResultMessage);
        if (!Objects.equals(output.ResultCode, ResultCode.Success)) {
            return;
        }
        com.example.testcreditcard.lib.controller.Transaction db = new com.example.testcreditcard.lib.controller.Transaction();
        for (Transaction item:TransactionList)
        {
            item.IS_SETTLED ="Y";
            item.LAST_MODIFY_DATETIME = System.currentTimeMillis();
            db.SaveData(item);
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
                TransactionList.addAll(Arrays.stream(db.LoadData(Adapter.deviceInfo._id,"SALE")).filter(x-> !Objects.equals(x.IS_SETTLED, "Y")).collect(Collectors.toList()));
                setOriginalTransactionIDList(TransactionList.stream().map(x -> x.TRANSACTION_ID).toArray(String[]::new));
            }
        }
    }
}
