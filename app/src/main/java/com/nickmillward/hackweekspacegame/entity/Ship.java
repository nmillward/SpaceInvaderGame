package com.nickmillward.hackweekspacegame.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.nickmillward.hackweekspacegame.Util.MathUtil;

/**
 * Created by nmillward on 5/8/16.
 * Hackweek - May 2016
 */
public class Ship {

    private float centerX, centerY;
    private float x, y;
    private float lastX, lastY;
    private float rotation;

    private int shipHeight, shipWidth;
    private Paint shipPaint;
    private Bitmap shipBitmap;
    private Matrix shipMatrix;
    private Direction driftDirection;
    private int driftRange;
    private int currentDrift;

    private enum Direction {
        OUTWARD,
        INWARD
    }

    public Ship() {
        shipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        shipPaint.setColor(ContextCompat.getColor(context, R.color.shipColor));
        shipPaint.setColor(0xff9bfad0);
        shipMatrix = new Matrix();
        driftDirection = Direction.OUTWARD;
    }

    public void createShipBitmap(int screenWidth) {
        //Use device screen size to determine ship size
        shipHeight = screenWidth / 8;
        shipWidth = shipHeight * 3/4;

        int shipBottomCorner = shipHeight / 4;

        Path shipPath = new Path();
        shipPath.moveTo(0, shipHeight - shipBottomCorner);              //Left most point of ship
        shipPath.lineTo(shipWidth / 2, 0);                              //Front of ship
        shipPath.lineTo(shipWidth, shipHeight - shipBottomCorner);      //Right most point of ship
        shipPath.lineTo(shipWidth - shipBottomCorner, shipHeight);      //Bottom Right
        shipPath.lineTo(shipBottomCorner, shipHeight);                  //Bottom Left
        shipPath.close();

        shipBitmap = Bitmap.createBitmap(shipWidth, shipHeight, Bitmap.Config.ARGB_8888);
        Canvas shipCanvas = new Canvas(shipBitmap);
        shipCanvas.drawPath(shipPath, shipPaint);

        driftRange = shipWidth / 3;
    }

    public void drawShip(Canvas canvas) {
        if (shipBitmap != null) {
            shipMatrix.reset();
            shipMatrix.setTranslate(x, y);
            shipMatrix.postRotate(rotation, x + shipHeight / 2, y + shipWidth / 2);     //Sets ship tilt
            canvas.drawBitmap(shipBitmap, shipMatrix, null);
        }
    }

    public void onFrame() {
        if (Math.abs(x - lastX) <= shipHeight / 16) {
            rotation = MathUtil.lerp(rotation, 0.f, 0.1f);
        }
        y = centerY + currentDrift;
    }

    public int getShipHeight() {
        return shipHeight;
    }

    public int getShipWidth() {
        return shipWidth;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        lastX = x;
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        lastY = y;
        this.y = y;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}