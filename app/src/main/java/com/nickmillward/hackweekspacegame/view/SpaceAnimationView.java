package com.nickmillward.hackweekspacegame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nickmillward.hackweekspacegame.R;
import com.nickmillward.hackweekspacegame.Util.MathUtil;
import com.nickmillward.hackweekspacegame.controller.GameController;
import com.nickmillward.hackweekspacegame.entity.Enemy;
import com.nickmillward.hackweekspacegame.entity.Explosion;
import com.nickmillward.hackweekspacegame.entity.Ship;
import com.nickmillward.hackweekspacegame.entity.Smoke;
import com.nickmillward.hackweekspacegame.entity.Star;
import com.nickmillward.hackweekspacegame.entity.Treasure;

import java.text.DecimalFormat;
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
    public static final int TREASURE_POINT_VAL = 10;

    private GameController controller;

    private TextView scoreTitleView, scoreCountView;
    protected DecimalFormat scoreFormatter = new DecimalFormat("#,###,###");

    private TimerTask spaceViewTask;
    private Timer spaceViewTimer;

    private static final Object spaceViewLock = new Object();

    private Paint starPaint;
    private int backgroundStarTicker;
    private ArrayList<Star> backgroundStars = new ArrayList<>();

    private int smokeTicker;
    private ArrayList<Smoke> smokes = new ArrayList<>();

    private int enemyTicker;
    private ArrayList<Enemy> enemies;
    private ArrayList<Enemy> enemiesToDelete;

    private int treasureTicker;
    private ArrayList<Treasure> treasures;
    private ArrayList<Treasure> treasuresToDelete;

    private Paint explosionPaint;
    private ArrayList<Explosion> explosions;
    private ArrayList<Explosion> explosionsToDelete;

    private Ship ship;
    private boolean isDead;

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
        explosionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

//        controller = (GameController) SpaceGameApplication.getController(Controller.GAME_CONTROLLER);
        controller = new GameController();

        setWillNotDraw(false);   //All ViewGroup sub-classes to call onDraw
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        addScoreView();

        enemies = new ArrayList<>();
        enemiesToDelete = new ArrayList<>();

        explosions = new ArrayList<>();
        explosionsToDelete = new ArrayList<>();

        treasures = new ArrayList<>();
        treasuresToDelete = new ArrayList<>();
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
            drawExplosion(canvas);
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
            ship.setY(height * 3 / 4);                                    //Set ship towards bottom of screen
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

        for (Treasure treasure : treasures) {
            treasure.y -= treasure.speed;
            if (treasure.y < 0.f) {
                treasure.shouldDelete = true;
            }
            if (treasure.shouldDelete) {
                treasuresToDelete.add(treasure);
                continue;
            }
            collideWithTreasure(treasure);
        }
        if (!treasuresToDelete.isEmpty()) {
            treasures.removeAll(treasuresToDelete);
            treasuresToDelete.clear();
        }
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

        for (Enemy enemy : enemies) {
            enemy.y -= enemy.speed;

            if (enemy.y < 0.f && !isDead) {
                enemy.shouldDelete = true;
            }
            collideWithEnemy(enemy, ship);
            if (enemy.shouldDelete) {
                enemiesToDelete.add(enemy);
                continue;
            }
        }
        if (!enemiesToDelete.isEmpty()) {
            enemies.removeAll(enemiesToDelete);
            enemiesToDelete.clear();
        }
    }

    public void removeAllEnemies() {
        enemies.clear();
        enemiesToDelete.clear();
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

    private void collideWithTreasure(Treasure treasure) {

        if (Math.abs(treasure.x - (ship.getX())) <= treasure.diameter / 2 &&
                Math.abs(treasure.y - (ship.getY())) <= treasure.diameter / 2) {

            Log.d("TREASURE", "SCORE COUNT BEFORE = " + controller.getCurrentScore());

            controller.incrementCurrentScore(TREASURE_POINT_VAL);

            treasuresToDelete.add(treasure);
            Log.d("TREASURE", "SCORE COUNT AFTER = " + controller.getCurrentScore());
        }
        //TODO: Add Treasure Collected Animation
    }

    private void collideWithEnemy(Enemy enemy, Ship ship) {

        ship.shouldDelete = true;

        if (Math.abs(enemy.x - (ship.getX())) <= enemy.diameter / 2 &&
                Math.abs(enemy.y - (ship.getY())) <= enemy.diameter / 2 && !isDead) {
            addExplosion(ship.getX(), ship.getY(), getResources().getColor(R.color.colorWhiteLight));
            isDead = true;
        }
    }

    private void destroyAllEnemies() {

    }

    private void updateExplosion() {
        for (Explosion explosion : explosions) {
            if (explosion.shouldDelete) {
                explosionsToDelete.add(explosion);
            }
        }
        explosions.removeAll(explosionsToDelete);
        explosionsToDelete.clear();
    }

    public void addExplosion(float explodeX, float explodeY, int color) {
        Explosion explosion = new Explosion();
        explosion.x = explodeX;
        explosion.y = explodeY;
        explosion.color = color;

        //Explosion Animation
        explosions.add(explosion);

    }

    private void drawExplosion(Canvas canvas) {
        for (Explosion explosion : explosions) {
            explosionPaint.setColor(explosion.color);
            explosionPaint.setAlpha(explosion.alpha);
            canvas.drawCircle(explosion.x, explosion.y, explosion.diameter / 2, explosionPaint);
        }
    }

    private void updateScoreView() {
        scoreTitleView.setText(String.format(getResources().getString(R.string.score), controller.getCurrentScore()));
    }

    private void addScoreView() {
//        String formattedScore = scoreFormatter.format("100");

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(16, 16, 16, 16);

        scoreTitleView = new TextView(getContext());
        scoreTitleView.setLayoutParams(layoutParams);
//        scoreTitleView.setText(getResources().getString(R.string.score));
//        scoreTitleView.setText(String.format(getResources().getString(R.string.score), formattedScore));
        scoreTitleView.setText(String.format(getResources().getString(R.string.score), controller.getCurrentScore()));
        scoreTitleView.setTextColor(getResources().getColor(R.color.colorWhiteLight));
        scoreTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        addView(scoreTitleView);

    }

}
