package com.example.eaglegeocamera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ShowPictureActivity extends AppCompatActivity {
    public ItemDAO dao;
    public List<Item> list = null;
    public double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);

        Intent intent = getIntent();
        String URI = intent.getStringExtra("URI Value");

        ImageView imageView = findViewById(R.id.itemImage);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        requestOptions.error(R.drawable.ic_launcher_background);
        requestOptions.fitCenter();
        requestOptions.centerCrop();

        Glide.with(this)
                .load(Uri.parse(URI)) // Uri of the picture
                .apply(requestOptions)
                .into(imageView);


    }

}
