package com.example.testcreditcard.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.testcreditcard.BR;
import com.example.testcreditcard.lib.Adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DeviceInfoViewModel extends BaseObservable {
    private boolean NoChangeSelectIndex = false;
    private void setDeviceInfo(com.example.testcreditcard.lib.model.DeviceInfo deviceInfo){
        if(deviceInfo.DEVICE_TYPE == null || deviceInfo.DEVICE_TYPE.equals(""))
        {
            deviceInfo.DEVICE_TYPE = Arrays.asList(_DeviceTypeList).get(0);
        }
        Adapter.deviceInfo = deviceInfo;
        //Refresh Screen
        notifyPropertyChanged(BR.name);
        notifyPropertyChanged(BR.port);
        notifyPropertyChanged(BR.timeout);
        notifyPropertyChanged(BR.retryTime);
        setSelectedDevicePosition(Arrays.asList(_DeviceTypeList).indexOf(deviceInfo.DEVICE_TYPE));
        setIP(deviceInfo.IP);
        setSaveButtonText();
        setIsDeleteButtonEnable();
    }

    public DeviceInfoViewModel()
    {
        setDeviceInfo(new com.example.testcreditcard.lib.model.DeviceInfo());
        setDeviceInfoList();
    }

    private int _SelectedDeviceInfoPosition =0;
    @Bindable
    public int getSelectedDeviceInfoPosition(){
        return _SelectedDeviceInfoPosition;
    }
    @Bindable
    public void setSelectedDeviceInfoPosition(int value){
        if(NoChangeSelectIndex){
            if(_SelectedDeviceInfoPosition>0){
                value = _SelectedDeviceInfoPosition-1;
            }
            NoChangeSelectIndex = false;
        }
        _SelectedDeviceInfoPosition = value;
        setDeviceInfo(_deviceInfList.get(value));
        notifyPropertyChanged(BR.selectedDeviceInfoPosition);
    }


    private final String[] _DeviceTypeList =new String[]{Util.CreditCard.PosLink, Util.CreditCard.SPIN, Util.CreditCard.Ingenico};
    @Bindable
    public String[] getDeviceList(){
        return _DeviceTypeList;
    }

    private int _SelectedDevicePosition =0;
    @Bindable
    public int getSelectedDevicePosition(){
        return _SelectedDevicePosition;
    }
    @Bindable
    public void setSelectedDevicePosition(int value){
        _SelectedDevicePosition = value;
        Adapter.deviceInfo.DEVICE_TYPE = Arrays.asList(_DeviceTypeList).get(value);
        notifyPropertyChanged(BR.selectedDevicePosition);
    }


    private ArrayList<com.example.testcreditcard.lib.model.DeviceInfo> _deviceInfList;
    private String[] _deviceInfoNameList;
    @Bindable
    public String[] getDeviceInfoList(){
        return _deviceInfoNameList;
    }
    private void  setDeviceInfoList(){
        com.example.testcreditcard.lib.controller.DeviceInfo db = new com.example.testcreditcard.lib.controller.DeviceInfo();
        _deviceInfList = new ArrayList<>();
        if(Adapter.deviceInfo._id==-1){
            _deviceInfList.add(Adapter.deviceInfo);
        }
        else
        {
            NoChangeSelectIndex = true;
        }
        _deviceInfList.addAll(Arrays.asList(db.LoadData()));
        _deviceInfoNameList = _deviceInfList.stream().map(n -> n.NAME).toArray(String[]::new);
        notifyPropertyChanged(BR.deviceInfoList);
    }


    private String _saveButtonText = "Add";
    @Bindable
    public String getSaveButtonText(){
        return _saveButtonText;
    }
    private void setSaveButtonText(){
        if(Adapter.deviceInfo._id == -1){
            _saveButtonText = "Add";
        }
        else
        {
            _saveButtonText = "Save";
        }
        notifyPropertyChanged(BR.saveButtonText);
    }

    @Bindable
    public String getName(){
        if(Adapter.deviceInfo.NAME == null){return "";}
        return Adapter.deviceInfo.NAME;
    }
    public void setName(String value){
        Adapter.deviceInfo.NAME = value;
        setDeviceInfoList();
        notifyPropertyChanged(BR.name);
    }

    private boolean _IsDeleteButtonEnable;
    @Bindable
    public boolean getIsDeleteButtonEnable(){
        return _IsDeleteButtonEnable;
    }
    private void  setIsDeleteButtonEnable(){
        _IsDeleteButtonEnable = Adapter.deviceInfo._id != -1;
        notifyPropertyChanged(BR.isDeleteButtonEnable);
    }

    private boolean _IsSaveButtonEnable;
    @Bindable
    public boolean getIsSaveButtonEnable(){
        return _IsSaveButtonEnable;
    }
    private void  setIsSaveButtonEnable(){
        _IsSaveButtonEnable = Objects.equals(_IPErrorMessage, "");
        notifyPropertyChanged(BR.isSaveButtonEnable);
    }

    private String _IPErrorMessage = "";
    @Bindable
    public String getIPErrorMessage(){
        return String.valueOf( _IPErrorMessage);
    }
    public void setIPErrorMessage(String value){
        _IPErrorMessage =value;
        setIsSaveButtonEnable();
        notifyPropertyChanged(BR.iPErrorMessage);
    }

    @Bindable
    public String getIP(){
        return Adapter.deviceInfo.IP;
    }
    public void setIP(String value){
        if(!com.hotsauce.creditcard.io.DeviceInfo.isValidIPv4(value)){
            setIPErrorMessage("IP Address is Not Valid");
        }
        else {
            setIPErrorMessage("");
        }
        Adapter.deviceInfo.IP = value;
        notifyPropertyChanged(BR.iP);
    }

    @Bindable
    public String getPort(){
        return String.valueOf(Adapter.deviceInfo.PORT);
    }
    public void setPort(String value){
        int port = 0;
        try
        {
            port =Integer.parseInt(value);
        }
        catch (Exception ignored) {}
        Adapter.deviceInfo.PORT = port;
        notifyPropertyChanged(BR.port);
    }

    @Bindable
    public String getTimeout(){
        return String.valueOf(Adapter.deviceInfo.TIMEOUT);
    }
    public void setTimeout(String value){
        int timeout = 0;
        try
        {
           timeout = Integer.parseInt(value);
        }
        catch (Exception ignored){}
        Adapter.deviceInfo.TIMEOUT = timeout;
        notifyPropertyChanged(BR.timeout);
    }

    @Bindable
    public String getRetryTime(){
        return String.valueOf(Adapter.deviceInfo.RETRY_TIME);
    }
    public void setRetryTime(String value){
        int retryTime = 0;
        try
        {
            retryTime= Integer.parseInt(value);
        }
        catch (Exception ignored){}
        Adapter.deviceInfo.RETRY_TIME = retryTime;
        notifyPropertyChanged(BR.timeout);
    }

    public void SaveData(){
        try
        {
            com.example.testcreditcard.lib.controller.DeviceInfo db = new com.example.testcreditcard.lib.controller.DeviceInfo();
            if(Adapter.deviceInfo._id == -1)
            {
                db.AddData(Adapter.deviceInfo);
                setDeviceInfo(db.LoadNewestData());
            }
            else
            {
                db.SaveData(Adapter.deviceInfo);
            }
            setDeviceInfoList();
        }
        catch(Exception ignored)
        {
        }
    }

    public void NewData(){
        try
        {
            setDeviceInfo(new com.example.testcreditcard.lib.model.DeviceInfo());
        }
        catch(Exception ignored)
        {
        }
    }

    public void DeleteData(){
        try
        {
            com.example.testcreditcard.lib.controller.DeviceInfo db = new com.example.testcreditcard.lib.controller.DeviceInfo();
            db.DeleteData(Adapter.deviceInfo);
            setDeviceInfo(new com.example.testcreditcard.lib.model.DeviceInfo());
            setDeviceInfoList();
        }
        catch(Exception ignored)
        {
        }
    }
}
