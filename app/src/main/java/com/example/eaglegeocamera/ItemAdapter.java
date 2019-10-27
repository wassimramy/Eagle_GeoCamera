package com.example.eaglegeocamera;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context context;
    private String imageTitle = "Image ";
    private List<Item> list;
    private OnItemAdapterItemClickListener onMyAdapterItemClickListener;

    //Instantiate the ToDoAdapter
    ItemAdapter(Context context, List<Item> list, OnItemAdapterItemClickListener onMyAdapterItemClickListener) {
        this.list = list;
        this.context = context;
        this.onMyAdapterItemClickListener = onMyAdapterItemClickListener;
    }

    //Inflate row.xml for each recycleView item
    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ItemViewHolder(view);
    }

    //Called to display item details in the recycler view
    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, final int position) {
        holder.itemTitleTextView.setText(imageTitle + (position + 1)); //Set the itemTitleTextView in the row layout to itemTitle value
        String format = "EEE, MMM d, yyyy '@' hh:mm a z"; //Format Date and Time
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        holder.itemDateAndTimeTextView.setText(sdf.format(list.get(position).itemDateAndTime)); //Set the itemDateAndTime in the row layout to the formatted date and time
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        requestOptions.error(R.drawable.ic_launcher_background);
        requestOptions.circleCrop();
        requestOptions.override(600, 600);
        Glide.with(context)
                .load(Uri.parse(list.get(position).itemPath)) // Uri of the picture
                .apply(requestOptions)
                .into(holder.itemImage);
        holder.itemView.setOnClickListener(v -> onMyAdapterItemClickListener.onItemClicked(position)); //Return the position of the item when it is clicked by the user
    }

    //Returns the count of the items displayed in the recyclerView
    public int getItemCount() {
        return list.size();
    }

    //Captures the objects from the row layout
    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitleTextView, itemDateAndTimeTextView;
        ImageView itemImage;

        ItemViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitleTextView = itemView.findViewById(R.id.itemTitleTextView);
            itemDateAndTimeTextView = itemView.findViewById(R.id.itemTimeAndDateTextView);
        }
    }
}
