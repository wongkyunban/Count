package com.wong.count;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //点此按钮计数器增加1
    private Button dingBtn;
    //重置按钮
    private Button resetBtn;
    //开始按钮
    private Button button;
    //显示计数器
    private TextView textView;
    //显示时分秒毫秒
    private TextView hour;
    private TextView minute;
    private TextView second;
    private TextView msec;
    private final static String NAME = "count";
    private final static String MOBILE = "mobile";
    private SharePreferencesHelper sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置状态栏的顔色
        getWindow().setStatusBarColor(getResources().getColor(R.color.green));
        //获取视图组件
        this.button = (Button)findViewById(R.id.button);
        this.dingBtn = (Button)findViewById(R.id.dingBtn);
        this.resetBtn = (Button)findViewById(R.id.resetBtn);
        this.textView = (TextView)findViewById(R.id.textView);
        this.hour = (TextView)findViewById(R.id.hour);
        this.minute = (TextView)findViewById(R.id.minute);
        this.second = (TextView)findViewById(R.id.second);
        this.msec = (TextView)findViewById(R.id.msec);
        this.sp = new SharePreferencesHelper(this,MOBILE);
        //将计数器重置为0
        this.sp.putValue(NAME,0);
        //初始文本值
        this.textView.setText(String.valueOf(this.sp.getValue(NAME)));


        //打卡按钮事件
        this.dingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = MainActivity.this.sp.getValue(NAME) + 1;
                MainActivity.this.sp.putValue(NAME,count);
                String str =  MainActivity.this.sp.getValue(NAME) + "";
                MainActivity.this.textView.setText(str);
            }
        });
        //重置按钮事件
        this.resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.show();
                Window window = alertDialog.getWindow();
                window.setContentView(R.layout.custom_alert_dialog);
                Button bothBtn = (Button)window.findViewById(R.id.bothBtn);
                Button jishuqiBtn = (Button)window.findViewById(R.id.jishuqiBtn);
                Button timeBtn = (Button)window.findViewById(R.id.timeBtn);
                bothBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.resetJiShuQi();
                        MainActivity.this.resetTime();
                        MainActivity.this.button.setText(getResources().getText(R.string.start));
                        MainActivity.this.button.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.start_button,null),null,null);
                        MainActivity.this.stopTimer();
                        MainActivity.this.tenMSecs = 0;
                        alertDialog.dismiss();


                    }
                });
                jishuqiBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.resetJiShuQi();
                        alertDialog.dismiss();
                    }
                });
                timeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.this.resetTime();
                        MainActivity.this.button.setText(getResources().getText(R.string.start));
                        MainActivity.this.button.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.start_button,null),null,null);
                        MainActivity.this.stopTimer();
                        MainActivity.this.tenMSecs = 0;
                        alertDialog.dismiss();

                    }
                });

            }
        });
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String indicator = MainActivity.this.button.getText().toString();
                if(indicator.equalsIgnoreCase("开始")){

                    MainActivity.this.button.setText(getResources().getText(R.string.pause));
                    MainActivity.this.button.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.pause_button,null),null,null);
                    MainActivity.this.startTimer();
                }
                if(indicator.equalsIgnoreCase("暂停")){
                    MainActivity.this.button.setText(getResources().getText(R.string.resume));
                    MainActivity.this.button.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.start_button,null),null,null);
                    MainActivity.this.stopTimer();

                }
                if(indicator.equalsIgnoreCase("继续")){
                    MainActivity.this.button.setText(getResources().getText(R.string.pause));
                    MainActivity.this.button.setCompoundDrawablesRelativeWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.pause_button,null),null,null);
                    MainActivity.this.startTimer();

                }
            }
        });


    }
    //重置时间
    private void resetTime(){
        this.hour.setText(getResources().getText(R.string.ling));
        this.minute.setText(getResources().getText(R.string.ling));
        this.second.setText(getResources().getText(R.string.ling));
        this.msec.setText(getResources().getText(R.string.ling));
        //关闭时钟


    }
    //重置计数器
    private void resetJiShuQi(){
        MainActivity.this.sp.putValue(NAME,0);
        String str =  MainActivity.this.sp.getValue(NAME) + "";
        MainActivity.this.textView.setText(str);
    }
    //使用Handler更新UI线程
    private static final int UPDATE_TIME = 1;
    //用来记录已过了的时间数
    private long tenMSecs = 0;
    //计时器
    private Timer timer = new Timer();
    private TimerTask timerTask = null; //timer要通过调用timerTask实现计时
    private TimerTask updateUITask = null;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_TIME:
                    DecimalFormat df = new DecimalFormat("##");
                    df.applyPattern("00");
                    String hour = df.format(tenMSecs/100/60/60)+"";
                    String minute = df.format(tenMSecs/100/60%60)+"";
                    String second = df.format(tenMSecs/100%60)+"";
                    String msec = df.format(tenMSecs%100)+"";
                    MainActivity.this.hour.setText(hour);
                    MainActivity.this.minute.setText(minute);
                    MainActivity.this.second.setText(second);
                    MainActivity.this.msec.setText(msec);
                    break;
                default:
                        break;
            }

        }
    };

    //开启定时更新UI作务
    private void startUpdateUITask(){
        if(this.updateUITask == null){
            this.updateUITask = new TimerTask() {
                @Override
                public void run() {
                    MainActivity.this.handler.sendEmptyMessage(UPDATE_TIME);
                }
            };
        }

        timer.schedule(this.updateUITask,10,10);
    }
    //停止更新UI作务
    private void stopUpdateUITask(){
        if(this.updateUITask != null){
            this.updateUITask.cancel();
            this.updateUITask = null;
        }
    }

        //开始计时
    private void startTimer(){
        if(this.timerTask == null){
            this.timerTask = new TimerTask() {
                @Override
                public void run() {
                    MainActivity.this.tenMSecs++;
                }
            };
            //timer.schedule的参数：第一个是要执行计时的任务，第二个参数，是第一次调用timerTask的run方法的时间间隔，在这里就是10毫秒后执行第一次任务，第三个参数是从第二次开始每隔多久就执行一次，这里是10毫秒。
            this.timer.schedule(this.timerTask,10,10);
            //同时开启更新UI定时任务
            this.startUpdateUITask();
        }
    }
    //结束计时
    private void stopTimer(){
        if(this.timerTask != null){
            this.timerTask.cancel();
            this.timerTask = null;
            //同时结束UI的更新任务
            this.stopUpdateUITask();
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消计时
        this.timer.cancel();
    }

    //连续点击两次退出程序
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {//如果两次按键时间间隔大于2秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;//更新firstTime
                return true;
            } else {//两次按键小于2秒时，退出应用
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
