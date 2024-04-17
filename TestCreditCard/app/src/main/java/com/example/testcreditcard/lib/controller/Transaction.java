package com.example.testcreditcard.lib.controller;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.testcreditcard.lib.DBInfo;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Transaction{
    private final static String TableName = "TRANSACTION_INFO";

    public Transaction() {
        String SQLTable = "CREATE TABLE IF NOT EXISTS " + TableName + "(" +
                "TRANSACTION_ID TEXT PRIMARY KEY, "+
                "DEVICE_ID INTEGER, "+
                "TRANSACTION_STATUS TEXT, "+
                "LAST_MODIFY_DATETIME INTEGER, "+
                "IS_SETTLED TEXT, "+
                "REF_NUMBER TEXT, " +
                "TIP REAL, "+
                "AMOUNT REAL, "+
                "TAX REAL, "+
                "SERVICE_FEE REAL "+
                ");";
        DBInfo.sqLite.getWritableDatabase().execSQL(SQLTable);
    }

    public void truncateTable() {
        String Sql =  "Delete from " + TableName;
        DBInfo.sqLite.getWritableDatabase().execSQL(Sql);
    }

    public com.example.testcreditcard.lib.model.Transaction[] LoadData(int deviceID,String status){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        String Sql = "SELECT * FROM " + TableName + " WHERE DEVICE_ID = " + deviceID +" AND TRANSACTION_STATUS = '" + status +"'";
        Cursor cursor = db.rawQuery(Sql,null);
        ArrayList<com.example.testcreditcard.lib.model.Transaction> list = new ArrayList<>();
        com.example.testcreditcard.lib.model.Transaction item;
        while (cursor.moveToNext())
        {
            item = new com.example.testcreditcard.lib.model.Transaction();
            item.TRANSACTION_ID = cursor.getString(0);
            item.DEVICE_ID = cursor.getInt(1);
            item.TRANSACTION_STATUS = cursor.getString(2);
            item.LAST_MODIFY_DATETIME = cursor.getLong(3);
            item.IS_SETTLED = cursor.getString(4);
            item.REF_NUMBER = cursor.getString(5);
            item.TIP = BigDecimal.valueOf(cursor.getLong(6));
            item.AMOUNT = BigDecimal.valueOf(cursor.getLong(7));
            item.TAX = BigDecimal.valueOf(cursor.getLong(8));
            item.SERVICE_FEE = BigDecimal.valueOf(cursor.getLong(9));
            list.add(item);
        }
        cursor.close();
        return list.toArray(new com.example.testcreditcard.lib.model.Transaction[0]);
    }

    public com.example.testcreditcard.lib.model.Transaction LoadNewestData(int deviceID){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        String Sql = "SELECT * FROM " + TableName +" WHERE LAST_MODIFY_DATETIME = (SELECT MAX(LAST_MODIFY_DATETIME) FROM " + TableName + " WHERE DEVICE_ID = " + deviceID + " );";
        Cursor cursor = db.rawQuery(Sql,null);
        com.example.testcreditcard.lib.model.Transaction item = new com.example.testcreditcard.lib.model.Transaction();
        while (cursor.moveToNext())
        {
            item = new com.example.testcreditcard.lib.model.Transaction();
            item.TRANSACTION_ID = cursor.getString(0);
            item.DEVICE_ID = cursor.getInt(1);
            item.TRANSACTION_STATUS = cursor.getString(2);
            item.LAST_MODIFY_DATETIME = cursor.getLong(3);
            item.IS_SETTLED = cursor.getString(4);
            item.REF_NUMBER = cursor.getString(5);
            item.TIP = BigDecimal.valueOf(cursor.getLong(6));
            item.AMOUNT = BigDecimal.valueOf(cursor.getLong(7));
            item.TAX = BigDecimal.valueOf(cursor.getLong(8));
            item.SERVICE_FEE = BigDecimal.valueOf(cursor.getLong(9));
        }
        cursor.close();
        return item;
    }

    public  void DeleteData(com.example.testcreditcard.lib.model.Transaction data){
        deleteData(data);
    }

    public void  AddData(com.example.testcreditcard.lib.model.Transaction data){
        addData(data);
    }

    public void  SaveData(com.example.testcreditcard.lib.model.Transaction data){
        modifyData(data);
    }

    private void addData(com.example.testcreditcard.lib.model.Transaction data){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TRANSACTION_ID",data.TRANSACTION_ID);
        values.put("DEVICE_ID",data.DEVICE_ID);
        values.put("TRANSACTION_STATUS",data.TRANSACTION_STATUS);
        values.put("LAST_MODIFY_DATETIME",data.LAST_MODIFY_DATETIME);
        values.put("IS_SETTLED",data.IS_SETTLED);
        values.put("REF_NUMBER",data.REF_NUMBER);
        values.put("TIP", String.valueOf(data.TIP));
        values.put("AMOUNT", String.valueOf(data.AMOUNT));
        values.put("TAX", String.valueOf(data.TAX));
        values.put("SERVICE_FEE", String.valueOf(data.SERVICE_FEE));
        db.insert(TableName,null,values);
    }

    private void modifyData(com.example.testcreditcard.lib.model.Transaction data){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TRANSACTION_ID",data.TRANSACTION_ID);
        values.put("DEVICE_ID",data.DEVICE_ID);
        values.put("TRANSACTION_STATUS",data.TRANSACTION_STATUS);
        values.put("LAST_MODIFY_DATETIME",data.LAST_MODIFY_DATETIME);
        values.put("IS_SETTLED",data.IS_SETTLED);
        values.put("REF_NUMBER",data.REF_NUMBER);
        values.put("TIP", String.valueOf(data.TIP));
        values.put("AMOUNT", String.valueOf(data.AMOUNT));
        values.put("TAX", String.valueOf(data.TAX));
        values.put("SERVICE_FEE", String.valueOf(data.SERVICE_FEE));
        db.update(TableName,values,"TRANSACTION_ID = " + data.TRANSACTION_ID,null);
    }

    private void deleteData(com.example.testcreditcard.lib.model.Transaction data){
        SQLiteDatabase db = DBInfo.sqLite.getWritableDatabase();
        db.delete(TableName,"TRANSACTION_ID = " + data.TRANSACTION_ID,null);
    }
}
