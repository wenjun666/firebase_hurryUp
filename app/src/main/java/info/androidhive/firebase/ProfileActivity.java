package info.androidhive.firebase;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity implements GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener {
    private ImageView cameraIcon;
    private ImageView imageViewRound;
    private Button createButton;
    private ImageView addFriend;
    private CharSequence method[] = new CharSequence[] {"Gallery", "Take a Photo"};
    static final int PICK_PHOTO = 1;
    static final int CAPTURE_PHOTO =2;
    private FirebaseAuth auth;
    private DatabaseReference mPostReference;
    private FirebaseAuth.AuthStateListener authListener;
    private TextView user_profile_name, user_score;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private String email,name,gender,userId,phone;
    private long score;
    private Set<String> friendList;
    private Button upcomingEvent;
    private Button topTen;
    private String DEBUG_TAG="tag";
    public static final String PREFS_NAME = "MyPref";

    private GestureDetectorCompat GD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        GD = new GestureDetectorCompat(this,this);
        //GD.setOnDoubleTapListener(ProfileActivity.this);
        GD.setOnDoubleTapListener(this);


        user_profile_name = (TextView)findViewById(R.id.user_profile_name);
        user_score = (TextView) findViewById(R.id.user_profile_short_bio);
        upcomingEvent = (Button) findViewById(R.id.btnUpcomingEvent);
        topTen = (Button) findViewById(R.id.btnTopTen);
        imageViewRound = (info.androidhive.firebase.RoundedImageView)findViewById(R.id.imageViewRound);  //profile picture
        addFriend = (ImageView)findViewById(R.id.add_friend);   // add friend icon
        createButton = (Button)findViewById(R.id.createButton); // Click to create event
        cameraIcon = (ImageView)findViewById(R.id.camera_icon); //Click to choose a picture or take a picture using camera


        Bitmap icon =  BitmapFactory.decodeResource(getResources(),R.drawable.background);


        SharedPreferences settings = this.getSharedPreferences("MyPref", 0);

        if(settings!=null){
            String image = settings.getString("imagepath","null");
            if(image!= "null") {
                //Log.i(DEBUG_TAG, image);
                //File imgFile = new File(image);
                //Log.i(DEBUG_TAG, imgFile.getAbsolutePath());
                Bitmap bmap = decodeBase64(image);
                //icon = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageViewRound.setImageBitmap(bmap);
            }
        }

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Initialize Database:
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users");


        // Get the profile info and initialize info
        name = user.getDisplayName();
        if (user.getEmail() == null) {
            email = "";
        } else {
            email = user.getEmail();
        }
        if (name==null){
            name = "Handsome boy";
        }
        userId = user.getUid();

        score = 0;
        phone = "";
        gender = "";



        /****************************************
         * If the user is not the Google login
         * Jason Edition
         * *************************************
         */
//        final String user_email = user.getEmail();
        final Query query = mPostReference.orderByKey().equalTo(userId);
        Log.i(TAG, "Try");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get a datasnapshot from firebase; since the return format is a jason format. we need to cast the data in a Map format
                Map<String, Map<String, Object>> user_profile = (HashMap<String, Map<String, Object>>) dataSnapshot.getValue();
                // if user profile exists, get all the profile info
                if (user_profile != null) {  //check if the user already has the
                    for (Map.Entry<String, Map<String, Object>> value : user_profile.entrySet()) {
                        // abc is reference to one user.
                        Map<String, Object> abc = value.getValue();

                        name = abc.get("name").toString();
                        gender = abc.get("gender").toString();
                        score = (long)abc.get("score");

                        user_profile_name.setText(name);
                        user_score.setText( score+"pts");
                        Map<String, Boolean> friends = (Map) abc.get("friend");
                        friendList = friends.keySet();

                    }
                    //start checking service! I have got the power!
                    startService(new Intent(ProfileActivity.this, Myservice.class));
                }
                // if null, create new user
                else {
                    // Push a new user profil
                    createProfile(userId, email, name, gender, phone, score);
                    //
                    user_profile_name.setText(name);
                    user_score.setText( score+"pts");
                    //start checking service! I have got the power!
                    startService(new Intent(ProfileActivity.this, Myservice.class));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /************************************
         * Jason Edition
         * Using query
         * ********************************
         */

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
                else{
                    user_profile_name.setText(user.getEmail());
                    Toast.makeText(getBaseContext(),user.getEmail(),Toast.LENGTH_LONG).show();
                }
            }
        };




        final AlertDialog.Builder builder = new AlertDialog.Builder(this); //build a pop up alert dialog
        builder.setTitle("Choose");
        //get strings from strings.xml file
        builder.setItems(getResources().getStringArray(R.array.picture_method), new DialogInterface.OnClickListener() {

            @Override
            // not finished. wait to be implemented.
            public void onClick(DialogInterface dialog, int position) {
                // the user clicked on colors[postition]
                if (position==0){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , PICK_PHOTO);//one can be replaced with any action code
                }

            }
        });

        //set camera icon onclick listener
        cameraIcon.setOnClickListener(new ImageButton.OnClickListener(){
            public  void onClick(View v){
                builder.show();
            }
        });

        //initialize add friend listener
        addFriend.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),SearchActivity.class);
                startActivity(intent);
            }
        });

        //set profile picture
        imageViewRound.setImageBitmap(icon);

        //initialize create event listener
        createButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getBaseContext(),CreateEventActivity.class);
                startActivity(intent);
            }

        });
        upcomingEvent.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getBaseContext(),UpcomingEventActivity.class);
                startActivity(intent);
            }
        });
        topTen.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getBaseContext(),TopTenUserActivity.class);
                startActivity(intent);
            }
        });



    }



    //sign out method
    public void signOut() {
        //start checking service! I have got the power!
        stopService(new Intent(ProfileActivity.this, Myservice.class));
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.cancel(PendingIntent.getService(this, 0, new Intent(this, Myservice.class), 0));
        auth.signOut();
        Intent signOutIntent = new Intent(this, LoginActivity.class);
        startActivity(signOutIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_friends:
                Intent intent = new Intent(getBaseContext(),SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.curfriendList:
                Intent intentFD = new Intent(getBaseContext(),FriendList.class);

                intentFD.putExtra("userid",userId);
                startActivity(intentFD);
                return true;
            case R.id.logout:
//                auth.signOut();
//                Intent signOutIntent = new Intent(this, LoginActivity.class);
//                startActivity(signOutIntent);
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.GD.onTouchEvent(event);
        Toast.makeText(getBaseContext(),"onTouch",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Toast.makeText(getBaseContext(),"onSingleTap",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Toast.makeText(getBaseContext(),"onDoubleTap",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Toast.makeText(getBaseContext(),"onDoubleTap",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Toast.makeText(getBaseContext(),"onDown",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Toast.makeText(getBaseContext(),"onSingleTapUp",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Toast.makeText(getBaseContext(),"onScroll",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Toast.makeText(getBaseContext(),"onfling",Toast.LENGTH_LONG).show();
        return false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream inputStream = getBaseContext().getContentResolver().openInputStream(data.getData());
                Bitmap bmap =  BitmapFactory.decodeStream(inputStream);
                imageViewRound.setImageBitmap(bmap);

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                //float[] rateArray = new float[lvAdapter.getCount()];
                //rateArray = lvAdapter.getRbEpisode();

                //editor.putFloat(Integer.toString(i),(float)lvAdapter.getItem(i));


                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                //String fileName = "/storage/emulated/0/Download/flower-631765_1280.jpg";
                //File file = new File(fileName);
                //Log.i(DEBUG_TAG, filePath);
                //Bitmap bmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                //imageViewRound.setImageBitmap(bmap);
                String myString = encodeTobase64(bmap);
                //Bitmap test =  decodeBase64(myString);
                //imageViewRound.setImageBitmap(test);
                editor.putString("imagepath",myString);
                //Log.i(DEBUG_TAG,filePath);
                //bmap.recycle();
                editor.commit();
                //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
            }
            catch(Exception e) {
                Log.i(DEBUG_TAG, e.toString());
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    // method for bitmap to base64
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }




    // Helper method Creating new profile
    public void createProfile(String userId, String email, String name, String gender, String phone, long score) {
        String new_user_key = userId;
        AppUser newUser = new AppUser(email,name,"",gender,phone,score);
        /*
        mPostReference.child(new_user_key).child("email").setValue(email);
        mPostReference.child(new_user_key).child("name").setValue(name);
        mPostReference.child(new_user_key).child("gender").setValue(gender);
        mPostReference.child(new_user_key).child("location").setValue("");
        mPostReference.child(new_user_key).child("phone").setValue(phone);
        mPostReference.child(new_user_key).child("score").setValue(score);*/
        mPostReference.child((new_user_key)).setValue(newUser);
        mPostReference.child(new_user_key).child("friend").child("abc").setValue(true);
        Log.i(TAG, "new user key:" + new_user_key);
    }

}
