package game.gameboard;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;

import org.opencv.core.Point;

import java.util.LinkedList;

/**
 * Created by Mark Mauerhofer on 17.10.2016.
 */
public class GameboardScaler {

//    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private Point _camLT;   // leftTopCorner
    private Point _camRT;   // rightTopCorner
    private Point _camRB;   // rightBottomCorner
    private Point _camLB;   // leftBottomCorner

    private Point _gameLT;   // leftTopCorner
    private Point _gameRT;   // rightTopCorner
    private Point _gameRB;   // rightBottomCorner
    private Point _gameLB;   // leftBottomCorner

    private Mat _homography;

    public boolean setCamCalibrationPoints1(Point camLT, Point camRB) {
        if (camLT == null || camRB == null){
            return false;
        }
        _camLT = camLT;
        _camRB = camRB;
        return true;
    }

    public boolean setCamCalibrationPoints2(Point camRT, Point camLB){
        if (camRT == null || camLB == null){
            return false;
        }
        _camRT = camRT;
        _camLB = camLB;
        return true;
    }

    public void initScaler(java.awt.Point[] gamePoints) {
        // Only two calibration points, so mirror points
        if (_camRT == null){
            _camRT = new Point(_camRB.x, _camLT.y);
            _camLB = new Point(_camLT.x, _camRB.y);
        }

        // Cast awt.point to opencv.point
        _gameLT = new Point(gamePoints[0].getX(), gamePoints[0].getY());
        _gameRT = new Point(gamePoints[1].getX(), gamePoints[1].getY());
        _gameRB = new Point(gamePoints[2].getX(), gamePoints[2].getY());
        _gameLB = new Point(gamePoints[3].getX(), gamePoints[3].getY());
        getPerspective();
    }

    private void getPerspective(){
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

    public java.awt.Point transformCameraPointToGameboardPoint(Point cameraPoint){
        if (cameraPoint == null){
            return null;
        }
        Mat src = new Mat();
        Mat dst = new Mat();
        src.push_back(new MatOfPoint2f(cameraPoint));
        Core.perspectiveTransform(src, dst, _homography);
        double[] points = dst.get(0, 0);
        java.awt.Point point = new java.awt.Point((int)points[0], (int)points[1]);
        return point;
    }
}




