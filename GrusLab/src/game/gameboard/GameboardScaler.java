package game.gameboard;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;

import javafx.scene.shape.Rectangle;
import org.opencv.core.Point;

import java.util.LinkedList;

/**
 * Created by Mark Mauerhofer on 17.10.2016.
 */
public class GameboardScaler {

    // Compulsory
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private Point pLT;   // leftTopCorner
    private Point pRT;   // rightTopCorner
    private Point pRB;   // rightBottomCorner
    private Point pLB;   // leftBottomCorner

    private Point gLT;   // leftTopCorner
    private Point gRT;   // rightTopCorner
    private Point gRB;   // rightBottomCorner
    private Point gLB;   // leftBottomCorner

    private Mat homography;

    public void initScaler(Rectangle gameboard, Point pLT, Point pRT, Point pRB, Point pLB){
        this.pLT = pLT;
        this.pRT = pRT;
        this.pRB = pRB;
        this.pLB = pLB;

        int x = (int) gameboard.getX();
        int y = (int) gameboard.getY();
        int width = (int) gameboard.getWidth();
        int height = (int) gameboard.getHeight();

        this.gLT = new Point(x, y);
        this.gRT = new Point(x+width, y);
        this.gRB = new Point(x+width, y+height);
        this.gLB = new Point(x, y+height);

        getPerspective();
    }

    private void getPerspective(){
        // source quadrangle
        LinkedList<Point> srcList = new LinkedList<Point>();
        srcList.add(pLT);
        srcList.add(pRT);
        srcList.add(pRB);
        srcList.add(pLB);

        // transformed quadrangle
        LinkedList<Point> dstList = new LinkedList<Point>();
        dstList.add(gLT);
        dstList.add(gRT);
        dstList.add(gRB);
        dstList.add(gLB);

        MatOfPoint2f src = new MatOfPoint2f();
        src.fromList(srcList);

        MatOfPoint2f dst = new MatOfPoint2f();
        dst.fromList(dstList);
        // Find Homography
        homography = Calib3d.findHomography(src, dst);
    }

    public java.awt.Point transformCameraPointToGameboardPoint(java.awt.Point cameraPoint){
        Mat src = new Mat();
        Mat dst = new Mat();
        src.push_back(new MatOfPoint2f(new Point(cameraPoint.getX(), cameraPoint.getY())));
        Core.perspectiveTransform(src, dst, homography);
        double[] points = dst.get(0, 0);
        return new java.awt.Point((int)points[0], (int)points[1]);
    }
}




