package swetha.remainderapp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by swetha on 4/14/18.
 */

public class WifiScanReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        WifiManager wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo= wifiManager.getConnectionInfo();
        String SSID = wifiInfo.getSSID();
        if (SSID.startsWith("\"")) {
            SSID = SSID.substring(1);
            if (SSID.endsWith("\"")) {
                SSID = SSID.substring(0, SSID.length() - 1);
            }
        }
        SQLiteDatabase sqLiteDatabase  = context.openOrCreateDatabase("Reminder",MODE_PRIVATE,null);
        String sqlString ="SELECT remText,checkBox,toNumber,wifi FROM items";
        Cursor cursor = sqLiteDatabase.rawQuery(sqlString,null);
        cursor.moveToFirst();
        try {
            while (cursor != null) {
                String wifi = cursor.getString(3);
                if (SSID.equals(wifi)) {
                    Toast.makeText(context, cursor.getString(0), Toast.LENGTH_LONG).show();
                    if (cursor.getInt(1) == 1) {
                     //   SendSMS.to(context, cursor.getString(2), cursor.getString(0));
                    }
                    sqlString = "DELETE FROM items WHERE wifi = '" + cursor.getString(3) + "'";
                    sqLiteDatabase.execSQL(sqlString);
                    break;
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Exception occured in wifi", Toast.LENGTH_LONG).show();
        }

    }
}
