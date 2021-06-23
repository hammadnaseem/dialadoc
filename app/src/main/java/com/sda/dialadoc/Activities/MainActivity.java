package com.sda.dialadoc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.sda.dialadoc.Adapters.DoctorListCustomAdapter;
import com.sda.dialadoc.Adapters.PatientListCustomAdapter;
import com.sda.dialadoc.Models.AppointmentModel;
import com.sda.dialadoc.Models.SharePrefrencesModel;
import com.sda.dialadoc.Models.User;
import com.sda.dialadoc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //All Ui
    private RecyclerView mainRecyclerView;

    //Data Structures
    private ArrayList<User>  doctorList;
    private ArrayList<AppointmentModel> appointmentModelsList;

    private User currentUser;


    //Firebase
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        initAllUi();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




    }

    //Function
    private void initAllUi() {

        //casting variables
        mainRecyclerView=(RecyclerView)findViewById(R.id.main_screen_recyclerview);
        //initialize variables
        doctorList=new ArrayList<User>();
        appointmentModelsList=new ArrayList<AppointmentModel>();
        mAuth=FirebaseAuth.getInstance();
        rootRef=FirebaseDatabase.getInstance().getReference();
        //get user id
        currentUserId=mAuth.getCurrentUser().getUid();

        userType= SharePrefrencesModel.getDefaults("usertype",MainActivity.this);
        //use variable for testing
        Log.d("usertyping",userType);
        currentUser=new User();

        if(getIntent().getSerializableExtra("user")!=null)
        {
            currentUser=(User) getIntent().getSerializableExtra("user");
        }


        //Calling function
        firebaseAuthListener();
        initRecyclerView();

    }



    private void initRecyclerView() {
        //use variable For testing
        Log.d("checkusinh",userType);


        //for patient login
        if(userType.equals("Patient")) {
            Log.d("checkusinh:p",userType);

            //declare and initialize adapter
            final DoctorListCustomAdapter doctorListCustomAdapter = new DoctorListCustomAdapter(MainActivity.this, doctorList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            mainRecyclerView.setLayoutManager(layoutManager);
            mainRecyclerView.setAdapter(doctorListCustomAdapter);

            //access child and add listner
            rootRef.child("users").addValueEventListener(new ValueEventListener() {
                User doctor = new User();

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        Log.d("usertpe", String.valueOf(dataSnapshot.getChildrenCount()));
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            doctor = dataSnapshot1.getValue(User.class);
                            //For testing
                            Log.d("usertpe", doctor.getUserType());
                            //for doctor login
                            if (doctor.getUserType().equals("Doctor")) {
                                doctorList.add(doctor);
                                doctorListCustomAdapter.notifyDataSetChanged();

                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }
        //For doctor
        else if(userType.equals("Doctor")){

            TextView listTittle=(TextView)findViewById(R.id.list_tittle);
            listTittle.setText("Patient List");

            //Declare and initialize CustomAdapter
            final PatientListCustomAdapter patientListCustomAdapter = new PatientListCustomAdapter(MainActivity.this, appointmentModelsList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            mainRecyclerView.setLayoutManager(layoutManager);
            mainRecyclerView.setAdapter(patientListCustomAdapter);

            //accessing child and add listner
            rootRef.child("appointment").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                //declare and initialize Appointment model
                AppointmentModel appointmentModel=new AppointmentModel();
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //for testing
                    Log.d("datacheck",dataSnapshot.getKey());

                    for (DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        appointmentModel=snapshot.getValue(AppointmentModel.class);
                        if(appointmentModel!=null)
                        {
                            appointmentModelsList.add(appointmentModel);
                            patientListCustomAdapter.notifyDataSetChanged();
                        }else {
                            //show message
                            Toast.makeText(MainActivity.this,"No Appointment Found",Toast.LENGTH_SHORT).show();
                        }
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }
    }




    @Override
    public void onBackPressed() {
        //casting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            Toast.makeText(MainActivity.this,"Home",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_chat) {
            Toast.makeText(MainActivity.this,"Chat",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,ChatListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {

            //initialize Firebaseauth with function
            FirebaseAuth.getInstance().signOut();
            //change screen
            Intent intent=new Intent(MainActivity.this,SplashScreen.class);
            startActivity(intent);
            finish();



        } else if (id == R.id.nav_share) {
            //show message
            Toast.makeText(MainActivity.this,"Share",Toast.LENGTH_SHORT).show();

        }

        //casting
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Function
    private void firebaseAuthListener() {
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //getting user type
                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                if(firebaseUser==null)
                {
                    //change screen
                    Intent intent=new Intent(MainActivity.this,SplashScreen.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

}
