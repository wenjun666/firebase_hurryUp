package info.androidhive.firebase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {
    private ImageView cameraIcon;
    private ImageView imageViewRound;
    private Button createButton;
    private ImageView addFriend;
    private CharSequence method[] = new CharSequence[] {"Gallery", "Take a Photo"};
    static final int PICK_PHOTO = 1;
    static final int CAPTURE_PHOTO =2;
    private Button signOut;
    private FirebaseAuth auth;
    private DatabaseReference mPostReference;
    private FirebaseAuth.AuthStateListener authListener;
    private TextView user_profile_name, user_score;
    private static final String TAG = ProfileActivity.class.getSimpleName();
    private String email,name,gender,score,userId;
    private Set<String> friendList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_profile_name = (TextView)findViewById(R.id.user_profile_name);
        user_score = (TextView) findViewById(R.id.user_profile_short_bio);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Initialize Database:
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users");

        name = user.getDisplayName();
        score = "0";
        if (user.getEmail() == null) {
            email = "";
        } else {
            email = user.getEmail();
        }
        userId = user.getUid();

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
                        //Log.i(TAG,value.getValue().get("phone"));
                        Map<String, Object> abc = value.getValue();
                        //Log.i(TAG,abc);
//                        email = abc.get("email").toString();
                        name = abc.get("name").toString();
                        gender = abc.get("gender").toString();
                        score = abc.get("score").toString();

                        user_profile_name.setText(name);
                        user_score.setText("score: " + score);
                        Map<String, Boolean> friends = (Map) abc.get("friend");
                        friendList = friends.keySet();

                        //Toast.makeText(ProfileActivity.this, email + name + gender + score + friendList, Toast.LENGTH_LONG).show();


                        //Log.i(TAG, "blablablbala");
                    }
                }
                // if null, create new user
                else {
                    // Push a new user profil
                    String new_user_key = userId;
                    mPostReference.child(new_user_key).child("email").setValue(email);
                    mPostReference.child(new_user_key).child("name").setValue(name);
                    mPostReference.child(new_user_key).child("gender").setValue("");
                    mPostReference.child(new_user_key).child("location").setValue("");
                    mPostReference.child(new_user_key).child("phone").setValue("");
                    mPostReference.child(new_user_key).child("score").setValue("");
                    mPostReference.child(new_user_key).child("friend").child("abc").setValue(true);
                    Log.i(TAG, "new user key:" + new_user_key);
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

        imageViewRound = (ImageView)findViewById(R.id.imageViewRound);  //profile picture
        addFriend = (ImageView)findViewById(R.id.add_friend);   // add friend icon
        createButton = (Button)findViewById(R.id.createButton); // Click to create event
        cameraIcon = (ImageView)findViewById(R.id.camera_icon); //Click to choose a picture or take a picture using camera
        signOut = (Button) findViewById(R.id.sign_out);

        Bitmap icon =  BitmapFactory.decodeResource(getResources(),R.drawable.background);


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
                if (position==1){
                    Intent pickPhoto = new Intent(Intent.ACTION_CAMERA_BUTTON,
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

        signOut.setOnClickListener(new Button.OnClickListener(){
            public  void onClick(View v){
                signOut();
            }

        });

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }
}
