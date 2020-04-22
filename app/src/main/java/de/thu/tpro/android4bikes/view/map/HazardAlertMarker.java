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

public class HazardAlertMarker {
    private LatLng latLng;
    private String title;
    private String snippet;
    private MarkerOptions marker;

    public HazardAlertMarker() {
    }

    public HazardAlertMarker(LatLng position, String title, String snippet) {
        this.latLng = position;
        this.title = title;
        this.snippet = snippet;
    }


    public MarkerOptions chooseMarker() {
        //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.material_bike);
        //TODO: make markercolor/icon diferent, depending on Markertype

        if (title.equals(HazardAlert.HazardType.ICY_ROAD.toString())) {

            MarkerOptions markerIR = new MarkerOptions()
                    .position(latLng)
                    .title("title")
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_material_poi));
            marker = markerIR;
        }

        if (title.equals(HazardAlert.HazardType.DAMAGED_ROAD.toString())) {

            MarkerOptions markerDR = new MarkerOptions()
                    .position(latLng)
                    .title("title")
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_material_poi));
            marker = markerDR;
        }

        if (title.equals(HazardAlert.HazardType.SLIPPERY_ROAD.toString())) {

            MarkerOptions markerSR = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_material_poi));
            marker = markerSR;

        }

        if (title.equals(HazardAlert.HazardType.ROADKILL.toString())) {

            MarkerOptions markerRK = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_material_poi));
            marker = markerRK;

        }

        if (title.equals(HazardAlert.HazardType.ROCKFALL.toString())) {

            MarkerOptions markerRF = new MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_material_poi));
            marker = markerRF;
        }
        return marker;
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


    public LatLng getPosition() {
        return latLng;
    }

    public void setPosition(LatLng position) {
        this.latLng = position;
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
