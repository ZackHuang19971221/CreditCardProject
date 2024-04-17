package com.example.testcreditcard.lib.model;

import android.graphics.Paint;

import java.math.BigDecimal;

public class Transaction {

    public class TransactionStatus{
        public static final String AUTH = "AUTH";
        public static final String SALE = "SALE";
        public static final String VOID = "VOID";
        public static final String VOID_AUTH = "VOID_AUTH";
    }

    public String TRANSACTION_ID;

    public int DEVICE_ID;

    public BigDecimal AMOUNT;

    public BigDecimal TAX;

    public BigDecimal SERVICE_FEE;
    public BigDecimal TIP;

    public String TRANSACTION_STATUS;

    public long LAST_MODIFY_DATETIME;

    public String IS_SETTLED;

    public String REF_NUMBER;
}
