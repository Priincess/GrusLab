package Game.Gameboard;

import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Mark Mauerhofer on 08.10.2016.
 */


public class Gameboard {

    private Rectangle rect_Gameboard;
    private Rectangle rect_GameboardCollisionBox;

    private IntegerProperty minionSize;
    private IntegerProperty itemSize;

    private IntegerProperty gameObjectDistance;

    private ObservableList<GameObject> gameObjects;
    private ArrayList<GameObject> minions;
    private ArrayList<GameObject> items;
    private ArrayList<IntegerProperty> points;

    private IntegerProperty gameTime;
    private Timer timer = new Timer();
    private TimerTask timerTask;

    public Gameboard(){
        minionSize = new SimpleIntegerProperty(50); // TODO: Use Preferences
        itemSize = new SimpleIntegerProperty(50);   // TODO: Use Preferences
        gameObjectDistance = new SimpleIntegerProperty(50);   // TODO: Use Preferences
        gameTime = new SimpleIntegerProperty(120);            // TODO: Use Preferences

        initGameboardRectangle();
        initGameboardCollisionBoxRectangle();

        gameObjects = FXCollections.observableArrayList();
        minions = new ArrayList<GameObject>();
        items = new ArrayList<GameObject>();
        points = new ArrayList<IntegerProperty>();
    }



    public Rectangle getRect_Gameboard(){
        return rect_Gameboard;
    }

    public Rectangle getRect_GameboardCollisionBox(){
        return rect_GameboardCollisionBox;
    }

    public IntegerProperty getMinionSize(){
        return minionSize;
    }

    public IntegerProperty getItemSize(){
        return itemSize;
    }

    public IntegerProperty getGameObjectDistance(){
        return gameObjectDistance;
    }

    public ObservableList<GameObject> getGameObjects(){
        return gameObjects;
    }

    public IntegerProperty getGameTime(){
        return gameTime;
    }

    public IntegerProperty getPointsOf(int minion){
        if (minion >= 0 && minion < minions.size()) {
            return points.get(minion);
        }
        return new SimpleIntegerProperty(-1);
    }


    private void initGameboardRectangle(){
        rect_Gameboard = new Rectangle(50, 90, 800, 400);    // TODO: Use Preferences
        rect_Gameboard.setFill(Color.SKYBLUE);
        rect_Gameboard.setStrokeType(StrokeType.OUTSIDE);
        rect_Gameboard.setStroke(Color.RED);
        rect_Gameboard.setStrokeWidth(50);   // TODO: Use Preferences
    }

    private void initGameboardCollisionBoxRectangle(){
        rect_GameboardCollisionBox = new Rectangle();
        rect_GameboardCollisionBox.setFill(Color.ANTIQUEWHITE);
        rect_GameboardCollisionBox.setVisible(false);
        NumberBinding x = rect_Gameboard.xProperty().add(new SimpleIntegerProperty(minionSize.intValue()));
        NumberBinding y = rect_Gameboard.yProperty().add(new SimpleIntegerProperty(minionSize.intValue()));
        NumberBinding width = rect_Gameboard.widthProperty().subtract(new SimpleIntegerProperty(2*minionSize.intValue()));
        NumberBinding height = rect_Gameboard.heightProperty().subtract(new SimpleIntegerProperty(2*minionSize.intValue()));

        rect_GameboardCollisionBox.xProperty().bind(x);
        rect_GameboardCollisionBox.yProperty().bind(y);
        rect_GameboardCollisionBox.widthProperty().bind(width);
        rect_GameboardCollisionBox.heightProperty().bind(height);
    }

    public void gameStartSetup(){
        createGameObject(GameObjectType.MINION, 90, 140);
        createGameObject(GameObjectType.MINION, 750, 400);
        generateGoggles();
        generateBeedo();
        generateBanana();
    }

