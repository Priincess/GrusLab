package game.game;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;

import org.opencv.core.Point;

import java.util.LinkedList;

/**
 * Created by Mark Mauerhofer on 17.10.2016.
 */
public class GameboardScaler {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private Point _camLT; // leftTopCorner
	private Point _camRT; // rightTopCorner
	private Point _camRB; // rightBottomCorner
	private Point _camLB; // leftBottomCorner

	private Point _gameLT; // leftTopCorner
	private Point _gameRT; // rightTopCorner
	private Point _gameRB; // rightBottomCorner
	private Point _gameLB; // leftBottomCorner

	private Mat _homography;

	private boolean _isInitialized = false;

	public boolean isInitialized(){ return _isInitialized; }


	public Point transformCameraPointToGameboardPoint(Point cameraPoint) {
		if (cameraPoint == null || !_isInitialized) {
			return null;
		}
		Mat src = new Mat();
		Mat dst = new Mat();
		src.push_back(new MatOfPoint2f(cameraPoint));
		Core.perspectiveTransform(src, dst, _homography);
		double[] points = dst.get(0, 0);
		return new Point((int) points[0], (int) points[1]);
	}

	public void initScaler() {
		if (_camRT == null && _camLB == null) { // Mirror points
			_camRT = new Point(_camRB.x, _camLT.y);
			_camLB = new Point(_camLT.x, _camRB.y);
		}
		if (_camLT.x != -1) { // calibration points not set
			getPerspective();
			_isInitialized = true;
		}
	}

	public void setCameraPoints(Point camLT, Point camRB) {
		_camLT = camLT;
		_camRB = camRB;
	}

	public void setGamePoints(Point gameLT, Point gameRT, Point gameRB, Point gameLB) {
		_gameLT = new Point(gameLT.x, gameLT.y);
		_gameRT = new Point(gameRT.x, gameRT.y);
		_gameRB = new Point(gameRB.x, gameRB.y);
		_gameLB = new Point(gameLB.x, gameLB.y);
	}

	private void getPerspective() {
		// source quadrangle == camera quadrangle
		LinkedList<Point> srcList = new LinkedList<Point>();
		srcList.add(_camLT);
		srcList.add(_camRT);
		srcList.add(_camRB);
		srcList.add(_camLB);

		// transformed quadrangle == virtual gameboard
		LinkedList<Point> dstList = new LinkedList<Point>();
		dstList.add(_gameLT);
		dstList.add(_gameRT);
		dstList.add(_gameRB);
		dstList.add(_gameLB);

		MatOfPoint2f src = new MatOfPoint2f();
		src.fromList(srcList);

		MatOfPoint2f dst = new MatOfPoint2f();
		dst.fromList(dstList);
		// Find Homography
		_homography = Calib3d.findHomography(src, dst);
	}

}
