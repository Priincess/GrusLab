package game.camera;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

public interface TrackingConstants {
	
	//upper & Lower bounds for yellow and evil minion
	 static final Scalar YELLOW_UPPER = new Scalar(30,255,255);
	 static final Scalar YELLOW_LOWER = new Scalar(20, 100, 100);
	 static final Scalar EVIL_UPPER = new Scalar(160, 127, 65);
	 static final Scalar EVIL_LOWER = new Scalar(120,77,10);
	 
	//average count of positions
	 static final int AVRG_COUNT = 10;
	 
	//frames to skip until camera ready
	 
	 static final int SKIP_FRAMES = 1000;
	 
	//ID for webcam to be used
	 
	 static final int CAM_ID = 1;
	 
	//if point not valid
	 static final Point NOT_VALID = null;

}
