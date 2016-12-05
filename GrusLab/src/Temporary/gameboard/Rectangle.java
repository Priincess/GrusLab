package Temporary.gameboard;

/**
 * Created by Mark Mauerhofer on 03.12.2016.
 */
public class Rectangle {
    private Point _lt;
    private Point _rb;

    public Rectangle(int x, int y, int width, int height){
        _lt = new Point(x,y);
        _rb = new Point(x+width, y+height);
    }

    public Point getCenter(){
        return new Point(_lt.getX() + getWidth()/2, _rb.getY() + getHeight()/2);
    }

    public Point getLT(){
        return _lt;
    }

    public Point getRB(){
        return _rb;
    }

    public int getWidth(){
        return _rb.getX() - _lt.getX();
    }

    public int getHeight(){
        return _rb.getY() - _lt.getY();
    }

    public int getX(){
        return _lt.getX();
    }

    public int getY(){
        return _lt.getY();
    }

    public void setPosition(Point point){
        int width = getWidth();
        int height = getHeight();
        _lt = point;
        _rb = new Point(_lt.getX()+width, _rb.getY()+height);
    }

    public boolean isColliding(Rectangle box){
        return !(box.getLT().getX() > _rb.getX() ||
                box.getRB().getX() < _lt.getX() ||
                box.getLT().getY() > _rb.getY()||
                box.getRB().getY() < _lt.getY());
    }

    public boolean contains(Point point){
        return (_lt.getX() < point.getX() && _lt.getY() < point.getY() &&
                _rb.getX() > point.getX() && _rb.getY() > point.getY());
    }


}
