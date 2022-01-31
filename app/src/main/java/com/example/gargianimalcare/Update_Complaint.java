package com.example.gargianimalcare;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class Update_Complaint extends AppCompatActivity {

    StorageReference ref;
    final Calendar myCalendar= Calendar.getInstance();

    TimePickerDialog timePickerDialog;
    RelativeLayout uploadPhotosBtn;
    String ComplainID="";
    LinearLayout FullView, extradetails_upd;
    ProgressBar progressBar;
    TextInputEditText empName_upd, phone_upd, time_upd, date_upd,desc_upd;
    Button upd_btn;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Task<Void> photoRef_database;
    UserHelperClass userHelperClass;
    ArrayList<Image_Model>imagehelper=new ArrayList<Image_Model>();

    Date setDate;

    RadioGroup radioGroup;
    RadioButton inprocess, completed;
    // Create lanucher variable inside onAttach or onCreate or global
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        // your operation....


                            Uri selectedImageUri = data.getData();

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();

                            //here you can choose quality factor in third parameter(ex. i choosen 25)
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] fileInBytes = baos.toByteArray();

                        AlertDialog.Builder builder = new AlertDialog.Builder(Update_Complaint.this);

                        // Set the message show for the Alert time
                        builder.setMessage("Are you sure you want to upload?");

                        // Set Alert Title
                        builder.setTitle("Confirm");

                        // Set Cancelable false
                        // for when the user clicks on the outside
                        // the Dialog Box then it will remain show
                        builder.setCancelable(false);

                        // Set the positive button with yes name
                        // OnClickListener method is use of
                        // DialogInterface interface.

                        builder
                                .setPositiveButton(
                                        "Yes",
                                        new DialogInterface
                                                .OnClickListener() {


                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which)
                                            {

                                                StorageReference photoref = ref.child(ComplainID).child(selectedImageUri.getLastPathSegment());

                                                //here i am uploading
                                                photoref.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        FullView.setVisibility(View.VISIBLE);
                                                        progressBar.setVisibility(View.INVISIBLE);
//                                                        Integer s=(imagehelper.size()+1);
//                                                        imagehelper.add(new Image_Model(s.toString(),taskSnapshot.));
                                                        // Download file From Firebase Storage
                                                        photoref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri downloadPhotoUrl) {
                                                                //Now play with downloadPhotoUrl
                                                                //Store data into Firebase Realtime Database

                                                                Image_Model image_model = new Image_Model( ComplainID, selectedImageUri.getLastPathSegment() ,downloadPhotoUrl.toString());
                                                                photoRef_database=firebaseDatabase.getReference().child("Photos/"+ComplainID+"/"+selectedImageUri.getLastPathSegment()).setValue(image_model);
                                                            }
                                                        });

                                                        Toast.makeText(Update_Complaint.this, "Photo uploaded" , Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull  Exception e) {
                                                        FullView.setVisibility(View.VISIBLE);
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        Toast.makeText(Update_Complaint.this, "Error, Please try again!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        });

                        // Set the Negative button with No name
                        // OnClickListener method is use
                        // of DialogInterface interface.
                        builder
                                .setNegativeButton(
                                        "No",
                                        new DialogInterface
                                                .OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which)
                                            {
                                                FullView.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                // If user click no
                                                // then dialog box is canceled.
                                                dialog.cancel();
                                            }
                                        });

                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();

                        // Show the Alert Dialog box
                        alertDialog.show();




                    }
                    else
                    {
                        FullView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_complaint);

        imagehelper.clear();

        ImageView backBtn=findViewById(R.id.backbtn_upd);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //initialize the objects
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance("https://gargi-animal-care-default-rtdb.firebaseio.com/");

        Intent i=getIntent();
        ComplainID=i.getStringExtra("complainID");

        progressBar=findViewById(R.id.progress_upd);
        FullView=findViewById(R.id.upd_complain_view);

        empName_upd=findViewById(R.id.empname_update);
        phone_upd=findViewById(R.id.phone_updcomplain);
        date_upd=findViewById(R.id.date_upd);
        time_upd=findViewById(R.id.time_upd);
        desc_upd=findViewById(R.id.description_sol_upd);
        upd_btn=findViewById(R.id.update_btn);
        radioGroup=findViewById(R.id.status_group_upd);
        inprocess=findViewById(R.id.inprocess_upd);
        completed=findViewById(R.id.completed_upd);
        extradetails_upd=findViewById(R.id.extradetails_upd);

        progressBar.setVisibility(View.VISIBLE);
        FullView.setVisibility(View.INVISIBLE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i)
                {
                    case R.id.inprocess_upd:
                        extradetails_upd.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.completed_upd:
                        extradetails_upd.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        if(inprocess.isChecked())
        {
            extradetails_upd.setVisibility(View.INVISIBLE);
        }
        databaseReference = firebaseDatabase.getReference("users/"+currentUser.getUid());
        // calling add value event listener method
        // for getting the values from database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // this method is call to get the realtime
                // updates in the data.
                // this method is called when the data is
                // changed in our Firebase console.
                // below line is for getting the data from
                // snapshot of our database.
               userHelperClass = snapshot.getValue(UserHelperClass.class);
               progressBar.setVisibility(View.INVISIBLE);
                FullView.setVisibility(View.VISIBLE);
                empName_upd.setText(userHelperClass.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // calling on cancelled method when we receive
                // any error or we are not able to get the data.
                Toast.makeText(Update_Complaint.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                empName_upd.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                FullView.setVisibility(View.VISIBLE);
            }
        });


       empName_upd.setEnabled(false);
       phone_upd.setText(currentUser.getPhoneNumber());


        timePickerinitialise();
        datePickerinitialise();


        uploadPhotosBtn=findViewById(R.id.uploadPhotosBtn);
        ref = FirebaseStorage.getInstance("gs://gargi-animal-care.appspot.com").getReference();

        upd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number =phone_upd.getText().toString();
                String Desc=desc_upd.getText().toString();
                String time = time_upd.getText().toString();
                String date= date_upd.getText().toString();
                if(number.isEmpty() || number.length() < 10)
                {
                    phone_upd.setError("Valid number is required");
                    phone_upd.requestFocus();
                    return;
                }
                DatabaseReference ref_update=firebaseDatabase.getReference("complaints");
                if(completed.isChecked())
                {
                    if(Desc.isEmpty())
                    {
                        desc_upd.setError("Add some description");
                        desc_upd.requestFocus();
                        return;
                    }
                    if(time.isEmpty())
                    {
                        time_upd.setError("Add time");
                        time_upd.requestFocus();
                        return;
                    }
                    if(date.isEmpty())
                    {
                        date_upd.setError("Add Date");
                        date_upd.requestFocus();
                        return;
                    }



                    ref_update.child(ComplainID.trim()+"/descriptionofSolution").setValue(desc_upd.getText().toString());
                    setDate= myCalendar.getTime();
                    long epochTime=setDate.getTime();
                    ref_update.child(ComplainID.trim()+"/timeofCompletion").setValue(epochTime/1000);
                    ref_update.child(ComplainID.trim()+"/status").setValue("completed");

                }
                else
                ref_update.child(ComplainID.trim()+"/status").setValue("in process");

                ref_update.child(ComplainID.trim()+"/employeeID").setValue(currentUser.getUid());
                ref_update.child(ComplainID.trim()+"/employeeName").setValue(userHelperClass.getName());
                ref_update.child(ComplainID.trim()+"/employeePhone").setValue(userHelperClass.getPhonenumber());

                Intent i=new Intent(Update_Complaint.this, ComplaintDetails.class);
                i.putExtra("complainID",ComplainID);
                i.putExtra("from",1);
                startActivity(i);
                finish();
            }
        });

        uploadPhotosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                FullView.setVisibility(View.INVISIBLE);
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent=new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                launchSomeActivity.launch(intent);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                FullView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        });




    }



    void timePickerinitialise()
    {
        TimePickerDialog.OnTimeSetListener time= new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                myCalendar.set(Calendar.HOUR,i);
                myCalendar.set(Calendar.MINUTE,i1);
                String myFormat="hh:mm a";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
                time_upd.setText(dateFormat.format(myCalendar.getTime()));
            }
        };
        time_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

               timePickerDialog =  new TimePickerDialog(Update_Complaint.this, time, hour, minute, false);
               timePickerDialog.setTitle("Select Time");
               timePickerDialog.show();
            }
        });
    }

    void datePickerinitialise()
    {


        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);

                String myFormat="dd/MM/yyyy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
                date_upd.setText(dateFormat.format(myCalendar.getTime()));
            }
        };
        date_upd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Update_Complaint.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }
    private void alertDialogAndRegister() {

    }
}