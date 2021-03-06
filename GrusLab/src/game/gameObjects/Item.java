package game.gameObjects;

import org.opencv.core.Point;

import game.game.GameObjectType;

public class Item extends GameObject {

	GameObjectType _type;

	public Item(Point lt, int height, int width, GameObjectType type) {
		super(lt, height, width);
		_type = type;
	}

	public GameObjectType getType(){
		return _type;
	}
	
}
