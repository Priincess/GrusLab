package game.camera;

import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.VideoCapture;

public class ObjTracker implements TrackingConstants {

	// load native lib - openCV
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	// camera
	private VideoCapture _capture;
	private Mat _actualFrame;

	// For trackings
	private HSVTracking _yellowMinion;
	private HSVTracking _evilMinion;

	// actual positions of minions over last NUM_FRAMES frames
	private List<Point> _lastYellowPos;
	private List<Point> _lastEvilPos;

	// average Position over last AVRG_COUNT num of frames
	private Point _yellowPos;
	private Point _evilPos;

	public ObjTracker() {

		// init values
		_capture = new VideoCapture();
		_yellowMinion = new HSVTracking(YELLOW_UPPER, YELLOW_LOWER);
		_evilMinion = new HSVTracking(EVIL_UPPER, EVIL_LOWER);

		_actualFrame = new Mat();

		// until now, no minions are tracked
		_yellowPos = null;
		_evilPos = null;

		_lastEvilPos = new LinkedList<>();
		_lastYellowPos = new LinkedList<>();

		//initCam();

	}

	private void initCam() {
		_capture.open(CAM_ID);

		// First time when camera runs - the first frames are white - so
		// tracking won't work
		// so i'll grab the first couple frames to make sure to have a "clear"
		// picture
		for (int i = 0; i < SKIP_FRAMES; i++) {
			_capture.read(_actualFrame);
			System.out.println(i/10 + "%");
		}

		System.out.println("Camera loading: 100%\tCamera is ready!");

	}

	public Point getYellowPos() {
		return _yellowPos;
	}

	public Point getEvilPos() {
		return _evilPos;
	}

	public void startTracking() {
		
		initCam();

		while (_capture.isOpened()) {

			_capture.read(_actualFrame);

			// if no minion - catch native exception. can't throw because thread
			// would stop
			try {
				// _yellowPos = _yellowMinion.trackMinion(_actualFrame.clone(),
				 //Minion.YELLOW);

				// if max frame num is reached - remove first pos
				if (_lastYellowPos.size() >= AVRG_COUNT) {
					_lastYellowPos.remove(0);
				}

				// add new pos to calc avrg
				_lastYellowPos.add(_yellowMinion.trackMinion(_actualFrame.clone(), Minion.YELLOW));

				// calc avrg position
				_yellowPos = getAvrgPos(_lastYellowPos);
			} catch (Exception e) {
				_yellowPos = NOT_VALID;
			}
			// to not interfere objectTracking from yellow minion, frame gets
			// closed

			try {
//				 _evilPos = _evilMinion.trackMinion(_actualFrame.clone(),
//				 Minion.EVIL);

				if (_lastEvilPos.size() >= AVRG_COUNT) {
					_lastEvilPos.remove(0);
				}

				_lastEvilPos.add(_evilMinion.trackMinion(_actualFrame.clone(), Minion.EVIL));

				_evilPos = getAvrgPos(_lastEvilPos);

			} catch (Exception e) {
				_evilPos = NOT_VALID;
			}
		}
	}

	private Point getAvrgPos(List<Point> avrgPoints) {

		double sumY = 0;
		double sumX = 0;

		// sum up all x and y of all points last tracked to calc avrg
		for (int i = avrgPoints.size()-1; i >= 0; i--) {
			sumY += avrgPoints.get(i).y;
			sumX += avrgPoints.get(i).x;
		}

		// use size of list - in case max frame number not reached
		return new Point(sumX / avrgPoints.size(), sumY / avrgPoints.size());
	}

	public boolean stopTracking() {

		// close camera if not longer tracking
		if (_capture.isOpened()) {
			_capture.release();
			return true;
		}
		return false;
	}

}
