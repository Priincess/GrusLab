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

    private Point camLT;   // leftTopCorner
    private Point camRT;   // rightTopCorner
    private Point camRB;   // rightBottomCorner
    private Point camLB;   // leftBottomCorner

    private Point gameLT;   // leftTopCorner
    private Point gameRT;   // rightTopCorner
    private Point gameRB;   // rightBottomCorner
    private Point gameLB;   // leftBottomCorner

    private Mat homography;

    public boolean setCamCalibrationPoints1(Point camLT, Point camRB) {
        if (camLT == null || camRB == null){
            return false;
        }
        this.camLT = camLT;
        this.camRB = camRB;
        return true;
    }

    public boolean setCamCalibrationPoints2(Point camRT, Point camLB){
        if (camRT == null || camLB == null){
            return false;
        }
        this.camRT = camRT;
        this.camLB = camLB;
        return true;
    }

    public void initScaler(java.awt.Point[] gamePoints) {
        // Only two calibration points, so mirror points
        if (camRT == null){
            this.camRT = new Point(camRB.x, camLT.y);
            this.camLB = new Point(camLT.x, camRB.y);
        }

        // Cast awt.point to opencv.point
        this.gameLT = new Point(gamePoints[0].getX(), gamePoints[0].getY());
        this.gameRT = new Point(gamePoints[1].getX(), gamePoints[1].getY());
        this.gameRB = new Point(gamePoints[2].getX(), gamePoints[2].getY());
        this.gameLB = new Point(gamePoints[3].getX(), gamePoints[3].getY());
        getPerspective();
    }

    private void getPerspective(){
        // source quadrangle == camera quadrangle
        LinkedList<Point> srcList = new LinkedList<Point>();
        srcList.add(camLT);
        srcList.add(camRT);
        srcList.add(camRB);
        srcList.add(camLB);

        // transformed quadrangle == virtual gameboard
        LinkedList<Point> dstList = new LinkedList<Point>();
        dstList.add(gameLT);
        dstList.add(gameRT);
        dstList.add(gameRB);
        dstList.add(gameLB);

        MatOfPoint2f src = new MatOfPoint2f();
        src.fromList(srcList);

        MatOfPoint2f dst = new MatOfPoint2f();
        dst.fromList(dstList);
        // Find Homography
        homography = Calib3d.findHomography(src, dst);
    }

    public java.awt.Point transformCameraPointToGameboardPoint(Point cameraPoint){
        if (cameraPoint == null){
            return null;
        }
        Mat src = new Mat();
        Mat dst = new Mat();
        src.push_back(new MatOfPoint2f(cameraPoint));
        Core.perspectiveTransform(src, dst, homography);
        double[] points = dst.get(0, 0);
        java.awt.Point point = new java.awt.Point((int)points[0], (int)points[1]);
        return point;
    }
}




