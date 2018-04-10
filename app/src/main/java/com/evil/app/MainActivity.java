package com.evil.app;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.evil.mail.EmailUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
        }
    }

    public void sendEmail(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File directory = Environment.getExternalStorageDirectory();
                    File file = new File(directory,"ota.log");
                    EmailUtil.sendMail2Other("hello","hahahhaha","1066537317@qq.com",file);
                    //                    EmailUtil.sendMail("13434159434@163.com",
                    //                                       "13434159434@163.com",
                    //                                       "Evil123456",
                    //                                       "1066537317@qq.com",
                    //                                       "smtp.163.com",
                    //                                       "hello",
                    //                                       "你好啊");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
