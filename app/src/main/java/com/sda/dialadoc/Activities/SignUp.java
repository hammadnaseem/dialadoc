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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sda.dialadoc.Models.SharePrefrencesModel;
import com.sda.dialadoc.Models.User;
import com.sda.dialadoc.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    //declare different variable
    private static final String TAG = "signupTag";
    private EditText name,email,pass,phoneNumber,age;
    private Button signup_up,login_btn;
    private String userName,userEmail,userPass,phone,gender,userAge;
    private RadioGroup radioGroup;
    private RadioGroup userTypeRadioGroup;
    private RadioButton userTypeRadioButton;


    private RadioButton genderRadioButton;

    // Dialog myProgressBar;
    private ProgressBar progressBar;

    //Firebase
    FirebaseAuth mAuth;
    DatabaseReference myRootRef;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //calling function
        initAllUi();
        buttonClickListeners();

    }
    //functionality of buttonclick
    private void buttonClickListeners() {
        //handle signup button
        signup_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  progressBar.setVisibility(View.VISIBLE);
                signUp();
            }
        });

        //handle login button
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change screen
                Intent intent=new Intent(SignUp.this,Login.class);

                startActivity(intent);
            }
        });

    }

    private void signUp() {

        //change data type of variables
        userName=name.getText().toString().trim();
        userEmail=email.getText().toString().trim();
        userPass=pass.getText().toString();
        phone=phoneNumber.getText().toString().trim();
        userAge=age.getText().toString().trim();



        //Fields Authentication
        if(TextUtils.isEmpty(userName))
        {


            name.setError("Entered Your Name");
        }
        else if(TextUtils.isEmpty(userEmail))
        {


            email.setError("Entered Your Email");
        }else if(TextUtils.isEmpty(userPass))
        {

            pass.setError("Entered Your Password");
        }
        else if(TextUtils.isEmpty(phone))
        {


            phoneNumber.setError("Entered Your Confirm Password");
        }else if(TextUtils.isEmpty(userAge)){
            age.setError("Enter your Age");
        }

        else if(radioGroup.getCheckedRadioButtonId()==-1){
            Toast.makeText(SignUp.this,"Select Gender",Toast.LENGTH_SHORT).show();
        }else
        {
            //setting progress bar
            progressBar.setVisibility(View.VISIBLE);
            signup_up.setVisibility(View.INVISIBLE);
            //setting variable after changing its Datatype
            user.setEmail(userEmail);
            user.setName(userName);
            user.setPhoneNum(phone);
            user.setAge(userAge);
            int selectedId = radioGroup.getCheckedRadioButtonId();


            // find the radiobutton by returned id
            genderRadioButton = findViewById(selectedId);



            //changing Datatype
            user.setGender(genderRadioButton.getText().toString());
            selectedId=userTypeRadioGroup.getCheckedRadioButtonId();

            //casting variables
            userTypeRadioButton=findViewById(selectedId);
            //change datatype
            user.setUserType(userTypeRadioButton.getText().toString());

            //check authentication by using email and password
            mAuth.createUserWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                //access id the user that login
                                String currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                user.setUserId(currentUserId);
                                myRootRef.child("users").child(currentUserId).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        SharePrefrencesModel.setDefaults("userType",user.getUserType(),SignUp.this);
                                        //show message
                                        Toast.makeText(SignUp.this,"Sign Up Success",Toast.LENGTH_SHORT).show();
                                        //check login user
                                        SharePrefrencesModel.setCurrentUser(user,SignUp.this);
                                        //change screen
                                        Intent intent=new Intent(SignUp.this,MainActivity.class);
                                        //sending data
                                        intent.putExtra("user",user);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                        //handle failuer result
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG,e.toString());
                                            }
                                        });

                            } else {

                                // If sign in fails, display a message to the user.
                                progressBar.setVisibility(View.INVISIBLE);
                                signup_up.setVisibility(View.VISIBLE);
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                //show message
                                Toast.makeText(SignUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }


                        }
                    });


        }

    }

    //functionality of function
    private void initAllUi() {

        //casting
        name=findViewById(R.id.signup_username);
        email=findViewById(R.id.signup_email);
        pass=findViewById(R.id.signp_pass);
        phoneNumber=findViewById(R.id.signup_phone_number);
        age=findViewById(R.id.signp_user_Age);

        progressBar=findViewById(R.id.signup_progressbar);
        signup_up=findViewById(R.id.signup_btn);
        login_btn=findViewById(R.id.signup_btn_login);
        radioGroup=findViewById(R.id.gender_radio_group);
        //initialize mauth
        mAuth=FirebaseAuth.getInstance();
        //getting path
        myRootRef= FirebaseDatabase.getInstance().getReference();
        //casting
        userTypeRadioGroup=findViewById(R.id.user_type_radio_group);
        //initialize function
        user=new User();
    }
}
