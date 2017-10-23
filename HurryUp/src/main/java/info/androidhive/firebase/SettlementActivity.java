package info.androidhive.firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettlementActivity extends AppCompatActivity {

    private TextView newScore;
    private Button goBackProfile;
    private DatabaseReference mPostReference;
    private DatabaseReference eventPostReference;
    private String eventKey;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        newScore = (TextView) findViewById(R.id.NewScore);
        goBackProfile = (Button) findViewById(R.id.goBackProfile);
        Intent eventIntent = getIntent();
        Bundle data = eventIntent.getExtras();
        eventKey = data.getString("eventKey");
        auth = FirebaseAuth.getInstance();


        // delete score. Note that this activity will always be called. But if user arraied on time, his score would already be added in GeofenceTransitionservice

        // Getting the current user profile and his child score value
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("score");
        eventPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("event").child(eventKey);

        Query scoreQuery = mPostReference;

        scoreQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Add the bonus to user

                Object scoreObject = dataSnapshot.getValue();
                long score =(long) scoreObject;

                score -= 5;
                newScore.setText(Long.toString(score));
                mPostReference.setValue(score);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        eventPostReference.removeValue();

        goBackProfile.setOnClickListener(new Button.OnClickListener(){
            public  void onClick(View v){
                Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(i);
            }
        });
    }


}
