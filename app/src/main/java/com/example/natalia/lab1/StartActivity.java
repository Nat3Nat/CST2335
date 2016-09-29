package com.example.natalia.lab1;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    protected static String ACTIVITY_NAME = "StartActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(ACTIVITY_NAME, "In onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button imButton = (Button) findViewById(R.id.imButton);
        imButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {

                Intent intent = new Intent(StartActivity.this, ListItemActivity.class);
                startActivityForResult(intent,5);

            }
        });}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         // Check which request we're responding to
         if (requestCode == 5 && resultCode==Activity.RESULT_OK) {
             Log.i(ACTIVITY_NAME, "Returned to StartActivity.onActivityResult");

             CharSequence messagePassed = data.getStringExtra("Response");
             int duration = Toast.LENGTH_LONG;
             Toast toast = Toast.makeText(this,messagePassed,duration);
             toast.show();

         }
    }

    protected void onResume() {
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    protected void onStart(){
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    protected void onPause(){
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    protected void onStop(){
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }
}
