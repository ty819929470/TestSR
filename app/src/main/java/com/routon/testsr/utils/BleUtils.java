package com.routon.testsr.utils;

public class BleUtils {

	public  static final long STOP_SCAN_PERIOD = 5000;
	
	

//	public  static final int UI_CONNECT= 0;
//	public  static final int UI_CONNECTING= 1;
//	public  static final int UI_CONNECTED= 2;

	public  static final int MAIN_UPDATE_CONNECTING= 2;
	

	public  static final int MAIN_UPDATE_CONNECT= 3;
	public  static final int MAIN_UPDATE_DISCONNECT= 4;


	public  static final int DISCONNECT= 5;

	//开始扫描
	public static final int start_scan = 11;

	public static final int s1703_DATA_RECEIVED=15;
	public static final int s1701_DATA_RECEIVED=16;
	public static final int s1705_DATA_RECEIVED=17;
	public static final int s1703_MAC_RECEIVED=18;
//	public  static final String connect = "连接";
//	public  static final String connecting = "连接中";
//	public  static final String connected = "已连接";

	public static final String service_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
	public static final String character_UUID1 = "0000fff1-0000-1000-8000-00805f9b34fb";
//	public static final String character_UUID2 = "00002a06-0000-1000-8000-00805f9b34fb";
//
//	public static final byte[] character_CMD1 = {1};
//	public static final byte[] character_CMD2 = {2};
}
