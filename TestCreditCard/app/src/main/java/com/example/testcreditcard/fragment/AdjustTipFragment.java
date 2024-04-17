package com.example.testcreditcard.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testcreditcard.R;
import com.example.testcreditcard.databinding.FragmentAdjustTipBinding;
import com.example.testcreditcard.databinding.FragmentEnterTipBinding;
import com.example.testcreditcard.databinding.FragmentSaleBinding;
import com.example.testcreditcard.viewmodel.AdjustTipViewModel;

public class AdjustTipFragment extends Fragment {
    private AdjustTipViewModel adjustTipViewModel = new AdjustTipViewModel();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentAdjustTipBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_adjust_tip, container, false);
        binding.setViewModel(adjustTipViewModel);
        View view = binding.getRoot();
        return view;
    }
}