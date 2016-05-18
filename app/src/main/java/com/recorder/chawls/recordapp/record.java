package com.recorder.chawls.recordapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class record extends AppCompatActivity {

    private TextView powerConsumptionTextView;
    private TextView areaCodeTextView;
    private TextView logTextView;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void buttonOnClick(View v) {
        Button button = (Button) v;
        //((Button) v).setText("Clicked");

        setPowerConsumptionTextView();
        setAreaCodeTextView();
        setLogTextView();

        if(((Button) v).getText().toString().equalsIgnoreCase("Clicked"))
        {
            ((Button) v).setText("InvertClicked");
        }
        else
        {
            ((Button) v).setText("Clicked");
        }
    }

    private void setAreaCodeTextView() {
        final TelephonyManager telephony = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        if(telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
        {
            final GsmCellLocation cellLocation = (GsmCellLocation)telephony.getCellLocation();
            if(cellLocation != null)
            {
                int cid = cellLocation.getCid() & 0xffff;
                int lac = cellLocation.getLac() & 0xffff;
                areaCodeTextView = (TextView) findViewById(R.id.areaCodeTextView);
                areaCodeTextView.setText("LAC: " + lac + " CID: " + cid);
            }
        }
    }
    private void setPowerConsumptionTextView() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int level = -1;
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                powerConsumptionTextView = (TextView) findViewById(R.id.powerConsumptionTextView);
                powerConsumptionTextView.setText("Battery Level Remaining: " + level + "%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }
    private void setLogTextView(){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }

            //writing to log-file via writeCommand
            writeCommand("logcat -d -f","/myLogs/mylog.log");

            //writing to log-file via writeFile()
            //writeFile(log.toString(),"/myLogs/mylog.log");

            //writing to text box on screen to verify
            logTextView = (TextView)findViewById(R.id.logTextView);
            logTextView.setText(log.toString());

        } catch (IOException e) {
        }
    }
    private boolean writeCommand(String commandToExec, String fullFilePath) throws IOException {
        File filename = new File(Environment.getExternalStorageDirectory()+fullFilePath); //"/mylog.log");
        filename.createNewFile();
        String cmd = commandToExec+filename.getAbsolutePath();  //"logcat -d -f"
        Runtime.getRuntime().exec(cmd);
        return true;
    }
    /*
    private boolean writeFile(String dataToWrite, String fullFilePath){
        FileOutputStream outputStream;
        try{
            outputStream = openFileOutput(fullFilePath, Context.MODE_PRIVATE);
            outputStream.write(dataToWrite.getBytes());
            outputStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "record Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.recorder.chawls.recordapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "record Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.recorder.chawls.recordapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
