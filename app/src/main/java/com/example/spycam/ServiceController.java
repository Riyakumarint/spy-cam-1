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
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.os.Process;

import androidx.annotation.Nullable;

public class ServiceController extends Service{
    private ServiceHandler serviceHandler;
    private BroadcastReceiver mReceiver;
    private Sensor mLightSensor;
    private float mLightQuantity;


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

        SensorEventListener listener3 = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x1=event.values[0];
                float y1=event.values[1];
                float z1=event.values[2];
                if(x1==0 && y1==0 && z1==0){ }
                else if(1==1){ }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };

        SensorEventListener listener2 = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x2=event.values[0];
                float y2=event.values[1];
                float z2=event.values[2];
                if(x2==0 && y2==0 && z2==0){ }
                else if(1==1){ }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        };

        SensorEventListener listener1 = new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                mLightQuantity = event.values[0];
                if(mLightQuantity<10){ }
                else if(1==1){}
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        };
        mSensorManager.registerListener(
                listener1, mLightSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(
                listener2, mMotionSensor1, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(
                listener3, mMotionSensor2, SensorManager.SENSOR_DELAY_UI);


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


    public static class ScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
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
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            stopSelf(msg.arg1);

        }

    }


    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}