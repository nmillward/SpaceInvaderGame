package com.nickmillward.hackweekspacegame.controller;

/**
 * Created by nmillward on 5/13/16.
 */
public class GameController extends Controller {

    public int currentScore;
    public int highScore = 0;
    public boolean isPlayerAlive;

    public GameController() {

    }

    public void resetGame() {
        if (currentScore > highScore) {
            highScore = currentScore;
        }
        resetScore();
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public void incrementCurrentScore(int pointVal) {
        currentScore += pointVal;
    }

    public void resetScore() {
        currentScore = 0;
    }

    public boolean isPlayerAlive() {
        return isPlayerAlive;
    }

    public void setPlayerAlive(boolean playerAlive) {
        isPlayerAlive = playerAlive;
    }
}
