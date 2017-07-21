package com.example.nschen.ifrogbeacon;


import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.IDNA;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.powenko.ifroglab_bt_lib.ifrog;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ifrog.ifrogCallBack, SensorEventListener, CompoundButton.OnCheckedChangeListener{

    //	private EditText editText1;
    private ListView listView1;

    /* 運用library */
    private ifrog mifrog;
    ArrayList<String> Names = new ArrayList<String>();
    ArrayList<String> Address = new ArrayList<String>();
    ArrayList<Integer> DeviceRssi = new ArrayList<Integer>();
    ArrayList<String> Information = new ArrayList<String>();

    /* set button */
    ToggleButton statusBT;

    private boolean mystatus = false;//first is off

    /* set textview */
    TextView deviceInfo;

    /* 調整distance */
    private double count = 0;
    private double distanceTotal = 0;
    double tempdis = 0;

    /* compass */
    private float currentDegree = 0f;// record the angle turned
    private SensorManager mSensorManager;// device sensor manager
    // define the display assembly compass picture
    private ImageView image;

    /* calculate direction */
    private float minRSSI = 1000000;
    private float turntoTarget = 0;

    /* detect page */
    private int page = 0;

    /* 若沒有開啟藍芽，預設畫面 */
    String[] testValues= new String[]{	"Apple","Banana","Orange","Tangerine"};
    String[] testValues2= new String[]{	"Red","Yello","Orange","Yello"};
    String[] testValues3 = new String[]{ "AppleInfo","BananaInfo","OrangeInfo","TangerineInfo"};

    private rowdata adapter;

    /* 通知的ID */
    int notificationId = 001;
    boolean isNotified = false;

    /* 藍芽 */
    final int REQUEST_ENABLE_BT = 18;
    private boolean firstOpen = true;

    /* 儲存所有Beacon裝置的陣列 */
    ArrayList<Beacon> beaconArray = new ArrayList<Beacon>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = 1;//設定現在在第幾個頁面，可直接跳轉layout
        setContentView(R.layout.activity_main);

        /* compass */
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// initialize your android device sensor capabilities
        image = (ImageView) findViewById(R.id.imageViewCompass);

        /* DeviceList */
        listView1=(ListView) findViewById(R.id.listView1);   //取得listView1
        //ListAdapter adapter = createAdapter();

        /* bluetooth */
        statusBT =  (ToggleButton) findViewById(R.id.status);
        statusBT.setOnCheckedChangeListener(this);//打開時負責檢查藍牙功能，有藍芽功能即要求開啟
        BTinit();

    }//end onCreate


    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
        mystatus = isChecked;
        if (isChecked) {// The toggle is enabled
           checkBTopen();
        }else{
            mifrog.scanLeDevice(isChecked,3600000);
        }

    }

    /*經過了dialog卻還是沒開啟 關掉check*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT && resultCode==RESULT_CANCELED){
            statusBT.setChecked(false);
            mystatus = false;
        }
    }

    public void checkBTopen(){
        if(mystatus){
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBluetoothAdapter.isEnabled()) {//要求開啟藍芽的視窗
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }else{
                mifrog.scanLeDevice(mystatus,3600000);
            }
        }else{
            mifrog.scanLeDevice(mystatus,3600000);
        }
    }

    public void BTinit(){//藍芽初始化動作
        mifrog=new ifrog();
        mifrog.setTheListener(this);//設定監聽->CallBack(當有什麼反應會有callback的動作)->新增SearchFindDevicestatus, onDestroy

        //取得藍牙service，並把這個service交給此有藍芽的設備(BLE)。有些人有藍芽的設備不見得有藍芽的軟體。// Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mifrog.InitCheckBT(bluetoothManager) == null) {
            Toast.makeText(this,"this Device doesn't support Bluetooth BLE", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{
            checkBTopen();
        }
    }

    /* compass */
    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);


        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree+turntoTarget,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        if(page == 2)
            // Start the animation
            image.startAnimation(ra);
        currentDegree = -degree;



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }


    /* appear my list */
    private void SetupList(){
        adapter=new rowdata(this,testValues,testValues2);//顯示的方式
        listView1.setAdapter(adapter);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener(){ //選項按下反應
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = testValues[position];      //哪一個列表
                Toast.makeText(MainActivity.this, item + " selected", Toast.LENGTH_LONG).show(); //顯示訊號

                /*換畫面 不換Activity*/
                setContentView(R.layout.activity_search);
//                getWindow().addContentView(getLayoutInflater().inflate(R.layout.activity_search, null),new FrameLayout.LayoutParams(
//                        FrameLayout.LayoutParams.MATCH_PARENT,
//                        FrameLayout.LayoutParams.WRAP_CONTENT ));

//                View viewToRemove= findViewById(R.id.activity_search);
//                if (viewToRemove != null && (ViewGroup) viewToRemove.getParent() != null && viewToRemove instanceof ViewGroup)
//                    ((ViewGroup) viewToRemove.getParent()).removeView(viewToRemove);

                /* infomation on the second page*/
                deviceInfo = (TextView) findViewById(R.id.beaconinfo);
                String itemlist = testValues3[position];
                deviceInfo.setText(itemlist);
                page = 2;


                //image direction
                image = (ImageView) findViewById(R.id.imageViewCompass);
                /*換頁面 有換Activity*/
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, search.class);
//                intent.putExtra("sayHi",123);//試著傳值
//                startActivity(intent);
            }
        } );

    }



    public double calculateDistance(int rssi){
        /*   d = 10^((abs(RSSI) - A) / (10 * n))  */
        double result = 0;

        //if(count>15){
        if(count>15){
            tempdis = distanceTotal/count;
            count = 0;
            distanceTotal = 0;
        }
        else{
            float txPower = -59;//hard coded power value. Usually ranges between -59 to -65
            if(rssi == 0){
                result = -1.0;
            }
            double ratio = rssi*1.0/txPower;
            if (ratio < 1.0) {
                result =  Math.pow(ratio,10);
            }
            else{
                double distance = (0.89976)*Math.pow(ratio,7.7095) + 0.111;
                result =  distance;
            }

            count ++;
            distanceTotal += result;
        }

        result = Math.round(result*10);

        /* direction */
        if( minRSSI > Math.abs(rssi)){
            minRSSI = Math.abs(rssi);
            turntoTarget = -currentDegree;//N:0, E:+
        }

        //return tempdis;
        return result;

    }






    @Override
    public void BTSearchFindDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        String t_address= device.getAddress();//有找到裝置的話先抓Address
        /* 固定beacon */
        String mac_address = "84:EB:18:7A:5B:80";
