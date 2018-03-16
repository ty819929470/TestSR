package com.routon.testsr.scan;

import android.annotation.SuppressLint;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.routon.testsr.utils.Const;

@SuppressLint("NewApi")
public class BleAdv {
	private static final String TAG = "BleAdv";
	private static SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");

	public static int manuId;
	private int mAdvType;
	private byte[] mData = null;
	private int mDataLen = 0;
	private int mAdvTimeOut = 0;
	private int mBsn = 0;// 基站编号
	private int mIden = 0;// 设备标示
	private int mCmd = 0;// 设备标示
	private boolean mStarted;

	private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;
	private AdvertiseCallback mAdvertiseCallback = null;

	public BleAdv() {
		// TODO Auto-generated constructor stub
	}

	public BleAdv(int advType) {
		// TODO Auto-generated constructor stub
		mAdvType = advType;
	}

	public BleAdv(int advType, byte[] data) {
		// TODO Auto-generated constructor stub
		mAdvType = advType;
		mData = data;
	}

	public void setBleAdvType(int advType) {
		mAdvType = advType;
	}

	public void setBleData(byte[] data, int dataLen) {
		mData = data;
		mDataLen = dataLen;
	}

	public void setBleAdvTimeout(int timeout) {
		mAdvTimeOut = timeout;
	}

	public void setBleAdvBSN(int bsn) {
		mBsn = bsn;
	}

	public void setBleAdvIden(int iden) {
		mIden = iden;
	}

	public void setAdvCMD(int cmd) {
		mCmd = cmd;
	}

	public int getManuId(byte Bsn, byte iden) {
		int manuid = 0;
		manuid += (iden & 0xFF) << 8;
		manuid += Bsn & 0xFF;
		return manuid;
	}

	public void startAdvertising(BluetoothLeAdvertiser leadv) {
		startAdvertising(leadv, mAdvTimeOut);
	}

	public void startAdvertising(BluetoothLeAdvertiser leadv, int timeout) {
		mAdvTimeOut = timeout;
		startAdvertising(leadv, timeout, mData);
	}

	private void startAdvertising(BluetoothLeAdvertiser bluetoothLeAdvertiser, int timeout, byte[] data) {
		stopAdvertising();
		mBluetoothLeAdvertiser = bluetoothLeAdvertiser;
		if (mBluetoothLeAdvertiser == null) {
			Log.d(TAG, "cannot get BluetoothLeAdvertiser .  mbluetoothLeAdvertiser is " + mBluetoothLeAdvertiser);
			return;
		} else
			Log.d(TAG, "getted BluetoothLeAdvertiser .  mbluetoothLeAdvertiser is " + mBluetoothLeAdvertiser);

//		Log.d(TAG, "timeout is " + timeout);

		try {
			manuId = getManuId((byte) mBsn, (byte) mIden);
			Log.d(TAG, "mBsn is " + mBsn + ";mIden is " + mIden + ";manuid is " + manuId);

			byte[] advData = new byte[Const.ADV_DATA_PRE_LEN + mDataLen];
			advData[0] = (byte) mCmd;
			
			if (mDataLen > 0) {
				for (int i = 0; i < mDataLen; i++) {
					advData[Const.ADV_DATA_PRE_LEN + i] = data[i];
				}
			}

			AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
			// dataBuilder.addManufacturerData(0x4321,
			// manufacturerSpecificData);// 设置广播数据
			dataBuilder.addManufacturerData(manuId, advData);// 设置广播数据

			AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
			settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
			settingsBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
			settingsBuilder.setConnectable(false);
			// settingsBuilder.setTimeout(timeout * 1000);

			mBluetoothLeAdvertiser.startAdvertising(settingsBuilder.build(), dataBuilder.build(),
					getAdvertiseCallback());
			Log.d(TAG, "Started advertisement with callback: " + getAdvertiseCallback());
		} catch (Exception e) {
			Log.d(TAG, "Cannot start advertising due to exception");
		}
	}

	public void stopAdvertising() {
		if (!mStarted) {
			Log.d(TAG, "Skipping stop advertising -- not started");
			return;
		}
		Log.i(TAG, "Stopping advertising with object " + mBluetoothLeAdvertiser);
		Log.i(TAG, "start stop advertising at " + df.format(new Date()));
		mBluetoothLeAdvertiser.stopAdvertising(getAdvertiseCallback());
		mStarted = false;
	}

	private AdvertiseCallback getAdvertiseCallback() {
		if (mAdvertiseCallback == null) {
			mAdvertiseCallback = new AdvertiseCallback() {

				@Override
				public void onStartFailure(int errorCode) {
					Log.d(TAG, "Advertisement start failed, code:" + errorCode);
				}

				@Override
				public void onStartSuccess(AdvertiseSettings settingsInEffect) {
					mStarted = true;
					Log.d(TAG, "Advertisement start succeeded.");
					Log.d(TAG, "success advertising at " + df.format(new Date()));

					// Intent intent = new Intent("1111");
					// BtEduService.context.sendBroadcast(intent);
				}
			};
		}
		return mAdvertiseCallback;
	}
}
