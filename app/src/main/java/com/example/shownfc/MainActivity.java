package com.example.shownfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.shownfc.base.baseNfc;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private TextView a;
    private TextView b;
    NfcAdapter tNfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tNfcAdapter=NfcAdapter.getDefaultAdapter(this);
        a = findViewById(R.id.q_q);
        b = findViewById(R.id.a_a);
        if (!ifNccuse()) {
            return;
        }
    }
    @Override
    public void onNewIntent(Intent intent){
        StringBuilder sb = new StringBuilder();
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] id = detectedTag.getId();
        Log.d("MainActivity",id.toString());
        sb.append("TID: ").append(getTid(id));
        Log.d("MainActivity",sb.toString());
        b.setText(sb.toString());
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
    private static String getTid(byte[] bytes){
        StringBuilder sb=new StringBuilder();
        for (int i =bytes.length - 1;i>= 0;--i){
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}