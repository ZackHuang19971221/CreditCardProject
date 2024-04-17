package com.example.testcreditcard.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.testcreditcard.R;
import com.example.testcreditcard.databinding.FragmentVoidAuthBinding;
import com.example.testcreditcard.viewmodel.VoidAuthViewModel;

public class VoidAuthFragment extends Fragment {
    private VoidAuthViewModel viewModel = new VoidAuthViewModel();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentVoidAuthBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_void_auth, container, false);
        binding.setViewModel(viewModel);
        View view = binding.getRoot();
        return view;
    }
}
