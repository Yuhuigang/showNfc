package com.example.shownfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private TextView a;
    NfcAdapter tNfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tNfcAdapter=NfcAdapter.getDefaultAdapter(this);
        a = findViewById(R.id.q_q);
        if (!ifNccuse()) {
            return;
        }
        Button button =(Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ReadUidActivity.class);
                startActivity(intent);
            }
        });
    }
    protected boolean ifNccuse() {
        if (tNfcAdapter == null) {
            a.setText("该手机不支持NFC功能");
            return false;
        }
        if (tNfcAdapter != null && !tNfcAdapter.isEnabled()) {
            a.setText("请在系统设置中先启用NFC功能！");
            return false;
        }
        else
            a.setText("本设备NFC正常");
        return true;
    }
}