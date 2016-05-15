package com.nickmillward.hackweekspacegame.controller;

/**
 * Created by nmillward on 5/13/16.
 */
public class GameController extends Controller {

    public static final int HOME = 0;
    public static final int PLAY = 1;
    public static final int END = 2;

    private int gameState;

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
//        setGameState(HOME);
        setGameState(PLAY);
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

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }
}
