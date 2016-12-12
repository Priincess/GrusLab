package game.gameObjects;

import org.opencv.core.Point;

public interface I_CollisionBox {

	public Point getLeftTop();

	public Point getRightBottom();

	public int getWidth();
	
	public Point getCenter();
			
	public int getHeight();
	
	
}
