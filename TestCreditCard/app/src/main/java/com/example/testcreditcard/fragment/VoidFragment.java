package com.example.testcreditcard.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testcreditcard.R;
import com.example.testcreditcard.databinding.FragmentSaleBinding;
import com.example.testcreditcard.databinding.FragmentVoidBinding;
import com.example.testcreditcard.viewmodel.VoidViewModel;

public class VoidFragment extends Fragment {

    private VoidViewModel voidViewModel = new VoidViewModel();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentVoidBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_void, container, false);
        binding.setViewModel(voidViewModel);
        View view = binding.getRoot();
        return view;
    }
}