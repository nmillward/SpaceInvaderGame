package com.nickmillward.hackweekspacegame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import com.nickmillward.hackweekspacegame.Util.MathUtil;
import com.nickmillward.hackweekspacegame.entity.Enemy;
import com.nickmillward.hackweekspacegame.entity.Ship;
import com.nickmillward.hackweekspacegame.entity.Smoke;
import com.nickmillward.hackweekspacegame.entity.Star;
import com.nickmillward.hackweekspacegame.entity.Treasure;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nmillward on 5/7/16.
 * Hackweek - May 2016
 */
public class SpaceAnimationView extends FrameLayout {

    public static final int FOREGROUND_INTERVAL = 20;
    public static final int BACKGROUND_STAR_INTERVAL = 30;
    public static final int SMOKE_INTERVAL = 2;
    public static final float ROTATION_RANGE = 20.f;

    private TimerTask spaceViewTask;
    private Timer spaceViewTimer;

    private static final Object spaceViewLock = new Object();

    private Paint starPaint;
    private int backgroundStarTicker;
    private ArrayList<Star> backgroundStars = new ArrayList<>();

    private int smokeTicker;
    private ArrayList<Smoke> smokes = new ArrayList<>();

    private int enemyTicker;
    private ArrayList<Enemy> enemies = new ArrayList<>();

    private int treasureTicker;
    private ArrayList<Treasure> treasures = new ArrayList<>();

    private Ship ship;

    private float lastX, lastY;
    private float minX, minY;
    private float maxX, maxY;
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
                        updateSmoke();
                        updateEnemy();
                        updateTreasure();
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

            if (ship != null) {
                for (Smoke smoke : smokes) {
                    smoke.drawSmoke(canvas);
                }
                ship.drawShip(canvas);
            }

            for (Enemy enemy : enemies) {
                enemy.drawEnemy(canvas);
            }

            for (Treasure treasure : treasures) {
                treasure.drawTreasure(canvas);
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
            ship.setY(height * 3/4);                                    //Set ship towards bottom of screen
        }
        maxX = width - ship.getShipWidth();
        minX = 0;

        maxY = height - ship.getShipHeight();
        minY = 0;

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
                    lastY = event.getY();
                    return true;

                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:

                    float deltaX = event.getX() - lastX;
                    float deltaY = event.getY() - lastY;
                    lastX = event.getX();
                    lastY = event.getY();

                    if (ship != null) {
                        ship.setX(Math.min(maxX, Math.max(minX, MathUtil.lerp(ship.getX(), ship.getX() + deltaX, 1.0f))));  //1.0f = 100% to follow finger movement
                        ship.setY(Math.min(maxY, Math.max(minY, MathUtil.lerp(ship.getY(), ship.getY() + deltaY, 1.0f))));
                        if (deltaX > 0) {
                            ship.setRotation(MathUtil.lerp(ship.getRotation(), ROTATION_RANGE * Math.min(1.f, Math.abs(deltaX) / touchSlop), 0.3f));
                        } else if (deltaX < 0) {
                            ship.setRotation(MathUtil.lerp(ship.getRotation(), -ROTATION_RANGE * Math.min(1.f, Math.abs(deltaX) / touchSlop), 0.3f));
                        }
//                        else if (deltaY > 0) {
//                            //TODO: Tilt ship forward
//                        } else if (deltaY < 0) {
//                            //TODO: Tilt ship backwards
//                        }
                    }
                    break;
            }

        }
        return super.onTouchEvent(event);
    }

    private void updateTreasure() {
        if (treasureTicker++ == FOREGROUND_INTERVAL * 3) {
            treasureTicker = 0;

            Treasure treasure = new Treasure(ship.getShipWidth() / 2);
            treasure.x = (float) Math.random() * getWidth();
            treasure.y = 0;
            treasure.speed = (getWidth() / 128) * -1;

            treasures.add(treasure);
        }

        ArrayList<Treasure> removalArray = new ArrayList<>();
        for (Treasure treasure : treasures) {
            treasure.y -= treasure.speed;
            if (treasure.y < 0.f) {
                removalArray.add(treasure);
            }
        }
        treasures.removeAll(removalArray);
        removalArray.clear();
    }

    private void updateEnemy() {
        if (enemyTicker++ == FOREGROUND_INTERVAL) {
            enemyTicker = 0;

            Enemy enemy = new Enemy(ship.getShipWidth() / 2);
            enemy.x = (float) (Math.random() * getWidth());
            enemy.y = 0;
            enemy.speed = (getWidth() / 128) * -1;

            enemies.add(enemy);
        }

        ArrayList<Enemy> removalArray = new ArrayList<>();
        for (Enemy enemy : enemies) {
//            enemy.rotation = MathUtil.lerp(enemy.rotation, getHeight() * 5, 0.05f);
//            enemy.y = MathUtil.lerp(enemy.y, 0, -.10f * enemy.rotation / 400);
            enemy.y -= enemy.speed;
            if (enemy.y < 0.f || enemy.x < 0.f) {
                removalArray.add(enemy);
            }
        }
        enemies.removeAll(removalArray);
        removalArray.clear();
    }

    private void updateSmoke() {
        if (smokeTicker++ == SMOKE_INTERVAL) {
            //emit smoke
            Smoke smoke = new Smoke(ship.getShipWidth() / 4);
            float smokeDisplacement = (float) (-ship.getShipHeight() / 6 + Math.random() * ship.getShipHeight() / 2);
            smoke.y = ship.getY() + ship.getShipHeight();
            smoke.x = ship.getX() + ship.getShipWidth() / 2 - smoke.diameter + smokeDisplacement;
            smokes.add(smoke);
            smokeTicker = 0;
        }

        ArrayList<Smoke> removalArray = new ArrayList<>();
        for (Smoke smoke : smokes) {
            //rotate and disburse smoke
            smoke.rotation = MathUtil.lerp(smoke.rotation, 360.f, 0.05f);
            smoke.y = MathUtil.lerp(smoke.y, -ship.getShipHeight() / 2, -.01f * smoke.rotation / 400);
            if (smoke.y >= ship.getY() + ship.getShipHeight() * 3) {
                removalArray.add(smoke);
            }
        }
        smokes.removeAll(removalArray);
        removalArray.clear();
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
        backgroundStars.remove(removalArray);
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
