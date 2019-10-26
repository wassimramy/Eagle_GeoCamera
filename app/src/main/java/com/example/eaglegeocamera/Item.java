package com.example.eaglegeocamera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.room.*;

import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

@Entity
public class Item  {

    private static final AtomicLong LAST_TIME_MS = new AtomicLong();

        //Item constructor
    public Item(String iid, String itemPath, double itemLongitude, double itemLatitude){
        this.iid = iid;
        this.itemPath = itemPath;
        this.itemDateAndTime = createUniqueTimeStamp();
        this.itemLongitude = itemLongitude;
        this.itemLatitude = itemLatitude;

    }



    /*
        //Item constructor
    public Item(String iid, String itemPath){
        this.iid = iid;
        this.itemPath = itemPath;
        itemDateAndTime = createUniqueTimeStamp();
    }
     */


    private long createUniqueTimeStamp() {
        long now = System.currentTimeMillis();
        while(true) {
            long lastTime = LAST_TIME_MS.get();
            if (lastTime >= now)
                now = lastTime+1;
            if (LAST_TIME_MS.compareAndSet(lastTime, now))
                return now;
        }
    }

    //The primary key of Item is long to store the time stamp when item is created
    //iid is not auto generated for easier synchronization between local and remote databases
    @PrimaryKey
    @NonNull
    public String iid;

    //Store itemPath attribute value in item_value column
    @ColumnInfo(name = "item_path")
    public String itemPath;

    //Store itemDateAndTime attribute value in item_date_and_time column
    @ColumnInfo(name = "item_date_and_time")
    public Long itemDateAndTime;

    //Store itemLongitude attribute value in item_status column
    @ColumnInfo(name = "item_longitude")
    public double itemLongitude;

    //Store itemLatitude attribute value in item_status column
    @ColumnInfo(name = "item_latitude")
    public double itemLatitude;
}