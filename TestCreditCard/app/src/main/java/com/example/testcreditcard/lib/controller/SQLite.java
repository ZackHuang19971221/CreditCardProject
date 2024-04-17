package com.example.testcreditcard.lib.controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.testcreditcard.lib.DBInfo;

public class SQLite extends SQLiteOpenHelper {

    public SQLite(Context context){
        super(context, DBInfo.DbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
