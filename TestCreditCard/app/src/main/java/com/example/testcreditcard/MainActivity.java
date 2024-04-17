package com.example.testcreditcard;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testcreditcard.component.TabComponent;
import com.example.testcreditcard.component.TabLayoutComponent;
import com.example.testcreditcard.fragment.AdjustTipFragment;
import com.example.testcreditcard.fragment.AuthCardFragment;
import com.example.testcreditcard.fragment.BatchFragment;
import com.example.testcreditcard.fragment.CaptureFragment;
import com.example.testcreditcard.fragment.DeleteDataFragment;
import com.example.testcreditcard.fragment.DeviceInfoFragment;
import com.example.testcreditcard.fragment.EnterTipFragment;
import com.example.testcreditcard.fragment.SaleFragment;
import com.example.testcreditcard.fragment.VoidAuthFragment;
import com.example.testcreditcard.fragment.VoidFragment;
import com.example.testcreditcard.lib.DBInfo;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBInfo.setContext(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TabLayoutComponent tabLayoutComponent = new TabLayoutComponent(this, getSupportFragmentManager());
        tabLayoutComponent.setBackgroundColor(Color.parseColor("#f5f5f5f5"));
        tabLayoutComponent.setFocusColor(Color.WHITE );
        tabLayoutComponent.setUnFocusColor(Color.parseColor("#f5f5f5"));
        setContentView(tabLayoutComponent,params);
        tabLayoutComponent.addTab(new TabComponent("Delete Data",new DeleteDataFragment()));
        tabLayoutComponent.addTab(new TabComponent("Device",new DeviceInfoFragment()));
        tabLayoutComponent.addTab(new TabComponent("Sale",new SaleFragment()));
        tabLayoutComponent.addTab(new TabComponent("Void",new VoidFragment()));
        tabLayoutComponent.addTab(new TabComponent("Auth",new AuthCardFragment()));
        tabLayoutComponent.addTab(new TabComponent("Capture",new CaptureFragment()));
        tabLayoutComponent.addTab(new TabComponent("EnterTip",new EnterTipFragment()));
        tabLayoutComponent.addTab(new TabComponent("AdjustTip",new AdjustTipFragment()));
        tabLayoutComponent.addTab(new TabComponent("Batch",new BatchFragment()));
        tabLayoutComponent.addTab(new TabComponent("Void Auth", new VoidAuthFragment()));
        Event.addOnMessageSendListener(message -> {
            Toast toast = Toast.makeText(MainActivity.this , message, Toast.LENGTH_SHORT);
            toast.show();
        });
    }
}