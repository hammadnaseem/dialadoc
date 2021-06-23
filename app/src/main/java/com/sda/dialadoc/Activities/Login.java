package com.sda.dialadoc.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sda.dialadoc.Models.SharePrefrencesModel;
import com.sda.dialadoc.Models.User;
import com.sda.dialadoc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    //Declare variables
    private EditText email,password;
    private Button login_btn,alreadyAccount;
    private TextView userTypeTextView;
    private String mail,pass;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference myRootRef;

    private User user;

    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        //Declare Function
        initAllUi();
        buttonClickListeners();
    }

    private void buttonClickListeners() {
        //handle login button
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //function
                login();
            }
        });
        //handle button Already account
        alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change screen
                Intent intent=new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });

    }

    //function
    private void login() {
        //getting data
        mail=email.getText().toString().trim();
        pass=password.getText().toString().trim();
        //check authentication
        if(TextUtils.isEmpty(mail))
        {
            // Toast.makeText(MainActivity.this,"Enter Your Email",Toast.LENGTH_SHORT).show();
            //show progress bar
            progressBar.setVisibility(View.GONE);
            email.setError("Enter Your Email");
        }
        //if the password field is empty
        else if(TextUtils.isEmpty(pass))
        {
            // Toast.makeText(MainActivity.this,"Enter You Password",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            password.setError("Enter Your Password");
        }else{

            progressBar.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.INVISIBLE);
            login_btn.setEnabled(false);
            //Check authentication by using email and password
            mAuth.signInWithEmailAndPassword(mail, pass)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                String currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();

                                user=new User();

                                myRootRef.child("users").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        //place data in datasnapshot that we can show
                                        user=dataSnapshot.getValue(User.class);
                                        if(!user.getUserType().equals(userType))
                                        {
                                            //declare function
                                            mAuth.signOut();
                                            //show message
                                            Toast.makeText(Login.this,"This is not "+userType+" login details",Toast.LENGTH_SHORT).show();
                                            //setting progress Dialoug
                                            progressBar.setVisibility(View.INVISIBLE);
                                            login_btn.setVisibility(View.VISIBLE);
                                            login_btn.setEnabled(true);

                                        }else
                                        {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            login_btn.setVisibility(View.VISIBLE);

                                            SharePrefrencesModel.setCurrentUser(user,Login.this);
                                            SharePrefrencesModel.setDefaults("usertype",userType,Login.this);

                                            //changing screen
                                            Intent intent=new Intent(Login.this,MainActivity.class);
                                            intent.putExtra("user",user);
                                            startActivity(intent);
                                            finish();

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else {

                                //setting progree bar
                                progressBar.setVisibility(View.GONE);
                                login_btn.setEnabled(true);
                                login_btn.setVisibility(View.VISIBLE);
                                //show message
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();


                            }


                        }
                    });


        }

    }

    //Functionality of Function
    private void initAllUi() {
        //casting the variables
        email=findViewById(R.id.login_email);
        password=findViewById(R.id.login_pass);
        login_btn=findViewById(R.id.login_btn);
        progressBar=findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);
        alreadyAccount=findViewById(R.id.login_signup_btn);
        //initialize Firebase
        mAuth=FirebaseAuth.getInstance();
        userTypeTextView=findViewById(R.id.usertype_login);
        myRootRef= FirebaseDatabase.getInstance().getReference();

        if(getIntent()!=null)
        {
            userType=getIntent().getStringExtra("userType");
            Log.d("useType",userType);
            userTypeTextView.setText(userType+" Login");
        }
        //  userType= SharePrefrencesModel.getDefaults("usertype",Login.this);

    }
}
