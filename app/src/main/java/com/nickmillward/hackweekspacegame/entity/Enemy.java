package com.nickmillward.hackweekspacegame.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by nmillward on 5/8/16.
 * Hackweek - May 2016
 *
 * Enemy "Astroids" in shape of W's
 * Emited in Foreground on same plain as ship
 */
public class Enemy {

    public float x, y;
    public float speed;

    private float diameter;
    private float rotation;
    private int color;
    private int enemyHeight, enemyWidth;

    private Paint enemyPaint;
    private Bitmap enemyBitmap;
    private Matrix enemyMatrix = new Matrix();

    public Enemy(float diameter) {
        this.diameter = diameter;

        enemyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        enemyPaint.setStyle(Paint.Style.STROKE);
        enemyPaint.setStrokeWidth(diameter / 10);
        enemyPaint.setColor(0xffffffff);
//        enemyPaint.setColor(0xff323299);

        if (enemyBitmap == null) {
            createEnemyBitmap();
        }
    }

    public void createEnemyBitmap() {

        Path enemyPath = new Path();
        enemyPath.moveTo(0, 0);                         //Top left corner
        enemyPath.lineTo(diameter / 3, diameter);       //Bottom left point
        enemyPath.lineTo(diameter / 2, diameter / 2);   //Center
        enemyPath.moveTo(diameter / 3, 0);              //Top middle point
        enemyPath.lineTo(diameter * 2/3, diameter);     //Bottom right point
        enemyPath.lineTo(diameter, 0);

        enemyBitmap = Bitmap.createBitmap((int) diameter, (int) diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(enemyBitmap);
        canvas.drawPath(enemyPath, enemyPaint);
    }

    public void drawEnemy(Canvas canvas) {
        enemyMatrix.reset();
        enemyMatrix.setTranslate(x, y);

        canvas.drawBitmap(enemyBitmap, enemyMatrix, null);
    }


}
