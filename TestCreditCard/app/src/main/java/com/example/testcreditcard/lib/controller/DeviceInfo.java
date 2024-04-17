package com.example.testcreditcard.lib.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.testcreditcard.lib.DBInfo;

import java.util.ArrayList;

public class DeviceInfo{
    private final static String TableName = "DEVICE_INFO";

    public DeviceInfo() {
        String SQLTable = "CREATE TABLE IF NOT EXISTS " + TableName + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "DEVICE_TYPE TEXT, "+
                "NAME TEXT, "+
                "IP TEXT, "+
                "PORT INTEGER, "+
                "TIMEOUT INTEGER,"+
                "RETRY_TIME INTEGER"+
                ");";
        DBInfo.sqLite.getWritableDatabase().execSQL(SQLTable);
    }

    public com.example.testcreditcard.lib.model.DeviceInfo[] LoadData(){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        String Sql = "SELECT * FROM " + TableName;
        Cursor cursor = db.rawQuery(Sql,null);
        ArrayList<com.example.testcreditcard.lib.model.DeviceInfo> list = new ArrayList<>();
        com.example.testcreditcard.lib.model.DeviceInfo item;
        while (cursor.moveToNext())
        {
            item = new com.example.testcreditcard.lib.model.DeviceInfo();
            item._id = cursor.getInt(0);
            item.DEVICE_TYPE = cursor.getString(1);
            item.NAME = cursor.getString(2);
            item.IP = cursor.getString(3);
            item.PORT = cursor.getInt(4);
            item.TIMEOUT=cursor.getInt(5);
            item.RETRY_TIME = cursor.getInt(6);
            list.add(item);
        }
        cursor.close();
        return list.toArray(new com.example.testcreditcard.lib.model.DeviceInfo[0]);
    }

    public com.example.testcreditcard.lib.model.DeviceInfo LoadNewestData(){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        String Sql = "SELECT * FROM " + TableName +" WHERE _id = (SELECT MAX(_id) FROM " + TableName + ");";
        Cursor cursor = db.rawQuery(Sql,null);
        com.example.testcreditcard.lib.model.DeviceInfo item = new com.example.testcreditcard.lib.model.DeviceInfo();
        while (cursor.moveToNext())
        {
            item._id = cursor.getInt(0);
            item.DEVICE_TYPE = cursor.getString(1);
            item.NAME = cursor.getString(2);
            item.IP = cursor.getString(3);
            item.PORT = cursor.getInt(4);
            item.TIMEOUT=cursor.getInt(5);
            item.RETRY_TIME = cursor.getInt(6);
        }
        cursor.close();
        return item;
    }

    public  void DeleteData(com.example.testcreditcard.lib.model.DeviceInfo data){
        deleteData(data);
    }

    public void  AddData(com.example.testcreditcard.lib.model.DeviceInfo data){
        addData(data);
    }

    public void  SaveData(com.example.testcreditcard.lib.model.DeviceInfo data){
        modifyData(data);
    }

    private void addData(com.example.testcreditcard.lib.model.DeviceInfo data){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("DEVICE_TYPE",data.DEVICE_TYPE);
        values.put("NAME",data.NAME);
        values.put("IP",data.IP);
        values.put("PORT",data.PORT);
        values.put("TIMEOUT",data.TIMEOUT);
        values.put("RETRY_TIME",data.RETRY_TIME);
        db.insert(TableName,null,values);
    }

    private void modifyData(com.example.testcreditcard.lib.model.DeviceInfo data){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("DEVICE_TYPE",data.DEVICE_TYPE);
        values.put("NAME",data.NAME);
        values.put("IP",data.IP);
        values.put("PORT",data.PORT);
        values.put("TIMEOUT",data.TIMEOUT);
        values.put("RETRY_TIME",data.RETRY_TIME);
        db.update(TableName,values,"_id = " + data._id,null);
    }

    private void deleteData(com.example.testcreditcard.lib.model.DeviceInfo data){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        db.delete(TableName,"_id = " + data._id,null);
    }
}
