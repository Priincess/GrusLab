package game.gameboard;

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
import java.util.*;
import java.util.prefs.Preferences;


/**
 * Created by Mark Mauerhofer on 08.10.2016.
 */


public class Gameboard {

    private Preferences gameboardPreferences;
    private Rectangle rect_Gameboard;
    private Rectangle rect_GameboardCollisionBox;

    private IntegerProperty minionSize = new SimpleIntegerProperty(0);  // Overwritten by Preferences
    private IntegerProperty itemSize = new SimpleIntegerProperty(0);    // Overwritten by Preferences

    private IntegerProperty gameObjectDistance = new SimpleIntegerProperty(0);  // Overwritten by Preferences

    private ObservableList<GameObject> gameObjects;
    private ArrayList<GameObject> minions;
    private ArrayList<GameObject> items;


    public Gameboard(){
        gameboardPreferences = Preferences.userNodeForPackage(this.getClass());
        initGameboardRectangle();
        loadGameboardPreferences();

        initGameboardCollisionBoxRectangle();

        gameObjects = FXCollections.observableArrayList();
        minions = new ArrayList<GameObject>();
        items = new ArrayList<GameObject>();
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

    public ArrayList<GameObject> getMinions() { return minions; }


    private void initGameboardRectangle(){
        rect_Gameboard = new Rectangle();
        rect_Gameboard.setFill(Color.BLACK);
        rect_Gameboard.setStrokeType(StrokeType.OUTSIDE);
        rect_Gameboard.setStroke(Color.RED);
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

    public boolean gameboardStartSetup(){
        if (minions.size() == 0) {
            createGameObject(GameObjectType.MINION, (int) rect_GameboardCollisionBox.getX(), (int) rect_GameboardCollisionBox.getY());
            createGameObject(GameObjectType.MINION, rect_GameboardCollisionBox.widthProperty().intValue() + minionSize.intValue(), rect_GameboardCollisionBox.heightProperty().intValue() + 2*minionSize.intValue());
            generateGoggles();
            generateBeedo();
            generateBanana();
            return true;
        }
        return false;
    }

    public void saveGameboardPreferences(){
        gameboardPreferences.putInt("GAMEBOARD_X", rect_Gameboard.xProperty().intValue());
        gameboardPreferences.putInt("GAMEBOARD_Y", rect_Gameboard.yProperty().intValue());
        gameboardPreferences.putInt("GAMEBOARD_WIDTH", rect_Gameboard.widthProperty().intValue());
        gameboardPreferences.putInt("GAMEBOARD_HEIGHT", rect_Gameboard.heightProperty().intValue());
        gameboardPreferences.putInt("GAMEBOARD_STROKE", (int) rect_Gameboard.getStrokeWidth());

        gameboardPreferences.putInt("MINION_SIZE", minionSize.intValue());
        gameboardPreferences.putInt("ITEM_SIZE", itemSize.intValue());
        gameboardPreferences.putInt("GAMEOBJECT_DISTANCE", gameObjectDistance.intValue());

    }

    private void loadGameboardPreferences(){
        rect_Gameboard.setX(gameboardPreferences.getInt("GAMEBOARD_X", 50));
        rect_Gameboard.setY(gameboardPreferences.getInt("GAMEBOARD_Y", 90));
        rect_Gameboard.setWidth(gameboardPreferences.getInt("GAMEBOARD_WIDTH", 800));
        rect_Gameboard.setHeight(gameboardPreferences.getInt("GAMEBOARD_HEIGHT", 400));
        rect_Gameboard.setStrokeWidth(gameboardPreferences.getInt("GAMEBOARD_STROKE", 50));

        minionSize.set(gameboardPreferences.getInt("MINION_SIZE", 50));
        itemSize.set(gameboardPreferences.getInt("ITEM_SIZE", 50));
        gameObjectDistance.set(gameboardPreferences.getInt("GAMEOBJECT_DISTANCE", 50));
    }


    public GameObject createGameObject(GameObjectType type, int x, int y){
        GameObject gObj = new GameObject(type, null);
        gObj.setPosition(x, y);
        switch (type){
            case MINION:
                gObj.setSize(minionSize);
                minions.add(gObj);
                break;
            case REFERENCEPOINT:
                gObj.setSize(minionSize);
                break;
            case CAMERAPOINT:
                gObj.setSize(minionSize);
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

    public boolean isOutsideOfGameboard(GameObject minion){
        if (!rect_GameboardCollisionBox.getBoundsInParent().intersects(minion.imageView.getBoundsInParent())){
            return true;
        }
        return false;
    }

    // TODO: Rewrite: Get called twice 0,1  && 1,0
    public GameObject isCollidingWithMinion(GameObject minion){
        for (GameObject temp : minions){
            if (temp.equals(minion) == false) {
                if (minion.imageView.getBoundsInLocal().intersects(temp.imageView.getBoundsInLocal())) {
                    return temp;
                }
            }
        }
        return null;
    }

    public GameObject isCollidingWithItem(GameObject minion){
        for (GameObject temp : items){
            if (minion.imageView.getBoundsInLocal().intersects(temp.imageView.getBoundsInLocal())) {
                return temp;
            }
        }
        return null;
    }

    public void removeGameObject(GameObject gObj){
        switch(gObj.type){
            case MINION:
                gameObjects.remove(gObj);
                int index = minions.indexOf(gObj);
                minions.remove(index);
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


    public void addObjects(LinkedList<Point> points, GameObjectType type){
        for(Point point : points){
            createGameObject(type, (int) point.getX(), (int) point.getY());
        }
    }

    public void removeObjects(GameObjectType type){
        Iterator<GameObject> iter = gameObjects.iterator();
        while (iter.hasNext()){
            GameObject gObj = iter.next();
            if(gObj.type == type){
                if (gObj.type == GameObjectType.MINION){
                    minions.remove(gObj);
                }
                iter.remove();
            }
        }
    }
}
