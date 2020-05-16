package com.example.spycam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void permanentStore(View view) {
        Intent intent = new Intent(this, permanentStore.class);
    }
    public void temporaryStore(View view) {
            Intent intent = new Intent (this, TempStore.class);
    }

    public void start_service(View view) {
            Intent intent = new Intent (this, ServiceController.class);
    }

    public void stop_service(View view) {
            Intent intent = new Intent (this, MainService.class);
    }

    public void settings(View view) {
            Intent intent = new Intent (this, settings.class);
    }

}
