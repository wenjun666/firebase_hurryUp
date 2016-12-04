package info.androidhive.firebase;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by tianyang on 12/4/16.
 */

public class Myservice extends Service {
    private DatabaseReference UserDatabaseReference;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String Name = user.getDisplayName();
        String userId = user.getUid();
        //get my own refrence
        UserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId);
        UserDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String score = dataSnapshot.child("score").getValue(String.class);
                Toast.makeText(getApplicationContext(), "My score is:"+score, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Query the database and show alarm if it applies

        // Here you can return one of some different constants.
        // This one in particular means that if for some reason
        // this service is killed, we don't want to start it
        // again automatically
        Intent intent2 = new Intent(Myservice.this, ProfileActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(Myservice.this, (int) System.currentTimeMillis(), intent2, 0);

        // Build notification
        // Actions are just fake
        Notification noti = new Notification.Builder(Myservice.this)
                .setContentTitle("We are testing the notifications here.")
                .setContentText("Subject").setSmallIcon(R.drawable.ic_stat_name)
                .setContentIntent(pIntent)
                .addAction(R.drawable.ic_stat_name, "Call", pIntent)
                .addAction(R.drawable.ic_stat_name, "More", pIntent)
                .addAction(R.drawable.ic_stat_name, "And more", pIntent).build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
        stopSelf();
        return START_NOT_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 5),
                PendingIntent.getService(this, 0, new Intent(this, Myservice.class), 0)
        );
    }
}
