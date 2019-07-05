package com.example.shownfc.base;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import androidx.appcompat.app.AppCompatActivity;


public class baseNfc extends AppCompatActivity {
    protected NfcAdapter aNfcAdapter;
    private PendingIntent mPendingTntent;
    @Override
    protected void onStart(){
        super.onStart();
        aNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingTntent =PendingIntent.getActivity(this,0,new Intent(this,getClass()),0);
    }
    @Override
    public void onResume(){
        super.onResume();
        if(aNfcAdapter!=null)
            aNfcAdapter.enableForegroundDispatch(this,mPendingTntent,null,null);
    }
    @Override
    public void onPause(){
        super.onPause();
        if(aNfcAdapter!=null)
            aNfcAdapter.disableForegroundDispatch(this);
    }
}
