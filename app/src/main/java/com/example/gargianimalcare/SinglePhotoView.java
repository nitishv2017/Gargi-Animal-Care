package com.example.gargianimalcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class SinglePhotoView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_photo_view);

        ImageView photo=findViewById(R.id.photoSingle);
        ImageView backBtn=findViewById(R.id.backbtn_singlePhoto);
        Intent i=getIntent();
        String url=i.getStringExtra("URL");
        Picasso.get().load(url).into(photo);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}