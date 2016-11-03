//package game.camera;
//
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//import org.opencv.core.RotatedRect;
//import org.opencv.core.Scalar;
//
//import org.opencv.highgui.VideoCapture;
//import org.opencv.imgproc.Imgproc;
//
//public class Controller {
//    // the FXML button
//    @FXML
//    private Button button;
//    // the FXML image view
//    @FXML
//    private ImageView currentFrame;
//
//    // a timer for acquiring the video stream
//    private ScheduledExecutorService timer;
//    // the OpenCV object that realizes the video capture
//    private VideoCapture capture = new VideoCapture();
//    // a flag to change the button behavior
//    private boolean cameraActive = false;
//    // the id of the camera to be used
//    private static int cameraId = 1;
//
//    /**
//     * The action triggered by pushing the button on the GUI
//     *
//     * @param event
//     *            the push button event
//     */
//    @FXML
//    protected void startCamera(ActionEvent event)
//    {
//        if (!this.cameraActive)
//        {
//            // start the video capture
//            this.capture.open(cameraId);
//
//            // is the video stream available?
//            if (this.capture.isOpened())
//            {
//                this.cameraActive = true;
//
//
//                int imCount = 100;
//        		
//        		while(imCount != 0){
//        			grabFrame();
//        			imCount--;
//        		}
//
//        		//TODO: MAGIC HAPPENS
//        		 HSVTracking hsvYellow = new HSVTracking(new Scalar(30,255,255), new Scalar(20, 100, 100));
//        		 HSVTracking hsvEvil = new HSVTracking(new Scalar(160, 127, 65), new Scalar(120,77,10));
//        		 //HSVTracking hsvEvil = new HSVTracking(new Scalar(160, 140, 65), new Scalar(120,50,10));
//        		 //HSVTracking hsvEvil = new HSVTracking(new Scalar(170, 200, 110), new Scalar(140,100,30));
//                // grab a frame every 33 ms (30 frames/sec)
//                Runnable frameGrabber = new Runnable() {
//
//                
//                	
//                    @Override
//                    public void run()
//                    {
//                    	
//                        // effectively grab and process a single frame
//                        Mat frame = grabFrame();
//                        // convert and show the frame
//                        
//                        //MORE MAGIC HAPPENS
//                        try{
//                        Point pYellow = hsvYellow.trackMinion(frame.clone(), "yellow");
//	        				Core.circle(frame, pYellow, 40, new Scalar(255,255,255));	
//							
//                        }catch (Exception e){
//                        	System.out.println("yellow missing");
//							}
//                        try{
//                        	Point pEvil = hsvEvil.trackMinion(frame.clone(), "evil");
//                        	Core.circle(frame, pEvil, 40, new Scalar(255,255,255));
//                        }catch (Exception e){
//                        	System.out.println("no evil MINION!!!");
//                        }
//	                        //TODO: MAGIC TRcker end
//	                        
//	                        
//	                        Image imageToShow = Utils.mat2Image(frame);
//	                        updateImageView(currentFrame, imageToShow);
//                    }
//                };
//
//                this.timer = Executors.newSingleThreadScheduledExecutor();
//                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
//
//                // update the button content
//                this.button.setText("Stop Camera");
//            }
//            else
//            {
//                // log the error
//                System.err.println("Impossible to open the camera connection...");
//            }
//        }
//        else
//        {
//            // the camera is not active at this point
//            this.cameraActive = false;
//            // update again the button content
//            this.button.setText("Start Camera");
//
//            // stop the timer
//            this.stopAcquisition();
//        }
//    }
//
//    /**
//     * Get a frame from the opened video stream (if any)
//     *
//     * @return the {@link Mat} to show
//     */
//    private Mat grabFrame()
//    {
//        // init everything
//        Mat frame = new Mat();
//
//        // check if the capture is open
//        if (this.capture.isOpened())
//        {
//            try
//            {
//                // read the current frame
//                this.capture.read(frame);
//
//                // if the frame is not empty, process it
////                if (!frame.empty())
////                {
////                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
////                }
//
//            }
//            catch (Exception e)
//            {
//                // log the error
//                System.err.println("Exception during the image elaboration: " + e);
//            }
//        }
//
//        return frame;
//    }
//
//    /**
//     * Stop the acquisition from the camera and release all the resources
//     */
//    private void stopAcquisition()
//    {
//        try
//        {
//            // stop the timer
//            this.timer.shutdown();
//            this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
//        }
//        catch (InterruptedException e)
//        {
//            // log any exception
//            System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
//        }
//
//        // release the camera
//        this.capture.release();
//    }
//
//    /**
//     * Update the {@link ImageView} in the JavaFX main thread
//     *
//     * @param view
//     *            the {@link ImageView} to update
//     * @param image
//     *            the {@link Image} to show
//     */
//    private void updateImageView(ImageView view, Image image)
//    {
//        Utils.onFXThread(view.imageProperty(), image);
//    }
//
//    /**
//     * On application close, stop the acquisition from the camera
//     */
//    protected void setClosed()
//    {
//        this.stopAcquisition();
//    }
//
//}
