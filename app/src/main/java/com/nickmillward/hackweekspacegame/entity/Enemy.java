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
    public float rotation;
    public boolean shouldDelete = false;
    public float diameter;

    private Paint enemyPaint, enemyBorderPaint;
    private Bitmap enemyBitmap;
    private Matrix enemyMatrix = new Matrix();

    public Enemy(float diameter) {
        this.diameter = diameter;

        enemyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        enemyPaint.setStyle(Paint.Style.STROKE);
        enemyPaint.setStrokeWidth(diameter / 8);
        enemyPaint.setColor(0xff323299);

        enemyBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        enemyBorderPaint.setColor(0xE6FFFFFF);
//        enemyBorderPaint.setColor(0xffE26660);

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
        canvas.drawRect(0, 0, diameter, diameter, enemyBorderPaint);
//        canvas.drawPath(enemyPath, enemyPaint);
    }

    public void drawEnemy(Canvas canvas) {
        enemyMatrix.reset();
        enemyMatrix.setTranslate(x, y);
        enemyMatrix.postRotate(rotation, x + diameter / 2, y + diameter / 2);
//        enemyMatrix.postRotate(rotation);
        canvas.drawBitmap(enemyBitmap, enemyMatrix, null);
    }

    public float getDiameter() {
        return diameter;
    }

    public void setDiameter(float diameter) {
        this.diameter = diameter;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }



    public class EnemySmoke {

        public float x, y;
        public float diameter;
        public float rotation;

        private Bitmap smokeBitmap;
        private Matrix smokeMatrix = new Matrix();
        private Paint smokePaint;

        public EnemySmoke(float diameter) {
            this.diameter = diameter;
            smokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            if (smokeBitmap == null) {
                createSmokeBitmap();
            }
        }

        private void createSmokeBitmap() {
            smokeBitmap = Bitmap.createBitmap((int) diameter, (int) diameter, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(smokeBitmap);
            canvas.drawColor(0xffcccccc); //light grey
//        canvas.drawColor(0xff616161); //grey
//        canvas.drawColor(0x88ff0000);   //red
//        canvas.drawColor(0x88fb7200);   //orange
        }

        public void drawSmoke(Canvas canvas) {
            smokeMatrix.reset();
            smokeMatrix.setTranslate(x, y);
            smokeMatrix.postRotate(rotation, x + diameter / 2, y + diameter / 2);
            smokePaint.setAlpha((int) (0xff * (1.f - rotation / 400.f)));
            canvas.drawBitmap(smokeBitmap, smokeMatrix, smokePaint);
        }

    }

}
