package Temporary.gameboard;


/**
 * Created by Mark Mauerhofer on 03.12.2016.
 */
public class GameObject {
    private GameObjectType _type;
    private Rectangle _box;

    public GameObject(GameObjectType type, Rectangle box){
        _type = type;
        _box = new Rectangle(box.getX(), box.getY(), box.getWidth(), box.getHeight());
    }

    public GameObjectType getType(){
        return _type;
    }

    public Point getPosition(){
        return _box.getLT();
    }

    public Point getCenter(){
        return _box.getCenter();
    }

    public int getX(){
        return _box.getX();
    }

    public int getY(){
        return _box.getY();
    }

    public void setPosition(Point point){
        _box.setPosition(point);
    }

    public boolean isColliding(Rectangle box){
        return _box.isColliding(box);
    }

    public boolean isCollidingMiddle(GameObject gameObject){
        return _box.contains(gameObject.getCenter());
    }
}
