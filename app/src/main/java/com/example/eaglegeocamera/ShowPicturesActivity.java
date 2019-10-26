package com.example.eaglegeocamera;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShowPicturesActivity extends AppCompatActivity {
    public ItemDAO dao;
    public List<Item> list = null;
    public double longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView recyclerView;
        RecyclerView.LayoutManager layoutManager;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pictures);
        recyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("Latitude", 0);
        longitude = intent.getDoubleExtra("Longitude", 0);

        dao = ((BaseApplication) getApplication()).getDatabase().itemDAO(); //retrieve the database information from the BaseApplication class to use the Data Access Object variable DAO
        list =  dao.findByLongitudeLatitude(longitude, latitude);

        //Retrieve the position of the item clicked in the recycleView and send it to startItemEditActivity to show the respective item information
        ItemAdapter itemAdapter = new ItemAdapter(this, list, position -> startItemEditActivity(position));
        recyclerView.setAdapter(itemAdapter); //Update the recyclerView
    }

    private void startItemEditActivity(int position) {

    }
}
