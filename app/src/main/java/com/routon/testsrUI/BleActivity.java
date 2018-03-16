package com.routon.testsrUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testsr.R;
import com.routon.bluetooth.BleAdapter;
import com.routon.testsr.scan.BtService;
import com.routon.testsr.utils.BleUtils;
import com.routon.testsr.utils.Const;
import com.routon.testsr.utils.PropertiesUtil;
import com.routon.testsr.utils.Utils;



public class BleActivity extends Activity {

    private final static String TAG = "BleActivity";
    //    String service_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    //    String character_UUID="0000fff1-0000-1000-8000-00805f9b34fb";
    private static String service_UUID = PropertiesUtil.load("service_UUID");
    private static String character_UUID = PropertiesUtil.load("character_UUID");
    public static ArrayList<String> selectMacList = new ArrayList<>();
    private HashMap<Integer, Boolean> selectMacMap = new HashMap<>();

    static ListView listView;
    static SimpleAdapter listItemAdapter; // ListView的适配器
    static ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表

    private static String selectedMac = "";
    private static ArrayList<String> macString;
    static int count = 0;
//    private boolean firstS1701 = true;
//    private boolean firstS1703 = true;
//    private boolean firstS1705 = true;

    private Button scan;
    private Button stopscan;
    private static ListView list;
    private static ArrayList<HashMap<String, Object>> deviceList = new ArrayList<>();
    static List<String> deviceMac = new ArrayList<String>();
    private static LinearLayout layout;
    private static LinearLayout layout1;
    private static TextView name;
    private static TextView address;
    private static TextView chara;
    private static TextView s1703Data;
    private static TextView s1703dataAnalyse;

    private static TextView macTextView;
    private static EditText edit_command;
    private static Button edit_button;

    private static boolean macScanning = false;
    private static boolean dataScanning = false;
    public static BleAdapter bleAdapter;
    static BluetoothAdapter bluetoothAdapter;
//    static List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();

    private static Button disconnect;
    private static Context mContext;

    //    private static Button searchVersion;
    public static boolean exit = true;

    private Button keyTurnOn;
    private Button keyTurnOff;
    private Button redTwinkle;
    private Button redShort;
    private Button redLong;
    private Button greenTwinkle;
    private Button greenShort;
    private Button greenLong;
    private Button blueTwinkle;
    private Button blueShort;
    private Button blueLong;
    private Button lightOff;
    private Button whiteLong;

    private Button s1703_mac;
    private Button s1701_mac;

    private Button s1705_mac;
    public static int iden = Const.S1703;

    private Button selectSure;
    /**
     * true: 显示全部可连接设备 false:只显示1708设备
     */
    private static boolean test = true;
    // BluetoothGattService bluetoothGattServices;
    // BluetoothGattCharacteristic characteristic_zd, characteristic_jb;

    // List<String> serviceslist = new ArrayList<String>();
    private static boolean mScanning;

