package de.thu.tpro.android4bikes.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.widget.ImageView;

import androidx.core.graphics.ColorUtils;

import com.squareup.picasso.Picasso;

import de.thu.tpro.android4bikes.R;
import de.thu.tpro.android4bikes.data.model.Profile;

public class ProfilePictureUtil {
    //Text in ImageView
    public static Bitmap textToBitmap(String text, int color) {
        double contrast_ratio = ColorUtils.calculateContrast(color, Color.parseColor("#FFFFFF"));
        int colorText = Color.BLACK;
        if (contrast_ratio > 4) {
            colorText = Color.WHITE;
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(20);
        paint.setColor(colorText);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent();
        int width = (int) (paint.measureText(text) + 14f);
        int height = (int) (baseline + paint.descent() + 15f);
        Bitmap imageB = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(imageB);
        canvas.drawText(text, 0, baseline, paint);
        return imageB;
    }

    public static void setProfilePicturetoImageView(ImageView imageView, Profile profile) {
        Uri uri = Uri.parse(profile.getProfilePictureURL());
        Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_face_blue_24dp)
                .error(R.drawable.ic_error_red_24dp)
                .resize(96, 96)
                .noFade()
                .centerInside()
                .into(imageView);
    }
}

