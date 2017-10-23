package info.androidhive.firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TopTenUserActivity extends AppCompatActivity {
    private DatabaseReference mPostReference;
    private ListView ListViewTopTen;
    private ArrayList<String> returnUserNameList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten_user);
        //list vire to show top ten users
        ListViewTopTen = (ListView)findViewById(R.id.ListViewTopTen);
        //Initialize Database:
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users");
        // query for getting reference to users with top ten scores, but in RESERVE ORDER <<<
        final Query query = mPostReference.orderByChild("score").limitToLast(10);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // note here dataSnapshot is reference of EVERY USER
                //the returned user is in reverse order, so we add to the front to reverse back >>>>>>
                returnUserNameList.add(0,dataSnapshot.child("name").getValue().toString()+":   "+dataSnapshot.child("score").getValue().toString());
                ArrayAdapter itemsAdapter = new ArrayAdapter(TopTenUserActivity.this, android.R.layout.simple_expandable_list_item_1, returnUserNameList);
                ListViewTopTen.setAdapter(itemsAdapter);

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
        });




    }
}
