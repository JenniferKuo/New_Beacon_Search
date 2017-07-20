package com.example.nschen.ifrogbeacon;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.powenko.ifroglab_bt_lib.ifrog;

import org.w3c.dom.Text;

public class search extends AppCompatActivity{


//    TextView hiword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        /*接收另一個Activity傳過來的值*/
//        int hi =   getIntent().getIntExtra("sayHi",1);
//        hiword = (TextView) findViewById(R.id.hihi);
//        hiword.setText(Integer.toString(hi));//測試

    }
}
