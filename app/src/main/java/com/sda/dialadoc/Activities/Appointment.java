package com.sda.dialadoc.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sda.dialadoc.Models.AppointmentModel;
import com.sda.dialadoc.Models.SharePrefrencesModel;
import com.sda.dialadoc.Models.User;
import com.sda.dialadoc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Appointment extends AppCompatActivity {
    private TextView name,gender,age,email,phone,patientDetails;
    private TextView txtName,txtGender,txtAge,txtEmail,textPhone;
    //  private String userName,userGender,userAge,userEmail,userPhone;
    private EditText details;
    private User doctor;
    private Button submitBtn;
    private ProgressBar progressBar;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private User currentUser;
    private String currentUserId;

    //variables
    private AppointmentModel appointmentModel;
    private User user;
    private String currentUserType;

    //Layouts
    private LinearLayout patientDetail;
    private LinearLayout patientInput;

    private AppointmentModel patientModel;
    private String appointmentStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        //Getting Data
        initUi();
        getIntentData();
        gettinCurrentUserData();

        //Handle Submit button
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                //submitBtn.setVisibility(View.GONE);
                if(appointmentStatus.equals("chat"))
                {
                    //For patient
                    if(currentUserType.equals("Patient")) {
                        //Declaring intent
                        Intent intent = new Intent(Appointment.this, ChatActivity.class);
                        //sending patient Data into next activity
                        intent.putExtra("user_id", doctor.getUserId());
                        intent.putExtra("user_name", doctor.getName());
                        startActivity(intent);
                    }
                    //For Doctor
                    else if(currentUserType.equals("Doctor"))
                    {
                        //Sending Doctor Data
                        Intent intent = new Intent(Appointment.this, ChatActivity.class);
                        intent.putExtra("user_id", patientModel.getUserId());
                        intent.putExtra("user_name", patientModel.getName());
                        startActivity(intent);
                    }

                }
                //Appointment generated
                else {
                    fixingAppointment();
                }
            }
        });
    }
    //Getting User Data
    private void gettinCurrentUserData() {

        //if user is patient
        if(currentUserType.equals("Patient")) {

            //getting Realtime database from firebase
            rootRef.child("appointment").child(doctor.getUserId()).child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                //Declaring and initalize Appointment
                AppointmentModel temp=new AppointmentModel();
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //use app data For testing on key
                    Log.d("appdata",snapshot.getKey());
                    progressBar.setVisibility(View.GONE);
                    submitBtn.setVisibility(View.VISIBLE);
                    //Appointment type
                    temp = snapshot.getValue(AppointmentModel.class);
                    if(temp!=null){
                        //if Status is pending
                        if (temp.getStatus().equals("pending")) {
                            Log.d("appcheck:p",snapshot.getKey());

                            //Functionality for Submit button
                            progressBar.setVisibility(View.GONE);
                            submitBtn.setEnabled(false);
                            submitBtn.setText("Pending");
                            submitBtn.setVisibility(View.VISIBLE);
                            appointmentStatus="pending";
                        }
                        //patient request accepted
                        else if (temp.getStatus().equals("accept")) {
                            Log.d("appcheck:a",snapshot.getKey());
                            progressBar.setVisibility(View.GONE);
                            submitBtn.setText("Start Chat");
                            appointmentStatus="chat";
                            //submitBtn.setVisibility(View.GONE);
                            submitBtn.setVisibility(View.VISIBLE);

                        } else {

                            Log.d("appcheck:n",snapshot.getKey());
                            progressBar.setVisibility(View.GONE);
                            submitBtn.setVisibility(View.VISIBLE);
                            submitBtn.setText("Submit Appointment");

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        //Doctor login
        else if(currentUserType.equals("Doctor")){
            if(patientModel.getStatus().equals("pending"))
            {
                progressBar.setVisibility(View.GONE);
                submitBtn.setText("Accept Appointment");
                submitBtn.setVisibility(View.VISIBLE);
            }
            //patient request accepted
            else if(patientModel.getStatus().equals("accept"))
            {
                progressBar.setVisibility(View.GONE);
                submitBtn.setText("Start Chat");
                appointmentStatus="chat";
                submitBtn.setVisibility(View.VISIBLE);
            }


        }

    }

    //Appointment Fixing between Doctor and patient
    private void fixingAppointment() {

        //for patient
        if(currentUserType.equals("Patient")) {

            appointmentModel.setUser(user);



            String userDetail = details.getText().toString();
            if (TextUtils.isEmpty(userDetail)) {
                userDetail = "";
            }
            //Set time
            appointmentModel.setDetails(userDetail);
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

            String formattedDate = df.format(c);

            int ampm = (int) Calendar.getInstance().get(Calendar.AM_PM);

            df = new SimpleDateFormat("HH:mm:ss");

            String time = df.format(c);

            if (ampm == Calendar.AM) {
                time += " AM";
            } else {
                time += " PM";
            }

            //Dialouge Box
            appointmentModel.setDate(formattedDate);
            appointmentModel.setTime(time);
            appointmentModel.setStatus("pending");
            rootRef.child("appointment").child(doctor.getUserId()).child(currentUserId).setValue(appointmentModel).addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            submitBtn.setText("Pending");
                            submitBtn.setEnabled(false);
                            submitBtn.setVisibility(View.VISIBLE);
                        }
                    }
                    //Failuer listen
            ).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Appointment.this, "Some Server Issue try again", Toast.LENGTH_SHORT).show();
                }
            });

        }else if(currentUserType.equals("Doctor"))
        {



            if(patientModel.getStatus().equals("pending")) {
                rootRef.child("appointment").child(currentUserId).child(patientModel.getUserId()).child("status").setValue("accept").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        submitBtn.setText("Start Chat");
                        submitBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(Appointment.this, "Chat", Toast.LENGTH_SHORT).show();
                        appointmentStatus="chat";
                    }
                });
            }else {
                Toast.makeText(Appointment.this, "Chat", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void getIntentData() {
        if(currentUserType.equals("Patient")) {

            patientInput.setVisibility(View.VISIBLE);
            patientDetail.setVisibility(View.GONE);

            if (getIntent().getSerializableExtra("doctor") != null) {
                doctor = (User) getIntent().getSerializableExtra("doctor");
            }


            //Get and Displaying
            name.setText(doctor.getName());
            gender.setText(doctor.getGender());
            age.setText(doctor.getAge());
            email.setText(doctor.getEmail());
            phone.setText(doctor.getPhoneNum());

        }else {

            patientInput.setVisibility(View.GONE);
            patientDetail.setVisibility(View.VISIBLE);
            //declaring variable
            AppointmentModel doctor=new AppointmentModel();
            if (getIntent().getSerializableExtra("appo") != null) {
                doctor = (AppointmentModel) getIntent().getSerializableExtra("appo");
            }
            patientModel=doctor;

            //Get And Display Data
            name.setText(doctor.getName());
            gender.setText(doctor.getGender());
            age.setText(doctor.getAge());
            email.setText(doctor.getEmail());
            phone.setText(doctor.getPhoneNum());
            patientDetails.setText(doctor.getDetails());

            txtName.setText("Patient Name");
            txtAge.setText("Patient Age");
            txtGender.setText("Patient Gender");
            txtEmail.setText("Patient Email");
            textPhone.setText("Patient Phone");
            details.setHint("Enter Note For Patient");

        }




    }

    private void initUi() {

        //ToolBar Casting
        Toolbar toolbar=(Toolbar)findViewById(R.id.appointment_toolbar);

        //Setting ToolBar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Appointment Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Casting the Variables
        name=(TextView)findViewById(R.id.doc_name);
        gender=(TextView)findViewById(R.id.doc_gender);
        age=(TextView)findViewById(R.id.doc_age);
        email=(TextView)findViewById(R.id.doc_email);
        phone=(TextView)findViewById(R.id.doc_phone);
        details=(EditText)findViewById(R.id.appoint_details);
        submitBtn=(Button)findViewById(R.id.fix_appointment_btn);

        //Fire Base Initialize
        mAuth=FirebaseAuth.getInstance();
        //Getting Fire base refrence
        rootRef= FirebaseDatabase.getInstance().getReference();
        currentUserId=mAuth.getCurrentUser().getUid();
        //Casting progress bar
        progressBar=(ProgressBar)findViewById(R.id.appoint_progress);
        user=new User();
        user=SharePrefrencesModel.getCurrentUser(Appointment.this);

        //Declare and initialize variable
        appointmentModel=new AppointmentModel();
        currentUserType=SharePrefrencesModel.getDefaults("usertype",Appointment.this);
        Log.d("appotype",currentUserType);


        //Casting the layout
        patientDetail=(LinearLayout) findViewById(R.id.textviewlinearlayout);
        patientInput=(LinearLayout)findViewById(R.id.patientinputdetail);

        //casting textview
        patientDetails=(TextView)findViewById(R.id.patientDetails);

        txtName=(TextView)findViewById(R.id.txtName);
        txtAge=(TextView)findViewById(R.id.txtage);
        txtEmail=(TextView)findViewById(R.id.txtmail);
        txtGender=(TextView)findViewById(R.id.txtgender);
        textPhone=(TextView)findViewById(R.id.textphone);
        patientModel=new AppointmentModel();

        appointmentStatus="";






    }
}
