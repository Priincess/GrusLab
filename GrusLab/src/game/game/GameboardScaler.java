package game.game;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;

import org.opencv.core.Point;

import java.util.LinkedList;

/**
 * Created by Mark Mauerhofer on 17.10.2016.
 */
public class GameboardScaler {

	// // Compulsory
	
	//TODO: Points -  X&Y
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

	// private Preferences _scalerPreferences;
	private boolean _isInitialized = false;

	private void loadScalerPreferences() {
		// _camLT = new Point(_scalerPreferences.getInt("CAMLT_X", -1),
		// _scalerPreferences.getInt("CAMLT_Y", -1));
		// _camRT = new Point(_scalerPreferences.getInt("CAMRT_X", -1),
		// _scalerPreferences.getInt("CAMRT_Y", -1));
		// _camRB = new Point(_scalerPreferences.getInt("CAMRB_X", -1),
		// _scalerPreferences.getInt("CAMRB_Y", -1));
		// _camLB = new Point(_scalerPreferences.getInt("CAMLB_X", -1),
		// _scalerPreferences.getInt("CAMLB_Y", -1));
		//
		// _gameLT = new Point(_scalerPreferences.getInt("GAMELT_X", -1),
		// _scalerPreferences.getInt("GAMELT_Y", -1));
		// _gameRT = new Point(_scalerPreferences.getInt("GAMERT_X", -1),
		// _scalerPreferences.getInt("GAMERT_Y", -1));
		// _gameRB = new Point(_scalerPreferences.getInt("GAMERB_X", -1),
		// _scalerPreferences.getInt("GAMERB_Y", -1));
		// _gameLB = new Point(_scalerPreferences.getInt("GAMELB_X", -1),
		// _scalerPreferences.getInt("GAMELB_Y", -1));
	}

	public void saveScalerPreferences() {
		// _scalerPreferences.putInt("CAMLT_X", (int) _camLT.x);
		// _scalerPreferences.putInt("CAMLT_Y", (int) _camLT.y);
		// _scalerPreferences.putInt("CAMRT_X", (int) _camRT.x);
		// _scalerPreferences.putInt("CAMRT_Y", (int) _camRT.y);
		// _scalerPreferences.putInt("CAMRB_X", (int) _camRB.x);
		// _scalerPreferences.putInt("CAMRB_Y", (int) _camRB.y);
		// _scalerPreferences.putInt("CAMLB_X", (int) _camLB.x);
		// _scalerPreferences.putInt("CAMLB_Y", (int) _camLB.y);
		//
		// _scalerPreferences.putInt("GAMELT_X", (int) _gameLT.x);
		// _scalerPreferences.putInt("GAMELT_Y", (int) _gameLT.y);
		// _scalerPreferences.putInt("GAMERT_X", (int) _gameRT.x);
		// _scalerPreferences.putInt("GAMERT_Y", (int) _gameRT.y);
		// _scalerPreferences.putInt("GAMERB_X", (int) _gameRB.x);
		// _scalerPreferences.putInt("GAMERB_Y", (int) _gameRB.y);
		// _scalerPreferences.putInt("GAMELB_X", (int) _gameLB.x);
		// _scalerPreferences.putInt("GAMELB_Y", (int) _gameLB.y);
	}

	public Point transformCameraPointToGameboardPoint(Point cameraPoint) {
		if (cameraPoint == null || _isInitialized == false) {
			return null;
		}
		Mat src = new Mat();
		Mat dst = new Mat();
		src.push_back(new MatOfPoint2f(cameraPoint));
		Core.perspectiveTransform(src, dst, _homography);
		double[] points = dst.get(0, 0);
		Point point = new Point((int) points[0], (int) points[1]);
		return point;
	}

	public void initScaler() {
		loadScalerPreferences();
		if (_camRT == null && _camLB == null) { // Mirror points
			_camRT = new Point(_camRB.x, _camLT.y);
			_camLB = new Point(_camLT.x, _camRB.y);
		}
		if (_camLT.x != -1) { // calibration points not set
			getPerspective();
			_isInitialized = true;
		}
	}

	// TODO: check why not called at init
	private void setCameraPoints(Point camLT, Point camRT, Point camRB, Point camLB) {
		_camLT = camLT;
		_camRT = camRT;
		_camRB = camRB;
		_camLB = camLB;
	}

	private void setGamePoints(Point gameLT, Point gameRT, Point gameRB, Point gameLB) {
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