    public static boolean isConnecting = false;

    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int m = msg.what;
            switch (m) {
                case BleUtils.MAIN_UPDATE_CONNECT:
                    disconnect.setEnabled(true);
                    disconnect.setText("断开连接");
                    chara.setText("");
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(callback);
                    deviceList.clear();
                    deviceMac.clear();
                    isConnecting = false;
                    list.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout1.setVisibility(View.GONE);
                    Bundle bundle = (Bundle) msg.obj;
                    name.setText(bundle.getString("name"));
                    address.setText(bundle.getString("address"));
                    break;
                case BleUtils.MAIN_UPDATE_DISCONNECT:
                    isConnecting = false;
                    // Toast.makeText(mContext, "已经断开", Toast.LENGTH_SHORT).show();
                    disconnect.setText("已断开，请重新扫描！");
                    disconnect.setEnabled(false);
                case BleUtils.MAIN_UPDATE_CONNECTING:
                    // isConnecting = true;
                    Log.i(TAG, "!!!!_________________正在连接");
                    // Toast.makeText(mContext, "正在连接，请稍等！",
                    // Toast.LENGTH_SHORT).show();
                    break;
                // 扫描到的数据
                case BleUtils.start_scan:
                    Bundle bun = (Bundle) msg.obj;
                    String mac = bun.getString("mac");
                    int rssi = bun.getInt("rssi");
                    String name = bun.getString("name");
                    byte[] data = bun.getByteArray("data");
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac);
//                    if (!deviceList.contains(device)) {
//                        // 将设备加入列表数据中
//                        deviceList.add(device);
//                        bleAdapter = new BleAdapter(mContext, deviceList);
//                        list.setAdapter(bleAdapter);
//                    }
                    if (!deviceMac.contains(mac)) {
                        // 将设备加入列表数据中
                        deviceMac.add(mac);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("device", device);
                        map.put("rssi", rssi);
                        deviceList.add(map);
                        bleAdapter = new BleAdapter(mContext, deviceList);
                        list.setAdapter(bleAdapter);
                    } else {
                        for (int i = 0; i < deviceList.size(); i++) {
                            HashMap<String, Object> map = deviceList.get(i);
                            if ((map.get("device")).equals(device)) {
                                map.put("rssi", rssi);
                            }

                        }
                    }
                    Collections.sort(deviceList, new Comparator<HashMap<String, Object>>() {
                        @Override
                        public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                            int lhsrssi = Integer.parseInt(lhs.get("rssi").toString());
                            int rhsrssi = Integer.parseInt(rhs.get("rssi").toString());
                            return (lhsrssi - rhsrssi) == 0 ? 0 :
                                    (lhsrssi - rhsrssi > 0 ? -1 : 1);
                        }
                    });
                    break;
                case BleUtils.s1703_DATA_RECEIVED:
                    Bundle dataBundle = msg.getData();
                    String s1703_mac = dataBundle.getString("mac");
                    int s1703_rssi = dataBundle.getInt("rssi");
                    String s1703name = dataBundle.getString("name");
                    byte[] dataCmd = dataBundle.getByteArray("data");

                    String s1703_str = "";
                    s1703_str = bytesToHex(dataCmd);
                    s1703Data.setText(s1703_str);
                    if (iden == Const.S1703 && s1703_str != null) {
                        if (s1703_str.substring(0, 2).equals("13")) {
                            s1703dataAnalyse.setText("查询版本号成功 版本号: " + Integer.parseInt(s1703_str.substring(2), 16));
                        } else if (s1703_str.substring(2, 4).equals("11")) {
                            s1703dataAnalyse.setText("点灯成功");
                        } else if (s1703_str.substring(2, 4).equals("16")) {
                            s1703dataAnalyse.setText("电容按键设置成功");
                        } else {
                            s1703dataAnalyse.setText("操作失败");
                        }
                    }
                    macTextView.setText(selectedMac);

                    break;
                case BleUtils.s1703_MAC_RECEIVED:
                    Bundle dataMac = msg.getData();
                    String macAll = dataMac.getString("mac");
                    int rssiAll = dataMac.getInt("rssi");
                    String nameAll = dataMac.getString("name");
                    byte[] dataCmdAll = dataMac.getByteArray("data");
                    int s1703Iden = dataCmdAll[3] & 0xFF;
                    Log.i("lch", "s1703Iden = " + s1703Iden);
                    String s1703show = "";
                    Log.i("lch", "iden = " + iden);
                    if(iden==Const.S1705){
                        if (s1703Iden == iden || s1703Iden==Const.S1706) {
                          s1703show = bytesToHex(dataCmdAll);
                          if (nameAll == null || nameAll.equals("")) {
                            nameAll = getDeviceName(s1703Iden);
                          }
                          addItem(nameAll, macAll, rssiAll + "", s1703show);
                        }
                    }else{
                        if (s1703Iden == iden ) {
                            s1703show = bytesToHex(dataCmdAll);
                            if (nameAll == null || nameAll.equals("")) {
                                nameAll = getDeviceName(s1703Iden);
                            }
                            addItem(nameAll, macAll, rssiAll + "", s1703show);

                        }
                    }
                    break;

                default:
                    break;
            }
        }
    };
    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == scan) {
                // 判断蓝牙是否开启，若开启，则扫描，若未开启，则打开然后扫描
                disconnect.setEnabled(true);
                // 判断是否已经连接蓝牙，如果连接，提示请先断开
                if (BleAdapter.isConnect) {
                    Toast.makeText(mContext, "请先断开连接！", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (!isConnecting) {
                        // 开始扫描前开启蓝牙
                        Intent turn_on = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(turn_on, 0);
                        Log.i(TAG, "蓝牙已经开启");
                        layout.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                        stopscan.setEnabled(true);
                        scan.setEnabled(false);
                        disconnect.setEnabled(false);
                        // scanThread.start();

                            scanThread = new Thread(scanRunnable);
                            scanThread.start();

                        if (listItem.size() > 0) {
                            listItem.removeAll(listItem);
                            listItemAdapter.notifyDataSetChanged();
                        }
                        s1703Data.setText("");
                        s1703dataAnalyse.setText("");
                        edit_command.setText("");

                    } else {

                        BleActivity.handler.obtainMessage(
                                BleUtils.MAIN_UPDATE_CONNECTING).sendToTarget();
                    }
                }
            } else if (v == stopscan) {
                if (test) {
                    // 停止扫描
                    handler.postDelayed(runnableStopScan, 0);
                } else {
                    mScanning = false;
                    scan.setEnabled(true);
                    stopscan.setEnabled(false);
                    BtService.mRevHandler.obtainMessage(Utils.STOPBORADCAST)
                            .sendToTarget();
                }

                stopscan.setEnabled(false);
                scan.setEnabled(true);
            } else if (v == keyTurnOn) {
                String value = PropertiesUtil.load("keyturnon");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                //               edit_command.setText(str);
                edit_command.setText(addSpace(str));
//                writeCharacter(0);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == keyTurnOff) {
                String value = PropertiesUtil.load("keyturnoff");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
//                edit_command.setText(str);
                edit_command.setText(addSpace(str));
//                writeCharacter(1);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == redTwinkle) {
//                writeCharacter(2);
                String value = PropertiesUtil.load("redtwinkle");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                //edit_command.setText(str);
                edit_command.setText(addSpace(str));
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == redShort) {
                //               writeCharacter(3);
                String value = PropertiesUtil.load("redshort");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
//                edit_command.setText(str);
                edit_command.setText(addSpace(str));
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == redLong) {
                String value = PropertiesUtil.load("redlong");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
//                edit_command.setText(str);
                edit_command.setText(addSpace(str));
//                writeCharacter(4);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == greenTwinkle) {
                String value = PropertiesUtil.load("greentwinkle");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                //               edit_command.setText(str);
                edit_command.setText(addSpace(str));
//                writeCharacter(5);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == greenShort) {
                String value = PropertiesUtil.load("greenshort");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                edit_command.setText(addSpace(str));
//                edit_command.setText(str);
//                writeCharacter(6);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == greenLong) {
                String value = PropertiesUtil.load("greenlong");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                edit_command.setText(addSpace(str));
//                edit_command.setText(str);
//                writeCharacter(7);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == blueTwinkle) {
                String value = PropertiesUtil.load("bluetwinkle");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                edit_command.setText(addSpace(str));
                //               edit_command.setText(str);
//                writeCharacter(8);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == blueShort) {
                String value = PropertiesUtil.load("blueshort");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                edit_command.setText(addSpace(str));
//                edit_command.setText(str);
//                writeCharacter(9);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == blueLong) {
                String value = PropertiesUtil.load("bluelong");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                edit_command.setText(addSpace(str));
//                edit_command.setText(str);
//                writeCharacter(10);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == whiteLong) {
                String value = PropertiesUtil.load("whitelong");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                edit_command.setText(addSpace(str));
                //               edit_command.setText(str);
//                writeCharacter(11);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == lightOff) {
                String value = PropertiesUtil.load("lightoff");
                Log.i("value", "value = " + value);
                String str = sendData(value, selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                edit_command.setText(addSpace(str));
//                edit_command.setText(str);
//                writeCharacter(12);
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
//            } else if (v == searchVersion) {
//                String value= PropertiesUtil.load("searchversion");
//                Log.i("value","value = "+ value);
//                String str=sendData(value,selectMacList);
//                writeCharacter2(service_UUID,character_UUID,str);
//                edit_command.setText(str);
////                writeCharacter(13);
//                if (!dataScanning) {
//                    Message msg = BtService.mRevHandler
//                            .obtainMessage(Utils.DATA_SCAN);
//                    msg.sendToTarget();
//                    dataScanning = true;
//                }
            } else if (v == s1703_mac) {
                String str = PropertiesUtil.load("macsearch");
//                if (!firstS1703 && selectMacList.size() > 0)
//                    str = sendData(str.replace("04", "07"), selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);

//                edit_command.setText(str);
                edit_command.setText(addSpace(str));
                macTextView.setText("");
                s1703Data.setText("");
                s1703dataAnalyse.setText("");

                iden = Const.S1703;
//                writeCharacter(14);
                if (!macScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.MAC_SCAN);
                    msg.sendToTarget();
                    macScanning = true;
                }
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
//                firstS1703 = false;
                clearItem();


            } else if (v == s1701_mac) {
                String str = PropertiesUtil.load("macsearch");
//                if (!firstS1701 && selectMacList.size() > 0)
//                    str = sendData(str.replace("04", "07"), selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
//                edit_command.setText(str);
                edit_command.setText(addSpace(str));
                macTextView.setText("");
                s1703Data.setText("");
                s1703dataAnalyse.setText("");

                //               writeCharacter(15);
                if (!macScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.MAC_SCAN);
                    msg.sendToTarget();
                    macScanning = true;
                }
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }

