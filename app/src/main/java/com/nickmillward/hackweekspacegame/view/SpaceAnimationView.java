package com.nickmillward.hackweekspacegame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.nickmillward.hackweekspacegame.Util.MathUtil;
import com.nickmillward.hackweekspacegame.entity.Ship;
import com.nickmillward.hackweekspacegame.entity.Star;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nmillward on 5/7/16.
 * Hackweek - May 2016
 */
public class SpaceAnimationView extends FrameLayout {

    public static final int FOREGROUND_ASTROID_INTERVAL = 20;
    public static final int BACKGROUND_STAR_INTERVAL = 30;
    public static final float ROTATION_RANGE = 20.f;

    private TimerTask spaceViewTask;
    private Timer spaceViewTimer;

    private static final Object spaceViewLock = new Object();

    private Paint starPaint;
    private int backgroundStarTicker;
    private ArrayList<Star> backgroundStars = new ArrayList<>();

    private Ship ship;

    private float lastX;
    private float minX, maxX;
    private float touchSlop;

    public SpaceAnimationView(Context context) {
        this(context, null);
    }

    public SpaceAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpaceAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);   //All ViewGroup sub-classes to call onDraw
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void initSpaceViewTask() {
        spaceViewTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (spaceViewLock) {
                    if (ship != null) {
                        ship.onFrame();
                        updateStar();
                    }
                }
                postInvalidate();
            }
        };
    }

    public void onActivityResume() {    //Schedule SpaceViewTask
        if (spaceViewTimer == null) {
            spaceViewTimer = new Timer();
        }
        if (spaceViewTask == null) {
            initSpaceViewTask();
        } else {
            spaceViewTask.cancel();
            spaceViewTask = null;
            initSpaceViewTask();
        }
        spaceViewTimer.schedule(spaceViewTask, 0, 16);
    }

    public void onActivityPause() {     //Pause SpaceViewTask
        if (spaceViewTimer != null) {
            spaceViewTimer.cancel();
            spaceViewTimer.purge();
        }
        if (spaceViewTask != null) {
            spaceViewTask.cancel();
            spaceViewTask = null;
            spaceViewTimer = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (spaceViewLock) {
            for (Star star : backgroundStars) {
                starPaint.setColor(star.color);
                canvas.drawCircle(star.x, star.y, star.radius, starPaint);
            }
            if ( ship != null) {
                ship.drawShip(canvas);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        if (ship == null) {
            ship = new Ship();
            ship.createShipBitmap(width);
            ship.setX((width / 2) - (ship.getShipWidth() / 2));         //Set ship to center X
            ship.setCenterY(height * 3/4);                              //Set ship towards bottom of screen
        }
        maxX = width - ship.getShipWidth();
        minX = 0;

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        synchronized (spaceViewLock) {

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    return true;
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    float deltaX = event.getX() - lastX;
                    lastX = event.getX();
                    if (ship != null) {
                        ship.setX(Math.min(maxX, Math.max(minX, MathUtil.lerp(ship.getX(), ship.getX() + deltaX, 0.7f))));
                        if (deltaX > 0) {
                            ship.setRotation(MathUtil.lerp(ship.getRotation(), ROTATION_RANGE * Math.min(1.f, Math.abs(deltaX) / touchSlop), 0.4f));
                        } else if (deltaX < 0) {
                            ship.setRotation(MathUtil.lerp(ship.getRotation(), -ROTATION_RANGE * Math.min(1.f, Math.abs(deltaX) / touchSlop), 0.4f));
                        }
                    }
                    break;
            }

        }
        return super.onTouchEvent(event);
    }

    private void updateStar() {
        if (backgroundStarTicker++ == BACKGROUND_STAR_INTERVAL) {
            backgroundStarTicker = 0;
            emitStar(
                    (int) (getWidth() / 128 + (getWidth() / 256) * Math.random()),
                    0x66ffffff,
                    getWidth() / 256,
                    backgroundStars);
        }

        ArrayList<Star> removalArray = new ArrayList<>();
        for (Star star : backgroundStars) {
            star.y -= star.speed;
            if (star.y < 0.f) {
                removalArray.add(star);
            }
        }
        removalArray.removeAll(backgroundStars);
        removalArray.clear();
    }

    //Place new Star at random width on screen and add it to List
    //star.y = 0 to start from Top of screen
    //star.speed is negative to reverse direction of stars
    private void emitStar(int radius, int color, int speed, ArrayList<Star> collection) {
        Star star = new Star();
        star.x = (float) (Math.random() * getWidth());
        star.y = 0;
        star.radius = radius;
        star.color = color;
        star.speed = -1 * speed;
        collection.add(star);
    }
}
