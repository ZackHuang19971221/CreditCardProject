package com.example.testcreditcard.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testcreditcard.R;
import com.example.testcreditcard.databinding.FragmentDeviceInfoBinding;
import com.example.testcreditcard.viewmodel.DeviceInfoViewModel;

public class DeviceInfoFragment extends Fragment {

    private DeviceInfoViewModel deviceInfoViewModel = new DeviceInfoViewModel();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentDeviceInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_info, container, false);
        binding.setViewModel(deviceInfoViewModel);
        View view = binding.getRoot();
        return view;
    }
}