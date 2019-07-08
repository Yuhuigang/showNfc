package com.example.shownfc;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.shownfc.base.baseNfc;
import com.example.shownfc.tools.UriPrefix;

import java.nio.charset.Charset;
import java.util.Locale;

public class WriteActivity extends baseNfc {
    private EditText mText;
    private  String en;
    private  String en1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        mText=(EditText)findViewById(R.id.E1);
        Button bt=(Button)findViewById(R.id.E_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                en=mText.getText().toString();
                en1=mText.getText().toString();
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (en == null)
            return;
        //获取Tag对象
        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        NdefMessage ndefMessage = new NdefMessage(
                new NdefRecord[]{createTextRecord(en)});
        boolean result = writeTag(ndefMessage, detectedTag);
        if (result) {
            Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
        }
        NdefMessage ndefMessage1 = new NdefMessage(new NdefRecord[]{createUriRecord(en1)});
        boolean result1 = writeTag1(ndefMessage1, detectedTag);
        if (result1) {
            Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "写入失败", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 这部分是写Text格式的:
     */
    public static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = Charset.forName("UTF-8");
        //将文本转换为UTF-8格式
        byte[] textBytes = text.getBytes(utfEncoding);
        //设置状态字节编码最高位数为0
        int utfBit = 0;
        //定义状态字节
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        //设置第一个状态字节，先将状态码转换成字节
        data[0] = (byte) status;
        //设置语言编码，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1到langBytes.length的位置
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        //设置文本字节，使用数组拷贝方法，从0开始拷贝到data中，拷贝到data的1 + langBytes.length
        //到textBytes.length的位置
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        //通过字节传入NdefRecord对象
        //NdefRecord.RTD_TEXT：传入类型 读写
        NdefRecord ndefRecord = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], data);
        return ndefRecord;
    }
    public static boolean writeTag(NdefMessage ndefMessage, Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            ndef.connect();
            ndef.writeNdefMessage(ndefMessage);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
    /**
     * 这部分是写URI的
     */
    public static NdefRecord createUriRecord(String uriStr) {
        byte prefix = 0;
        for (Byte b : UriPrefix.URI_PREFIX_MAP.keySet()) {
            String prefixStr = UriPrefix.URI_PREFIX_MAP.get(b).toLowerCase();
            if ("".equals(prefixStr))
                continue;
            if (uriStr.toLowerCase().startsWith(prefixStr)) {
                prefix = b;
                uriStr = uriStr.substring(prefixStr.length());
                break;
            }
        }
        byte[] data = new byte[1 + uriStr.length()];
        data[0] = prefix;
        System.arraycopy(uriStr.getBytes(), 0, data, 1, uriStr.length());
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], data);
        return record;
    }
    public static boolean writeTag1(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    return false;
                }
                ndef.writeNdefMessage(message);
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}