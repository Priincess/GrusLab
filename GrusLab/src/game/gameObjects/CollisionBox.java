package game.gameObjects;

import org.opencv.core.Point;

/**
 * Created by Mark Mauerhofer on 03.12.2016.
 */
public class CollisionBox implements I_CollisionBox{
	private Point _leftTop;
	private Point _rightBottom;
	private Point _center;

	public CollisionBox(Point lt, int width, int height) {
		_leftTop = lt;
		_rightBottom = new Point(lt.x + width, lt.y + height);

		_center = new Point((_leftTop.x + getWidth() / 2), (_leftTop.y + getHeight() / 2));
	}

	public Point getCenter() {
		return _center;
	}

	public Point getLeftTop() {
		return _leftTop;
	}

	public Point getRightBottom() {
		return _rightBottom;
	}

	public int getWidth() {
		return (int) (_rightBottom.x - _leftTop.x);
	}

	public int getHeight() {
		return (int) (_rightBottom.y - _leftTop.y);
	}

	public void updatePosition(Point point) {

		_center = point;

		setNewTopLeft((int) (point.x - (getWidth() / 2)), (int) (point.y - (getHeight() / 2)));
		setNewBottomRight((int) point.x + (getHeight() / 2), (int) (point.y + (getHeight() / 2)));

	}

	private void setNewTopLeft(int x, int y) {
		_leftTop.x = x;
		_leftTop.y = y;
	}

	private void setNewBottomRight(int x, int y) {
		_rightBottom.x = x;
		_rightBottom.y = y;
	}

}
