package game.gameboard;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private Rectangle rect_GameboardOutlineBox;

    private IntegerProperty minionSize = new SimpleIntegerProperty(0);  // Overwritten by Preferences
    private IntegerProperty itemSize = new SimpleIntegerProperty(0);    // Overwritten by Preferences

    private IntegerProperty gameObjectDistance = new SimpleIntegerProperty(0);  // Overwritten by Preferences

    private GameObject yellowMinion;
    private GameObject purpleMinion;
    private ObservableList<GameObject> gameObjects;


    public Gameboard(){
        gameboardPreferences = Preferences.userNodeForPackage(this.getClass());
        initGameboardRectangle();
        initGameboardOutlineBox();
        initGameboardCollisionBoxRectangle();

        loadGameboardPreferences();
        gameObjects =  FXCollections.observableArrayList();
        addGameboardChangeListener();
    }


    public Rectangle getRect_Gameboard(){
        return rect_Gameboard;
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

    public GameObject getYellowMinion(){
        return yellowMinion;
    }

    public GameObject getPurpleMinion(){
        return purpleMinion;
    }


    private void initGameboardRectangle(){
        rect_Gameboard = new Rectangle();
        rect_Gameboard.setFill(Color.BLACK);
        rect_Gameboard.setStrokeType(StrokeType.OUTSIDE);
        rect_Gameboard.setStroke(Color.RED);
    }

    private void initGameboardOutlineBox(){
        rect_GameboardOutlineBox = new Rectangle();
        rect_GameboardOutlineBox.xProperty().bind(rect_Gameboard.xProperty());
        rect_GameboardOutlineBox.yProperty().bind(rect_Gameboard.yProperty());
        rect_GameboardOutlineBox.widthProperty().bind(rect_Gameboard.widthProperty());
        rect_GameboardOutlineBox.heightProperty().bind(rect_Gameboard.heightProperty());
    }

    private void initGameboardCollisionBoxRectangle(){
        rect_GameboardCollisionBox = new Rectangle();
        rect_GameboardCollisionBox.setFill(Color.ANTIQUEWHITE);
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
        if (yellowMinion == null && purpleMinion == null) {
            createMinions();
            generateGoggles();
            generateBeedo();
            generateBanana();
            return true;
        }
        return false;
    }

    public void createMinions() {
        if (yellowMinion == null && purpleMinion == null) {
            createGameObject(GameObjectType.YELLOWMINION, 0, 0);
            createGameObject(GameObjectType.PURPLEMINION, 0, 0);
            setMinionStartPosition1();
        }
    }

    public void setMinionStartPosition1(){
        Point[] points = getStartPositions();
        if (yellowMinion != null && purpleMinion != null) {
            setMinionPosition(GameObjectType.YELLOWMINION, points[0]);  // Left Top
            setMinionPosition(GameObjectType.PURPLEMINION, points[2]);  // Right Bottom
        }
    }

    public void setMinionStartPosition2(){
        Point[] points = getStartPositions();
        if (yellowMinion != null && purpleMinion != null) {
            setMinionPosition(GameObjectType.YELLOWMINION, points[1]);  // Right Top
            setMinionPosition(GameObjectType.PURPLEMINION, points[3]);  // Left Bottom
        }
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
        rect_Gameboard.setStrokeWidth(gameboardPreferences.getInt("MINION_SIZE", minionSize.intValue()));

        minionSize.set(gameboardPreferences.getInt("MINION_SIZE", 50));
        itemSize.set(gameboardPreferences.getInt("ITEM_SIZE", 50));
        gameObjectDistance.set(gameboardPreferences.getInt("GAMEOBJECT_DISTANCE", 50));
    }


    public GameObject createGameObject(GameObjectType type, int x, int y){
        GameObject gObj = new GameObject(type, null);
        gObj.setPosition(x, y);
        switch (type){
            case YELLOWMINION:
                gObj.setSize(minionSize);
                yellowMinion = gObj;
                break;
            case PURPLEMINION:
                gObj.setSize(minionSize);
                purpleMinion = gObj;
                break;
            case REFERENCEPOINT:
                gObj.setSize(minionSize);
                break;
            case CAMERAPOINT:
                gObj.setSize(minionSize);
                break;
            default:
                gObj.setSize(itemSize);
        }

        gameObjects.add(gObj);
        return gObj;
    }


    private Point generateRandomPointOnGameboard(){
        int minX = rect_Gameboard.xProperty().intValue();
        int maxX = rect_Gameboard.widthProperty().intValue() + minX - itemSize.intValue();    // substract itemSize otherwise items in bottom/right redzone

        int minY = rect_Gameboard.yProperty().intValue();
        int maxY = rect_Gameboard.heightProperty().intValue() + minY - itemSize.intValue();   // substract itemSize otherwise items in bottom/right redzone

        Random random = new Random();
        int x = 0;
        int y = 0;

        boolean collision = true;
        Rectangle itemCollisionBox;
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

            for (GameObject gameObject : gameObjects){
                if (gameObject.getImageView().getBoundsInLocal().intersects(itemCollisionBox.getBoundsInLocal()) == true){
                    collision = true;
                    break;
                }
            }
            tries++;
        }
        return (tries != 1000) ? new Point(x,y) : new Point((int) rect_GameboardCollisionBox.getX(),(int) rect_Gameboard.getY());
    }

    public void setMinionPosition(GameObjectType minion, Point point){
        int x = (int) point.getX() - minionSize.intValue()/2;   // Camerapoint is center of minion, but gui point is left top corner
        int y = (int) point.getY() - minionSize.intValue()/2;   // Camerapoint is center of minion, but gui point is left top corner

        switch (minion){
            case YELLOWMINION: yellowMinion.setPosition(x,y);
                break;
            case PURPLEMINION: purpleMinion.setPosition(x,y);
        }
    }

    public GameObject isCollidingGameObject(GameObject minion){
        // TODO: Minion Collision
        // 0 == yellow minion && 1 == purple minion
        for (int i = 2; i < gameObjects.size(); i++){
            // collision with item
            GameObject temp = gameObjects.get(i);
            if (temp.getType() != GameObjectType.YELLOWMINION && temp.getType() != GameObjectType.PURPLEMINION) {
                double minionX = minion.getImageView().getX() + minionSize.intValue() / 2;
                double minionY = minion.getImageView().getY() + minionSize.intValue() / 2;
                if(temp.getImageView().contains(minionX, minionY) == true){
                    return temp;
                }
            }
        }
        return null;
    }

    public boolean isOutsideOfGameboard(GameObject minion){
        if (!rect_GameboardOutlineBox.getBoundsInParent().intersects(minion.getImageView().getBoundsInParent())){
            return true;
        }
        return false;
    }

    public void removeGameObject(GameObject gObj){
        switch(gObj.getType()){
            case YELLOWMINION:
                gameObjects.remove(gObj);
                yellowMinion = null;
                break;
            case PURPLEMINION:
                gameObjects.remove(gObj);
                purpleMinion = null;
                break;
            default:
                gameObjects.remove(gObj);
                break;
        }
    }

    public void generateBanana(){
        Point p = generateRandomPointOnGameboard();
        createGameObject(GameObjectType.BANANA, p.x, p.y);
    }

    public void generateBeedo(){
        Point p = generateRandomPointOnGameboard();
        createGameObject(GameObjectType.BEEDO, p.x, p.y);
    }

    public void generateGoggles(){
        Point p = generateRandomPointOnGameboard();
        createGameObject(GameObjectType.GOGGLES, p.x, p.y);
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
            if(gObj.getType() == type){
                if (gObj.getType() == GameObjectType.YELLOWMINION){
                    yellowMinion = null;
                } else if (gObj.getType() == GameObjectType.PURPLEMINION){
                    purpleMinion = null;
                }
                iter.remove();
            }
        }
    }

    public Point[] getStartPositions(){
        int xl = (int)rect_Gameboard.getX() + minionSize.intValue()/2;
        int xr = (int) rect_Gameboard.getX() + rect_Gameboard.widthProperty().intValue() - minionSize.intValue()/2;
        int yt = (int) rect_Gameboard.getY() + minionSize.intValue()/2;
        int yb = (int) rect_Gameboard.getY() + rect_Gameboard.heightProperty().intValue() - minionSize.intValue()/2;
        Point pLT = new Point(xl, yt);
        Point pRT = new Point(xr, yt);
        Point pRB = new Point(xr, yb);
        Point pLB = new Point(xl, yb);
        Point[] startPositions = {pLT, pRT, pRB, pLB};
        return startPositions;
    }

    private void addGameboardChangeListener(){
        rect_Gameboard.xProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                setMinionStartPosition1();
            }
        });
        rect_Gameboard.yProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                setMinionStartPosition1();
            }
        });
        rect_Gameboard.widthProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                setMinionStartPosition1();
            }
        });
        rect_Gameboard.heightProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                setMinionStartPosition1();
            }
        });
    }

}
