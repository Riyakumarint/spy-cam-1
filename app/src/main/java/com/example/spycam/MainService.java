package com.example.spycam;
import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
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
import java.io.IOException;

public class MainService extends Service{
    private ServiceHandler serviceHandler;
    private Camera mCamera;
    private MediaRecorder mediaRecorder;
    final String dir = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/TempVid");
    File tempDirec = new File(dir);
    if (!(tempDirec.exists())){
        tempDirec.mkdirs();}
    public Uri NewFile(){
        String file = dir+"count"+".mp4";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        }
        catch (IOException e)
        { return  null;
        }
        return Uri.fromFile(newfile);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        //creating threads
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        return START_STICKY;
    }

    private final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        //function to get instance
        Camera getCameraInstance(){
            Camera cam = null;
            try {
                cam = Camera.open();
            } catch (Exception e){
            }
            return cam;
        }
        //preparing recorder
        private boolean prepareVideoRecorder(){
            mCamera = getCameraInstance();
            mediaRecorder = new MediaRecorder();
            mCamera.unlock();
            mediaRecorder.setCamera(mCamera);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            File tempFiles = null;
            mediaRecorder.setOutputFile(NewFile().toString());

            try {
                mediaRecorder.prepare();
            } catch (Exception e) {
                releaseMediaRecorder();
                return false;
            }
            return true;
        }
        boolean x=prepareVideoRecorder();
        private void releaseMediaRecorder(){
            if (mediaRecorder != null) {
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                mCamera.lock();
            }
        }

        public void handleMessage(Message msg) {
            try {
                mediaRecorder.start();
                mediaRecorder.stop();
                releaseMediaRecorder();
                mCamera.lock();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
            stopSelf(msg.arg1);

        }

    }


    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        releaseMediaRecorder();
        releaseCamera();

    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            mCamera.lock();}
    }
}
