package info.androidhive.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class AddFriendActivity extends AppCompatActivity {
    private DatabaseReference mPostReference,UserDatabaseReference, FriendDatabaseReference;
    //UI Design

    private Button btnFinishEvent;
    private RecyclerView friendList;

    private static final String TAG = AddFriendActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String Email = user.getEmail();
        final String userId = user.getUid();

        //Get the event key
        Intent eventIntent = getIntent();
        final String eventId = eventIntent.getExtras().getString("eventKey");
//        mPostReferenceEvent = FirebaseDatabase.getInstance().getReference()
//                .child("events").child(eventId);


        //final Map<String, Boolean> friends = (Map) user.get("friend");
        UserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId);
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users");

        btnFinishEvent = (Button)findViewById(R.id.btnFinishEvent);
        friendList = (RecyclerView) findViewById(R.id.friendList);
        friendList.setHasFixedSize(true);
        friendList.setLayoutManager(new LinearLayoutManager(this));




        btnFinishEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Query checkEventQuery = mPostReference.child(userId).child("event").orderByKey().equalTo(eventId);
                checkEventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> hasEvent = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (hasEvent == null) {
                            mPostReference.child(userId).child("event").child(eventId).setValue(true);
                            backToProfile();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "You have already in this event", Toast.LENGTH_SHORT).show();
                            backToProfile();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        final FirebaseRecyclerAdapter<AppUser, SearchActivity.UserListViewHolder> adapter =
                new FirebaseRecyclerAdapter<AppUser, SearchActivity.UserListViewHolder>(
                        AppUser.class,
                        android.R.layout.two_line_list_item,
                        SearchActivity.UserListViewHolder.class,
                        mPostReference
                ) {



                    @Override
                    protected void populateViewHolder(SearchActivity.UserListViewHolder viewHolder, AppUser friend, final int position) {
                        final String friendName = friend.getName();
                        viewHolder.mText.setText(friendName);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {

//                                UserDatabaseReference.child("friend").child(friendName).setValue(true);
//                                //Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
//                                Toast.makeText(getApplicationContext(),UserName+":true"+Email, Toast.LENGTH_SHORT).show();
//                                //adapter.getRef(position).removeValue();

                                // Get the user you click
                                Query friendQuery = mPostReference.orderByChild("name").equalTo(friendName);
                                friendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get the selected friend profile
                                        Map<String, Map<String, Object>> friend_profile = (HashMap<String, Map<String, Object>>) dataSnapshot.getValue();
                                        Log.i(TAG, "Friend Event List");

                                        // Get the selected firend ID
                                        final String friendId = friend_profile.keySet().iterator().next();

                                        // Check if the invited friend already has this event by using eventID
                                        Query checkEventQuery = mPostReference.child(friendId).child("event").orderByKey().equalTo(eventId);
                                        checkEventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Map<String, Object> hasEvent = (HashMap<String, Object>) dataSnapshot.getValue();
                                                if (hasEvent == null) {

                                                    mPostReference.child(friendId).child("event").child(eventId).setValue(true);
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(), "You have already invited this friend", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                        });
                    }
                };
        friendList.setAdapter(adapter);


    }
    public static class UserListViewHolder
            extends RecyclerView.ViewHolder{
        View mView;
        static TextView mText;

        public UserListViewHolder(View v){
            super(v);
            mText=(TextView) v.findViewById(android.R.id.text1);
            mView = v;
        }

    }

/*    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search:
                //searchFriend();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/



    public void backToProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}
