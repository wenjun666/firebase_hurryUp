package info.androidhive.firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianyang on 12/5/16.
 */

public class NotificationPush extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //sending notification
        Intent intent2 = new Intent(NotificationPush.this, UpcomingEventActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(NotificationPush.this, (int) System.currentTimeMillis(), intent2, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(NotificationPush.this)
                .setContentTitle("5 Minutes to your next event! hurry up!")
                .setContentText("Subject").setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_stat_name, "Call", pIntent)
                .addAction(R.drawable.ic_stat_name, "More", pIntent)
                .addAction(R.drawable.ic_stat_name, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
                        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);

        // Query the database and show alarm if it applies

        // Here you can return one of some different constants.
        // This one in particular means that if for some reason
        // this service is killed, we don't want to start it
        // again automatically
        stopSelf();
        return START_NOT_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
