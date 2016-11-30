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

    private Preferences _gameboardPreferences;
    private Rectangle _rect_Gameboard = new Rectangle();
    private boolean _useGameboardPreferences = false;
    private Rectangle _rect_GameboardCollisionBox;
    private Rectangle _rect_GameboardOutlineBox;

    private IntegerProperty _minionSize = new SimpleIntegerProperty(0);  // Overwritten by Preferences
    private IntegerProperty _itemSize = new SimpleIntegerProperty(0);    // Overwritten by Preferences

    private IntegerProperty _gameObjectDistance = new SimpleIntegerProperty(0);  // Overwritten by Preferences

    private GameObject _yellowMinion;
    private GameObject _purpleMinion;
    private ObservableList<GameObject> _gameObjects;


    public Gameboard(){
        _gameboardPreferences = Preferences.userNodeForPackage(this.getClass());
        loadGameboardPreferences();
        initGameboardRectangle();
        initGameboardOutlineBox();
        initGameboardCollisionBoxRectangle();

        _gameObjects =  FXCollections.observableArrayList();
        addGameboardChangeListener();
        addMinionSizeChangeListener();
    }


    public Rectangle getRect_Gameboard(){
        return _rect_Gameboard;
    }

    public boolean getUseGameboardPreferences() { return _useGameboardPreferences; }

    public void setUseGameboardPreferences(boolean value){ _useGameboardPreferences = value; }

    public IntegerProperty getMinionSize(){
        return _minionSize;
    }

    public IntegerProperty getItemSize(){
        return _itemSize;
    }

    public IntegerProperty getGameObjectDistance(){
        return _gameObjectDistance;
    }

    public ObservableList<GameObject> getGameObjects(){
        return _gameObjects;
    }

    public GameObject getYellowMinion(){
        return _yellowMinion;
    }

    public GameObject getPurpleMinion(){
        return _purpleMinion;
    }


    private void initGameboardRectangle(){
        _rect_Gameboard.setFill(Color.BLACK);
        _rect_Gameboard.setStrokeType(StrokeType.OUTSIDE);
        _rect_Gameboard.setStroke(Color.RED);
        int strokeWidth = _minionSize.intValue();
        _rect_Gameboard.setStrokeWidth(strokeWidth/2);
        double x1 = _rect_Gameboard.getStrokeWidth();
        int x = 0;
    }

    private void initGameboardOutlineBox(){
        _rect_GameboardOutlineBox = new Rectangle();
        _rect_GameboardOutlineBox.xProperty().bind(_rect_Gameboard.xProperty());
        _rect_GameboardOutlineBox.yProperty().bind(_rect_Gameboard.yProperty());
        _rect_GameboardOutlineBox.widthProperty().bind(_rect_Gameboard.widthProperty());
        _rect_GameboardOutlineBox.heightProperty().bind(_rect_Gameboard.heightProperty());
    }

    private void initGameboardCollisionBoxRectangle(){
        _rect_GameboardCollisionBox = new Rectangle();
        _rect_GameboardCollisionBox.setFill(Color.ANTIQUEWHITE);
        NumberBinding x = _rect_Gameboard.xProperty().add(new SimpleIntegerProperty(_minionSize.intValue()));
        NumberBinding y = _rect_Gameboard.yProperty().add(new SimpleIntegerProperty(_minionSize.intValue()));
        NumberBinding width = _rect_Gameboard.widthProperty().subtract(new SimpleIntegerProperty(2*_minionSize.intValue()));
        NumberBinding height = _rect_Gameboard.heightProperty().subtract(new SimpleIntegerProperty(2*_minionSize.intValue()));

        _rect_GameboardCollisionBox.xProperty().bind(x);
        _rect_GameboardCollisionBox.yProperty().bind(y);
        _rect_GameboardCollisionBox.widthProperty().bind(width);
        _rect_GameboardCollisionBox.heightProperty().bind(height);
    }

    public boolean gameboardStartSetup(){
        if (_yellowMinion == null && _purpleMinion == null) {
            createMinions();
            generateGoggles();
            generateBeedo();
            generateBanana();
            return true;
        }
        return false;
    }

    public void createMinions() {
        if (_yellowMinion == null && _purpleMinion == null) {
            createGameObject(GameObjectType.YELLOWMINION, 0, 0);
            createGameObject(GameObjectType.PURPLEMINION, 0, 0);
            setMinionStartPosition1();
        }
    }

    public void setMinionStartPosition1(){
        Point[] points = getStartPositions();
        if (_yellowMinion != null && _purpleMinion != null) {
            setMinionPosition(GameObjectType.YELLOWMINION, points[0]);  // Left Top
            setMinionPosition(GameObjectType.PURPLEMINION, points[2]);  // Right Bottom
        }
    }

    public void setMinionStartPosition2(){
        Point[] points = getStartPositions();
        if (_yellowMinion != null && _purpleMinion != null) {
            setMinionPosition(GameObjectType.YELLOWMINION, points[1]);  // Right Top
            setMinionPosition(GameObjectType.PURPLEMINION, points[3]);  // Left Bottom
        }
    }

    public void saveGameboardPreferences(){
        _gameboardPreferences.putInt("GAMEBOARD_X", _rect_Gameboard.xProperty().intValue());
        _gameboardPreferences.putInt("GAMEBOARD_Y", _rect_Gameboard.yProperty().intValue());
        _gameboardPreferences.putInt("GAMEBOARD_WIDTH", _rect_Gameboard.widthProperty().intValue());
        _gameboardPreferences.putInt("GAMEBOARD_HEIGHT", _rect_Gameboard.heightProperty().intValue());
        _gameboardPreferences.putInt("GAMEBOARD_STROKE", (int) _rect_Gameboard.getStrokeWidth());

        _gameboardPreferences.putInt("MINION_SIZE", _minionSize.intValue());
        _gameboardPreferences.putInt("ITEM_SIZE", _itemSize.intValue());
        _gameboardPreferences.putInt("GAMEOBJECT_DISTANCE", _gameObjectDistance.intValue());

    }

    private void loadGameboardPreferences(){
        _rect_Gameboard.setX(_gameboardPreferences.getInt("GAMEBOARD_X", -1));
        _rect_Gameboard.setY(_gameboardPreferences.getInt("GAMEBOARD_Y", -1));
        _rect_Gameboard.setWidth(_gameboardPreferences.getInt("GAMEBOARD_WIDTH", 1000));
        _rect_Gameboard.setHeight(_gameboardPreferences.getInt("GAMEBOARD_HEIGHT", 1000));

        if (_rect_Gameboard.getX() == -1){
            _useGameboardPreferences = false;
        }

        _minionSize.set(_gameboardPreferences.getInt("MINION_SIZE", 50));
        _itemSize.set(_gameboardPreferences.getInt("ITEM_SIZE", 50));
        _gameObjectDistance.set(_gameboardPreferences.getInt("GAMEOBJECT_DISTANCE", 50));
    }


    public GameObject createGameObject(GameObjectType type, int x, int y){
        GameObject gObj = new GameObject(type, null);
        gObj.setPosition(x, y);
        switch (type){
            case YELLOWMINION:
                gObj.setSize(_minionSize);
                _yellowMinion = gObj;
                break;
            case PURPLEMINION:
                gObj.setSize(_minionSize);
                _purpleMinion = gObj;
                break;
            case REFERENCEPOINT:
                gObj.setSize(_minionSize);
                break;
            case CAMERAPOINT:
                gObj.setSize(_minionSize);
                break;
            default:
                gObj.setSize(_itemSize);
        }

        _gameObjects.add(gObj);
        return gObj;
    }


    private Point generateRandomPointOnGameboard(){
        int minX = _rect_Gameboard.xProperty().intValue();
        int maxX = _rect_Gameboard.widthProperty().intValue() + minX - _itemSize.intValue();    // substract itemSize otherwise items in bottom/right redzone

        int minY = _rect_Gameboard.yProperty().intValue();
        int maxY = _rect_Gameboard.heightProperty().intValue() + minY - _itemSize.intValue();   // substract itemSize otherwise items in bottom/right redzone

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
                    x-_itemSize.intValue()/2 - _gameObjectDistance.intValue(),
                    y-_itemSize.intValue()/2 - _gameObjectDistance.intValue(),
                    2*_itemSize.intValue() + _gameObjectDistance.intValue(),
                    2*_itemSize.intValue() + _gameObjectDistance.intValue()
            );

            for (GameObject gameObject : _gameObjects){
                if (gameObject.getImageView().getBoundsInLocal().intersects(itemCollisionBox.getBoundsInLocal()) == true){
                    collision = true;
                    break;
                }
            }
            tries++;
        }
        return new Point(x,y); // After 1000 tries, just use the random point despite of collision, so that the game can go on
    }

    public void setMinionPosition(GameObjectType minion, Point point){
        int x = (int) point.getX() - _minionSize.intValue()/2;   // Camerapoint is center of minion, but gui point is left top corner
        int y = (int) point.getY() - _minionSize.intValue()/2;   // Camerapoint is center of minion, but gui point is left top corner

        switch (minion){
            case YELLOWMINION: _yellowMinion.setPosition(x,y);
                break;
            case PURPLEMINION: _purpleMinion.setPosition(x,y);
        }
    }

    public GameObject isCollidingGameObject(GameObject minion){
        // TODO: Minion Collision
        // 0 == yellow minion && 1 == purple minion
        for (int i = 2; i < _gameObjects.size(); i++){
            // collision with item
            GameObject temp = _gameObjects.get(i);
            if (temp.getType() != GameObjectType.YELLOWMINION && temp.getType() != GameObjectType.PURPLEMINION) {
                double minionX = minion.getImageView().getX() + _minionSize.intValue() / 2;
                double minionY = minion.getImageView().getY() + _minionSize.intValue() / 2;
                if(temp.getImageView().contains(minionX, minionY) == true){
                    return temp;
                }
            }
        }
        return null;
    }

    public boolean isOutsideOfGameboard(GameObject minion){
        if (!_rect_GameboardOutlineBox.getBoundsInParent().intersects(minion.getImageView().getBoundsInParent())){
            return true;
        }
        return false;
    }

    public void removeGameObject(GameObject gObj){
        switch(gObj.getType()){
            case YELLOWMINION:
                _gameObjects.remove(gObj);
                _yellowMinion = null;
                break;
            case PURPLEMINION:
                _gameObjects.remove(gObj);
                _purpleMinion = null;
                break;
            default:
                _gameObjects.remove(gObj);
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
        Iterator<GameObject> iter = _gameObjects.iterator();
        while (iter.hasNext()){
            GameObject gObj = iter.next();
            if(gObj.getType() == type){
                if (gObj.getType() == GameObjectType.YELLOWMINION){
                    _yellowMinion = null;
                } else if (gObj.getType() == GameObjectType.PURPLEMINION){
                    _purpleMinion = null;
                }
                iter.remove();
            }
        }
    }

    public Point[] getStartPositions(){
        int xl = (int) _rect_Gameboard.getX() + _minionSize.intValue()/2;
        int xr = (int) _rect_Gameboard.getX() + _rect_Gameboard.widthProperty().intValue() - _minionSize.intValue()/2;
        int yt = (int) _rect_Gameboard.getY() + _minionSize.intValue()/2;
        int yb = (int) _rect_Gameboard.getY() + _rect_Gameboard.heightProperty().intValue() - _minionSize.intValue()/2;
        Point pLT = new Point(xl, yt);
        Point pRT = new Point(xr, yt);
        Point pRB = new Point(xr, yb);
        Point pLB = new Point(xl, yb);
        Point[] startPositions = {pLT, pRT, pRB, pLB};
        return startPositions;
    }

    private void addGameboardChangeListener(){
        _rect_Gameboard.xProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
                setMinionStartPosition1();
            }
        });
        _rect_Gameboard.yProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
                setMinionStartPosition1();
            }
        });
        _rect_Gameboard.widthProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
                setMinionStartPosition1();
            }
        });
        _rect_Gameboard.heightProperty().addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal, Object newVal){
                setMinionStartPosition1();
            }
        });
    }

    private void addMinionSizeChangeListener(){
        _minionSize.addListener(new ChangeListener(){
            @Override public void changed(ObservableValue o, Object oldVal,
                                          Object newVal){
                setMinionStartPosition1();
                _rect_Gameboard.setStrokeWidth( _minionSize.intValue()/2);
            }
        });
    }

}
