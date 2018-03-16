package com.routon.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.example.testsr.R;
import com.routon.testsr.utils.BleUtils;
import com.routon.testsr.utils.Const;
import com.routon.testsrUI.BleActivity;

/**
 * Created by zyf
 */
public class BleAdapter extends BaseAdapter {

    private final static String TAG = "BleAdapter";

 //   public List<BluetoothDevice> mlist;
    public List<HashMap<String,Object>> list;
    private LayoutInflater mInflater;

    private static Context mContext;

    public static boolean isConnect = false;

    static BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;
    BluetoothGattService bluetoothGattServices;
    BluetoothGattCharacteristic characteristic_zd, characteristic_jb;
    List<String> serviceslist = new ArrayList<String>();

//    public BleAdapter(Context context, List<BluetoothDevice> list) {
//        mlist = list;
//        mInflater = LayoutInflater.from(context);
//        mContext = context;
//    }

    public BleAdapter(Context context, List<HashMap<String,Object>> list) {
//        mlist = list;
        this.list=list;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }
    // 获取传入的数组大小
    @Override
    public int getCount() {
        return list.size();
    }

    // 获取第N条数据
    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    // 获取item id
    @Override
    public long getItemId(int i) {
        return i;
    }

    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int m = msg.what;
            switch (m) {

                case BleUtils.DISCONNECT:
                    isConnect = false;
                    disconnect();
                    // Toast.makeText(mContext, "断开连接", Toast.LENGTH_SHORT).show();
                    Message msg2 = BleActivity.handler.obtainMessage();
                    msg2.what = BleUtils.MAIN_UPDATE_DISCONNECT;
                    BleActivity.handler.sendMessage(msg2);
                default:
                    break;
            }
        }
    };

    // 主要方法
    //添加rssi，根据rssi排序
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        if (view == null) {
            // 首先为view绑定布局
            view = mInflater.inflate(R.layout.devices_item, null);
            viewHolder.name = (TextView) view.findViewById(R.id.bluetoothname);
            viewHolder.uuid = (TextView) view.findViewById(R.id.uuid);
            viewHolder.rssi = (TextView) view.findViewById(R.id.rssi);
//            viewHolder.status = (TextView) view.findViewById(R.id.status);
            viewHolder.connect = (Button) view.findViewById(R.id.connect);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
   //     BluetoothDevice bd = mlist.get(i);

//        viewHolder.name.setText(bd.getName());
//        viewHolder.uuid.setText(bd.getAddress());
        HashMap<String,Object> map=list.get(i);
        viewHolder.name.setText(((BluetoothDevice)map.get("device")).getName());
        viewHolder.uuid.setText(((BluetoothDevice)map.get("device")).getAddress());
        Log.i("rssi","rssi = "+map.get("rssi").toString());
        viewHolder.rssi.setText(map.get("rssi").toString());
        viewHolder.connect.setOnClickListener(new BtnListener(i));

        // viewHolder.status.setText(R.string.noconnect);
        return view;
    }

    private class BtnListener implements OnClickListener {
        int mPosition;

        public BtnListener(int inPosition) {
            mPosition = inPosition;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            // 点击连接
            if (!isConnect) {
                handler.postDelayed(runnableStopConnect, Const.outTimes);
                bluetoothDevice = (BluetoothDevice) list.get(mPosition).get("device");
                // 连接设备的方法,返回值为bluetoothgatt类型
                new Thread() {
                    public void run() {
                        bluetoothGatt = bluetoothDevice.connectGatt(mContext,
                                false, gattcallback);
                        Log.i(TAG, "连接" + bluetoothDevice.getName() + "中...");
                        BleActivity.isConnecting = true;
                    }

                }.start();
            }
        }
    }

    Runnable runnableStopConnect = new Runnable() {
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            if (!isConnect) {
                Log.i(TAG, "dd--- [runnableStopConnect.run] stopDisConnect");
                Message msg = BleAdapter.handler
                        .obtainMessage(BleUtils.DISCONNECT);
                msg.sendToTarget();
            }
        }
    };

    class ViewHolder {
        private TextView name, uuid, rssi,status;
        private Button connect;
    }

    public BluetoothGattCallback gattcallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            final int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            // runOnUiThread();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String status;
                    switch (newState) {
                        // 已经连接
                        case BluetoothGatt.STATE_CONNECTED:
                            Log.i(TAG, "已连接");
                            Message msg1 = BleActivity.handler.obtainMessage();
                            Bundle bundle = new Bundle();
                            bundle.putString("name", bluetoothDevice.getName());
                            bundle.putString("address",
                                    bluetoothDevice.getAddress());
                            msg1.obj = bundle;
                            msg1.what = BleUtils.MAIN_UPDATE_CONNECT;

                            BleActivity.handler.sendMessage(msg1);

                            isConnect = true;
                            // 该方法用于获取设备的服务，寻找服务
                            bluetoothGatt.discoverServices();
                            break;
                        // 正在连接
                        case BluetoothGatt.STATE_CONNECTING:
                            // lianjiezhuangtai.setText("正在连接");
                            Log.i(TAG, "正在连接");
                            break;
                        // 连接断开
                        case BluetoothGatt.STATE_DISCONNECTED:

                            Message msg2 = BleActivity.handler.obtainMessage();
                            msg2.what = BleUtils.MAIN_UPDATE_DISCONNECT;
                            BleActivity.handler.sendMessage(msg2);
                            isConnect = false;
                            Log.i(TAG, "已断开");

                            break;
                        // 正在断开
                        case BluetoothGatt.STATE_DISCONNECTING:
                            Log.i(TAG, "断开中");
                            break;
                    }
                    // pd.dismiss();
                }
            };
            new Thread(runnable).start();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            // 寻找到服务时
            if (status == bluetoothGatt.GATT_SUCCESS) {
//				final List<BluetoothGattService> services = bluetoothGatt
//						.getServices();
                BluetoothGattService service = gatt.getService(UUID.fromString(BleUtils.service_UUID));
            }


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            if (status == bluetoothGatt.GATT_SUCCESS) {

            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    public static void disconnect() {
        bluetoothGatt.disconnect();
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
        // Log.i(TAG, "断开连接");
    }

    // a.获取服务

    public BluetoothGattService getService(UUID uuid) {
        if (bluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return null;
        }
        return bluetoothGatt.getService(uuid);
    }

    // b.获取特征
    private BluetoothGattCharacteristic getCharcteristic(String serviceUUID,
                                                         String characteristicUUID) {

        // 得到服务对象
        BluetoothGattService service = getService(UUID.fromString(serviceUUID)); // 调用上面获取服务的方法
        if (service == null) {
            Log.e(TAG, "Can not find 'BluetoothGattService'");
            return null;
        }

        // 得到此服务结点下Characteristic对象
        final BluetoothGattCharacteristic gattCharacteristic = service
                .getCharacteristic(UUID.fromString(characteristicUUID));
        if (gattCharacteristic != null) {
            return gattCharacteristic;
        } else {
            Log.e(TAG, "Can not find 'BluetoothGattCharacteristic'");
            return null;
        }
    }

    // 获取数据
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.readCharacteristic(characteristic);
    }

    public boolean setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (bluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        return bluetoothGatt.setCharacteristicNotification(characteristic,
                enabled);
    }

    public boolean write(byte[] data, String serviceUUID, String characterUUID) { // 一般都是传byte
        // 得到可写入的characteristic Utils.isAIRPLANE(mContext) &&
        // if (!mBleManager.isEnabled()) {
        // Log.e(TAG, "writeCharacteristic 开启飞行模式");
        // // closeBluetoothGatt();
        // isGattConnected = false;
        // // broadcastUpdate(Config.ACTION_GATT_DISCONNECTED);
        // return;
        // }
        BluetoothGattCharacteristic writeCharacteristic = getCharcteristic(
                serviceUUID, characterUUID); // 这个UUID都是根据协议号的UUID
        if (writeCharacteristic == null) {
            Log.e(TAG, "Write failed. GattCharacteristic is null.");
            return false;
        }
        writeCharacteristic.setValue(data); // 为characteristic赋值
        return writeCharacteristicWrite(writeCharacteristic);

    }

    public boolean writeCharacteristicWrite(
            BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        Log.e(TAG, "BluetoothAdapter 写入数据");
        boolean isBoolean = false;
        isBoolean = bluetoothGatt.writeCharacteristic(characteristic);
        Log.e(TAG, "BluetoothAdapter_writeCharacteristic = " + isBoolean); // 如果isBoolean返回的是true则写入成功
        return isBoolean;
    }
}