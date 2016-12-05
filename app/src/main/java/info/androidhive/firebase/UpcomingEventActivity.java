package info.androidhive.firebase;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static android.content.Context.ALARM_SERVICE;

public class UpcomingEventActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private DatabaseReference mPostReference,mEventReference;
    private
    ListView lvEvents;
    ListAdapter lvAdapter;
    private String eventName, evenTime, eventLocation, eventDate;
    private Set<String> eventList;
    private ArrayList<String> eventNameList = new ArrayList<>();
    private String[] eventListView;
    private ArrayList<String> eventDateList = new ArrayList<>();
    private ArrayList<String> eventTimeList = new ArrayList<>();
    private ArrayList<String> eventLong = new ArrayList<>();
    private ArrayList<String> eventLat = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_event);

        lvEvents = (ListView)findViewById(R.id.lvEvents);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Initialize Database:
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users");
        //String test=mPostReference.child(user.getUid()).child("event");
        //Toast.makeText(UpcomingEventActivity.this,test,Toast.LENGTH_LONG).show();

        Query query = mPostReference.orderByKey().equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get a datasnapshot from firebase; since the return format is a jason format. we need to cast the data in a Map format
                Map<String, Map<String, Object>> user_profile = (HashMap<String, Map<String, Object>>) dataSnapshot.getValue();
                // if user profile exists, get all the profile info
                if (user_profile != null) {  //check if the user already has the
                    for (Map.Entry<String, Map<String, Object>> value : user_profile.entrySet()) {

                        Map<String, Object> abc = value.getValue();

                        //name = abc.get("name").toString();
                        //gender = abc.get("gender").toString();
                        //score = abc.get("score").toString();

                        //user_profile_name.setText(name);
                        //user_score.setText("score: " + score);
                        Map<String, Boolean> events = (Map) abc.get("event");
                        eventList = events.keySet();
                        eventListView = eventList.toArray(new String[eventList.size()]);


                        //Toast.makeText(UpcomingEventActivity.this, "b"+eventListView.length, Toast.LENGTH_SHORT).show();


                        //Log.i(TAG, "blablablbala");
                    }


                    for(int i =0; i<eventListView.length;i++) {
                        //Toast.makeText(UpcomingEventActivity.this, "a" + eventListView[0], Toast.LENGTH_SHORT).show();
                        mEventReference = FirebaseDatabase.getInstance().getReference().child("events");
                        Query query2 = mEventReference.orderByKey().equalTo(eventListView[i]);

                        query2.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                Map<String, Map<String, Object>> event_profile = (HashMap<String, Map<String, Object>>) dataSnapshot1.getValue();
                                // if user profile exists, get all the profile info
                                if (event_profile != null) {  //check if the user already has the
                                    for (Map.Entry<String, Map<String, Object>> value : event_profile.entrySet()) {

                                        Map<String, Object> abc = value.getValue();

                                        String eventName = (String) abc.get("eventName");
                                        String eventDate = (String) abc.get("date");
                                        String eventTime = (String) abc.get("time");
                                        String longtitute = (String) abc.get("longtitute");
                                        String latitute = (String) abc.get("latitute");
                                        eventDateList.add(eventDate);
                                        eventNameList.add(eventName);
                                        eventTimeList.add(eventTime);
                                        eventLong.add(longtitute);
                                        eventLat.add(latitute);
                                        //Toast.makeText(UpcomingEventActivity.this, eventLong + " " +eventLat, Toast.LENGTH_SHORT).show();



                                        //Toast.makeText(UpcomingEventActivity.this, eventName, Toast.LENGTH_SHORT).show();


                                        //Log.i(TAG, "blablablbala");
                                    }

                                        lvAdapter = new MyCustomAdapter(UpcomingEventActivity.this, eventNameList, eventDateList, eventTimeList,eventLong,eventLat);  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                                        lvEvents.setAdapter(lvAdapter);
                                        //Toast.makeText(UpcomingEventActivity.this, Integer.toString(eventNameList.size()), Toast.LENGTH_SHORT).show();

                                        //Toast.makeText(getBaseContext(), eve, Toast.LENGTH_SHORT).show();


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }





                }
                // if null, create new user
                else {
                        Toast.makeText(UpcomingEventActivity.this,"error",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}


class MyCustomAdapter extends BaseAdapter {
    Context context;
    private
    ArrayList<String> eventNameList;
    ArrayList<String> eventDateList;
    ArrayList<String> eventTimeList;
    ArrayList<String> eventLong;
    ArrayList<String> eventLat;



    public MyCustomAdapter(Context aContext, ArrayList<String> eventNameList, ArrayList<String> eventDateList,ArrayList<String> eventTimeList, ArrayList<String> eventLong, ArrayList<String> eventLat) {
        context = aContext;
        this.eventNameList=eventNameList;
        this.eventDateList=eventDateList;
        this.eventTimeList=eventTimeList;
        this.eventLong=eventLong;
        this.eventLat=eventLat;
    }


    @Override
    public int getCount() {
        return eventNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;  //this will refer to the row to be inflated or displayed if it's already been displayed. (listview_row.xml)
        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //CRASH
            row = inflater.inflate(R.layout.listview_row, parent, false);

        }

        else{
            row = convertView;
        }

        TextView textViewName=(TextView)row.findViewById(R.id.textViewName);
        TextView textViewDate=(TextView)row.findViewById(R.id.textViewDate);
        TextView textViewTime=(TextView)row.findViewById(R.id.textViewTime);
        //TextView textViewLocation=(TextView)row.findViewById(R.id.textViewLocation);
        Button notifyMe = (Button) row.findViewById(R.id.notifyMe);

        textViewName.setText(eventNameList.get(position));
        textViewDate.setText(eventDateList.get(position));
        textViewTime.setText(eventTimeList.get(position));
        //textViewLocation.setText("location");

        textViewName.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("long",Double.parseDouble(eventLong.get(position)));
                intent.putExtra("lat", Double.parseDouble(eventLat.get(position)));
                context.startActivity(intent);

            }
        });

        notifyMe.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String time=eventTimeList.get(position);
                String date = eventDateList.get(position);
                Calendar cal = new GregorianCalendar();
                //cal.set(selectedYear, selectedMonth, selectedDay,selectedHour, selectedMinute);
                //cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
                cal.set(Calendar.HOUR_OF_DAY, 18);
                cal.set(Calendar.MINUTE, 32);
                AlarmManager alarm = (AlarmManager)context.getSystemService(ALARM_SERVICE);
                alarm.set(
                        alarm.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        PendingIntent.getActivity(context, 0, new Intent(context, ProfileActivity.class), 0)
                );
            }
        });



        return row;
    }
}