package com.nickmillward.hackweekspacegame;

import android.app.Application;

import com.nickmillward.hackweekspacegame.controller.Controller;
import com.nickmillward.hackweekspacegame.controller.GameController;

import java.util.HashMap;

/**
 * Created by nmillward on 5/14/16.
 */
public class SpaceGameApplication extends Application {

    private static HashMap<Integer, Controller> controllerHashMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        GameController controller = new GameController();
        controllerHashMap.put(Controller.GAME_CONTROLLER, controller);
    }

    public static Controller getController(int controller) {
        return controllerHashMap.get(controller);
    }
}
