package com.nickmillward.hackweekspacegame.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by nmillward on 5/10/16.
 * Hackweek - May 2016
 */
public class Smoke {

    public float x, y;
    public float diameter;
    public float rotation;

    private Bitmap smokeBitmap;
    private Matrix smokeMatrix = new Matrix();
    private Paint smokePaint;

    public Smoke(float diameter) {
        this.diameter = diameter;
        smokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (smokeBitmap == null) {
            createSmokeBitmap();
        } else if (diameter != smokeBitmap.getWidth()) {
            createSmokeBitmap();
        }
    }

    private void createSmokeBitmap() {
        smokeBitmap = Bitmap.createBitmap((int) diameter, (int) diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(smokeBitmap);
        canvas.drawColor(0x88616161); //grey
//        canvas.drawColor(0x88ff0000);   //red
    }

    public void drawSmoke(Canvas canvas) {
        smokeMatrix.reset();
        smokeMatrix.setTranslate(x, y);
        smokeMatrix.postRotate(rotation, x + diameter / 2, y + diameter / 2);
        smokePaint.setAlpha((int) (0xff * (1.f - rotation/400.f)));
        canvas.drawBitmap(smokeBitmap, smokeMatrix, smokePaint);
    }
}
