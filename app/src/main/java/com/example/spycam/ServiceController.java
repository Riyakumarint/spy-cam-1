package com.example.spycam;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.os.Process;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceController extends Service{
    private ServiceHandler serviceHandler;
    private BroadcastReceiver mReceiver;
    public Timer timer;
    public TimerTask timerTask;
    private float mLightQuantity;
    final Handler handler = new Handler();
    int flag=0;
    boolean start1;
    boolean start2;
    final String dir = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/TempVid");
    File tempDirec = new File(dir);


    public void onCreate() {
        //thread handler

        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

        //broadcast receiver for screen
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        //sensor manager
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        Sensor mMotionSensor1= mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor mMotionSensor2= mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        SensorEventListener listener2 = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x1=event.values[0];
                float y1=event.values[1];
                float z1=event.values[2];
                if(x1==0 && y1==0 && z1==0){start2=true;}
                else{start2=false;}
                motionController();

            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };

        SensorEventListener listener1 = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x2=event.values[0];
                float y2=event.values[1];
                float z2=event.values[2];
                if(x2==0 && y2==0 && z2==0){start1=true; }
                else { start1=false;}
                motionController();
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };

        SensorEventListener listener = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                mLightQuantity = event.values[0];
                if(mLightQuantity<10){ StopIt();}
                else {StartIt();}
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        };
        mSensorManager.registerListener(
                listener, mLightSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(
                listener1, mMotionSensor1, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(
                listener2, mMotionSensor2, SensorManager.SENSOR_DELAY_UI);


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        return START_STICKY;
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //motion detection start stop
    public  void motionController(){
        if(start1 && start2){
            StopIt();
        }
        else {
            StartIt();
        }
    }
    //timer makes video of specified time video
    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 100, 10000); //
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        StopMainService();
                        StartMainService();
                        File[] childFiles = tempDirec.listFiles();
                        if(false){
                            childFiles[0].delete();
                        }
                    }
                });
            }
        };
    }
    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void StartMainService(){
        if(flag==0){
            Intent intent = new Intent(this, MainService.class);
            startService(intent);
            flag=1;
        }
    }
    public void StopMainService(){
        if(flag==1) {
            Intent intent = new Intent(this, MainService.class);
            stopService(intent);
            flag=0;
        }
    }
    public void StartIt(){
        StartMainService();
        startTimer();
    }
    public void StopIt(){
        StopMainService();
        stoptimertask();
    }


    public class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                StartIt();
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
                StopIt();
            }
            }

        }
        //keeping service alive.
    private final class ServiceHandler extends Handler{
        ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            try {
                startTimer();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
            stopSelf(msg.arg1);

        }

    }


    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        stoptimertask();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}