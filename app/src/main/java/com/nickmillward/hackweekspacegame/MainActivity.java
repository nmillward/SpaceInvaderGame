package com.nickmillward.hackweekspacegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nickmillward.hackweekspacegame.view.SpaceAnimationView;

public class MainActivity extends AppCompatActivity {

    private SpaceAnimationView spaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spaceView = (SpaceAnimationView) findViewById(R.id.view_spaceAnimationView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (spaceView != null) {
            spaceView.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (spaceView != null) {
            spaceView.onActivityPause();
        }
    }
}
