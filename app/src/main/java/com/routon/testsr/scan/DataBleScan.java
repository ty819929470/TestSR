package com.routon.testsr.scan;

import java.util.ArrayList;
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
import com.routon.testsrUI.BleActivity;

public class DataBleScan {
    private static final String TAG = "DataBleScan";
    private BluetoothLeScanner mLeScanner;
    private static int iden = Const.S1703;

    private String testing_mac = "";
    private List<ScanFilter> mScanFilter = new ArrayList<ScanFilter>();

    public static boolean F1hasSend = false;// F1是否已经发送
    public static boolean F2hasRCV = false;// F2是否已经接受

    public static boolean exit = false;

    public ArrayList<String> macList;

    public DataBleScan(BluetoothLeScanner scanner) {
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
            String name = device.getName();
            // Manufacturer Specific data 就是厂商指定私有广播内容，0xFF
            // 打头，数据头两个字节为厂商信息,后面内容为厂商自定义内容。
            // SparseArray<byte[]> manufacturers =
            // result.getScanRecord().getManufacturerSpecificData();

            macList = BleActivity.selectMacList;
            iden = BleActivity.iden;
            byte[] manu = result.getScanRecord().getManufacturerSpecificData(
                    getManuId((byte) Const.BSN_S1701, (byte) iden));
                if (manu != null && mac != null) {
                    if (macList.contains(mac)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("mac", mac);
                        bundle.putInt("rssi", rssi);
                        bundle.putString("name", name);
                        bundle.putByteArray("data", manu);
                        Message msg = BleActivity.handler.obtainMessage(BleUtils.s1703_DATA_RECEIVED);
                        msg.setData(bundle);
                        msg.sendToTarget();
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