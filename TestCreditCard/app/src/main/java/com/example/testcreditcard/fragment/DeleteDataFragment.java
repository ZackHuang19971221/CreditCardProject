package com.example.testcreditcard.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testcreditcard.R;
import com.example.testcreditcard.databinding.FragmentBatchBinding;
import com.example.testcreditcard.databinding.FragmentDeleteDataBinding;
import com.example.testcreditcard.viewmodel.DeleteDataViewModel;

public class DeleteDataFragment extends Fragment {
    private DeleteDataViewModel viewModel = new DeleteDataViewModel();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentDeleteDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delete_data, container, false);
        binding.setViewModel(viewModel);
        View view = binding.getRoot();
        return view;
    }
}