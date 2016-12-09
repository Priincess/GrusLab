package Temporary.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.opencv.core.Point;

import Temporary.gameObjects.GameObject;
import Temporary.gameObjects.Item;
import Temporary.gameObjects.Minion;

/**
 * Created by Mark Mauerhofer on 03.12.2016.
 */
public class Gameboard {

	private static int MINION_HEIGHT = 10;
	private static int MINION_WIDTH = 10;

	private static int START_POS_X_YELLOW = 0;
	private static int START_POS_Y_YELLOW = 0;
	private static int START_POS_X_PURPLE = 0;
	private static int START_POS_Y_PURPLE = 0;

	private Point _startPoint;
	private int _width;
	private int _height;

	private Minion _yellowMinion;
	private Minion _purpleMinion;

	private List<Item> _items;

	public Gameboard(int width, int height) {

		_width = width;
		_height = height;
		_items = new ArrayList<Item>();

		// _yellowMinion = new Minion(x, y, height, width, offset);
		// _purpleMinion = new Minion(x, y, height, width, offset);
		//
	}

	public List<GameObject> getGameObjects() {
		List<GameObject> objects = new ArrayList<>();

		objects.addAll(_items);
		objects.add(_yellowMinion);
		objects.add(_purpleMinion);

		return objects;
	}

	// TODO: offset?
	public void createMinions(int offset) {
		_yellowMinion = new Minion(new Point(START_POS_X_YELLOW, START_POS_Y_YELLOW), MINION_HEIGHT, MINION_WIDTH, offset);
		_purpleMinion = new Minion(new Point(START_POS_X_PURPLE, START_POS_Y_PURPLE), MINION_HEIGHT, MINION_WIDTH, offset);

	}

	public void createItem(GameObjectType item, int itemWidth, int itemHeight) {

		Point pos = generateRandomPoint(itemHeight, itemWidth);
		Item obj=null;
		// TODO: größe?
		switch (item) {
		case BANANA:
			obj = new Item(pos, itemHeight, itemWidth, GameObjectType.BANANA);
			break;
		case GOGGLES:
			obj = new Item(pos, itemHeight, itemWidth, GameObjectType.GOGGLES);
			break;
		case BEEDO:
			obj = new Item(pos, itemHeight, itemWidth, GameObjectType.BEEDO);
			break;
		default:
			break;
		}
		
		List<GameObject> gos = getGameObjects();
		
		
		int tries =0;
		for(int i = 0; i< gos.size() && tries <= 100; i++){
			if(obj.isColliding(gos.get(i))){
				obj.setPosition(generateRandomPoint(itemHeight, itemWidth));
				tries++;
				i = -1;
			}
		}
		
		_items.add(obj);

	}

	public void setMinionPosition(GameObjectType minion, Point point) {

		switch (minion) {

		case PURPLEMINION:
			if (_purpleMinion != null) {
				_purpleMinion.setPosition(point);
			}
			break;
		case YELLOWMINION:
			if (_yellowMinion != null) {
				_yellowMinion.setPosition(point);
			}
			break;

		default:
			break;
		}
	}

	// half of the minion is outside
	// TODO: not valid
	public boolean isOutsideOfGameboard() {

		if (yellowMinionOutOfBoard() || purpleMinionOutOfBoard()) {

			return true;
		}
		return false;
	}

	public GameObjectType CollidesWithMinion(GameObjectType miniontype) {

		GameObject minion;

		if (miniontype.equals(GameObjectType.PURPLEMINION)) {

			minion = _purpleMinion;

		} else {

			minion = _yellowMinion;

		}

		for (int i = 0; i < _items.size(); i++) {

			Item collider = _items.get(i);

			if (collider.isColliding(minion)) {
				//item is collected - remove from list
				_items.remove(collider);
				return collider.getType();

			}
		}
		// TODO: nimu nimu not valid value
		return null;
	}
	
	public boolean checkIfMinionsTooClose(){
		return _yellowMinion.isColliding(_purpleMinion);
	}

	// TODO: wieso schreit der do nid? ^^"
	public void removeItem(Item gObj) {
		_items.remove(gObj);
	}

	public void removeObjectsFromType(GameObjectType type) {
		Iterator<Item> iter = _items.iterator();
		while (iter.hasNext()) {
			GameObjectType gObj = iter.next().getType();
			if (gObj == type) {
				iter.remove();
			}
		}
	}

	public void removeAllItems() {
		_items.clear();

	}
	
	public boolean containsObjectType(GameObjectType objType){
		
		for(Item object : _items){
			if(object.getType().equals(objType)){
				return true;
			}
		}
		return false;
		
	}

	private boolean yellowMinionOutOfBoard() {

		if ((_yellowMinion.getBox().getCenter().x > _startPoint.x)
				&& (_yellowMinion.getBox().getCenter().x < (_startPoint.x + _width))
				&& (_yellowMinion.getBox().getCenter().y > _startPoint.y)
				&& (_yellowMinion.getBox().getCenter().y < (_startPoint.y + _height))) {
			return true;
		}
		return false;
	}

	private boolean purpleMinionOutOfBoard() {
		if ((_purpleMinion.getBox().getCenter().x > _startPoint.x)
				&& (_purpleMinion.getBox().getCenter().x < (_startPoint.x + _width))
				&& (_purpleMinion.getBox().getCenter().y > _startPoint.y)
				&& (_purpleMinion.getBox().getCenter().y < (_startPoint.y + _height))) {
			return true;
		}
		return false;
	}

	private Point generateRandomPoint(int height, int width){
		
		
		 Random random = new Random();
		 int x = (int)(random.nextInt((int)(_width-width))+_startPoint.x);
		 int y = (int)(random.nextInt((int)(_height-height))+_startPoint.y);
						
		return new Point(x,y);
	}
}
