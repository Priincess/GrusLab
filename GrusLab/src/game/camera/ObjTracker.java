package game.camera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;

public class ObjTracker {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	//upper & Lower bounds for nice and evil minion
	private static final Scalar YELLOW_UPPER = new Scalar(1,2);
	private static final Scalar YELLOW_LOWER = new Scalar(1,2);
	private static final Scalar EVIL_UPPER = new Scalar(1,2);
	private static final Scalar EVIL_LOWER = new Scalar(1,2);
	
	//ID for webcam to be used
	private static final int CAM_ID = 1;
	private static final int NUM_OF_SCALEPOINTS = 4;
	
	private static final Point NOT_VALID = new Point(-1,-1);

    private VideoCapture _capture;
	private Mat _actualFrame;
	
	//variables For trackings
	HSVTracking _yellowMinion;
	HSVTracking _evilMinion;
	
	

	
	//actual positions of minions
	Point _yellowPos;
	Point _evilPos;
	
	
	public ObjTracker() {
	
		//init values
		_capture = new VideoCapture();
		_yellowMinion = new HSVTracking(YELLOW_UPPER, YELLOW_LOWER);
		_evilMinion = new HSVTracking(EVIL_UPPER, EVIL_LOWER);
		
		_actualFrame = new Mat();
		
		
		//until now, no minions are tracked - so -1 is not valid value
		_yellowPos = NOT_VALID;
		_evilPos = NOT_VALID;
	}
	
	
	public Point getYellowPos(){
		return _yellowPos;
	}
	
	public Point getEvilPos(){
		return _evilPos;
	}
	
	
	public void startTracking(){
		
		_capture.open(CAM_ID);
		
		
		//First time when camrea runs - the first frames are white - so trakcing won't work
		//so i'll grab the first 1000 frames to make sure to have "clear" PIKTSCHAAA
		for(int i = 0; i < 1000; i++){
			_capture.read(_actualFrame);
		}

		while(_capture.isOpened()){
			
			_capture.read(_actualFrame);
			
			_yellowPos = _yellowMinion.trackMinion(_actualFrame,  Minion.YELLOW);
			
			//to not interfere objectTracking from yellow minion, frame gets closed
			
			_evilPos = _evilMinion.trackMinion(_actualFrame.clone(),  Minion.EVIL);
			
		}
		
		
	}
	
	public boolean stopTracking(){
		
		//close camera if not longer tracking
		if(_capture.isOpened()){
			_capture.release();
			return true;
		}
		return false;
	}
	
	public Point[] getScalePoints(){
		Point[] scalePoints = new Point[NUM_OF_SCALEPOINTS];
		
		
		_capture.open(CAM_ID);
		
		if(_capture.isOpened()){
			
			for(int i = 0; i < 1000; i++){
				_capture.read(_actualFrame);
			}
			
			Point yellowPoint = _yellowMinion.trackMinion(_actualFrame, Minion.YELLOW);
			Point evilPoint = _evilMinion.trackMinion(_actualFrame.clone(), Minion.EVIL);
			
			int corner = 0;
			
			scalePoints[corner] = yellowPoint; 
			scalePoints[++corner] = new Point(evilPoint.x, yellowPoint.y);
			scalePoints[++corner] = new Point(yellowPoint.x, evilPoint.y);
			scalePoints[++corner] = evilPoint;
			
			_capture.release();
			
			return scalePoints;
		}
		
		return new Point[]{NOT_VALID, NOT_VALID, NOT_VALID, NOT_VALID};
	}
	

	
	
	
	
}
