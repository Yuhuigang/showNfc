package com.example.shownfc;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import com.example.shownfc.base.baseNfc;
import com.example.shownfc.tools.UriPrefix;

import java.nio.charset.Charset;
import java.util.Arrays;

public class ReadActivity extends baseNfc {
    private TextView tv1;
    private TextView tv2;
    private String tv11;
    private String tv22;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
    }
    @Override
    public void onNewIntent(Intent intent){
        Tag dtag=intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef=Ndef.get(dtag);
        tv11 =ndef.getType() + "\nmaxsize:" + ndef.getMaxSize() + "bytes\n\n";
        tv22 =ndef.getType() + "\n max size:" + ndef.getMaxSize() + " bytes\n\n";
        readNfcTag(intent);
        readNfcTag1(intent);
        tv1.setText(tv11);
        tv2.setText(tv22);
    }
    private void readNfcTag(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage msgs[] = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    contentSize += msgs[i].toByteArray().length;
                }
            }
            try {
                if (msgs != null) {
                    NdefRecord record = msgs[0].getRecords()[0];
                    String textRecord = parseTextRecord(record);
                    tv11 += textRecord + "\n\ntext\n" + contentSize + " bytes";
                }
            } catch (Exception e) {
            }
        }
    }
    public static String parseTextRecord(NdefRecord ndefRecord) {
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
            return null;
        }
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0x3f;
            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            String textRecord = new String(payload, languageCodeLength + 1,
                    payload.length - languageCodeLength - 1, textEncoding);
            return textRecord;
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }
    private void readNfcTag1(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                    NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage ndefMessage = null;
            int contentSize = 0;
            if (rawMsgs != null) {
                if (rawMsgs.length > 0) {
                    ndefMessage = (NdefMessage) rawMsgs[0];
                    contentSize = ndefMessage.toByteArray().length;
                } else {
                    return;
                }
            }
            try {
                NdefRecord ndefRecord = ndefMessage.getRecords()[0];
                Log.i("JAVA", ndefRecord.toString());
                Uri uri = parse(ndefRecord);
                Log.i("JAVA", "uri:" + uri.toString());
                tv22 += uri.toString() + "\n\nUri\n" + contentSize + " bytes";
            } catch (Exception e) {
            }
        }
    }
    public static Uri parse(NdefRecord record) {
        short tnf = record.getTnf();
        if (tnf == NdefRecord.TNF_WELL_KNOWN) {
            return parseWellKnown(record);
        } else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
            return parseAbsolute(record);
        }
        throw new IllegalArgumentException("Unknown TNF " + tnf);
    }
    private static Uri parseAbsolute(NdefRecord ndefRecord) {
        //获取所有的字节数据
        byte[] payload = ndefRecord.getPayload();
        Uri uri = Uri.parse(new String(payload, Charset.forName("UTF-8")));
        return uri;
    }
    private static Uri parseWellKnown(NdefRecord ndefRecord) {
        //判断数据是否是Uri类型的
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_URI))
            return null;
        //获取所有的字节数据
        byte[] payload = ndefRecord.getPayload();
        String prefix = UriPrefix.URI_PREFIX_MAP.get(payload[0]);
        byte[] prefixBytes = prefix.getBytes(Charset.forName("UTF-8"));
        byte[] fullUri = new byte[prefixBytes.length + payload.length - 1];
        System.arraycopy(prefixBytes, 0, fullUri, 0, prefixBytes.length);
        System.arraycopy(payload, 1, fullUri, prefixBytes.length, payload.length - 1);
        Uri uri = Uri.parse(new String(fullUri, Charset.forName("UTF-8")));
        return uri;
    }
}
