package swetha.remainderapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Context.WIFI_SERVICE;

/**
 * Created by swetha on 4/13/18.
 */

public class ReminderReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;
    private static final String TAG = ReminderReceiver.class.getSimpleName();
    public static final String SMS_CONTENT = "sms_content";
    private static  SQLiteDatabase sqLiteDatabase;
    @Override
    public void onReceive(Context context, Intent intent) {

        sqLiteDatabase  = context.openOrCreateDatabase("Reminder",MODE_PRIVATE,null);

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.i(TAG, "Intent recieved: " + intent.getAction());
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            String message = msgs[0].getMessageBody();
            String sender = msgs[0].getOriginatingAddress();
            Cursor c = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
            c.moveToFirst();
            String smsBody = c.getString(12);
            try {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT remText,checkBox,toNumber,fromNumber FROM items",null);
                cursor.moveToFirst();
                while (cursor != null) {
                    Log.i("numberfromDB",cursor.getString(3));
                    String fromNumber = "+1" + cursor.getString(3);
                    if(sender.equals(fromNumber)) {
                        Toast.makeText(context, "NewApp"+message + " " + sender
                            , Toast.LENGTH_LONG).show();

                        if (cursor.getInt(1) == 1) {
                            SendSMS.to(context, cursor.getString(2), cursor.getString(0));
                        }
                        String sqlString = "DELETE FROM items WHERE fromNumber =   '" + cursor.getString(3) +"'";
                        sqLiteDatabase.execSQL(sqlString);
                        break;
                    }
                }
            } catch (Exception e){
                Log.i("exception-db",e.getMessage());
                Toast.makeText(context,"Exception in SMS receive",Toast.LENGTH_LONG).show();
            }
            // alarm
            c.close();
        }
        else {

            Date date = new Date();
            int hours = date.getHours();
            int min = date.getMinutes();
            try {
                String sqlString = "SELECT remText,checkBox,toNumber FROM items WHERE hourOfDay = " + hours + " AND minuteOftheHr = " + min;
                Cursor cursor = sqLiteDatabase.rawQuery(sqlString, null);
                cursor.moveToFirst();
                String remText = "";
                while (cursor != null) {
                    remText = cursor.getString(0);
                     if (cursor.getInt(1) == 1) {
                        SendSMS.to(context, cursor.getString(2), cursor.getString(0));
                    }
                    break;
                }
                sqlString = "DELETE FROM items WHERE hourOfDay = " + hours + " AND minuteOftheHr = " + min;
                sqLiteDatabase.rawQuery(sqlString, null);

                mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                mediaPlayer.start();
                Toast.makeText(context, remText, Toast.LENGTH_LONG).show();

                Intent notificationIntent = new Intent(context, MainActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, notificationIntent, 0);

                Notification notification = new Notification.Builder(context)
                        .setContentTitle("Reminder")
                        .setContentText(remText)
                        .setContentIntent(pendingIntent)
                        .addAction(android.R.drawable.sym_action_chat, "chat", pendingIntent)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .build();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification);
            } catch(Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Exception in alarm", Toast.LENGTH_LONG).show();
            }
        }
    }
}
