package com.example.gargianimalcare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class Home extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<complaintsHelperClass> array_complains;
    FloatingActionButton flbtn;
    DrawerLayout drawerLayout;
    ProgressBar progressBar;
    //
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, ref1;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    ValueEventListener valueEventListener;
    ImageView statusIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        //initialize the objects
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://gargi-animal-care-default-rtdb.firebaseio.com/");

        checkGPS();

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.loginEmailToolbar);
        setSupportActionBar(toolbar);


        drawerLayout=findViewById(R.id.drawer_layout);
        progressBar=findViewById(R.id.progress_home);


         recyclerView= findViewById(R.id.allComplaintsView);
         array_complains=new ArrayList<complaintsHelperClass>();
         generateList(new CustomerDatafetch() {
             @Override
             public void onAvailableCallback() {


                 // Set layout manager to position the items
                 recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                 Collections.sort(array_complains, new Comparator<complaintsHelperClass>() {
                     @Override
                     public int compare(complaintsHelperClass t1, complaintsHelperClass t2) {
                         long c=t2.getTimeOfComplain()-t1.getTimeOfComplain();
                         if(c>0)
                         {
                             return 1;
                         }
                         return -1;
                     }
                 });
                 // Create adapter passing in the sample user data
                 customerComplainAdapter adapter = new customerComplainAdapter(array_complains,Home.this);
                 // Attach the adapter to the recyclerview to populate items
                 recyclerView.setAdapter(adapter);
                 progressBar.setVisibility(View.GONE);
                 recyclerView.setVisibility(View.VISIBLE);
             }
         });

        Query query= firebaseDatabase.getReference("complaints").orderByChild("userID").equalTo(currentUser.getUid());
        query.addListenerForSingleValueEvent(valueEventListener);

         flbtn=findViewById(R.id.addComplain);

         flbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i= new Intent(Home.this,MapsActivity.class);
                 startActivity(i);
             }
         });






    }

    public void  ClickedDrawer(View v)
    {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void  logout_navigate(MenuItem item)
    {
        FirebaseAuth.getInstance().signOut();
        Intent j = new Intent(getApplicationContext(), LoginViaPhone.class);
        startActivity(j);
        finish();
    }
    public void  home_navigate(MenuItem item)
    {
        drawerLayout.closeDrawer(GravityCompat.START);
    }
    public void  account_navigate(MenuItem item)
    {
        Intent j = new Intent(getApplicationContext(), All_complaintsReport.class);
        startActivity(j);
        finish();
    }
    public void  allC_navigate(MenuItem item)
    {
        Intent j = new Intent(getApplicationContext(), All_complaintsReport.class);
        startActivity(j);
        finish();
    }




    private void generateList(CustomerDatafetch callback) {

        reference = firebaseDatabase.getReference("complaints");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for( DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {
                        complaintsHelperClass c=dataSnapshot1.getValue(complaintsHelperClass.class);
                        array_complains.add(c);
                    }

                    callback.onAvailableCallback();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(Home.this, "Please reopen app, error connecting database", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error getting database"); //Don't ignore potential errors!
            }
        };
//        reference.addListenerForSingleValueEvent(valueEventListener);

    }


    LocationRequest locationRequest;

    private void checkGPS() {
        locationRequest= LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);
        Task<LocationSettingsResponse> locationSettingsResponseTask= LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response= task.getResult(ApiException.class);
                }
                catch (ApiException e)
                {
                    if(e.getStatusCode()== LocationSettingsStatusCodes.RESOLUTION_REQUIRED)
                    {
                        ResolvableApiException resolvableApiException= (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(Home.this, 101);
                        } catch(IntentSender.SendIntentException sendIntentException)
                        {
                            sendIntentException.printStackTrace();
                        }
                    }
                    if(e.getStatusCode()==LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE)
                    {
                        Toast.makeText(Home.this, "Settings not available", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101)
        {
            if(resultCode==RESULT_OK)
            {
                Toast.makeText(this, "Location is enabled", Toast.LENGTH_SHORT).show();
            }
            if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "Denied Location", Toast.LENGTH_SHORT).show();
            }
        }
    }



}