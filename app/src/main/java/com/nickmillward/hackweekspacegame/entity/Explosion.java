package com.nickmillward.hackweekspacegame.entity;

/**
 * Created by nmillward on 5/13/16.
 */
public class Explosion {

    public float diameter;
    public float x, y;
    public int color;
    public int alpha;
    public int radius;
    public boolean shouldDelete;

//    private Bitmap explosionBitmap;
//    private Matrix explosionMatrix = new Matrix();
//    private Paint explosionPaint;
//
//
//    public Explosion() {
//        this.diameter = diameter;
//
//        explosionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//
//        if (explosionBitmap == null) {
//            createExplosionBitmap();
//        }
//    }
//
//    private void createExplosionBitmap() {
//        explosionBitmap = Bitmap.createBitmap((int) diameter, (int) diameter, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(explosionBitmap);
//        canvas.drawColor(0xffcccccc);
//    }
//
//    public void drawExplosion(Canvas canvas) {
//        explosionMatrix.reset();
//        explosionMatrix.setTranslate(x, y);
//        canvas.drawBitmap(explosionBitmap, explosionMatrix, explosionPaint);
//    }

}
