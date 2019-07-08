package com.example.shownfc;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shownfc.base.baseNfc;

import java.net.Inet4Address;

public class ReadUidActivity extends baseNfc {
    private TextView b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_uid);
        b=(TextView)findViewById(R.id.textView_1);
        Button button1=(Button)findViewById(R.id.button2);
        Button button2=(Button)findViewById(R.id.button3);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReadUidActivity.this,ReadActivity.class);
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ReadUidActivity.this,WriteActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onNewIntent(Intent intent) {
        StringBuilder sb = new StringBuilder();
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] id = detectedTag.getId();
        sb.append("TID: ").append(getTid(id));
        b.setText(sb);
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
