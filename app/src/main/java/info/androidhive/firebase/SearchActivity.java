package info.androidhive.firebase;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import info.androidhive.firebase.AppUser;

public class SearchActivity extends AppCompatActivity {
    private DatabaseReference mPostReference,UserDatabaseReference;
    //UI
    private EditText inputText;
    private Button btnSearch;
    private RecyclerView UserList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String Email = user.getEmail();
        final String userId = user.getUid();
        //final Map<String, Boolean> friends = (Map) user.get("friend");
        UserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId);
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users");
        inputText = (EditText)findViewById(R.id.editText2);
        btnSearch =(Button)findViewById(R.id.button2);
        UserList = (RecyclerView) findViewById(R.id.userList);
        UserList.setHasFixedSize(true);
        UserList.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                final String name=inputText.getText().toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(name).child("name");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name1 = dataSnapshot.getValue(String.class);
                        Toast.makeText(getApplicationContext(), "Read "+name+"'s name just for once: "+name1, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        final FirebaseRecyclerAdapter<AppUser, UserListViewHolder> adapter =
                new FirebaseRecyclerAdapter<AppUser, UserListViewHolder>(
                        AppUser.class,
                        android.R.layout.two_line_list_item,
                        UserListViewHolder.class,
                        mPostReference
                ) {



                    @Override
                    protected void populateViewHolder(UserListViewHolder viewHolder, AppUser user1, final int position) {
                            final String UserName = user1.getName();
                            viewHolder.mText.setText(UserName);
                            viewHolder.mView.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {

                                    UserDatabaseReference.child("friend").child(UserName).setValue(true);
                                    //Toast.makeText(getApplicationContext(), a, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(),UserName+":true"+Email, Toast.LENGTH_SHORT).show();
                                    //adapter.getRef(position).removeValue();
                                }
                            });
                    }
                };
        UserList.setAdapter(adapter);


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

    public boolean onCreateOptionsMenu(Menu menu) {
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
                searchFriend();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void searchFriend(){
        final String name=inputText.getText().toString();
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(name).child("name");
        mPostReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name1 = dataSnapshot.getValue(String.class);
                Toast.makeText(getApplicationContext(), name1, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
