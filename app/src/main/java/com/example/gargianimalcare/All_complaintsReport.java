package com.example.gargianimalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class All_complaintsReport extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView actionBar_text;
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_complaints_report);

        drawerLayout=findViewById(R.id.drawer_layout_allc);

        actionBar_text=findViewById(R.id.actionBarText);
        actionBar_text.setText("All Complaints Report");

        floatingActionButton=findViewById(R.id.filter_allc);




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
        Intent j = new Intent(getApplicationContext(), Home.class);
        startActivity(j);
        finish();
    }
    public void  account_navigate(MenuItem item)
    {
        Intent j = new Intent(getApplicationContext(), All_complaintsReport.class);
        startActivity(j);
        finish();

    }
    public void  allC_navigate(MenuItem item)
    {

        drawerLayout.closeDrawer(GravityCompat.START);
    }
}