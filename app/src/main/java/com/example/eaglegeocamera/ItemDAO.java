package com.example.eaglegeocamera;

import androidx.room.*;

import java.util.Date;
import java.util.List;


@Dao
public interface ItemDAO {

    //Called to return all the items in the table item. It is called when the app is updating the remote database to decide whether synchronization is needed or not.
    @Query("SELECT * FROM item")
    List<Item> getAll();

    //Called to return the item using the id to fetch all the item details.
    @Query("SELECT * FROM item WHERE iid == :id")
    Item findByID(long id);

/*
    //Called to update a specific item attributes with new values using the ID (primary key)
    @Query("UPDATE item SET item_title = :itemTitle , item_description = :itemDescription," +
            " item_date_and_time = :itemDateAndTime, item_status = :itemStatus WHERE iid = :id;")
    void updateItem(long id, String itemTitle, String itemDescription,
                    Date itemDateAndTime, boolean itemStatus);
 */


    //Called to insert the new created item in the item table located in the remote database
    @Insert
    void insertAll(Item... items);

    //Called to delete a specific item from the item table located in the remote database
    @Delete
    void delete(Item item);
}