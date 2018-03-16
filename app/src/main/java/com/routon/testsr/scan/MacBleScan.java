package com.routon.testsr.scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.routon.testsr.utils.BleUtils;
import com.routon.testsr.utils.Const;
import com.routon.testsr.utils.Utils;
import com.routon.testsrUI.BleActivity;

public class MacBleScan {
    private static final String TAG = "MacBleScan";
    private BluetoothLeScanner mLeScanner;
    //    private String testing_mac = "";
    private List<ScanFilter> mScanFilter = new ArrayList<ScanFilter>();

//    public static boolean F1hasSend = false;// F1是否已经发送
//    public static boolean F2hasRCV = false;// F2是否已经接受

    public static boolean exit = false;

    public MacBleScan(BluetoothLeScanner scanner) {
        // TODO Auto-generated constructor stub
        this.mLeScanner = scanner;
    }

    public void startScan() {
        Log.d(TAG, "start scan");
        new Thread() {
            @Override
            public void run() {
                ScanSettings scanSettings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                mLeScanner.startScan(mScanFilter, scanSettings, mScanCallback);
            }
        }.start();
    }

    public void stopScan() {
        mLeScanner.stopScan(mScanCallback);
        Log.d(TAG, "stop scan...");
    }

    public int getManuId(byte Bsn, byte iden) {
        int manuid = 0;
        manuid += (iden & 0xFF) << 8;
        manuid += Bsn & 0xFF;
        return manuid;
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String mac = device.getAddress();
            int rssi = result.getRssi();
            String name=device.getName();

            byte[] manufacturers = result.getScanRecord().getBytes();

            if (manufacturers != null) {
                //ADType == 0xff 说明是我们需要的的广播

                if ((manufacturers[1] & 0xFF) == Const.BLE_ADV_FACT) {
                    if (mac != null) {// 不发送重复包
                        Bundle bundle = new Bundle();
                        bundle.putString("mac", mac);
                        bundle.putInt("rssi", rssi);
                        bundle.putString("name", name);
                        bundle.putByteArray("data", manufacturers);
                        Message msg = BleActivity.handler.obtainMessage(BleUtils.s1703_MAC_RECEIVED);
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }

                }

            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "scan failed !!!  errorCode = " + errorCode);
            super.onScanFailed(errorCode);
        }
    };
}