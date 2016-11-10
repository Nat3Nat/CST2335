package com.example.natalia.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    protected static String ACTIVITY_NAME = "WeatherForecastActivity";
    ProgressBar mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_weather_forecast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ProgressBar pr = (ProgressBar) findViewById(R.id.progressBar);

        mProgress.setVisibility(View.VISIBLE);
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        ForecastQuery qry = new ForecastQuery();
        qry.execute(null,null,null);
    }

    public class ForecastQuery extends AsyncTask<String, Integer, String> {
        String maxTemp;
        String minTemp;
        String currentTemp;
        String iconName;
        Bitmap currentPic;
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric";

        protected String doInBackground(String... params) {
            Log.i(ACTIVITY_NAME, "Starting background task...");
            URL url = null;
            try {
                url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                Log.i(ACTIVITY_NAME, "Connected to HTTP. Parsing XML...");

                XmlPullParser parser = Xml.newPullParser();
                //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(conn.getInputStream(), null);
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    // Starts by looking for the entry tag
                    if (name.equals("temperature")) {
                        currentTemp = parser.getAttributeValue(null, "value");
                        publishProgress(25);
                        minTemp = parser.getAttributeValue(null, "min");
                        publishProgress(50);
                        maxTemp = parser.getAttributeValue(null, "max");
                        publishProgress(75);
                    }
                    if (name.equals("weather")) {
                        iconName = parser.getAttributeValue(null, "icon");
                    }
                }

                    String imagefile =  iconName + ".png";
                    String imageURL = "http://openweathermap.org/img/w/" + iconName + ".png";
                    if (fileExistance(imagefile)){
                        FileInputStream fis = null;
                        try {    fis = new FileInputStream(getBaseContext().getFileStreamPath(imagefile));
                                 currentPic = BitmapFactory.decodeStream(fis);
                                 Log.i(ACTIVITY_NAME, "Reading local file " + imagefile);
                                 fis.close();
                        }
                        catch (FileNotFoundException e) {    e.printStackTrace();  }

                      }
                    else{

                        HttpUtils utils = new HttpUtils();
                        currentPic  = utils.getImage(imageURL);
                        FileOutputStream outputStream = openFileOutput(imagefile, Context.MODE_PRIVATE);
                        currentPic.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                        Log.i(ACTIVITY_NAME, "Downloading file " + imageURL);
                        outputStream.flush();
                        outputStream.close();
                    }
                    publishProgress(100);
                    Log.i(ACTIVITY_NAME, "DoBackground is complete");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        protected void onPostExecute(String result)
        {
            Log.i(ACTIVITY_NAME, "onPostExecute()");
            TextView txtTemp = (TextView) findViewById(R.id.temp);
            txtTemp.setText("Current Temp " + currentTemp);

            TextView minTxtTemp = (TextView) findViewById(R.id.minTemp);
            minTxtTemp.setText("Minimum Temp " + minTemp);

            TextView maxTxtTemp = (TextView) findViewById(R.id.maxTemp);
            maxTxtTemp.setText("Maximum Temp " + maxTemp);

            ImageView forecastView = (ImageView) findViewById(R.id.imageView);
            forecastView.setImageBitmap(currentPic);

            mProgress.setVisibility(View.INVISIBLE );
        }

        protected void onPreExecute()
        {
            Log.i(ACTIVITY_NAME, "onPreExecute()");
            // do something before start
        }
        public void onProgressUpdate(Integer... value) {
            Log.i(ACTIVITY_NAME, "onProgressUpdate()");
            mProgress.setVisibility(View.VISIBLE);
            mProgress.setProgress(value[0]);
        }
         class HttpUtils {
            public Bitmap getImage(String urlString) {
                try {
                    URL url = new URL(urlString);
                    return getImage(url);
                } catch (MalformedURLException e) {
                    return null;


                }
            }
            public Bitmap getImage(URL url) {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        return BitmapFactory.decodeStream(connection.getInputStream());
                    } else
                        return null;
                } catch (Exception e) {
                    return null;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
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
    };
