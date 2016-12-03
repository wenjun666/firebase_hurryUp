package info.androidhive.firebase;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener,OnMapReadyCallback {
    private DatePicker calendarView;
    private EditText editTextDate, editTxtEventName, editTxtTime, editTxtlocation;
    private DatabaseReference mEventReference;
    private Button btnCreate,btnInvite;
    private Button searchLocate;
    private CameraUpdate camUpdate;
    private String longtitute;
    private String latitute;
    private Button btnInviteFriend;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        editTextDate = (EditText) findViewById(R.id.date);
        editTxtTime = (EditText) findViewById(R.id.time);
        btnCreate = (Button)findViewById(R.id.btnCreateEvent);
        btnInvite =(Button) findViewById(R.id.btnInvite);
        editTxtEventName = (EditText)findViewById(R.id.txtEventName);
        editTxtlocation = (EditText)findViewById(R.id.editTextLocation);
        searchLocate = (Button)findViewById(R.id.searchLocation);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        //Calendar c = Calendar.getInstance();
        //int date = c.get(Calendar.DAY_OF_YEAR);
        //editTextDate.setText(currentDateTimeString);


        // Initialize Database
        mEventReference = FirebaseDatabase.getInstance().getReference()
                .child("events");

        editTextDate.setOnClickListener(new EditText.OnClickListener(){
            public  void onClick(View v){
                //Intent intent = new Intent(getBaseContext(),EditDate.class);
                //startActivity(intent);

                PickDate newFragment = new PickDate();
                newFragment.show(getSupportFragmentManager(), "com.example.wenjun.hurryup.DatePicker");


            }
        });

        editTxtTime.setOnClickListener(new EditText.OnClickListener(){
            public void onClick(View v){
                PickTime newFragment = new PickTime();
                newFragment.show(getSupportFragmentManager(),"PickTime");
            }
        });

        editTxtlocation.setOnClickListener(new EditText.OnClickListener(){
            public void onClick(View v){

            }
        });

        btnInvite.setOnClickListener(new Button.OnClickListener(){
            public  void onClick(View v){


                String eventName = editTxtEventName.getText().toString();
                String location = editTxtlocation.getText().toString();
                String time= editTxtTime.getText().toString();
                String date = editTextDate.getText().toString();

                if(!eventName.isEmpty() && !location.isEmpty() && !time.isEmpty() && !date.isEmpty()){
                    Event event = new Event(eventName,date,time,longtitute,latitute);
                    String eventKey = mEventReference.push().getKey();
                    //mEventReference.push().setValue(event);
                    mEventReference.child(eventKey).setValue(event);
                    Intent intent = new Intent(CreateEventActivity.this,AddFriendActivity.class);
                    intent.putExtra("eventKey",eventKey);
                    startActivity(intent);


                }
                else{
                    Toast.makeText(getBaseContext(),"Please enter all fields",Toast.LENGTH_SHORT).show();
                }
        }

    });
        searchLocate.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                String strLoc = editTxtlocation.getText().toString();
                List<Address> addresses;
                try {
                    Geocoder gc = new Geocoder(CreateEventActivity.this);
                    try {
//                        addresses = gc.getFromLocationName("Eifel Tower", 1);  //address, max number of address resolutions.
                        addresses = gc.getFromLocationName(strLoc, 1);  //address, max number of address resolutions.a
                        //check if we can search for this address
                        if(addresses.size()==0){
                            Toast.makeText(CreateEventActivity.this, "please enter full address.",Toast.LENGTH_SHORT).show();
                        }else {
                            strLoc = addresses.get(0).getLocality();  //Retrieving the "known" name from Location Services (might be different than the string we submitted.)
                            double lat = addresses.get(0).getLatitude();

                            double lon = addresses.get(0).getLongitude();
                            latitute = Double.toString(lat);
                            longtitute = Double.toString(lon);
                            Toast.makeText(CreateEventActivity.this,latitute+" "+longtitute,Toast.LENGTH_LONG).show();
                            gotoLocation((float) lat, (float) lon, 12, strLoc);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateEventActivity.this, " GPS not Setup. ", Toast.LENGTH_LONG);
                }
            }
        });



    }
    //----this is what happens when a language doesn(Java) 't have default parms! icky... --------//
    void gotoLocation(float aLat, float aLong) {
        gotoLocation(aLat, aLong, 12, "BU Headquarters");
    }

    void gotoLocation(float aLat, float aLong, int aZoom) {
        gotoLocation(aLat, aLong, aZoom, "BU Headquarters");
    }

    void gotoLocation(float aLat, float aLong, String aStrLoc) {
        gotoLocation(aLat, aLong, 12, aStrLoc);
    }

    void gotoLocation(float aLat, float aLong, int aZoom, String aStrLoc) {
        LatLng latLng = new LatLng(aLat, aLong);
        camUpdate = CameraUpdateFactory.newLatLngZoom(latLng, aZoom);
        mMap.animateCamera(camUpdate);
        mMap.addMarker(new MarkerOptions().position(latLng).title(aStrLoc));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));  //don't call zoomTo, buggy.
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        editTextDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

    }

    public void onTimeSet(TimePicker view, int hour, int minute){
        editTxtTime.setText(hour+ ":" + minute);
    }


}

class Event {
    public String eventName;
    public String longtitute;
    public String latitute;
    public String date;
    public String time;

    public Event(){}

    public Event(String eventName, String date, String time, String longtitute,String latitute){
        this.eventName = eventName;
        this.date= date;
        this.time = time;
        this.longtitute=longtitute;
        this.latitute=latitute;
    }

    public String getEventName(){
        return  this.eventName;
    }
}
//mEventReference.limitToLast(1).addChildEventListener(new ChildEventListener() {
//@Override
 /*                   public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Event name = dataSnapshot.getValue(Event.class);
                        Toast.makeText(getBaseContext(),name.getEventName(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


            });*/
