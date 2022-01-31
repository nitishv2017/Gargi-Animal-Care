package com.example.gargianimalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class AddComplains extends AppCompatActivity {
    Button registerbtn;
    AlertDialog dialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference, ref1;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    String address;
    double latitude, longitude;
    complaintsHelperClass helperClass;
    UserHelperClass user_details;
    EditText description_add, subject_add, name_add, phone_add, address_add;
    private String ComplaintID;
    ProgressBar progressBar_add;
    LinearLayout fullView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_complains);

        Intent i= getIntent();
         address= i.getStringExtra("address");
         address=address.replaceAll("\\r\\n|\\r|\\n", " ");
         latitude=i.getDoubleExtra("latitude",0);
         longitude=i.getDoubleExtra("longitude",0);

        ImageView backBtn=findViewById(R.id.backbtn_add);
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
        generateUserHelper();

        //autofill
        name_add=findViewById(R.id.name_addcomplain);


        phone_add=findViewById(R.id.phone_addcomplain);

        address_add=findViewById(R.id.address_add);
        subject_add= findViewById(R.id.subject_add);
        description_add=findViewById(R.id.description_add);

        progressBar_add=findViewById(R.id.progress_add);
        fullView=findViewById(R.id.add_complain_view);


        registerbtn=findViewById(R.id.registerComplain);

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = phone_add.getText().toString().trim();
                String name = name_add.getText().toString().trim();
                String address= address_add.getText().toString().trim();
                String desc=description_add.getText().toString().trim();
                String subject=subject_add.getText().toString().trim();

                if(number.isEmpty() || number.length() < 10)
                {
                    phone_add.setError("Valid number is required");
                    phone_add.requestFocus();
                    return;
                }
                if(name.isEmpty())
                {
                    name_add.setError("Name is required");
                    name_add.requestFocus();
                    return;
                }
                if(address.isEmpty())
                {
                    address_add.setError("Address is required");
                    address_add.requestFocus();
                    return;
                }
                if(subject.isEmpty())
                {
                    subject_add.setError("Subject is required");
                    subject_add.requestFocus();
                    return;
                }
                if(desc.isEmpty())
                {
                    description_add.setError("Description is required");
                    description_add.requestFocus();
                    return;
                }


                alertDialogAndRegister();

            }
        });


    }

    private void alertDialogAndRegister() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddComplains.this);

        // Set the message show for the Alert time
        builder.setMessage("Do you want to Register complain?");

        // Set Alert Title
        builder.setTitle("Register");

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
                                progressBar_add.setVisibility(View.VISIBLE);
                                fullView.setVisibility(View.INVISIBLE);
                                generateComplaintIDandSave();

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

    private void generateUserHelper()
    {
        //helperclass generate

        ref1=firebaseDatabase.getReference("users/"+currentUser.getUid());
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                user_details= snapshot.getValue(UserHelperClass.class);
                name_add.setText(user_details.getName());
                phone_add.setText(user_details.getPhonenumber());
                address_add.setText(address);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                System.out.println("The read failed: " + error.getCode());

            }
        });
    }

    private void generateComplaintIDandSave() {
        reference = firebaseDatabase.getReference("complaints");
        ComplaintID= "GAC";
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long count = dataSnapshot.getChildrenCount();
                count++;
                String temp=count.toString();
                String tempId="";
                for(int i=0;i<6-temp.length();i++)
                {
                    ComplaintID+= '0';
                }

                ComplaintID+=temp;

                Log.d("TAG", "id= " + ComplaintID);

                long currentTime = System.currentTimeMillis() / 1000L;


                helperClass= new complaintsHelperClass(address,ComplaintID,user_details.getName(),description_add.getText().toString(),"null","null","null","null",latitude,longitude,user_details.getPhonenumber(),"pending",subject_add.getText().toString(),currentTime,(long)0,currentUser.getUid());

                reference.child(ComplaintID).setValue(helperClass);
                Toast.makeText(AddComplains.this, "Your complain is registered.", Toast.LENGTH_SHORT).show();
                //go to home
                startActivity(new Intent(AddComplains.this,Home.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(AddComplains.this, "Please try again, error connecting database", Toast.LENGTH_SHORT).show();
                fullView.setVisibility(View.VISIBLE);
                progressBar_add.setVisibility(View.INVISIBLE);

                Log.d(TAG, "error getting database"); //Don't ignore potential errors!
            }
        };
        reference.addListenerForSingleValueEvent(valueEventListener);




    }




}