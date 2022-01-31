package com.example.gargianimalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class ComplaintDetails extends AppCompatActivity implements OnMapReadyCallback {

    ImageView backbtn;
    complaintsHelperClass details;
    Fragment mapView;
    GoogleMap gmap;
    //1- allcomplaint se aaye
    int fromwhichactivityYoucame=0;

    String ComplainID;



    RelativeLayout fullView;
    ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, ref1;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    ValueEventListener valueEventListener;
    CardView photobtn;
    String flagUser="";
    SharedPreferences sharedPref;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_details);


        Intent i= getIntent();
        fromwhichactivityYoucame=i.getIntExtra("from",0);
        ComplainID =i.getStringExtra("complainID");


        // get or create SharedPreferences
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        flagUser = sharedPref.getString("flagUser", "0");



        progressBar=findViewById(R.id.progress_details);
        fullView=findViewById(R.id.fullView_details);

        photobtn=findViewById(R.id.photosOpen_det);
        photobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent (ComplaintDetails.this, PhotosActivity.class);
                i.putExtra("ComplainID", ComplainID);
                startActivity(i);
            }
        });
        //initialize the objects
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://gargi-animal-care-default-rtdb.firebaseio.com/");

        generateList(new CustomerDatafetch() {
            @Override
            public void onAvailableCallback() {
                progressBar.setVisibility(View.GONE);
                fullView.setVisibility(View.VISIBLE);

                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                setLocation(new LatLng(details.getLatitude(),details.getLongitude()));
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

                TextView complainID, complainedBy, phone, subject, desc, address, status, employeeName, empphone, timeOfComplain,solutiontime, solutiondesc, labeltime, labeldesc;
                LinearLayout employee_view;
                ImageView icon_status;
                CardView statusView,photosClickedCard, callEmployee;
                complainID=findViewById(R.id.complaindID_det);
                complainedBy=findViewById(R.id.cust_name_det);
                phone=findViewById(R.id.phone_det);
                subject=findViewById(R.id.subject_det);
                desc=findViewById(R.id.description_det);
                address=findViewById(R.id.address_det);
                status=findViewById(R.id.status_det);
                employeeName=findViewById(R.id.emplName_det);
                empphone=findViewById(R.id.emplPhone_det);
                timeOfComplain=findViewById(R.id.timeofcom_det);
                solutiontime=findViewById(R.id.timesolve_det);
                solutiondesc=findViewById(R.id.desc_sol_det);
                employee_view=findViewById(R.id.status_employee_det);
                statusView=findViewById(R.id.status_card);
                photosClickedCard=findViewById(R.id.photosOpen_det);
                icon_status=findViewById(R.id.icon_status_det);
                callEmployee=findViewById(R.id.call_emp_det);
                labeldesc=findViewById(R.id.desc_Label);
                labeltime=findViewById(R.id.solTime_Label);

                complainID.setText(details.getComplainID());
                complainedBy.setText(details.getCustomerName());
                phone.setText(details.getPhoneNumber());
                subject.setText(details.getSubject());
                desc.setText(details.getDescription());
                address.setText(details.getAddress());
                status.setText(details.getStatus());
                employeeName.setText(details.getEmployeeName());
                empphone.setText(details.getEmployeePhone());
                Date date = new Date ();
                date.setTime((long)details.getTimeOfComplain()*1000);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                timeOfComplain.setText(dateFormat.format(date));
                date.setTime((long)details.getTimeofCompletion()*1000);
                solutiontime.setText(dateFormat.format(date));
                solutiondesc.setText(details.getDescriptionofSolution());



                if(details.getStatus().equals("in process"))
                {
                    solutiondesc.setVisibility(View.GONE);
                    solutiontime.setVisibility(View.GONE);
                    labeldesc.setVisibility(View.GONE);
                    labeltime.setVisibility(View.GONE);
                }

                phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setClipboard(getApplicationContext(),phone.getText().toString());
                    }
                });
                empphone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setClipboard(getApplicationContext(),empphone.getText().toString());
                    }
                });

                if(details.getStatus().equals("pending"))
                {
                    statusView.setCardBackgroundColor(getResources().getColor(R.color.status_pending));
                    employee_view.setVisibility(View.GONE);
                    photosClickedCard.setVisibility(View.GONE);
                    icon_status.setImageResource(R.drawable.ic_baseline_pending1_outline_24);
                }
                else if(details.getStatus().equals("in process"))
                {
                    statusView.setCardBackgroundColor(getResources().getColor(R.color.status_process));
                    photosClickedCard.setVisibility(View.GONE);
                    icon_status.setImageResource(R.drawable.ic_baseline_process_24);
                }
                else
                {
                    statusView.setCardBackgroundColor(getResources().getColor(R.color.status_completed));
                    icon_status.setImageResource(R.drawable.ic_baseline_done_24);
                }

                callEmployee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", details.getEmployeePhone(), null));
                        startActivity(intent);
                    }
                });


            }
        });

        Query query= firebaseDatabase.getReference("complaints/"+ComplainID);
        query.addListenerForSingleValueEvent(valueEventListener);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.det_toolbar);
        setSupportActionBar(toolbar);
        backbtn=findViewById(R.id.backbtn_details);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }
    FusedLocationProviderClient client;
    SupportMapFragment mapFragment;
    private void setLocation(LatLng latLng) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps_details);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(details.getAddress());
                googleMap.clear();
                googleMap.addMarker(markerOptions);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            }
        });

    }

    public void Edit_details(MenuItem item) {
        Intent i=new Intent(ComplaintDetails.this, Update_Complaint.class);
        i.putExtra("complainID",details.getComplainID());
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(fromwhichactivityYoucame==1) {

                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.edit_details_menu, menu);

            return true;
        }
        return false;
    }


    @Override
    public void onMapReady(@NonNull  GoogleMap googleMap) {
        gmap = googleMap;

        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gmap.setMyLocationEnabled(true);

        gmap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(fromwhichactivityYoucame==1) {
            startActivity(new Intent(ComplaintDetails.this, All_complaintsReport.class));
            finish();
        }
        else {
            startActivity(new Intent(ComplaintDetails.this, Home.class));
            finish();
        }
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Phone number copied", Toast.LENGTH_SHORT).show();
    }

    private void generateList(CustomerDatafetch callback) {
        fullView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                        details =dataSnapshot.getValue(complaintsHelperClass.class);

                    callback.onAvailableCallback();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(ComplaintDetails.this, "Please reopen app, error connecting database", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error getting database"); //Don't ignore potential errors!
            }
        };


    }
}