//                firstS1701 = false;
                clearItem();
                iden = Const.S1701;

            } else if (v == s1705_mac) {
                String str = PropertiesUtil.load("macsearch");
//                if (!firstS1705 && selectMacList.size() > 0)
//                    str = sendData(str.replace("04", "07"), selectMacList);
                writeCharacter2(service_UUID, character_UUID, str);
                edit_command.setText(addSpace(str));
                //               edit_command.setText(str);
                macTextView.setText("");
                s1703Data.setText("");
                s1703dataAnalyse.setText("");

//                writeCharacter(16);
                if (!macScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.MAC_SCAN);
                    msg.sendToTarget();
                    macScanning = true;
                }
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }

//                firstS1705 = false;
                clearItem();
                iden = Const.S1705 ;
            } else if (v == edit_button) {
                if (edit_command.getText() != null && !edit_command.getText().toString().equals("")) {
                    String edit = edit_command.getText().toString();
                    String command = edit.replaceAll(" ", "");
                    int strLen = command.length();
                    int len = Integer.parseInt(command.substring(0, 2), 16);
                    StringBuffer buffer = new StringBuffer(command);
                    for (int i = (len + 1) * 2; i < strLen; i += (len + 1) * 2) {
                        buffer.insert(i, " ");
                    }
//                    xmltools.insertXML("0000fff1-0000-1000-8000-00805f9b34fb", command);
//                    writeCharacter(xmltools.characterLength() - 1);
                    writeCharacter2(service_UUID, character_UUID, buffer.toString());
                } else {
                    Toast.makeText(getApplicationContext(), "输入命令为空", Toast.LENGTH_LONG).show();
                }
                if (!dataScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_SCAN);
                    msg.sendToTarget();
                    dataScanning = true;
                }
            } else if (v == disconnect) {

                Message msg = BleAdapter.handler
                        .obtainMessage(BleUtils.DISCONNECT);
                msg.sendToTarget();
                if (macScanning) {
                    msg = BtService.mRevHandler
                            .obtainMessage(Utils.MAC_STOP_SCAN);
                    msg.sendToTarget();
                    macScanning = false;
                }
                if (dataScanning) {
                    msg = BtService.mRevHandler
                            .obtainMessage(Utils.DATA_STOP_SCAN);
                    msg.sendToTarget();
                    dataScanning = false;
                }
                layout1.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
                deviceMac.clear();
                deviceList.clear();
            } else if (v == selectSure) {
                selectedMac = "";
                selectMacList.clear();
                //获取选择的mac
                for (Map.Entry<Integer, Boolean> entry : selectMacMap.entrySet()) {
                    int key = entry.getKey();
                    boolean value = entry.getValue();
                    Log.i("selectMac", "key = " + key + " value = " + value);
                    if (value) {
                        HashMap map = listItem.get(key);
                        String mac = map.get("mac").toString();
                        selectMacList.add(mac);
                        selectedMac += mac + " ";
                    }

                }
                macTextView.setText(selectedMac);
                if(iden == Const.S1703){layout1.setVisibility(View.VISIBLE);}
//                updateData(selectMacList);
//                selectMacMap.clear();
                if (macScanning) {
                    Message msg = BtService.mRevHandler
                            .obtainMessage(Utils.MAC_STOP_SCAN);
                    msg.sendToTarget();
                    macScanning = false;
                }

            }

        }
    };

    Runnable scanRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.i(TAG, "开始扫描 ...");
