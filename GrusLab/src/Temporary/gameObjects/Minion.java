package Temporary.gameObjects;

import org.opencv.core.Point;

public class Minion extends GameObject {

	
	public Minion(Point lt, int height, int width, int offset) {
		
		super(new Point(lt.x-offset, lt.y - offset), height + (offset * 2) , width + (offset * 2));
		
	}

	@Override
	public boolean isColliding(GameObject object) {
		
		CollisionBox toCompBox = object.getBox();
		
		if((_box.getLeftTop().x < toCompBox.getLeftTop().x + toCompBox.getWidth())
				&& ((_box.getLeftTop().x + _box.getWidth()) > toCompBox.getLeftTop().x)
				&& (_box.getLeftTop().y < (toCompBox.getLeftTop().y + toCompBox.getHeight()))
				&& ((_box.getLeftTop().y + _box.getHeight()) > toCompBox.getLeftTop().y)){
			return true;
		}
		
		return false;
	}

}
