package com.example.gargianimalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class Account_details extends AppCompatActivity {
    TextInputEditText name_acc,phone_acc, email_acc;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseRef;
    UserHelperClass userHelperClass;
    ProgressBar progressBar;
    LinearLayout details_acc;
    Button update_acc,resetPass_acc;
    DrawerLayout drawerLayout;

    SharedPreferences sharedPref;
    NavigationView navigationView;
    String flagUser;
    TextView actionBartext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        MaterialToolbar toolbar = (MaterialToolbar) findViewById(R.id.loginEmailToolbar);
        setSupportActionBar(toolbar);

        name_acc=findViewById(R.id.userName_acc);
        name_acc.setEnabled(false);
        phone_acc=findViewById(R.id.phoneNumber_acc);
        email_acc=findViewById(R.id.email_acc);
        email_acc.setEnabled(false);
        progressBar=findViewById(R.id.progress_acc);
        details_acc=findViewById(R.id.details_acc);
        update_acc=findViewById(R.id.update_acc);
        resetPass_acc=findViewById(R.id.reset_password);
        drawerLayout=findViewById(R.id.drawer_layout_acc);
        navigationView=findViewById(R.id.nav_view_acc);
        actionBartext=findViewById(R.id.actionBarText);
        actionBartext.setText("My Account");

        progressBar.setVisibility(View.VISIBLE);
        details_acc.setVisibility(View.INVISIBLE);

        // get or create SharedPreferences
        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        flagUser = sharedPref.getString("flagUser", "0");
        navigationView.getMenu().clear();
        if(flagUser.equals("0"))
        {
            navigationView.inflateMenu(R.menu.side_navigation_cust);
        }
        else
        {
            navigationView.inflateMenu(R.menu.side_navigation);
        }

        //initialize the objects
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance("https://gargi-animal-care-default-rtdb.firebaseio.com/").getReference("users/"+currentUser.getUid());

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               userHelperClass=dataSnapshot.getValue(UserHelperClass.class);

               userHelperClass.setPhonenumber(userHelperClass.getPhonenumber().substring(3));

                progressBar.setVisibility(View.INVISIBLE);
                name_acc.setText(userHelperClass.getName());
                phone_acc.setText(userHelperClass.getPhonenumber());
                email_acc.setText(userHelperClass.getEmail());
                details_acc.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Account_details.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        update_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phone_acc.getText().toString().equals(userHelperClass.getPhonenumber()))
                {
                    Toast.makeText(Account_details.this, "Already updated", Toast.LENGTH_SHORT).show();
                }
                else if(phone_acc.getText().toString().length()!=10 )
                {
                    phone_acc.setError("Valid number is required");
                    phone_acc.requestFocus();
                    return;
                }
                else if(phone_acc.getText().toString().length()==10 )
                {
                    Intent i=new Intent(Account_details.this, PhoneOTPPage.class);
                    i.putExtra("phonenumber","+91"+phone_acc.getText().toString());
                    startActivity(i);
                }

            }
        });

        resetPass_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Account_details.this, ForgotPasswordActivity.class);
                i.putExtra("flagFrom",1);
                startActivity(i);
            }
        });

    }

    private void killActivity() {
        finish();
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
        killActivity();
    }
    public void  home_navigate(MenuItem item)
    {
        Intent j = new Intent(getApplicationContext(), Home.class);
        startActivity(j);
        killActivity();
    }
    public void  account_navigate(MenuItem item)
    {
        drawerLayout.closeDrawer(GravityCompat.START);

    }
    public void  allC_navigate(MenuItem item)
    {
        Intent j = new Intent(getApplicationContext(), All_complaintsReport.class);
        startActivity(j);
        killActivity();
    }

    public void allC_feedback(MenuItem item)
    {
        Intent j=new Intent(getApplicationContext(),Feedback.class);
        startActivity(j);
        killActivity();
    }

    Boolean doubleBackToExitPressedOnce=false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}