package de.thu.tpro.android4bikes.view.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.HazardAlert;

public class BikeTrackMarker {
    private LatLng latLng;
    private String title;
    private String snippet;
    private MarkerOptions marker;

    public BikeTrackMarker() {
    }

    public BikeTrackMarker(LatLng latLng, String title, String snippet, MarkerOptions marker) {
        this.latLng = latLng;
        this.title = title;
        this.snippet = snippet;
        this.marker = marker;
    }

    public MarkerOptions makeMarker() {

        MarkerOptions markerRF = new MarkerOptions()
                .position(latLng)
                .title(HazardAlert.HazardType.ROCKFALL.toString())
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bike_track));
        return markerRF;
    }

    public BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {

        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }


}
