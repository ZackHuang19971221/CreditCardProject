package com.example.testcreditcard.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testcreditcard.R;
import com.example.testcreditcard.databinding.FragmentBatchBinding;
import com.example.testcreditcard.databinding.FragmentSaleBinding;
import com.example.testcreditcard.viewmodel.BatchViewModel;

public class BatchFragment extends Fragment {
    private BatchViewModel batchViewModel = new BatchViewModel();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentBatchBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_batch, container, false);
        binding.setViewModel(batchViewModel);
        View view = binding.getRoot();
        return view;
    }
}