//             deviceList.clear();
//             bluetoothAdapter.startLeScan(callback);
            scanLeDevice(true);
        }
    };
    Thread scanThread;
    // Stops scanning after a pre-defined scan period.
    Runnable runnableStopScan = new Runnable() {
        @SuppressWarnings("deprecation")
        @Override
        public void run() {
            mScanning = false;
            scan.setEnabled(true);
            stopscan.setEnabled(false);
            Log.i(TAG, "dd--- [runnableStopScan.run] stopLeScan");
            bluetoothAdapter.stopLeScan(callback);
            handler.removeCallbacks(scanThread);
        }
    };

    @SuppressWarnings("deprecation")
    private void scanLeDevice(final boolean enable) {
        Log.i(TAG, "dd--- [scanLeDevice] enable = " + enable);
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            Log.i(TAG, "dd--- [scanLeDevice] postDelayed runnableStopScan");
            handler.postDelayed(runnableStopScan, BleUtils.STOP_SCAN_PERIOD);
            mScanning = true;
            deviceList.clear();
            deviceMac.clear();
            bluetoothAdapter.startLeScan(callback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(callback);
        }
    }
//
//    public static void getDeviceByMac(String mac) {
//        // MAC地址
//        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mac);
////        deviceList.add(device);
//    }

    // 扫描回调
    public static BluetoothAdapter.LeScanCallback callback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice bluetoothDevice, int rssi,
                             byte[] bytes) {
            Log.i(TAG,
                    "onLeScan: " + bluetoothDevice.getName() + "/t"
                            + bluetoothDevice.getAddress() + "/t"
                            + bluetoothDevice.getBondState());

            // 重复过滤方法，列表中不包含该设备才加入列表中，并刷新列表
//            if (!deviceList.contains(bluetoothDevice)) {
//                // 将设备加入列表数据中
//                deviceList.add(bluetoothDevice);
//                bleAdapter = new BleAdapter(mContext, deviceList);
//                list.setAdapter(bleAdapter);
//            }
            //添加rssi
                // 将设备加入列表数据中
                if (!deviceMac.contains(bluetoothDevice.getAddress()) ){
                    if (bluetoothDevice.getName()!=null&&!bluetoothDevice.getName().equals("") && bluetoothDevice.getName().startsWith("S1708")){
                    deviceMac.add(bluetoothDevice.getAddress());
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("device", bluetoothDevice);
                    map.put("rssi", rssi);
                    deviceList.add(map);}
                    bleAdapter = new BleAdapter(mContext, deviceList);
                    list.setAdapter(bleAdapter);
                } else {
                    for (int i = 0; i < deviceList.size(); i++) {
                        HashMap<String, Object> map = deviceList.get(i);
                        //更新rssi
                        if ((map.get("device")).equals(bluetoothDevice)) {
                            map.put("rssi", rssi);
                        }

                    }
                }

            Collections.sort(deviceList, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                    int lhsrssi = Integer.parseInt(lhs.get("rssi").toString());
                    int rhsrssi = Integer.parseInt(rhs.get("rssi").toString());
                    return (lhsrssi - rhsrssi) == 0 ? 0 :
                            (lhsrssi - rhsrssi > 0 ? -1 : 1);
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        exit = false;
        setContentView(R.layout.ble_activity);

        // 开启服务
        Intent intent = new Intent(BleActivity.this, BtService.class);
        startService(intent);

        mContext = this.getApplicationContext();
        // 蓝牙管理，这是系统服务可以通过getSystemService(BLUETOOTH_SERVICE)的方法获取实例
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        // 通过蓝牙管理实例获取适配器，然后通过扫描方法（scan）获取设备(device)
        bluetoothAdapter = bluetoothManager.getAdapter();

        list = (ListView) this.findViewById(R.id.blelist);
        layout = (LinearLayout) this.findViewById(R.id.layout);
        layout1 = (LinearLayout) this.findViewById(R.id.layout1);
        name = (TextView) this.findViewById(R.id.name);
        address = (TextView) this.findViewById(R.id.address);
        chara = (TextView) this.findViewById(R.id.chara);
        s1703Data = (TextView) this.findViewById(R.id.s1703_data);
        s1703dataAnalyse = (TextView) this.findViewById(R.id.s1703_data_analyse);
        macTextView = (TextView) this.findViewById(R.id.s1703_mac);
        disconnect = (Button) this.findViewById(R.id.disconnect);

        scan = (Button) this.findViewById(R.id.scan);
        stopscan = (Button) this.findViewById(R.id.stopscan);
        stopscan.setEnabled(false);
        disconnect.setEnabled(false);

        edit_command = (EditText) this.findViewById(R.id.edit_command);
        edit_button = (Button) this.findViewById(R.id.edit_button);

        keyTurnOn = (Button) this.findViewById(R.id.key_on);
        keyTurnOff = (Button) this.findViewById(R.id.key_off);
        redTwinkle = (Button) this.findViewById(R.id.red_twinkle);
        redShort = (Button) this.findViewById(R.id.red_short);
        redLong = (Button) this.findViewById(R.id.red_long);
        greenTwinkle = (Button) this.findViewById(R.id.green_twinkle);
        greenShort = (Button) this.findViewById(R.id.green_short);
        greenLong = (Button) this.findViewById(R.id.green_long);
        blueTwinkle = (Button) this.findViewById(R.id.blue_twinkle);
        blueShort = (Button) this.findViewById(R.id.blue_short);
        blueLong = (Button) this.findViewById(R.id.blue_long);
        lightOff = (Button) this.findViewById(R.id.turn_off_light);
        whiteLong = (Button) this.findViewById(R.id.whiteLong);
        //       searchVersion = (Button) this.findViewById(R.id.S1703_version);
        s1703_mac = (Button) this.findViewById(R.id.S1703_MAC);
        s1701_mac = (Button) this.findViewById(R.id.S1701_MAC);
        s1705_mac = (Button) this.findViewById(R.id.S1705_MAC);

        disconnect.setOnClickListener(listener);
        scan.setOnClickListener(listener);
        stopscan.setOnClickListener(listener);

        keyTurnOn.setOnClickListener(listener);
        keyTurnOff.setOnClickListener(listener);
        lightOff.setOnClickListener(listener);
        whiteLong.setOnClickListener(listener);

        redTwinkle.setOnClickListener(listener);
        redShort.setOnClickListener(listener);
        redLong.setOnClickListener(listener);

        greenTwinkle.setOnClickListener(listener);
        greenShort.setOnClickListener(listener);
        greenLong.setOnClickListener(listener);

        blueTwinkle.setOnClickListener(listener);
        blueShort.setOnClickListener(listener);
        blueLong.setOnClickListener(listener);
        s1703_mac.setOnClickListener(listener);
        s1701_mac.setOnClickListener(listener);
        s1705_mac.setOnClickListener(listener);
//        searchVersion.setOnClickListener(listener);
        edit_button.setOnClickListener(listener);

        selectSure = (Button) this.findViewById(R.id.selectSure);
        selectSure.setOnClickListener(listener);
        String[] from = {"name", "mac", "rssi", "data"};
        int[] to = {R.id.name, R.id.mac, R.id.rssi, R.id.data};

        macString = new ArrayList<String>();

        listItem = new ArrayList<HashMap<String, Object>>();
        listItemAdapter = new SimpleAdapter(this, listItem, R.layout.items, from, to) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                //获取相应的view中的checkbox对象
                if (convertView == null)
                    convertView = View.inflate(BleActivity.this, R.layout.items, null);
                CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.cbox);

                //添加单击事件，在map中记录
//                checkBox.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        if (((CheckBox) v).isChecked()) {
//
//                            selectMacMap.put(position, true);
//                            Log.i("selectMac", "position = " + position + "");
//                        } else {
//                            if (selectMacMap.containsKey(position)) {
//                                selectMacMap.put(position, false);
//                                Log.i("selectMac", "delete " + position + "");
//                            }
//                        }
//
//                    }
//
//                });
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {

                            selectMacMap.put(position, true);
                            Log.i("selectMac", "position = " + position + "");
                        } else {
                            selectMacMap.remove(position);

                        }
                    }
                });
                if (selectMacMap!=null&&selectMacMap.containsKey(position)) {
                    checkBox.setChecked(true);
                    Log.i("selectMac", "delete " + position + "");
                }else {
                    checkBox.setChecked(false);
                }

                return super.getView(position, convertView, parent);
            }
        };


        listView = (ListView) findViewById(R.id.datalist);
        listView.setAdapter(listItemAdapter);

    }

    public void onDestroy() {
        super.onDestroy();
        Message msg = BleAdapter.handler
                .obtainMessage(BleUtils.DISCONNECT);
        msg.sendToTarget();
        exit = true;
        if (macScanning) {
            Message message = BleAdapter.handler
                    .obtainMessage(Utils.MAC_STOP_SCAN);
            message.sendToTarget();
            macScanning = false;
        }
        if (dataScanning) {
            Message message = BleAdapter.handler
                    .obtainMessage(Utils.DATA_STOP_SCAN);
            message.sendToTarget();
            dataScanning = false;
        }


    }

