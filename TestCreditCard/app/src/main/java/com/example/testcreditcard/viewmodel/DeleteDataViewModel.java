package com.example.testcreditcard.viewmodel;

import androidx.databinding.BaseObservable;

import com.example.testcreditcard.Event;
import com.example.testcreditcard.lib.controller.Transaction;

public class DeleteDataViewModel extends BaseObservable {
    Transaction transaction = new Transaction();
    public void deleteData() {
        transaction.truncateTable();
        Event.Invoke("Success");
    }
}
