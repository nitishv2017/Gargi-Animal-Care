package com.example.gargianimalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class ComplaintDetails extends AppCompatActivity implements OnMapReadyCallback {

    ImageView backbtn;
    complaintsHelperClass details;
    Fragment mapView;
    GoogleMap gmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_details);

        Intent i= getIntent();
        details= i.getExtras().getParcelable("object");

        backbtn=findViewById(R.id.backbtn_details);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Log.i(TAG, "onCreate:gg= "+ details.getLatitude()+ " "+ details.latitude);

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

        TextView complainID, complainedBy, phone, subject, desc, address, status, employeeName, empphone, timeOfComplain,solutiontime, solutiondesc;
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
}