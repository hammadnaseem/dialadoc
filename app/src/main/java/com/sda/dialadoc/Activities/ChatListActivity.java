package com.sda.dialadoc.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.sda.dialadoc.Models.ChatAdapter;
import com.sda.dialadoc.Models.Message;
import com.sda.dialadoc.Models.User;
import com.sda.dialadoc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {
    //declare variables
    private RecyclerView mConvList;
    //declare different dtabase
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mLastMessage;
    //declaring Fire base
    private FirebaseAuth mAuth;
    private String date;

    private String mCurrent_user_id;
    //declaring different adapter and variables
    private ChatAdapter friendsAdapter;
    private ArrayList<User> friendsArrayList;
    public static ArrayList<String> list_user_friend_key;
    private View mMainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        //calling function
        init();

        //handle data base
        mMessageDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String user_id ="";
                user_id  = dataSnapshot.getKey().toString();
                mLastMessage.child(user_id).limitToLast(1).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Message messages = dataSnapshot.getValue(Message.class);
                        if(messages.getType().equals("text")){
                            date = messages.getMessage();
                        }else {
                            date = "image";
                        }
                        Log.d("last_message",messages.getMessage());

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
                //handle database and add listner
                mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    User temp=new User();
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       /* String name = dataSnapshot.child("name").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();
                        String userOnline = "online";*/
                        //getting value from child user
                        temp=dataSnapshot.getValue(User.class);
                        if(list_user_friend_key.contains(dataSnapshot.getKey().toString())){

                            friendsAdapter.notifyDataSetChanged();
                        }else {
                            list_user_friend_key.add(dataSnapshot.getKey().toString());
                            friendsArrayList.add(temp);
                            friendsAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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

    public void init(){
        //casting different variables
        mConvList = (RecyclerView)findViewById(R.id.chat_list);
        Toolbar toolbar=(Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize array list
        friendsArrayList = new ArrayList<>();
        list_user_friend_key = new ArrayList<>();
        friendsAdapter = new ChatAdapter(ChatListActivity.this,friendsArrayList);
        mAuth = FirebaseAuth.getInstance();
        //getting
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        //initialize and give path
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersDatabase.keepSynced(true);
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("message").child(mCurrent_user_id);
        mLastMessage = FirebaseDatabase.getInstance().getReference().child("message").child(mCurrent_user_id);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);
        mConvList.setAdapter(friendsAdapter);

    }
}
