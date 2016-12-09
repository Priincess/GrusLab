package Temporary.gameObjects;

import org.opencv.core.Point;

/**
 * Created by Mark Mauerhofer on 03.12.2016.
 */
public abstract class GameObject {
	protected CollisionBox _box;

	public GameObject(Point lt, int height, int width) {
		_box = new CollisionBox(lt, width, height);
	}

	public CollisionBox getBox() {
		return _box;
	}

	public void setPosition(Point point) {
		_box.updatePosition(point);
	}

	public boolean isColliding(GameObject object) {
		
		CollisionBox toCompBox = object.getBox();
		
		if (object instanceof Minion) {
			
			if ((_box.getLeftTop().x <= toCompBox.getCenter().x)
					&& (_box.getLeftTop().y <= object.getBox().getCenter().y)
					&& (_box.getRightBottom().x >= object.getBox().getCenter().x)
					&& (_box.getRightBottom().y >= object.getBox().getCenter().y)) {
				return true;
			}
		} else {
			if((_box.getLeftTop().x < toCompBox.getLeftTop().x + toCompBox.getWidth())
					&& ((_box.getLeftTop().x + _box.getWidth()) > toCompBox.getLeftTop().x)
					&& (_box.getLeftTop().y < (toCompBox.getLeftTop().y + toCompBox.getHeight()))
					&& ((_box.getLeftTop().y + _box.getHeight()) > toCompBox.getLeftTop().y)){
				return true;
			}
		}

		return false;
	}
}