//    public void writeCharacter(int index) {
//        String service_UUID = xmltools.readServerUUID();
//        xmlBean xb = xmltools.readCharacter(index);
//
//        if (xb != null) {
//            String character_UUID = xb.uuid;
//            String data[] = xb.data.split(" ");
//            int len = Integer.parseInt(data[0], 16);
//            Log.i("lch", "len = " + len);
//
//            boolean flag = false;
//            if (len > data.length) {
//                byte[] send = new byte[data.length];
//                for (int i = 0; i < data.length; i++) {
//                    send[i] = (byte) (Integer.parseInt(data[i], 16) & 0xFF);
//                }
//
//                flag = bleAdapter.write(send, service_UUID, character_UUID);
//            } else {
//                byte[] send = new byte[len + 1];
//                for (int i = 0; i < data.length; i += (len + 1)) {
//                    String str = "";
//                    for (int j = 0; j <= len; j++) {
//                        send[j] = (byte) (Integer.parseInt(data[i + j], 16) & 0xFF);
//                        str += Integer.toHexString(send[j]);
//                    }
//                    try {
//                        Thread.sleep(100);
//                        flag = bleAdapter.write(send, service_UUID, character_UUID);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//            }
//            if (!flag)
//                chara.setText("发送特征值失败");
//            else
//                chara.setText(xb.data);
//        } else {
//            chara.setText("特征值未配置！");
//        }
//    }
//
//    public void writeCharacter2(int index) {
//        String service_UUID = xmltools.readServerUUID();
//        xmlBean xb = xmltools.readCharacter(index);
//        if (xb != null) {
//            String character_UUID = xb.uuid;
//            String data[] = xb.data.split(" ");
//            byte[] send = new byte[data.length];
//            for (int i = 0; i < send.length; i++) {
//                send[i] = (byte) (Integer.parseInt(data[i], 16) & 0xFF);
//            }
//            boolean flag = bleAdapter.write(send, service_UUID, character_UUID);
//            if (!flag)
//                chara.setText("发送特征值失败");
//            else
//                chara.setText(xb.data);
//        } else {
//            chara.setText("特征值未配置！");
//        }
//    }

    //向S1708写数据
    public void writeCharacter2(String service_UUID, String character_UUID, String str) {
        String[] data = str.split(" ");
        boolean flag = false;
        for (int i = 0; i < data.length; i++) {
            byte[] send = hexStrToByteArray(data[i]);
            try {
                Thread.sleep(100);
                flag = bleAdapter.write(send, service_UUID, character_UUID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!flag)
            chara.setText("发送特征值失败");
        else
            chara.setText(str);
    }

//    public static String stringToSplit(String str) {
//        String spaceStr = "";
//        for (int i = 0; i < str.length() - 1; i++) {
//            if (i % 2 != 0) {
//                spaceStr += str.charAt(i) + " ";
//            } else {
//                spaceStr += str.charAt(i);
//            }
//        }
//        spaceStr += str.charAt(str.length() - 1);
//
//        return spaceStr;
//    }

//    public static void updateData(List<String> macList) {
//        for (int i = 0; i < xmltools.commandLength(); i++) {
//            xmlBean xb = xmltools.readCharacter(i);
//            if (xb != null) {
//                String character_UUID = xb.uuid;
//                String data[] = xb.data.split(" ");
//                String prefix = resetData(data);
//                String sureData = "";
//
//                int len = data.length;
////                if (!data[len - 1].equals("12")) {
//                for (int j = 0; j < macList.size(); j++) {
//                    sureData += prefix + last3MAC(macList.get(j));
//                }
//                xmltools.updateXML(i, stringToSplit(sureData));
////                }
//
//            }
//        }
//    }

    //字节之间添加空格
    public static String addSpace(String str) {
        String spaceStr = "";
        str = str.replaceAll(" ", "");
        int len = str.length();
        if (len >= 2) {
            for (int i = 0; i < len - 2; i += 2) {
                spaceStr += str.substring(i, i + 2);
                spaceStr += " ";
            }
            spaceStr += str.substring(len - 2);
        }
        return spaceStr;
    }

    //数据中添加mac地址的最后三个字节
    public static String sendData(String data, List<String> macList) {
        String sureData = "";

        for (int j = 0; j < macList.size(); j++) {
            sureData += (data + last3MAC(macList.get(j)));
            sureData += " ";
        }
        return sureData;

    }

//    public static String resetData(String[] s) {
//        String str = "";
//        int len = s.length;
//        if (Integer.parseInt(s[0], 16) <= len - 1) {
//            for (int i = 0; i < Integer.parseInt(s[0], 16) - 2; i++) {
//                str += s[i];
//            }
//        } else {
//            for (int i = 0; i < s.length; i++) {
//                str += s[i];
//            }
//        }
//        return str;
//    }

    //获取mac地址的最后三个字节
    public static String last3MAC(String mac) {
        String last3Bytes = "";
        String macArray[] = mac.split(":");
        for (int i = 3; i < macArray.length; i++) {
            last3Bytes += macArray[i];
        }
        return last3Bytes;
    }

    //byte数组转为16进制
    public static String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }

    //16进制转为byte数组
    public static byte[] hexStrToByteArray(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    //添加设备
    private static void addItem(String name, String mac, String rssi,
                                String data) {
//更新rssi
        if (macString.contains(mac)) {
            int index = macString.indexOf(mac);
            HashMap<String, Object> map = listItem.get(index);
//            map.put("name", name);
//            map.put("mac", mac);
            map.put("rssi", rssi);
            map.put("data", data);
            // listItem.add(index, map);
        } else {
            count++;
            macString.add(mac);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("mac", mac);
            map.put("rssi", rssi);
            map.put("data", data);
            listItem.add(map);
            listView.setSelection(listItemAdapter.getCount() - 1);


        }
        //信号量强度排序
        Collections.sort(listItem, new Comparator<HashMap<String, Object>>() {
            @Override
            public int compare(HashMap<String, Object> lhs, HashMap<String, Object> rhs) {
                int lhsrssi = Integer.parseInt(lhs.get("rssi").toString());
                int rhsrssi = Integer.parseInt(rhs.get("rssi").toString());
                return (lhsrssi - rhsrssi) == 0 ? 0 :
                        (lhsrssi - rhsrssi > 0 ? -1 : 1);
            }
        });
        listItemAdapter.notifyDataSetChanged();
    }

    private static String getDeviceName(int iden) {
        String result = "";
        if (iden == Const.S1701) {
            result = "S1701";
        } else if (iden == Const.S1703) {
            result = "S1703";
        } else if (iden == Const.S1705) {
            result = "S1705";
        }

        return result;
    }

    private void clearItem() {
        listItem.clear();
        macString.clear();
        count = 0;
        listItemAdapter.notifyDataSetChanged();
    }


}
