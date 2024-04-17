package com.example.testcreditcard.lib;

import android.content.Context;

import com.example.testcreditcard.lib.controller.SQLite;

public class DBInfo {
    public static void setContext(Context context){
        sqLite = new SQLite(context);
    }

    public static final String DbName="hotsauce.db";
    public static SQLite sqLite;
}
