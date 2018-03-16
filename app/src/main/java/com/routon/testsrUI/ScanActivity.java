package com.routon.testsrUI;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.testsr.R;
import com.routon.testsr.scan.BtService;
import com.routon.testsr.utils.Const;
import com.routon.testsr.utils.Utils;

/**
 * @author zyf
 */
public class ScanActivity extends Activity {

    private static final String TAG = "testSR";
    static ListView listView;
    static SimpleAdapter listItemAdapter; // ListView的适配器
    static ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表

    private static ArrayList<String> macString;
    static int count = 0;


    private Button start;
    private Button stop;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == start) {
//			if (v == setting1) {
                clearItem();
                // 开始广播
                if (BtService.mRevHandler == null) {
                    Log.i(TAG, "Handler is null!!!");
                } else {
                    BtService.mRevHandler.obtainMessage(Utils.STARTBORADCAST)
                            .sendToTarget();
                    // start.setClickable(false);
                    start.setEnabled(false);
                }
            } else if (v == stop) {
                // 停止广播
                BtService.mRevHandler.obtainMessage(Utils.STOPBORADCAST)
                        .sendToTarget();
                // start.setClickable(true);
                start.setEnabled(true);
            }
        }
    };

    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 数据格式：Len ADType BSn Iden Cmd Data
                case Utils.UI_UPDATE:
                    Bundle bundle = (Bundle) msg.obj;
                    String mac = bundle.getString("mac");
                    // byte[] data = bundle.getByteArray("data");
                    int rssi = bundle.getInt("rssi");
                    String name = bundle.getString("name");
                    byte[] data = bundle.getByteArray("data");
                    int length = data[0];
                    int Iden = data[3] & 0xFF;
                    String show = "";
                    if (Iden == Const.S1701 ) {
                        for (int i = 0; i <= length; i++) {
                            if (Integer.toHexString(data[i] & 0xFF).length() == 1) {
                                show = show + "  0"
                                        + Integer.toHexString(data[i] & 0xFF);
                            } else {
                                show = show + "  "
                                        + Integer.toHexString(data[i] & 0xFF);
                            }
                        }
                        if (name == null || name.equals("")) {
                            name = getDeviceName(Iden);
                        }
                        // String name = "S1701";
                        addItem(name, mac, rssi + "", show);
                    } else if (Iden == Const.S1703 ) {
                        for (int i = 0; i <= length; i++) {
                            if (Integer.toHexString(data[i] & 0xFF).length() == 1) {
                                show = show + "  0"
                                        + Integer.toHexString(data[i] & 0xFF);
                            } else {
                                show = show + "  "
                                        + Integer.toHexString(data[i] & 0xFF);
                            }
                        }
                        if (name == null || name.equals("")) {
                            name = getDeviceName(Iden);
                        }
                        // String name = "S1703";
                        addItem(name, mac, rssi + "", show);
                    } else if (Iden == Const.S1705 ) {
                        for (int i = 0; i <= length; i++) {
                            if (Integer.toHexString(data[i] & 0xFF).length() == 1) {
                                show = show + "  0"
                                        + Integer.toHexString(data[i] & 0xFF);
                            } else {
                                show = show + "  "
                                        + Integer.toHexString(data[i] & 0xFF);
                            }
                        }
                        // String name = "S1705";
                        if (name == null || name.equals("")) {
                            name = getDeviceName(Iden);
                        }
                        addItem(name, mac, rssi + "", show);
                    } else if (Iden == Const.X8 ) {
                        for (int i = 0; i <= length; i++) {
                            if (Integer.toHexString(data[i] & 0xFF).length() == 1) {
                                show = show + "  0"
                                        + Integer.toHexString(data[i] & 0xFF);
                            } else {
                                show = show + "  "
                                        + Integer.toHexString(data[i] & 0xFF);
                            }
                        }
                        // String name = "X8";
                        if (name == null || name.equals("")) {
                            name = getDeviceName(Iden);
                        }
                        addItem(name, mac, rssi + "", show);
                    } else if (Iden == Const.CI24T ) {
                        for (int i = 0; i <= length; i++) {
                            if (Integer.toHexString(data[i] & 0xFF).length() == 1) {
                                show = show + "  0"
                                        + Integer.toHexString(data[i] & 0xFF);
                            } else {
                                show = show + "  "
                                        + Integer.toHexString(data[i] & 0xFF);
                            }
                        }
                        // String name = "CI24T";
                        if (name == null || name.equals("")) {
                            name = getDeviceName(Iden);
                        }
                        addItem(name, mac, rssi + "", show);
                    } else if (Iden == Const.CI14T) {
                        for (int i = 0; i <= length; i++) {
                            if (Integer.toHexString(data[i] & 0xFF).length() == 1) {
                                show = show + "  0"
                                        + Integer.toHexString(data[i] & 0xFF);
                            } else {
                                show = show + "  "
                                        + Integer.toHexString(data[i] & 0xFF);
                            }
                        }
                        if (name == null || name.equals("")) {
                            name = getDeviceName(Iden);
                        }
                        // String name = "CI14T";
                        addItem(name, mac, rssi + "", show);

                    }

                    break;

            }
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

        setContentView(R.layout.scan_activity);
        // 开启服务
        Intent intent = new Intent(ScanActivity.this, BtService.class);
        startService(intent);
        start = (Button) findViewById(R.id.button1);
        stop = (Button) findViewById(R.id.button2);


        start.setOnClickListener(listener);
        stop.setOnClickListener(listener);


        String[] from = {"name", "mac", "rssi", "data"};
        int[] to = {R.id.name, R.id.mac, R.id.rssi, R.id.data};

        macString = new ArrayList<String>();

        listItem = new ArrayList<HashMap<String, Object>>();
        listItemAdapter = new SimpleAdapter(this, listItem, R.layout.items,
                from, to);
        listView = (ListView) findViewById(R.id.datalist);
        listView.setAdapter(listItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                TextView tv0 = (TextView) view.findViewById(R.id.name);
                TextView tv1 = (TextView) view.findViewById(R.id.mac);
                TextView tv2 = (TextView) view.findViewById(R.id.rssi);
                TextView tv3 = (TextView) view.findViewById(R.id.data);

                String name = tv0.getText().toString();
                String mac = tv1.getText().toString();
                String rssi = tv2.getText().toString();
                String data = tv3.getText().toString();

                showListDialog(name, mac, rssi, data);
                // Toast.makeText(MainActivity.this, "Click item" + i,
                // Toast.LENGTH_SHORT).show();
            }
        });
    }


    private static void addItem(String name, String mac, String rssi,
                                String data) {

        if (macString.contains(mac)) {
            int index = macString.indexOf(mac);
            HashMap<String, Object> map = listItem.get(index);
            map.put("name", name);
            map.put("mac", mac);
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
//			listView.getChildAt(0).setVisibility(View.GONE);
        }
        listItemAdapter.notifyDataSetChanged();
    }

    private void clearItem() {
        listItem.clear();
        macString.clear();
        count = 0;
        listItemAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void showListDialog(String name, String mac, String rssi,
                                String data) {
        final String[] items = {"name = " + name, "mac:" + mac,
                "rssi:" + rssi, "data:" + data};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(
                ScanActivity.this);
        listDialog.setTitle("详细信息");
        listDialog.setItems(items, null);

        listDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
        listDialog.show();
    }

    private static String getDeviceName(int name) {
        String result = "";
        if (name == Const.S1701) {
            result = "S1701";
        } else if (name == Const.S1703) {
            result = "S1703";
        } else if (name == Const.S1705) {
            result = "S1705";
        } else if (name == Const.X8) {
            result = "X8";
        } else if (name == Const.CI24T) {
            result = "CI24T";
        } else if (name == Const.CI14T) {
            result = "CI14T";
        }

        return result;
    }

}
