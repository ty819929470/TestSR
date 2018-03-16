package com.routon.testsrUI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.testsr.R;
import com.routon.testsr.scan.BtService;
import com.routon.testsr.utils.BleUtils;
import com.routon.testsr.utils.Const;
import com.routon.testsr.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchActivity extends Activity {
    private static int iden = Const.S1705;

    static ListView listView;
    static SimpleAdapter listItemAdapter; // ListView的适配器
    static ArrayList<HashMap<String, Object>> listItem; // ListView的数据源，这里是一个HashMap的列表

    static boolean scanning=false;

    private static ArrayList<String> macString;
    static int count = 0;
    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int m = msg.what;
            switch (m) {
                case BleUtils.s1703_MAC_RECEIVED:
                    Bundle dataMac = msg.getData();
                    String macAll = dataMac.getString("mac");
                    int rssiAll = dataMac.getInt("rssi");
                    String nameAll = dataMac.getString("name");
                    byte[] dataCmdAll = dataMac.getByteArray("data");
                    int s1705Iden = dataCmdAll[3] & 0xFF;

//                    if (s1705Iden == iden) {
                        if (nameAll == null || nameAll.equals("")) {
                            nameAll = getDeviceName(s1705Iden);
                        }
                        addItem(nameAll, macAll, rssiAll + "");
 //                   }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search);
        Intent intent = new Intent(SearchActivity.this, BtService.class);
        startService(intent);
        listView = (ListView) findViewById(R.id.s1705_maclist);
        Log.i("lch","start service");
        macString = new ArrayList<String>();

        String[] from = {"name", "mac", "rssi"};
        int[] to = {R.id.name, R.id.mac, R.id.rssi, R.id.data};
        listItem = new ArrayList<HashMap<String, Object>>();
        listItemAdapter = new SimpleAdapter(SearchActivity.this, listItem, R.layout.newitems, from, to);

        listView.setAdapter(listItemAdapter);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Message msg = BtService.mRevHandler.obtainMessage(Utils.START_TEST_s1705);
                msg.sendToTarget();
                scanning=true;
                Log.i("lch","start handle");
            }
        },60);
    }

    public void onDestroy() {
        super.onDestroy();
        if (scanning) {
            Message msg = BtService.mRevHandler.obtainMessage(Utils.STOP_TEST_s1705);
            msg.sendToTarget();
            scanning=false;
        }


    }

    private static void addItem(String name, String mac, String rssi) {

        if (macString.contains(mac)) {
            int index = macString.indexOf(mac);
            HashMap<String, Object> map = listItem.get(index);
            map.put("name", name);
            map.put("mac", mac);
            map.put("rssi", rssi);
            // listItem.add(index, map);
        } else {

            count++;
            macString.add(mac);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("mac", mac);
            map.put("rssi", rssi);
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

    private static String getDeviceName(int name) {
        String result = "";
        if (name == Const.S1701) {
            result = "S1701";
        } else if (name == Const.S1703) {
            result = "S1703";
        } else if (name == Const.S1705) {
            result = "S1705";
        }

        return result;
    }

}
