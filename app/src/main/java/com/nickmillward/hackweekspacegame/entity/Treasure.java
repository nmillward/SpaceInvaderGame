package com.nickmillward.hackweekspacegame.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by nmillward on 5/8/16.
 * Hackweek - May 2016
 *
 * To be collected to earn points
 */
public class Treasure {

    public float x, y;
    public float speed;
    public float diameter;
    public boolean shouldDelete = false;

    private Paint treasurePaint;
    private Bitmap treasureBitmap;
    private Matrix treasureMatrix = new Matrix();

    public Treasure(float diameter) {
        this.diameter = diameter;

        treasurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        treasurePaint.setStyle(Paint.Style.STROKE);
//        treasurePaint.setStrokeWidth(diameter / 10);
        treasurePaint.setColor(0xFF72FF66);     //Lime Green
//        treasurePaint.setColor(0xffffd700);     //Gold

        if (treasureBitmap == null) {
            createTreasureBitmap();
        }
    }

    public void createTreasureBitmap() {

//        Path treasurePath = new Path();

        treasureBitmap = Bitmap.createBitmap((int) diameter, (int) diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(treasureBitmap);
//        canvas.drawPath(treasurePath, treasurePaint);
        canvas.drawCircle(diameter / 2, diameter / 2 , diameter / 2, treasurePaint);
    }

    public void drawTreasure(Canvas canvas) {
        treasureMatrix.reset();
        treasureMatrix.setTranslate(x, y);

        canvas.drawBitmap(treasureBitmap, treasureMatrix, null);
    }
}
