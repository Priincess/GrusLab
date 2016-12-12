package game.camera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.VideoCapture;

public class ObjTracker {

	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	private static final int SKIP_CAM_FRAMES = 1000;
	
	//upper & Lower bounds for nice and evil minion
	private static final Scalar YELLOW_UPPER = new Scalar(30,255,255);
	private static final Scalar YELLOW_LOWER = new Scalar(20, 100, 100);
	private static final Scalar EVIL_UPPER = new Scalar(160, 127, 65);
	private static final Scalar EVIL_LOWER = new Scalar(120,77,10);
		
	private static final Point NOT_VALID = null;

	private int _camId = 1;
    private VideoCapture _capture;
	private Mat _actualFrame;
	
	private boolean _isReady = false;
	
	//variables For trackings
	private HSVTracking _yellowMinion;
	private HSVTracking _evilMinion;

	//actual positions of minions
	private Point _yellowPos;
	private Point _evilPos;
	
	
	public ObjTracker() {
	
		//init values
		_capture = new VideoCapture();
		_yellowMinion = new HSVTracking(YELLOW_UPPER, YELLOW_LOWER);
		_evilMinion = new HSVTracking(EVIL_UPPER, EVIL_LOWER);
		
		_actualFrame = new Mat();
		
		
		//until now, no minions are tracked
		_yellowPos = null;
		_evilPos = null;

	}
	
	public void setCamID(int id){
		_camId = id;
	}
	
	
	public Point getYellowPos(){
		return _yellowPos;
	}
	
	public Point getEvilPos(){
		return _evilPos;
	}
	
	public boolean isReady(){
		return _isReady;
	}
	
	public void startTracking(){
		
		_capture.open(_camId);

		//First time when camera runs - the first frames are white - so tracking won't work
		//so i'll grab the first 1000 frames to make sure to have "clear" picture
		for(int i = 0; i < SKIP_CAM_FRAMES; i++){
			_capture.read(_actualFrame);
		}
		_isReady=true;
		System.out.println("Camera loading: 100%\tCamera is ready!");

		while(_capture.isOpened()){

			_capture.read(_actualFrame);


			//if no minion - catch native exception. can't throw because thread would stop
			try {
				_yellowPos = _yellowMinion.trackMinion(_actualFrame, Minion.YELLOW);
			} catch (Exception e ){
				_yellowPos = NOT_VALID;
			}
			//to not interfere objectTracking from yellow minion, frame gets closed

			try {
				_evilPos = _evilMinion.trackMinion(_actualFrame.clone(), Minion.EVIL);
			} catch (Exception e) {
				_evilPos = NOT_VALID;
			}
		}
	}

	public boolean stopTracking(){
		
		_isReady=false;
		
		//close camera if not longer tracking
		if(_capture.isOpened()){
			_capture.release();
			return true;
		}
		return false;
	}

}
