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
import com.hotsauce.creditcard.io.adjusttips.Input;
import com.hotsauce.creditcard.io.adjusttips.Output;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class AdjustTipViewModel extends BaseObservable {
    //Get Sale and EnterTip > 0
    private final com.hotsauce.creditcard.io.adjusttips.Input _adjustTipInput = new Input();
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public AdjustTipViewModel(){
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
        return _adjustTipInput.TransactionID ;
    }
    private void setTransactionID(String value){
        _adjustTipInput.TransactionID = value;
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
        notifyPropertyChanged(BR.isAdjustTipButtonEnable);
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
        _adjustTipInput.RefNumber =TransactionList.get(value).REF_NUMBER;
        notifyPropertyChanged(BR.selectedOriginalTransactionIDPosition);
    }


    @Bindable
    public String getTip(){
        return df.format(_adjustTipInput.Tip);
    }
    public void setTip(String value){
        double NewValue =Double.parseDouble(value);
        _adjustTipInput.Tip = BigDecimal.valueOf(NewValue);
        setErrorMessage("");
        if(0 > NewValue){
            setErrorMessage("Tip must be better than or equal 0");
        }
        notifyPropertyChanged(BR.tip);
    }


    private String _ErrorMessage;
    @Bindable
    public String getErrorMessage(){
        return _ErrorMessage;
    }
    private void setErrorMessage(String value){
        _ErrorMessage = value;
        notifyPropertyChanged(BR.errorMessage);
        notifyPropertyChanged(BR.isAdjustTipButtonEnable);
    }


    @Bindable
    public boolean getIsAdjustTipButtonEnable(){
        return _originalTransactionIDList.length!=0 & Objects.equals(_ErrorMessage, "");
    }


    public void SendAdjustTipRequest(){
        ICreditCard creditCard;
        creditCard = CreditCardFactory.CreateInstance(Util.GetCreditCardType(Adapter.deviceInfo.DEVICE_TYPE));
        _adjustTipInput.deviceInfo = new DeviceInfo();
        _adjustTipInput.deviceInfo.IP = Adapter.deviceInfo.IP;
        _adjustTipInput.deviceInfo.Port = Adapter.deviceInfo.PORT;
        _adjustTipInput.deviceInfo.Timeout = Adapter.deviceInfo.TIMEOUT;
        _adjustTipInput.deviceInfo.RetryTime = Adapter.deviceInfo.RETRY_TIME;
        Output output = creditCard.adjustTips(_adjustTipInput);
        Event.Invoke(output.ResultMessage);
        if (Objects.equals(output.ResultCode, ResultCode.Success))
        {
            com.example.testcreditcard.lib.controller.Transaction db = new com.example.testcreditcard.lib.controller.Transaction();
            com.example.testcreditcard.lib.model.Transaction transaction = new Transaction();
            transaction.IS_SETTLED = "N";
            transaction.AMOUNT = TransactionList.get(_SelectedOriginalTransactionIDPosition).AMOUNT;
            transaction.TAX = TransactionList.get(_SelectedOriginalTransactionIDPosition).TAX;
            transaction.SERVICE_FEE = TransactionList.get(_SelectedOriginalTransactionIDPosition).SERVICE_FEE;
            transaction.TIP = _adjustTipInput.Tip;
            transaction.TRANSACTION_ID = _adjustTipInput.TransactionID;
            transaction.DEVICE_ID = Adapter.deviceInfo._id;
            transaction.LAST_MODIFY_DATETIME = System.currentTimeMillis();
            transaction.REF_NUMBER = output.RefNumber;
            transaction.TRANSACTION_STATUS = "SALE";
            db.AddData(transaction);
            transaction.TRANSACTION_ID = TransactionList.get(_SelectedOriginalTransactionIDPosition).TRANSACTION_ID;
            transaction.TRANSACTION_STATUS = "ADJUST_TIP";
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
                TransactionList.addAll(Arrays.stream(db.LoadData(Adapter.deviceInfo._id,"SALE")).filter(x->x.TIP.compareTo(bigDecimal)==1).collect(Collectors.toList()));
                setOriginalTransactionIDList(TransactionList.stream().map(x -> x.TRANSACTION_ID).toArray(String[]::new));
            }
        }
    }
}
