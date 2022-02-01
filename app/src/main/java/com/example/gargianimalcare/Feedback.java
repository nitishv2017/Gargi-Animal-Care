package com.example.gargianimalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Feedback extends AppCompatActivity {

    EditText fdback;
    Button submit;
    SharedPreferences sharedPref;
    NavigationView navigationView;
    String flagUser="";
    TextView feedback_text;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        fdback=findViewById(R.id.feedback);
        submit=findViewById(R.id.submitFeedback);
        feedback_text=findViewById(R.id.actionBarText);
        feedback_text.setText("Feedback");
        drawerLayout=findViewById(R.id.drawer_layout_feedback);

        navigationView=findViewById(R.id.nav_view_feedback);
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback=fdback.getText().toString().trim();

                if(feedback.isEmpty())
                {
                    fdback.setError("Feedback cant be empty");
                    fdback.requestFocus();
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Regarding App Feedback");
                intent.putExtra(Intent.EXTRA_TEXT, feedback);
                    startActivity(intent);

            }
        });
    }

    boolean doubleBackToExitPressedOnce=false;
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
        Intent j = new Intent(getApplicationContext(), Account_details.class);
        startActivity(j);
        killActivity();

    }
    public void  allC_navigate(MenuItem item)
    {
        Intent j=new Intent(getApplicationContext(),All_complaintsReport.class);
        startActivity(j);
        killActivity();
    }

    public void allC_feedback(MenuItem item)
    {
        drawerLayout.closeDrawer(GravityCompat.START);

    }
}