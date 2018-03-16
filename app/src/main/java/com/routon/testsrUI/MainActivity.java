package com.routon.testsrUI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.testsr.R;
import com.routon.testsr.scan.BtService;
import com.routon.xml.xmlConstant;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity {

	private Button offline;
	private Button online;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// // 无title
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		// // 全屏
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_acitivity);
		// 开启服务
		Intent intent = new Intent(MainActivity.this, BtService.class);
		startService(intent);

		offline = (Button) this.findViewById(R.id.offline);
		online = (Button) this.findViewById(R.id.online);

		offline.setOnClickListener(listener);
		online.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v == offline) {
				Intent intent = new Intent(MainActivity.this,
						ScanActivity.class);
				startActivity(intent);
				save();
				Log.i("lch","存储文件成功");
			} else if (v == online) {
				Intent intent = new Intent(MainActivity.this,
						BleActivity.class);
				startActivity(intent);
				save();
				Log.i("lch","存储文件成功");
			}
		}
	};
	public void save() {
		FileOutputStream out = null;
		BufferedWriter writer = null;
		BufferedReader br = null;
		try {
			out = openFileOutput("data.xml", Context.MODE_PRIVATE);
			writer = new BufferedWriter(new OutputStreamWriter(out));
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(xmlConstant.filepath)), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				writer.write(line);
				writer.flush();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
