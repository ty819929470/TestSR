package com.routon.xml;

import com.routon.testsrUI.BleActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by lch on 18-2-5.
 */

public class FileTools {
    public static ArrayList<xmlBean> list = new ArrayList<>();

    public static  void readAndWrite() {
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
                /* 读入TXT文件 */
 //           String mac = BleActivity.last3mac;
//           String mac="75:BE:93";
//            String dataMac[] = mac.split(":");
//            String lastData = dataMac[0] + " " + dataMac[1] + " " + dataMac[2];
            String pathname = "app/src/main/java/com/routon/xml/BeanData.txt"; // 绝对路径或相对路径都可以，写入文件时演示相对路径
            File filename = new File(pathname);
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String data = "";
 //           line = br.readLine();
            while (data != null) {
                String uuid = br.readLine(); // 一次读入一行数据
                 data=br.readLine();
                System.out.println(uuid + " " + data);
//                if (!("04 ff 01 80 12").equals(data)){
//                    xmlBean bean=new xmlBean(uuid,data+" "+lastData);
//                    list.add(bean);
//                }else {
                    xmlBean bean=new xmlBean(uuid,data);
                    list.add(bean);
//                }
            }
            System.out.println(list.size());
            list.remove(list.size()-1);
            System.out.println(list.size());
            xmltools.createXML("0000fff0-0000-1000-8000-00805f9b34fb",list);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        readAndWrite();
//        xmltools.createXML("0000fff0-0000-1000-8000-00805f9b34fb",list);
    }

}

