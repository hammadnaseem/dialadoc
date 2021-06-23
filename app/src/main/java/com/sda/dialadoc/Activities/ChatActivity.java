package com.sda.dialadoc.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sda.dialadoc.Adapters.MessageAdapter;
import com.sda.dialadoc.Models.Message;
import com.sda.dialadoc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    //declare variables
    private String mChatUser;
    private String userName;
    private Toolbar mToolbar;
    private ActionBar actionBar;
    private LayoutInflater layoutInflater;

    private RecyclerView mMessageList;
    //declare and initialize Array list
    private ArrayList<Message> messageList = new ArrayList <>();
    private MessageAdapter messageAdapter;
    // private SwipeRefreshLayout mSwipeRefreshLayout;

    // private TextView mTittleView, lastSeenView;
    private CircleImageView mProfileImage;

    private ImageButton camera_btn, send_btn;
    private EditText msg_enter;
    private String message;
    //Firebase database
    private DatabaseReference myref;
    private DatabaseReference mRootRef;

    //User Id
    private String currentUserId;

    private final static int TOTAL_NO_ITEM_COUNT=10;
    private int mCurrentPage=1;

    //Solution

    private  int itemPos=0;
    private String mLastkey="";
    private String mPrevKey="";

    //Button
    private ImageButton cameraBtn;

    //Camera length
    private static final int CAMERA_REQUEST = 1888;
    //Storage
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private StorageReference storageRef;
    private String imageMessageUrl;
    private String push_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //getting Data from ChatList
        mChatUser = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");


        //Casting Button
        camera_btn=(ImageButton)findViewById(R.id.camera_btn);

        //storage initialize and give firebase refrence
        storageRef= FirebaseStorage.getInstance().getReference();

        imageMessageUrl="";

        //using variable for test
        Log.d("chat_user",mChatUser);

        //storage initialize and give firebase refrence
        myref = FirebaseDatabase.getInstance().getReference().child("users");
        mRootRef = FirebaseDatabase.getInstance().getReference();

        //Getting user id
        currentUserId = FirebaseAuth.getInstance().getUid().toString();

        mMessageList = (RecyclerView) findViewById(R.id.messages_list);
        //  mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);



        messageAdapter = new MessageAdapter(ChatActivity.this, messageList,currentUserId,message);
        //  mMessageList.setHasFixedSize(true);
        mMessageList.setAdapter(messageAdapter);
        mMessageList.setLayoutManager(new LinearLayoutManager(this));
        mMessageList.getRecycledViewPool().setMaxRecycledViews(0, 0);
        loadMessage();
        //casting toolbar
        mToolbar = (Toolbar) findViewById(R.id.chat_bar_layout);


        //setting toolbar
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(userName);

        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Casting the button
        send_btn = findViewById(R.id.send_msg_btn);
        camera_btn = findViewById(R.id.camera_btn);


        msg_enter = findViewById(R.id.messagge_Edit_text);


        //  mTittleView.setText(userName);

       /* myref.child(mChatUser).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String imr_url = dataSnapshot.child("thumb_img").getValue().toString();
                        Picasso.get().load(imr_url).into(mProfileImage);


                        Boolean check = (Boolean) dataSnapshot.child("online").getValue();
                        if (check != null) {
                            if (check == true) {
                                lastSeenView.setText("online");
                            } else {
                                String last_Seen = dataSnapshot.child("lastseen").getValue().toString();
                                GetTimeAgo getTime = new GetTimeAgo();
                                Long last_time = Long.parseLong(last_Seen);
                                String last_time_seen = getTime.getTimeAgo(last_time, getApplicationContext());
                                lastSeenView.setText(last_time_seen);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );*/


        //Getting Child of parent child and add click function
        mRootRef.child("Chat").child(currentUserId).addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.hasChild(mChatUser)) {


                            Map chatAddMap = new HashMap();
                            chatAddMap.put("seen", false);
                            chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                            Map chatUserMap = new HashMap();
                            chatUserMap.put("Chat/" + currentUserId + "/" + mChatUser, chatAddMap);
                            chatUserMap.put("Chat/" + mChatUser + "/" + currentUserId, chatAddMap);

                            mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.d("ChatLog", databaseError.getMessage().toString());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

        //handle send_button
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Clicked", "done");
                imageMessageUrl="";
                DatabaseReference user_message_push = mRootRef.child("message").child(currentUserId).child(mChatUser).push();
                push_id = user_message_push.getKey();
                sendMessage();
            }
        });

      /*  mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mCurrentPage++;
                        itemPos=0;
                        loadMoreMessage();
                    }
                }
        );*/

        cameraButton();

    }

    //Camera button function
    private void cameraButton() {
        //handle camera button
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        //change Screen
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            }
        });






    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //Show message
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

                //change Screen
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                //Show message
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }


    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] biosToByte = baos.toByteArray();

            DatabaseReference user_message_push = mRootRef.child("message").child(currentUserId).child(mChatUser).push();
            push_id = user_message_push.getKey();



            UploadTask uploadTask = storageRef.child(currentUserId).child(push_id).putBytes(biosToByte);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    storageRef.child(currentUserId).child(push_id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            Log.d("imageurlchec",uri.toString());
                            imageMessageUrl=uri.toString();
                            sendMessage();



                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


                }
            });
        }
    }

    private void loadMoreMessage() {
        //Access child from child
        DatabaseReference messageRef=mRootRef.child("message").child(currentUserId).child(mChatUser);
        Query messageQueerey=messageRef.orderByKey().endAt(mLastkey).limitToLast(10);
        messageQueerey.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message message = dataSnapshot.getValue(Message.class);


                        String key = dataSnapshot.getKey();



                        if(!mPrevKey.equals(dataSnapshot.getKey()))
                        {
                            messageList.add(itemPos++,message);
                        }
                        else
                        {
                            mPrevKey=mLastkey;
                        }
                        if(itemPos==1)
                        {
                            mLastkey=dataSnapshot.getKey();
                        }

                        messageAdapter.notifyDataSetChanged();

                        //  mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    private void loadMessage() {
        Log.d("loadmessage", "load");

        //path of Database
        DatabaseReference messageRef=mRootRef.child("message").child(currentUserId).child(mChatUser);

        //handle variable message ref
        messageRef.addChildEventListener(
                new ChildEventListener() {
                    String sender_key;
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Message message = dataSnapshot.getValue(Message.class);

                        //use variable for test
                        Log.d("frommessage", "onChildAdded:"+dataSnapshot);
                        String key = dataSnapshot.getKey();
                        Log.d("singlemessagekey", key);
                        Log.d("frommessage",message.getMessage());
                        Log.d("frommessage",message.getUrl());
                        itemPos++;
                        if(itemPos==1)
                        {
                            mLastkey=dataSnapshot.getKey();
                            mPrevKey=dataSnapshot.getKey();
                        }
                        messageList.add(message);


                        messageAdapter.notifyDataSetChanged();
                        mMessageList.scrollToPosition(messageList.size()-1);
                        // mSwipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
//        Log.d("customadapterm",messageList.get(0).getMessage());

    }

    private void sendMessage() {
        String message = msg_enter.getText().toString();
        if (!TextUtils.isEmpty(message) || !imageMessageUrl.equals("") )

        {
            Log.d("Click", "Entered");
            String current_user_ref = "message/" + currentUserId + "/" + mChatUser;
            String chat_user_ref = "message/" + mChatUser + "/" + currentUserId;

            Log.d("messagesize",String.valueOf(messageList.size()));



            // Message single_message = new Message(message, "text", false, 0);

            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("timestamp", ServerValue.TIMESTAMP);
            messageMap.put("from",currentUserId);
            messageMap.put("url",imageMessageUrl);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
            msg_enter.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });

        }
    }
}
