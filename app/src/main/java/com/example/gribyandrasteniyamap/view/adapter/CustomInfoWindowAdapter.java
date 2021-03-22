package com.example.gribyandrasteniyamap.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.view.model.MarkerTag;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {
    private final View mWindow;
    private final Context context;


    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        mWindow = ((Activity)context).getLayoutInflater().inflate(R.layout.map_item_info, null);
    }

    private void render(Marker marker, View view) {
        TextView tvTitle = view.findViewById(R.id.title);
        TextView tvSnippet = view.findViewById(R.id.snippet);

        tvTitle.setText(marker.getTitle());
        tvSnippet.setText(marker.getSnippet());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        MarkerTag markerTag = (MarkerTag) marker.getTag();
        Toast.makeText(context, markerTag.toString(), Toast.LENGTH_LONG).show();
    }

}