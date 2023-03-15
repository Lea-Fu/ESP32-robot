package com.example.app;

import org.opencv.aruco.Aruco;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;



/**
 * this class contains a thread, which exists besides the main thread and the CommunicationCallback thread
 */
public class Controller implements ICommunicationCallback, Runnable {

    private volatile Position position;
    private ICommunication communication;
    private volatile Mat image;
    private volatile int currentID;
    private volatile double currentAngle;
    private Mat cameraMatrix;
    private Mat distCoeffs;

    private static final int ROTATION_SPEED = 150;
    private static final int DRIVING_SPEED = 60;
    private static final int ROTATION_TIME = 100;
    private static final int DRIVING_TIME = 1000;
    private static final double ANGLE_EPS = 7.5;


    /**
     * Constructor
     */
    public Controller() {
        position = null;
        image = null;
        currentID = -1;
        currentAngle = 0;

        //3x3 matrix with 64 bit float, one channel
        cameraMatrix = new Mat(3, 3, CvType.CV_64FC1);
        cameraMatrix.put(0, 0, 1); //random eyeMatrix
        cameraMatrix.put(0, 1, 0);
        cameraMatrix.put(0, 2, 48);
        cameraMatrix.put(1, 0, 0);
        cameraMatrix.put(1, 1, 1); //random eyeMatrix
        cameraMatrix.put(1, 2, 48);
        cameraMatrix.put(2, 0, 0);
        cameraMatrix.put(2, 1, 0);
        cameraMatrix.put(2, 2, 1);

        distCoeffs = new Mat(4, 1, CvType.CV_64FC1);
        distCoeffs.put(0, 0, 0);
        distCoeffs.put(1, 0, 0);
        distCoeffs.put(2, 0, 0);
        distCoeffs.put(3, 0, 0);
    }

    /**
     * Setter for the communication class
     * @param communication
     */
    public void setCommunication(ICommunication communication) {
        this.communication = communication;
    }


    /**
     * the main thread enters this function to start the movement of the robot
     * @param position
     */
    public synchronized void goToPosition(Position position) {
        this.position = new Position(position);
        currentID = -1;
        notify();
    }

    /**
     * this is a synchronization between the main thread and the controller thread
     * @return position
     * @throws InterruptedException
     */
    private synchronized Position helper() throws InterruptedException {
        while (position == null) {
            wait();
        }
        Position tmp = new Position(position);
        position = null;
        return tmp;
    }

    /**
     * Sets the image. This function will be called from the communication.
     * @param image
     */
    @Override
    public synchronized void waitForImage(Mat image) {
        this.image = image.clone();
        Mat markerIds = new Mat();
        List<Mat> markerCorners = new ArrayList<>();
        Aruco.detectMarkers(this.image, Aruco.getPredefinedDictionary(Aruco.DICT_4X4_250), markerCorners, markerIds);

        //Pose estimation (Roatation)

        //results (array of output rotation and translation vectors)
        Mat rvecs = new Mat();
        Mat tvecs = new Mat();

        Aruco.estimatePoseSingleMarkers(markerCorners, 2, cameraMatrix, distCoeffs, rvecs, tvecs);

        if (!markerIds.empty()) {
            currentID = (int) markerIds.get(0, 0)[0];
        }

        if (!rvecs.empty()) {
            currentAngle = calculateAngle(rvecs);
        }

        notify();
    }

    /**
     * synchronize image
     * @return Mat
     * @throws InterruptedException
     */
    private synchronized Mat getImage() throws InterruptedException {
        while (image == null) {
            wait();
        }
        Mat tmp = image;
        image = null;
        return tmp;
    }

    /**
     * synchronize ArUco id
     * @return id
     * @throws InterruptedException
     */
    private synchronized int getID() throws InterruptedException {
        while(currentID < 0) {
            wait();
        }
        return currentID;
    }

    /**
     * synchronize angle
     * @return angle
     * @throws InterruptedException
     */
    private synchronized double getAngle() throws InterruptedException {
        return currentAngle;
    }

    /**
     * the position on which marker the robot is now
     * ID estimation (translation)
     * @param markerId The ID of the current marker
     * @return position of the robot in xy-coordinates
     */
    public Position getMarkerPos(int markerId) {
        int id = markerId;
        int x = id % 3;
        int y = (id - x) / 3;

        System.out.println("x pos: " + x + " " + "y pos: " + y);

        return new Position(x, y);
    }

