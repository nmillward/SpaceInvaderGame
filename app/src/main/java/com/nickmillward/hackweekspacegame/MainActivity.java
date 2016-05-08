package com.nickmillward.hackweekspacegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nickmillward.hackweekspacegame.view.SpaceAnimationView;

public class MainActivity extends AppCompatActivity {

    private SpaceAnimationView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (SpaceAnimationView) findViewById(R.id.view_spaceAnimationView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (view != null) {
            //resume SpaceAnimationView
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (view != null) {
            //pause SpaceAnimationView
        }
    }
}
