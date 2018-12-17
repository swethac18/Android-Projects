package swetha.remainderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CheckBox smsBox;
    EditText phoneNumber;
    Spinner remindEvent;
    TextView textView;
    EditText details;
    EditText reminderText;
    TimePicker timePicker;
    String latLong=" ";
    String spinnerSelected;
    SQLiteDatabase sqLiteDatabase;
    int boxVisible = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Reminder App");

        smsBox = findViewById(R.id.smsCheckBox);
        phoneNumber = findViewById(R.id.numberEditText);
        remindEvent = findViewById(R.id.reminderSpinner);
        timePicker = findViewById(R.id.timePicker);
        textView = findViewById(R.id.textView6);
        details = findViewById(R.id.detailsEditText);
        reminderText = findViewById(R.id.remainderEditText);

        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");

        sqLiteDatabase = this.openOrCreateDatabase("Reminder",MODE_PRIVATE,null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS items (remText VARCHAR, checkBox INTEGER, toNumber INTEGER, hourOfDay INTEGER, minuteOftheHr INTEGER, wifi VARCHAR, lat REAL, lng REAL, fromNumber VARCHAR);");
        smsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (smsBox.isChecked()) {
                    boxVisible = 1;
                    phoneNumber.setVisibility(View.VISIBLE);
                } else {
                    boxVisible = 0;
                    phoneNumber.setVisibility(View.INVISIBLE);
                }
            }
        });

        remindEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelected = remindEvent.getSelectedItem().toString();

                boolean textViewVisible = false;
                boolean dateVisible =false;
                String textViewText = "";

                // logic to set various flags
                if (spinnerSelected.equals("Date")) {
                    dateVisible=true;
                } else if (spinnerSelected.equals("Wifi")) {
                    textViewVisible = true;
                    textViewText = "Wifi SSID name:";
                } else if (spinnerSelected.equals("Location")) {
                    textViewVisible = true;
                    textViewText = "Location (Address):";
                } else if (spinnerSelected.equals("SMS")) {
                    textViewVisible = true;
                    textViewText = "SMS From:";
                } else {
                 textViewVisible = true;
                 textViewText = "Call From:";
                }


                // flag based downstream logic
                if(dateVisible){
                    timePicker.setVisibility(View.VISIBLE);
                }
                else{
                    timePicker.setVisibility(View.INVISIBLE);
                }

                if (textViewVisible) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(textViewText);
                    details.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                    details.setVisibility(View.INVISIBLE);
                }

                // reset flags
                dateVisible = false;
                textViewVisible = false;
                textViewText = "";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(MainActivity.this, "Select an value", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClickRemindMe(View view){

        String remText = reminderText.getText().toString();
        String phoneNum = null;
        Integer remHour = null;
        Integer remMinute = null;
        String remWifi = null;
        Double remLat = null;
        Double remLng = null;
        String remFromNum = null;
        String detailString =details.getText().toString();
        sqLiteDatabase = this.openOrCreateDatabase("Reminder",MODE_PRIVATE,null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS items (remText VARCHAR, checkBox INTEGER, toNumber VARCHAR, hourOfDay INTEGER, minuteOftheHr INTEGER, wifi VARCHAR, lat REAL, lng REAL, fromNumber VARCHAR);");
        sqLiteDatabase.execSQL("DELETE FROM items");
        if(boxVisible == 1){
            phoneNum = phoneNumber.getText().toString();
        }

       if(spinnerSelected.equals("Location")){
           double[] latlng = getLocationFromAddress(details.getText().toString());
           remLat = latlng[0];
           remLng = latlng[1];
       }
       else if(spinnerSelected.equals("Wifi")){
           remWifi = details.getText().toString();
           getWifiSSID();
       }
       else if(spinnerSelected.equals("Call")){
           remFromNum = (details.getText().toString());
       }
       else if(spinnerSelected.equals("SMS")){
           remFromNum = details.getText().toString();
       }
       else if (spinnerSelected.equals("Date")) {
           remHour = timePicker.getHour();
           remMinute = timePicker.getMinute();
           setAlarm(remHour,remMinute, remText);
       }

       String sqlString ="INSERT INTO items (remText,checkBox,toNumber,hourOfDay,minuteOftheHr,wifi,lat,lng,fromNumber) VALUES ('"+remText+"','"+boxVisible+"','"+phoneNum+"','"+remHour+"','" +remMinute+"','" +  remWifi+"','"+remLat+"','"+remLng+"','"+remFromNum+"');";

        sqLiteDatabase.execSQL(sqlString);
        try {
            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM items  WHERE remText='" + remText + "'", null);
            int smsCheckIndex = c.getColumnIndex("remText");
            c.moveToFirst();
            while (c != null) {
                Toast.makeText(MainActivity.this, c.getString(smsCheckIndex) + c.getString(c.getColumnIndex("checkBox")), Toast.LENGTH_LONG).show();
                c.moveToNext();
            }
        } catch (Exception e){
            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void setAlarm(Integer hour,Integer minute, String remText) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.setAction("AlarmAction");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0010000,intent,0);

        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        time.set(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP,time.getTimeInMillis(),pendingIntent);
    }

    public double[] getLocationFromAddress(String addr){
        Geocoder coder = new Geocoder(MainActivity.this);
        List<Address> address;
        double lat=0.0;
        double lng=0.0;
        double[] latlng = new double[2];

        try {
            address = coder.getFromLocationName(addr, 5);
            if (address == null) {
                Log.i("null-address","null");
            }
            Address location = address.get(0);
            Log.i("address",address.get(0).toString());
             lat = location.getLatitude();
             lng = location.getLongitude();
             latlng[0]=lat;
             latlng[1]=lng;

            Log.i("lat-long",String.valueOf(lat)+"  "+","+String.valueOf(lng));
        } catch (Exception e) {
            Log.i("exception",e.getMessage());
        }
        return latlng;
    }

    public void getWifiSSID(){
        WifiManager wifiManager = (WifiManager) MainActivity.this.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo= wifiManager.getConnectionInfo();
        if(wifiInfo != null){
            String ssid = wifiInfo.getSSID();
            Toast.makeText(MainActivity.this,ssid,Toast.LENGTH_LONG).show();

        }
    }
}
