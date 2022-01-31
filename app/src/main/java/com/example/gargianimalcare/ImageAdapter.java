package com.example.gargianimalcare;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private ArrayList<Image_Model> array_images;

    public ImageAdapter(Context context, ArrayList<Image_Model> uploads) {
        mContext = context;
        array_images = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Image_Model uploadCurrent = array_images.get(position);
        Picasso.get().load(uploadCurrent.getmImageUrl()).into(holder.imageView);
        holder.imageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(mContext, SinglePhotoView.class);
                i.putExtra("URL",uploadCurrent.getmImageUrl());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return array_images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CardView imageItem;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageItem=itemView.findViewById(R.id.imageItem);
            imageView = itemView.findViewById(R.id.image_photos);
        }
    }
}
