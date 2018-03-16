package com.routon.testsr.scan;

import com.routon.testsr.utils.BleUtils;
import com.routon.testsr.utils.Const;
import com.routon.testsr.utils.Utils;
import com.routon.testsrUI.BleActivity;
import com.routon.testsrUI.ScanActivity;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

@SuppressLint({"SimpleDateFormat", "NewApi"})
public class BtService extends Service {
    private static final String TAG = "BtEduService";
    private boolean mycheck = true; // 蓝牙支持检查
    private boolean firstflag = true;
    private BluetoothManager mBluetoothManager;
    private BluetoothLeScanner mLeScanner = null;

    private BleScan mBleScan = null;

    private BleAdv mBleAdv = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;

    //	public static boolean startCheck = true;
    public static Context context = null;
    public static Handler mRevHandler;
    public static BluetoothAdapter mBluetoothAdapter;

    private MacBleScan macBleScan = null;
    private DataBleScan dataBleScan = null;

    public BtService() {
        mRevHandler = new revHandler();
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            Log.d(TAG, "mBluetoothManager == null！");
            mycheck = false;
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "本机没有找到蓝牙硬件或驱动！");
            mycheck = false;
        }

//        mRevHandler = new revHandler();
        context = this;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mBluetoothAdapter.isEnabled()) {
            turnOnBluetooth();
            return START_STICKY;// 等待Bluettoth开启成功的广播
        } else if (firstflag) {
            if (checkPrerequisites()) {
                mycheck = true;
                Log.d(TAG, "wx: mycheck is true......startAdvertising......");
                firstflag = false;
//				BtEduReceiver.BluetoothState = true;// 防止Service在蓝牙开启的状态下死掉！！！
            }
        } else if (mycheck) {
            Log.d(TAG, "wx: mycheck is true and not first run。。。。。。。");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            turnOnBluetooth();
            return START_STICKY;// 等待Bluettoth开启成功的广播
        }

        return START_STICKY;
    }

    @SuppressLint("HandlerLeak")
    private class revHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utils.TURNONBLUETOOTH:
                    if (!mBluetoothAdapter.isEnabled()) {
                        turnOnBluetooth();
                    }
                    break;

                case Utils.TURNONBLUETOOTHSUCCESS:
                    break;

                case Utils.UploadData: {
                    if (BleActivity.exit) {
                        Bundle bundle = msg.getData();
                        ScanActivity.handler.obtainMessage(Utils.UI_UPDATE, bundle).sendToTarget();
                    } else {
                        Bundle bundle = msg.getData();
                        byte[] data = bundle.getByteArray("data");
                        int iden = data[3]&0xFF;

                        if (iden == Const.S1701) {
                            BleActivity.handler.obtainMessage(BleUtils.start_scan, bundle).sendToTarget();
                        }
                    }
                }

                break;
                case Utils.STARTBORADCAST:

                    if (mLeScanner == null)
                        mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

                    if (mBleScan == null)
                        mBleScan = new BleScan(mLeScanner);

                    mBleScan.startScan();

                    if (mLeScanner == null)
                        Log.i(TAG, "mLeScanner is NULL!");
                    else {
                        Log.i(TAG, "mLeScanner is not NULL!");
                    }
                    // 发送f1
                        mBluetoothLeAdvertiser = mBluetoothAdapter
                                .getBluetoothLeAdvertiser();
                        if (mBleAdv == null)
                            mBleAdv = new BleAdv(Const.BLE_ADV_FACT);
                        Log.i(TAG, "start advertising");
                        mBleAdv.setAdvCMD(Const.CTL_F1 & 0xFF);
                        mBleAdv.setBleAdvBSN(Const.BSN_S1701);
                        mBleAdv.setBleAdvIden(Const.CI14T);
                        mBleAdv.setBleData(null, 0);
                        mBleAdv.startAdvertising(mBluetoothLeAdvertiser, 0);
                    break;

                case Utils.STOPBORADCAST:
                    mBleScan.stopScan();
                    if (mBleAdv != null)
                        mBleAdv.stopAdvertising();
                    mBleAdv = null;
                    break;
                case Utils.MAC_SCAN:
                    if (mLeScanner == null) {
                        mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    }
                    if (macBleScan == null)
                        macBleScan = new MacBleScan(mLeScanner);
                    macBleScan.startScan();
                    break;
                case Utils.MAC_STOP_SCAN:
                    macBleScan.stopScan();
                    break;
                case Utils.DATA_SCAN:
                    if (mLeScanner == null) {
                        mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    }
                    if (dataBleScan == null)
                        dataBleScan = new DataBleScan(mLeScanner);
                    dataBleScan.startScan();
                    break;
                case Utils.DATA_STOP_SCAN:
                    dataBleScan.stopScan();
                    break;
