package game.camera;


import java.util.ArrayList;


import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import org.opencv.core.Scalar;
import org.opencv.core.Size;

//import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;



public class HSVTracking {
	
	private static final Point NOT_VALID = new Point(-1,-1);
	
	private Scalar _upperound;
	
	private Scalar _lowerbound;
	
	//NANANANANANA
	
	Mat _mask;
	
	Mat _tracked;
	
	public HSVTracking(Scalar upperbound, Scalar lowerbound) {
		
		_upperound = upperbound;
		_lowerbound = lowerbound;
		_mask = new Mat();
		
		
	}
	
	/*returns the found point of the minion - 
	 * if not valid - it returns null
	 */
	public Point trackMinion(Mat frame, Minion name) {
		getMinionBlob(frame, name);
		return getPosition();
	}
	
	/* 
	 * get the HSV image and cdheck
	 * if boundaries are in range somewhere in the picture
	 * 
	 * */
	private void getMinionBlob(Mat frame,Minion name){

		//BLuuuur for the colour
		Imgproc.GaussianBlur(frame, _mask, new Size(11, 11), 3);

		//Magic HSV picture generated
		Imgproc.cvtColor(frame, _mask, Imgproc.COLOR_BGR2HSV);
		
		//Check if boundaries are in range somewhere - returns black white bit img
		Core.inRange(_mask, _lowerbound, _upperound, _mask);
		
		//not sure if erode is needed too much
		//Imgproc.erode(_mask, _mask, new Mat(), new Point(-1,-1), 1);

		//make sure that one blob is connected as much as possible
		
		//yellow minion recognition is working better - so only 2 dilates are needed
		if(!name.equals(Minion.YELLOW)){
			
		Imgproc.dilate(_mask, _mask, new Mat(), new Point(-1,-1), 10);
		
		}else{
			Imgproc.dilate(_mask, _mask, new Mat(), new Point(-1,-1), 2);
		}
		
	//	Highgui.imwrite("dilli" + name + ".jpeg", _mask);

	}
	
	
	/* 
	 * the Position is determined by searching the largest 
	 *
	 * blob which was in range of the boundaries
	 * */
	private Point getPosition(){
		//TODO: PROGRAMMIER DAS SCH�NER! D:
		
		
		//all contours
		ArrayList<MatOfPoint> contour = new ArrayList<>();
		
		//largestBlob
		MatOfPoint maxPoint = new MatOfPoint();
		
		//the centroid of minion
		Point center = new Point();


		
		//getAllContours from binary image
		Imgproc.findContours(_mask, contour, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));
		
		double max = 0;
		double comp = 0;
		
		for(MatOfPoint p : contour){
			comp = Imgproc.contourArea(p, false);
			if(max < comp){
				max = comp;
				maxPoint = p;
			}
		}

		//hack for getting centroids out of contours
		 MatOfPoint2f  centroids = new MatOfPoint2f( maxPoint.toArray() );
		Imgproc.minEnclosingCircle(centroids, center, new float[5]);
	
		
		if (centroids.empty()){
			return null;	// No Point found
		}
		else{
			return center;
		}	
	}
	

}
