package Temporary.gameboard;

import Temporary.Settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;

/**
 * Created by Mark Mauerhofer on 03.12.2016.
 */
public class Gameboard {

    private static final int MINION_COUNT = 2;
    private static final int YELLOW_MINION = 0;
    private static final int PURPLE_MINION = 1;

    private Preferences _gameboardPreferences;
    private Rectangle _rectGameboard;
    private Rectangle _rectMinion;
    private Rectangle _rectBanana;
    private Rectangle _rectBeedo;
    private Rectangle _rectGoggle;
    private int _gameObjectDistance;    // minimum distance between gameobjects

    private List<GameObject> _gameObjects;


    public Gameboard(){
        _gameboardPreferences = Preferences.userRoot().node(Settings.SETTINGSPATH);
        loadGameboardPreferences();

        _gameObjects = new ArrayList<GameObject>();
    }


    private void loadGameboardPreferences(){
        int gameboardX = _gameboardPreferences.getInt(Settings.GAMEBOARD_X, Settings.GAMEBOARD_X_DEFAULT);
        int gameboardY = _gameboardPreferences.getInt(Settings.GAMEBOARD_Y, Settings.GAMEBOARD_Y_DEFAULT);
        int gameboardWidth = _gameboardPreferences.getInt(Settings.GAMEBOARD_WIDTH, Settings.GAMEBOARD_WIDTH_DEFAULT);
        int gameboardHeight = _gameboardPreferences.getInt(Settings.GAMEBOARD_HEIGHT, Settings.GAMEBOARD_HEIGHT_DEFAULT);

        int minionWidth  = _gameboardPreferences.getInt(Settings.MINION_WIDTH, Settings.MINION_WIDTH_DEFAULT);
        int minionHeight  = _gameboardPreferences.getInt(Settings.MINION_HEIGHT, Settings.MINION_HEIGHT_DEFAULT);

        int bananaWidth  = _gameboardPreferences.getInt(Settings.BANANA_WIDTH, Settings.BANANA_WIDTH_DEFAULT);
        int bananaHeight  = _gameboardPreferences.getInt(Settings.BANANA_HEIGHT, Settings.BANANA_HEIGHT_DEFAULT);

        int goggleWidth  = _gameboardPreferences.getInt(Settings.GOGGLE_WIDTH, Settings.GOGGLE_WIDTH_DEFAULT);
        int goggleHeight  = _gameboardPreferences.getInt(Settings.GOGGLE_HEIGHT, Settings.GOGGLE_HEIGHT_DEFAULT);

        int beedoWidth  = _gameboardPreferences.getInt(Settings.BEEDO_WIDTH, Settings.BEEDO_WIDTH_DEFAULT);
        int beedoHeight  = _gameboardPreferences.getInt(Settings.BEEDO_HEIGHT, Settings.BEEDO_HEIGHT_DEFAULT);

        int _gameObjectDistance = _gameboardPreferences.getInt(Settings.GAMEOBJECT_DISTANCE, Settings.GAMEOBJECT_DISTANCE_DEFAULT);

        _rectGameboard = new Rectangle(gameboardX, gameboardY, gameboardWidth, gameboardHeight);
        _rectMinion = new Rectangle(0, 0, minionWidth, minionHeight);
        _rectBanana = new Rectangle(0, 0, bananaWidth, bananaHeight);
        _rectGoggle = new Rectangle (0,0, goggleWidth, goggleHeight);
        _rectBeedo = new Rectangle(0, 0, beedoWidth, beedoHeight);
    }

    public GameObject getYellowMinion(){
        if (_gameObjects != null || _gameObjects.size() > 0) {
            return _gameObjects.get(YELLOW_MINION);
        }
        return null;
    }

    public GameObject getPurpleMinion(){
        if (_gameObjects != null || _gameObjects.size() > 1) {
            return _gameObjects.get(PURPLE_MINION);
        }
        return null;
    }

    public void createMinions() {
        createGameObject(GameObjectType.MINION, new Point(0,0));
        createGameObject(GameObjectType.MINION, new Point(0,0));
        setMinionStartPosition();
    }


    public void gameboardStartSetup(){
        createMinions();
        generateItem(GameObjectType.BANANA);
        generateItem(GameObjectType.GOGGLES);
        generateItem(GameObjectType.BEEDO);
    }

