package Temporary.gameboard;

/**
 * Created by Mark Mauerhofer on 03.12.2016.
 */
public class Point {
    private int _x;
    private int _y;

    public Point(int x, int y){
        _x = x;
        _y = y;
    }

    public int getX(){
        return _x;
    }

    public int getY(){
        return _y;
    }
}