    /**
     * euler z rotation for robot pose
     * @param rvecs Estimation from OpenCV
     * @return euler angle of the robot
     */
    private double calculateAngle(Mat rvecs) {
        Mat curR = new Mat(1, 3, CvType.CV_64FC1);
        curR.put(0, 0, rvecs.get(0, 0)[0]);
        curR.put(1, 0, rvecs.get(0, 0)[1]);
        curR.put(2, 0, rvecs.get(0, 0)[2]);
        //    System.out.println("rvec: " /* + rvecs.get(0, 0)[0] + ", " + rvecs.get(0, 0)[1] */ + ", " + rvecs.get(0, 0)[2]);


        //rotation matrix to euler angle  https://learnopencv.com/rotation-matrix-to-euler-angles/
        Mat rotMat = new Mat();
        Calib3d.Rodrigues(curR, rotMat);
        //System.out.println(rotMat.dump());
        double sy = Math.sqrt(rotMat.get(0, 0)[0] * rotMat.get(0, 0)[0] + rotMat.get(1, 0)[0] * rotMat.get(1, 0)[0]);
        boolean singular = sy < 1e-6; //1*10^-6
        double x, y, z;
        if (!singular) {
            //x = Math.atan2(rotMat.get(2,1)[0],rotMat.get(2,2)[0]);
            //y = Math.atan2(-1*rotMat.get(2,0)[0],sy);
            z = Math.atan2(rotMat.get(1, 0)[0], rotMat.get(0, 0)[0]);
        } else {
            //x = Math.atan2(-1*rotMat.get(1,2)[0],rotMat.get(1,1)[0]);
            //y = Math.atan2(-1*rotMat.get(2,0)[0],sy);
            z = 0.0;
        }
        //Euler angle z (rotation)
        //System.out.print("z: " + z);
        //System.out.print(", y: " + y);
        //System.out.println(", x: " + x);

        return (2*Math.PI + z) % (2*Math.PI); //2*PI = 360 degree
    }

    /**
     * rotate to correct the pose so that the robot can drive to the next marker
     * @param angle Current angle of the robot
     */
    private void rotation(double angle) {
        angle = Math.toRadians(angle);
        boolean rotateRight = true;
        while (true) {
            //to rotate the shorter way
            double currentAngle = 0;
            try {
                currentAngle = getAngle();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //against the 0 = 360 degree problem:
            if(angle == 0) {
                if (((currentAngle + Math.PI)%(2*Math.PI)) - ((angle + Math.PI)%(2*Math.PI)) < 0) {
                    rotateRight = true;
                } else {
                    rotateRight = false;
                }
            } else {
                if (currentAngle - angle < 0) {
                    rotateRight = true;
                } else {
                    rotateRight = false;
                }
            }

            if(currentAngle > (2*Math.PI-2*Math.toRadians(ANGLE_EPS))) {
                currentAngle -= 2*Math.PI; //2*PI = 360 degree
            }

            System.out.println(Math.toDegrees(currentAngle));

            if (currentAngle >= angle - Math.toRadians(ANGLE_EPS) && currentAngle <= angle + Math.toRadians(ANGLE_EPS)) {
                communication.motorStop();
                //finished rotation
                break;
            }

            //to rotate the shorter way
            if (rotateRight) {
                communication.motorStart(false, true, ROTATION_SPEED, ROTATION_TIME);
            } else {
                communication.motorStart(true, false, ROTATION_SPEED, ROTATION_TIME);
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * drives and stops in x direction. If it drives the whole time,
     * the robot can not see the markers,
     * because it is too fast and the motors make inferences so it comes to lost packages
     * @param xPosition Current x position
     * @param angle Current angle
     */
    private void driveStraightX(int xPosition, double angle) {
        boolean reachedPos = false;
        while (!reachedPos) {
            rotation(angle);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            communication.motorStart(true, true, DRIVING_SPEED, DRIVING_TIME);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Position robotPos = null;
            try {
                robotPos = getMarkerPos(getID());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Robotpos x: " + robotPos.xPosition + ", Robotpos y: " + robotPos.yPosition);
            if (xPosition == robotPos.xPosition) {
                communication.motorStop();
                reachedPos = true;
            }
        }
    }

    /**
     * drives and stops in y direction. If it drives the whole time,
     * the robot can not see the markers,
     * because it is too fast and the motors make inferences so it comes to lost packages
     * @param yPosition Current y position
     * @param angle Current angle
     */
    private void driveStraightY(int yPosition, double angle) {
        boolean reachedPos = false;
        while (!reachedPos) {
            rotation(angle);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            communication.motorStart(true, true, DRIVING_SPEED, DRIVING_TIME);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Position robotPos = null;
            try {
                robotPos = getMarkerPos(getID());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Robotpos x: " + robotPos.xPosition + ", Robotpos y: " + robotPos.yPosition);
            if (yPosition == robotPos.yPosition) {
                communication.motorStop();
                reachedPos = true;
            }
        }
    }


    private Position lastPos;


    /**
     * Super-loop of the controller thread.
     */
    @Override
    public void run() {
        lastPos = null;
        int id = -1;
        while (true) {
            Position pos = null;
            try {
                pos = helper();
            } catch (InterruptedException e) {
                e.printStackTrace();
                //for retrying
                continue;
            }

            //1. get the current position of the robot and store it in lastPos
            try {
                id = getID();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lastPos = getMarkerPos(id);

            //2. check orientation of the robot in x Direction (the field is 3x3 so ID0 to ID8, with 3 IDs in a row. Orientation is -90 or 90 degree in x and 0 or 180 in y.
            if (lastPos.xPosition < pos.xPosition) {
                driveStraightX(pos.xPosition, 90);
            } else if (lastPos.xPosition > pos.xPosition) {
                driveStraightX(pos.xPosition, 270);
            } else { //xPos is already correct

            }

            //3. check orientation of the robot in y Direction
            if (lastPos.yPosition < pos.yPosition) {
                driveStraightY(pos.yPosition, 0);
            } else if (lastPos.yPosition > pos.yPosition) {
                driveStraightY(pos.yPosition, 180);
            } else { //yPos is already correct

            }

            lastPos = null;
        }
    }
}
