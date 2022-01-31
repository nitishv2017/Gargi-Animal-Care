package com.example.gargianimalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class PhotosActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private ImageAdapter mAdapter;

    String ComplainID;

    private ProgressBar progressBar;

    private DatabaseReference mDatabaseRef;
    private ArrayList<Image_Model> array_images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        ImageView backBtn=findViewById(R.id.backbtn_photos);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent i=getIntent();
        ComplainID=i.getStringExtra("ComplainID");

        recyclerView = findViewById(R.id.recyclerView_photos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progress_photos);
        progressBar.setVisibility(View.VISIBLE);

        array_images = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance("https://gargi-animal-care-default-rtdb.firebaseio.com/").getReference("Photos/"+ComplainID);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Image_Model image_model = postSnapshot.getValue(Image_Model.class);
                    Log.i(TAG, "onDataChange: "+image_model.getmName());
                    array_images.add(image_model);
                }

                mAdapter = new ImageAdapter(PhotosActivity.this, array_images);

                recyclerView.setAdapter(mAdapter);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PhotosActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });


    }
}