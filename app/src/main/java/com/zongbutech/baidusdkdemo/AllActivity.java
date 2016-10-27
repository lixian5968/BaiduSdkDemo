package com.zongbutech.baidusdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zongbutech.baidusdkdemo.Demo.LXLocationDemo;
import com.zongbutech.baidusdkdemo.Demo.LXLocationDemo2;
import com.zongbutech.baidusdkdemo.Demo.LocationDemo;
import com.zongbutech.baidusdkdemo.Map.OverlayDemo;

public class AllActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
    }

   public void Location(View v){
       startActivity(new Intent(AllActivity.this, LocationDemo.class));
   }
    public void Location2(View v){
        startActivity(new Intent(AllActivity.this, LXLocationDemo.class));
    }
    public void Location3(View v){
        startActivity(new Intent(AllActivity.this, LXLocationDemo2.class));
    }
    public void OverlayDemo(View v){
        startActivity(new Intent(AllActivity.this, OverlayDemo.class));
    }

}
