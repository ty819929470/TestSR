package com.routon.testsr.scan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.routon.testsr.utils.BleUtils;
import com.routon.testsr.utils.Const;
import com.routon.testsr.utils.Utils;
import com.routon.testsrUI.BleActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

public class BleScan {

    private static final String TAG = "BleScan";
    private BluetoothLeScanner mLeScanner;
    private List<ScanFilter> mScanFilter = new ArrayList<ScanFilter>();
    private boolean mStarted = true;


    public BleScan(BluetoothLeScanner scanner) {
        // TODO Auto-generated constructor stub
        this.mLeScanner = scanner;
    }

    public BleScan(BluetoothLeScanner scanner, List<ScanFilter> filters) {
        // TODO Auto-generated constructor stub
        this.mLeScanner = scanner;
        this.mScanFilter = filters;
    }


    public void setWhiteName(String whiteName) {

        mScanFilter.removeAll(mScanFilter);
        ScanFilter filter = new ScanFilter.Builder()
                .setDeviceAddress(whiteName).build();
        mScanFilter.add(filter);
        Log.i(TAG, "whiteName :  " + whiteName);
    }

    public void startScan() {
        Log.d(TAG, "start scan with utils.cmd ");
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
        mScanFilter.clear();
    }

    public void shutScan() {
        Log.d(TAG, "shutup scan...");
        mStarted = false;
    }

    public void continueScan() {
        mStarted = true;
        Log.d(TAG, "continue scan...");

    }

    public int getManuId(byte Bsn, byte iden) {
        int manuid = 0;
        manuid += (iden & 0xFF) << 8;
        manuid += Bsn & 0xFF;
        return manuid;
    }

    @SuppressLint("NewApi")
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String mac = device.getAddress();
            String name = device.getName();
            int rssi = result.getRssi();
            // byte[] manufacturers =
            // result.getScanRecord().getManufacturerSpecificData(BleAdv.manuId);
//			byte[] manufacturers = result.getScanRecord()
//			.getManufacturerSpecificData(
//					getManuId((byte) Const.BSN_S1701,
//							(byte) Const.S1701));
            // 数据格式：Len  ADType  BSn  Iden  Cmd  Data
            byte[] manufacturers = result.getScanRecord().getBytes();
            int length = 0;
            if (manufacturers != null) {
                length = manufacturers[0];
                Log.i(TAG, "manufacturers = " + Arrays.toString(manufacturers));
                //ADType == 0xff 说明是我们需要的的广播
 //               if ((manufacturers[1] & 0xFF) == Const.BLE_ADV_FACT) {
                    if (mac != null) {// 不发送重复包
                        Message msg = BtService.mRevHandler.obtainMessage(Utils.UploadData);
                        Bundle bundle = new Bundle();
                        bundle.putString("mac", mac);
                        bundle.putInt("rssi", rssi);
                        bundle.putByteArray("data", manufacturers);
                        bundle.putString("name", name);
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }
                }

            }

 //       }


        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "scan failed !!!  errorCode = " + errorCode);
            super.onScanFailed(errorCode);
        }
    };

}
