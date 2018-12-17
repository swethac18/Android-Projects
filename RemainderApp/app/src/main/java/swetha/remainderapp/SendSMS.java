package swetha.remainderapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by swetha on 4/14/18.
 */

public class SendSMS {
    public static boolean to(Context context, String phoneNumber, String message) {

        try {


            String permission = Manifest.permission.SEND_SMS;
            int grant = ContextCompat.checkSelfPermission(context, permission);
            if (grant != PackageManager.PERMISSION_GRANTED) {
                String[] permission_list = new String[1];
                permission_list[0] = permission;
                ActivityCompat.requestPermissions((Activity) context, permission_list, 1);
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            return true;
        } catch(Exception e) {
            return false;
        }
    }
}
