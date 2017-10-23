package info.androidhive.firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FriendList extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private Set<String> friendList;
    private String[] friendListView;
    private ListView listViewFriend;
    private TextView noFriend;
    private Button gotoSearchFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userid");
        listViewFriend=(ListView)findViewById(R.id.listViewFriend);
        noFriend = (TextView) findViewById(R.id.noFriend);
        gotoSearchFriend = (Button) findViewById(R.id.gotoSearchFriend);
        //Toast.makeText(getBaseContext(),userId,Toast.LENGTH_SHORT).show();
        //Initialize Database:
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users");

        // get user reference with specific userid.
        final Query query = mPostReference.orderByKey().equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get a datasnapshot from firebase; since the return format is a jason format. we need to cast the data in a Map format
                Map<String, Map<String, Object>> user_profile = (HashMap<String, Map<String, Object>>) dataSnapshot.getValue();
                // if user profile exists, get all the profile info
                if (user_profile != null) {  //check if the user already has the
                    for (Map.Entry<String, Map<String, Object>> value : user_profile.entrySet()) {
                        Map<String, Object> abc = value.getValue();

                        Map<String, Boolean> friends = (Map) abc.get("friend");
                        friendList = friends.keySet();
                        if(friendList.size()>0) {
                            friendListView = friendList.toArray(new String[friendList.size()]);
                            ArrayAdapter itemsAdapter = new ArrayAdapter(FriendList.this, android.R.layout.simple_expandable_list_item_1, friendListView);
                            listViewFriend.setAdapter(itemsAdapter);
                        }else{
                            noFriend.setText("You don't have friend yet, come and join the community!");

                        }

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        gotoSearchFriend.setOnClickListener(new ImageButton.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),SearchActivity.class);
                startActivity(intent);
            }
        });

    }
}
