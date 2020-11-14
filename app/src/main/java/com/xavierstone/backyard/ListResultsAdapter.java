package com.xavierstone.backyard;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Manages the RecycleView for ListResults Activity
public class ListResultsAdapter extends RecyclerView.Adapter<ListResultsAdapter.ResultsHolder> {
    // Campsite List
    private ArrayList<DBData> searchResults = new ArrayList<>();
    private Context context;

    // ViewHolder class
    public static class ResultsHolder extends RecyclerView.ViewHolder {
        // Campsites
        public RelativeLayout layout;
        public ImageView mainPhoto;
        public TextView name;

        public ResultsHolder(View view) {
            super(view);
            name = view.findViewById(R.id.itemName);
            mainPhoto = view.findViewById(R.id.itemPhoto);
            layout = view.findViewById(R.id.itemLayout);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListResultsAdapter(Context parentContext, ArrayList<DBData> results) {
        searchResults = results;
        context = parentContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListResultsAdapter.ResultsHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.campsite_list_item, parent, false);

        // set on click listener


        ResultsHolder vh = new ResultsHolder(itemView);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ResultsHolder holder, final int position) {
        DBData curSite = searchResults.get(position);
        holder.name.setText(curSite.getData("name"));

        // Load photos
        DBHandler dbHandler = new DBHandler(context, null, null,1);
        ArrayList<DBData> photos = dbHandler.search(DBHandler.photosTable,
                "campsite_id",curSite.getData("id"));

        if (!photos.isEmpty()) {
            // Load first photo
            if (photos.get(0).getData("type").equals("1")) {
                holder.mainPhoto.setImageBitmap(InternalStorage.loadInternalImage(context,
                        photos.get(0).getData("path")));
            }else{
                Uri uri = Uri.parse(InternalStorage.getDrawPath() + photos.get(0).getData("path"));
                holder.mainPhoto.setImageBitmap(InternalStorage.loadExternalImage(context, uri.toString()));
            }
        }else{
            // Hide image view
            holder.mainPhoto.setVisibility(View.GONE);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                DisplayCampsiteActivity.currentCampsite = Long.parseLong(searchResults.get(position).getData("id"));
                Intent intent = new Intent(context, DisplayCampsiteActivity.class);
                context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}

