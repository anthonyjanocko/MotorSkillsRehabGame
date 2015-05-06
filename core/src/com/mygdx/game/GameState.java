package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.sql.Timestamp;

public class GameState extends com.badlogic.gdx.Game {

    public Character character;
    public OptimalPath opt;
    public CoinPath cp;
    public PowerPath powerPath;
    private DataFile dataFile;



    public SpriteBatch batch;
    public BitmapFont font;

    private int height = 1280;
    private int width = 800;

    public int scrollVelocity = 200;
    public int remainingTimeSecs = 60;

    public Long startTime = System.currentTimeMillis();

    public boolean isRunning = false;
    public static float gameScrollSpeed = 75 + 25 * Settings.getInstance().difficulty;
    public static int difficulty = 1;


    @Override
    public void create() {
        // instantiate the dataFile
        instantiateDataFile();
        // create a Rectangle to logically represent the charShape
        character = new Character(width, height);
        // create the coin path
        opt = new OptimalPath(width, height, dataFile);
        // create the optimal path
        cp = new CoinPath(width, height, opt);
        // create the power path
        powerPath = new PowerPath(width, height);



        batch = new SpriteBatch();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();
        this.setScreen(new LoginScreen(this));
    }

    public void startGame() {
        isRunning = true;
    }

    public void stopGame() {
        isRunning = false;
    }

    private void instantiateDataFile() {
        String userTag = LoginScreen.username;
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        String filename = (userTag + timestamp + ".csv").replace(":", "-").replace(" ","_");
        dataFile = new DataFile(filename);
        dataFile.writeHeader(LoginScreen.username, timestamp);
    }

    public void render() {
        super.render(); //important!
        if (isRunning) update();
    }

    public void update() {
        updateCoinPathPos();
        // update character position and attributes
        character.update();
        powerPath.update(character);


        checkIfGameOver();
    }

    private void updateCoinPathPos() {
        // update the optimal path, coin path, and range of motion
        cp.updateCoinPath(character.charShape);
        opt.updateOptimalPath(character.charShape);
        opt.updateRangeOfMotion(character.getX());
        long timeGameHasBeenRunning = (System.currentTimeMillis() - startTime);
        gameScrollSpeed += .001;
        System.out.println(gameScrollSpeed);

    }

    public void checkIfGameOver() {
        //Return to MainMenu Screen after a minutes of game play
        long timeGameHasBeenRunning = (System.currentTimeMillis() - startTime);
        if (timeGameHasBeenRunning > Settings.getInstance().gameDuration * 1000 * 60){
            dataFile.close();
            this.setScreen(new MainMenu(this));
        }
    }


    @Override
    public void pause() {
        isRunning = false;
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        cp.tearDown();
    }

}

