package com.example.testcreditcard.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testcreditcard.R;
import com.example.testcreditcard.databinding.FragmentCaptureBinding;
import com.example.testcreditcard.databinding.FragmentSaleBinding;
import com.example.testcreditcard.viewmodel.CaptureViewModel;

public class CaptureFragment extends Fragment {
    private CaptureViewModel captureViewModel = new CaptureViewModel();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentCaptureBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_capture, container, false);
        binding.setViewModel(captureViewModel);
        View view = binding.getRoot();
        return view;
    }
}