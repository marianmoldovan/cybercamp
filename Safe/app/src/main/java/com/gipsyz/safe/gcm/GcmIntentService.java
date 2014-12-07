package com.gipsyz.safe.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.gipsyz.safe.AppUtils;
import com.gipsyz.safe.MainActivity;
import com.gipsyz.safe.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by batman on 06/12/2014.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(AppUtils.TAG, "Received notification");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("type"), extras.getString("message"));
                Log.i(AppUtils.TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String title, String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Bitmap compare = Bitmap.createBitmap(10,10, Bitmap.Config.ARGB_8888);
        compare.setPixel(0,0, Color.RED);

        int resource = title.equals("Intrusion")?R.drawable.allert:R.drawable.innfo;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_bell)
            .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(),
                    resource))
            .setContentTitle(title)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
            .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}