    public void setMinionStartPosition(){
        GameObject yellowMinion = getYellowMinion();
        GameObject purpleMinion = getPurpleMinion();
        if (yellowMinion != null && purpleMinion != null) {
            Point yellowStartPosition = _rectGameboard.getLT();
            Point purpleStartPosition = new Point(_rectGameboard.getRB().getX()-_rectMinion.getWidth(), _rectGameboard.getRB().getY()-_rectMinion.getHeight());
            setMinionPosition(GameObjectType.YELLOWMINION, yellowStartPosition);
            setMinionPosition(GameObjectType.PURPLEMINION, purpleStartPosition);
        }
    }

    private Point generateRandomPoint(GameObjectType type){
        int width = 0;
        int height = 0;
        switch(type){
            case BANANA:
                width = _rectBanana.getWidth();
                height = _rectBanana.getHeight();
                break;
            case GOGGLES:
                width = _rectGoggle.getWidth();
                height = _rectGoggle.getHeight();
                break;
            case BEEDO:
                width = _rectBeedo.getWidth();
                height = _rectBeedo.getHeight();
                break;
            default:
                break;
        }
        // plus-minus item-size, so there is a minimum distance between item and borderline of gameboard
        int minX = _rectGameboard.getX() + width;
        int maxX = _rectGameboard.getX() + _rectGameboard.getWidth() - width;

        int minY = _rectGameboard.getY() + height;
        int maxY = _rectGameboard.getY() + _rectGameboard.getHeight() - height;

        Random random = new Random();
        int x = 0;
        int y = 0;

        boolean collision = true;
        Rectangle tempItemBox;
        int tries = 0;

        while (collision == true && tries < 1000){
            collision = false;
            x = random.nextInt(maxX - minX + 1) + minX;
            y = random.nextInt(maxY - minY + 1) + minY;

            // make itembox bigger
            x = x - _gameObjectDistance/2;
            y = y - _gameObjectDistance/2;
            int tempWidth = width + _gameObjectDistance;
            int tempHeight = height + _gameObjectDistance;
            tempItemBox = new Rectangle(x, y, width, height);

            for (GameObject gameObject : _gameObjects){
                collision = gameObject.isColliding(tempItemBox);
                break;  // collision happened -> generate new point
            }
            tries++;
        }
        return new Point(x,y); // After 1000 tries, just use the random point despite of collision, so that the game can go on
    }

    private GameObject createGameObject(GameObjectType type, Point point){
        GameObject gameObject = null;
        switch (type){
            case MINION:
                if (_gameObjects.size() < 2) {
                    gameObject = new GameObject(type, _rectMinion);
                }
                break;
            case BANANA:
                gameObject = new GameObject(type, _rectBanana);
                break;
            case GOGGLES:
                gameObject = new GameObject(type, _rectGoggle);
                break;
            case BEEDO:
                gameObject = new GameObject(type, _rectBeedo);
                break;
            default:
                break;
        }
        if (gameObject != null) {
            _gameObjects.add(gameObject);
        }
        return gameObject;
    }

    public void generateItem(GameObjectType type){
        if (type != null){
            Point point = generateRandomPoint(type);
            createGameObject(type, point);
        }
    }

//    TODO: use global enum (yellow/purple)
    public void setMinionPosition(GameObjectType minion, Point point){
        int x = point.getX() - _rectMinion.getWidth()/2;   // Camerapoint is center of minion, but gui point is left top corner
        int y = point.getY() - _rectMinion.getHeight()/2;  // Camerapoint is center of minion, but gui point is left top corner
        Point position = new Point(x,y);
        switch (minion){
            case YELLOWMINION: getYellowMinion().setPosition(position);
                break;
            case PURPLEMINION: getPurpleMinion().setPosition(position);
                break;
            default:
                break;
        }
    }

    public GameObject collidingWithGameObject(GameObject minion){
        // TODO: Minion Collision
        // 0 == yellow minion && 1 == purple minion
        for (int i = MINION_COUNT; i < _gameObjects.size(); i++){
            // collision with item
            GameObject temp = _gameObjects.get(i);
            if(temp.isCollidingMiddle(minion)){
                return temp;
            }
        }
        return null;
    }

    // half of the minion is outside
    public boolean isOutsideOfGameboard(GameObject minion){
        if (!_rectGameboard.contains(minion.getCenter())){
            return true;
        }
        return false;
    }

    public void removeGameObject(GameObject gObj){
        _gameObjects.remove(gObj);
    }

    public void removeObjects(GameObjectType type){
        Iterator<GameObject> iter = _gameObjects.iterator();
        while (iter.hasNext()){
            GameObject gObj = iter.next();
            if(gObj.getType() == type){
                iter.remove();
            }
        }
    }
}