//                case Utils.START_TEST_s1705:
//                    if (!MacBleScan.exit) {
//                        if (mLeScanner == null) {
//                            mLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
//                        }
//                        // 扫描
//                        if (macBleScan == null)
//                            macBleScan = new MacBleScan(mLeScanner);
//                        macBleScan.startScan();
//
//                        // 发送f1
//                        mBluetoothLeAdvertiser = mBluetoothAdapter
//                                .getBluetoothLeAdvertiser();
//                        if (mBleAdv == null)
//                            mBleAdv = new BleAdv(Const.BLE_ADV_FACT);
//                        Log.i("lch", "start advertising");
//                        mBleAdv.setAdvCMD(Const.CTL_F1 & 0xFF);
//                        mBleAdv.setBleAdvBSN(Const.BSN_S1701);
//                        mBleAdv.setBleAdvIden(Const.CI14T);
//                        mBleAdv.setBleData(null, 0);
//                        mBleAdv.startAdvertising(mBluetoothLeAdvertiser, 0);
//                    }
//                    break;
//                case Utils.STOP_TEST_s1705:
//                    MacBleScan.exit=true;
//                    if (macBleScan != null)
//                        macBleScan.stopScan();
//                    macBleScan = null;
//                    if (mBleAdv != null)
//                        mBleAdv.stopAdvertising();
//                    mBleAdv = null;
//                    break;
                default:
                    break;
            }
        }
    }


    private boolean turnOnBluetooth() {
        Log.d(TAG, "本机蓝牙没有开启，开启中。。。！");
        if (mBluetoothAdapter.enable() == false) {
            Log.d(TAG, "本机蓝牙开启失败！！！！！");
            return false;
        }
        return true;
    }

    private boolean checkPrerequisites() {

        if (android.os.Build.VERSION.SDK_INT < 18) {
            Log.d(TAG, "wx: because android.os.Build.VERSION.SDK_INT < 18\n"
                    + "Bluetooth LE not supported by this device's operating system\n"
                    + "You will not be able to transmit as a Beacon");
            return false;
        }
        if (mycheck) {
            try {
                // Check to see if the getBluetoothLeAdvertiser is available. If
                // not, this will throw an exception indicating we are not
                // running Android L
                mBluetoothAdapter.getBluetoothLeAdvertiser();
                Log.d(TAG, "wx: Bluetooth LE advertising is available\n");
            } catch (Exception e) {
                Log.d(TAG,
                        "wx: Bluetooth LE advertising unavailable\n"
                                + "Sorry, the operating system on this device does not support Bluetooth LE advertising.\n"
                                + " As of July 2014, only the Android L preview OS supports this feature in user-installed apps");
                return false;
            }
        }
        Log.d(TAG,
                "支持BLE吗？--"
                        + getApplicationContext().getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_BLUETOOTH_LE));
        if (!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(TAG, "wx: Bluetooth LE not supported by this device's operating system\n"
                    + "You will not be able to transmit as a Beacon");
            return false;
        }
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