    public void startGameCountdown(){
        if (timerTask == null){
            timerTask = new TimerTask() {
                public void run() {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            gameTime.set(gameTime.intValue()-1);
                            // TODO: GameOver
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 1000);
        }
    }

    public void stopGameCountdown(){
        timerTask.cancel();
        timerTask = null;
    }


    public GameObject createGameObject(GameObjectType type, int x, int y){
        GameObject gObj = new GameObject(type, null);
        gObj.setPosition(x, y);
        switch (type){
            case MINION:
                gObj.setSize(minionSize);
                minions.add(gObj);
                points.add(new SimpleIntegerProperty(0));
                break;
            default:
                gObj.setSize(itemSize);
                items.add(gObj);
        }

        gameObjects.add(gObj);
        return gObj;
    }


    private Point generateRandomPointOnGameboard(){
        int minX = rect_Gameboard.xProperty().intValue();
        int maxX = rect_Gameboard.widthProperty().intValue() + minX - itemSize.intValue();    // substract itemSize otherwise items in bottem/right redzone

        int minY = rect_Gameboard.yProperty().intValue();
        int maxY = rect_Gameboard.heightProperty().intValue() + minY - itemSize.intValue();   // substract itemSize otherwise items in bottem/right redzone

        Random random = new Random();
        int x = 0;
        int y = 0;

        boolean collision = true;
        Rectangle itemCollisionBox;
        Rectangle minionCollisionBox;
        int tries = 0;

        while(collision == true && tries < 1000){
            collision = false;
            x = random.nextInt(maxX - minX + 1) + minX;
            y = random.nextInt(maxY - minY + 1) + minY;
            itemCollisionBox = new Rectangle(
                    x-itemSize.intValue()/2 - gameObjectDistance.intValue(),
                    y-itemSize.intValue()/2 - gameObjectDistance.intValue(),
                    2*itemSize.intValue() + gameObjectDistance.intValue(),
                    2*itemSize.intValue() + gameObjectDistance.intValue()
            );
            minionCollisionBox = new Rectangle(
                    x-minionSize.intValue()/2 - gameObjectDistance.intValue()/2,
                    y-minionSize.intValue()/2 - gameObjectDistance.intValue()/2,
                    2*minionSize.intValue() + gameObjectDistance.intValue(),
                    2*minionSize.intValue() + gameObjectDistance.intValue()
            );

            for (GameObject gameObject : gameObjects){
                if (gameObject.imageView.getBoundsInLocal().intersects(itemCollisionBox.getBoundsInLocal()) == true
                        || gameObject.imageView.getBoundsInLocal().intersects(minionCollisionBox.getBoundsInLocal()) == true){
                    collision = true;
                    break;
                }
            }
            tries++;
        }
        return (tries != 1000) ? new Point(x,y) : null;
    }

    public void setMinionPosition(int minion, int x, int y){
        if(minion >= 0 && minion < minions.size()) {
            minions.get(minion).setPosition(x,y);
        }
    }

    public void updateGameRoutine(){
        for (GameObject minion : minions){
            if (isCollidingWithRedzone(minion) == true){
                // TODO: doSomething;
            } else {
                GameObject minion2 = isCollidingWithMinion(minion);
                if (minion2 != null){
                    // TODO: doSomething();
                }
                GameObject item = isCollidingWithItem(minion);
                if (item != null){
                    itemCollisionHandler(minion, item);
                }
            }
        }
    }

    private boolean isCollidingWithRedzone(GameObject minion){
        if (!rect_GameboardCollisionBox.getBoundsInParent().intersects(minion.imageView.getBoundsInParent())){
            return true;
        }
        return false;
    }

    // TODO: Rewrite: Get called twice 0,1  && 1,0
    private GameObject isCollidingWithMinion(GameObject minion){
        for (GameObject temp : minions){
            if (temp.equals(minion) == false) {
                if (minion.imageView.getBoundsInLocal().intersects(temp.imageView.getBoundsInLocal())) {
                    return temp;
                }
            }
        }
        return null;
    }

    private GameObject isCollidingWithItem(GameObject minion){
        for (GameObject temp : items){
            if (minion.imageView.getBoundsInLocal().intersects(temp.imageView.getBoundsInLocal())) {
                return temp;
            }
        }
        return null;
    }

    private void itemCollisionHandler(GameObject minion, GameObject item){
        switch(item.type){
            case BANANA:
                bananaCollisionHandler(minion,item);
                break;
            case BEEDO:
                beedoCollisionHandler(minion,item);
                break;
            case GOGGLES:
                gogglesCollisionHandler(minion,item);
                break;
        }
    }

    private void bananaCollisionHandler(GameObject minion, GameObject item){
        int index = minions.indexOf(minion);
        points.get(index).set(points.get(index).intValue()+1);
        item.playSound();
        Point p = generateRandomPointOnGameboard();
        item.setPosition(p.x, p.y);
    }

    private void beedoCollisionHandler(GameObject minion, GameObject item){
        item.playSound();
        removeGameObject(item);
    }

    private void gogglesCollisionHandler(GameObject minion, GameObject item){
        item.playSound();
        removeGameObject(item);
    }

    private void removeGameObject(GameObject gObj){
        switch(gObj.type){
            case MINION:
                gameObjects.remove(gObj);
                int index = minions.indexOf(gObj);
                minions.remove(index);
                points.remove(index);
                break;
            default:
                gameObjects.remove(gObj);
                items.remove(gObj);
                break;
        }
    }


    public void generateBanana(){
        Point p = generateRandomPointOnGameboard();
        if (p != null) {
            createGameObject(GameObjectType.BANANA, p.x, p.y);
        }
    }

    public void generateBeedo(){
        Point p = generateRandomPointOnGameboard();
        if (p != null) {
            createGameObject(GameObjectType.BEEDO, p.x, p.y);
        }
    }

    public void generateGoggles(){
        Point p = generateRandomPointOnGameboard();
        if (p != null) {
            createGameObject(GameObjectType.GOGGLES, p.x, p.y);
        }
    }
}
