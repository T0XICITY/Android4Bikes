package de.thu.tpro.android4bikes.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.graphics.ColorUtils;

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
}