//        Toast.makeText(getBaseContext(),t_address, Toast.LENGTH_SHORT).show();
        if (t_address.equals(mac_address) && !isNotified) {
            broadcastNotice(device.getName(), "記得去跑步");
            isNotified = true;
        }
        int index=0;
        boolean t_NewDevice=true;
        for(int i=0;i<Address.size();i++){
            String t_Address2=Address.get(i);
            if(t_Address2.compareTo(t_address)==0){//如果address和列表中的address一模一樣
                t_NewDevice=false;//登記說他不是新的device
                index=i;//把index記起來
                break;
            }
        }
        if(device.getName() != null){
            if(t_NewDevice==true){//如果是新的advice
                Address.add(t_address);
                //null can appear
                Names.add(device.getName());//+" RSSI="+Integer.toString(rssi)+" d="+calculateDistance(rssi)+"cm"+" myD ="+Float.toString(turntoTarget));//抓名字然後放進列表
                Information.add(
                        "Device : "+device.getName()+
                        "\nAddress : "+t_address+
                        "\nRssi : "+Integer.toString(rssi)+
                        "\nDistance : "+Double.toString(calculateDistance(rssi))
                );
                testValues = Names.toArray(new String[Names.size()]);
                testValues2 =Address.toArray(new String[Address.size()]);
                testValues3 = Information.toArray(new String[Information.size()]);
            }else{//如果不是新的device
                Names.set(index,device.getName());//+" RSSI="+Integer.toString(rssi)+" d="+calculateDistance(rssi)+"cm"+" myD ="+Float.toString(turntoTarget));//更改device名字，RSSI:藍芽4.0裡面可以知道訊號強度
                Information.add(
                        "Device : "+device.getName()+
                        "\nAddress : "+t_address+
                        "\nRssi : "+Integer.toString(rssi)+
                        "\nDistance : "+Double.toString(calculateDistance(rssi))+"cm"
                );
                testValues = Names.toArray(new String[Names.size()]);//放進array
                testValues3 = Information.toArray(new String[Information.size()]);
            }
        }


        SetupList();//更新畫面
    }

    @Override
    public void BTSearchFindDevicestatus(boolean arg0) {//arg0:true/false，代表有沒有在找
        if(arg0==false){
            Toast.makeText(getBaseContext(),"Stop Search", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(),"Start Search",  Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {//當程式離開了就把service關掉，不然service一直跑會浪費電。
        super.onDestroy();
        mifrog.BTSearchStop();
    }

    public void broadcastNotice(String title, String content) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.compass)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setDefaults(Notification.DEFAULT_VIBRATE);
        // Creates an explicit intent for an Activity in your app
        builder.setDefaults(0);

        // 取得NotificationManager物件
        NotificationManager manager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // 建立通知物件
        Notification notification = builder.build();

        // 使用設定的通知編號為編號發出通知
        manager.notify(notificationId, notification);

    }

    public Beacon deviceToBeacon(Beacon beacon, BluetoothDevice device) {
        beacon.macAddress = device.getAddress();
        beacon.bName = device.getName();
        return beacon;
    }

    public void addBeacon() {

    }

    @Override
    public void onBackPressed() {
        Intent backPressedIntent = new Intent();
        backPressedIntent .setClass(getApplicationContext(), MainActivity.class);
        startActivity(backPressedIntent );
        finish();
    }
}
