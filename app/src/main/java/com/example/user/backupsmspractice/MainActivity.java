package com.example.user.backupsmspractice;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //点击按钮 查询短信数据内容 然后进行备份
    public void backup(View v){
        try {
            //[1]获取xml序列化实例
            XmlSerializer serializer = Xml.newSerializer();
            //[2]设置序列化参数

            File file = new File(Environment.getExternalStorageDirectory().getPath(),"smsBackUp.xml");
            FileOutputStream fos = new  FileOutputStream(file);
            serializer.setOutput(fos, "utf-8");

            //[3]开始写xml文档开头
            serializer.startDocument("utf-8", true);
            //[4]开始写根节点
            serializer.startTag(null, "smss");
            //[5]由于短信数据库 系统也通过内容提供者给暴露出来了了 所以我们只需要通过内容解析者去操作数据库
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = getContentResolver().query(uri, new String[]{"address","date","body"}, null, null, null);
            while (cursor.moveToNext()) {
                String address = cursor.getString(0);
                String date = cursor.getString(1);
                String body= cursor.getString(2);

                Log.v("Mainactivity", "address:" + address + "---date:" + date + "---body:" + body);

                //[6]写sms节点

                serializer.startTag(null, "sms");
                //[7]写address节点
                serializer.startTag(null, "address");
                serializer.text(address);
                serializer.endTag(null, "address");

                //[8]写body节点
                serializer.startTag(null, "body");
                serializer.text(body);
                serializer.endTag(null, "body");

                //[9]写date节点
                serializer.startTag(null, "date");
                serializer.text(date);
                serializer.endTag(null, "date");

                serializer.endTag(null, "sms");
            }
            serializer.endTag(null, "smss");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //点击按钮 往短信数据库里面插入一条数据
    public void insert(View v) {
        //由于短信数据库已经通过内容提供者暴露出来了 所以我们可以直接通过内容解析者 操作数据库
        Uri uri = Uri.parse("content://sms/");
        ContentValues values = new ContentValues();
        values.put("address", "110");
        values.put("body", "hello");
        values.put("date", System.currentTimeMillis());
        getContentResolver().insert(uri, values);
    }
}
