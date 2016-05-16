package com.nickmillward.hackweekspacegame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nickmillward.hackweekspacegame.Util.SharedPrefConstants;
import com.nickmillward.hackweekspacegame.controller.GameController;
import com.nickmillward.hackweekspacegame.view.SpaceAnimationView;

public class MainActivity extends AppCompatActivity {

    private GameController controller;
    private SpaceAnimationView spaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spaceView = (SpaceAnimationView) findViewById(R.id.view_spaceAnimationView);

        controller = new GameController();
        controller.resetGame();
    }

    @Override
    protected void onResume() {

        SharedPreferences prefs = getSharedPreferences(SharedPrefConstants.PREF_NAME, MODE_PRIVATE);

        int highScore = prefs.getInt(SharedPrefConstants.KEY_HIGH_SCORE, -1);
        if (highScore >= 0) {
            controller.setHighScore(highScore);
        }

        if (spaceView != null) {
            spaceView.onActivityResume();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {

        SharedPreferences prefs = getSharedPreferences(SharedPrefConstants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(SharedPrefConstants.KEY_HIGH_SCORE, controller.getHighScore());
        editor.apply();

        if (spaceView != null) {
            spaceView.onActivityPause();
        }

        super.onPause();
    }
}
