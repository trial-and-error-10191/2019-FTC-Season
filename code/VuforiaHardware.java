package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;



public class VuforiaHardware {
    public static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    public static final boolean PHONE_IS_PORTRAIT = false;
    WebcamName webcamName = null; // webcam name for vuforia parameters

    public static final String VUFORIA_KEY =
            "Acd3Mhv/////AAABmQHTPP6MLkaAuT4ajCpWMLFIAsffT0PglAjW5YBhoEBRGmKeJcOmf37joiF+BKOuseAqCQ+Dq6THvITLD+L/v5UI/RaEka+Egq7V+JYnS26F1HnEGFG0pYR6TxQksLltAKf7HvyKRgfZLwtRSGPvA8/Pvu936WpjlRDOizksUUMQ8+iaM/aPUKGrlswF8QrzncCcmCGOSq+HwwHCJH6pJSQK7HTgGmg5TMLkXK5Q5D3OILskNBUI8LVrAEzWY7mDZVGpYekIBeb4IoNu7tShpgOmj4Sx0VLoT5eidMHOzN+BAoTkoZkawqGqWIHHwykX0cLcxeh8hWhHhN2n56mbv/57u/h9eUUdXzNZikOhPRff";

    // Math for converting image scale to physical dimensions
    public static final float mmPerInch = 25.4f;
    public static final float mmTargetHeight = (6) * mmPerInch;          // the height of the center of the target image above the floor

    // Constant for Stone Target
    public static final float stoneZ = 2.00f * mmPerInch;

    // Constants for the center support targets
    public static final float bridgeZ = 6.42f * mmPerInch;
    public static final float bridgeY = 23 * mmPerInch;
    public static final float bridgeX = 5.18f * mmPerInch;
    public static final float bridgeRotY = 59;                                 // Units are degrees
    public static final float bridgeRotZ = 180;

    // Constants for perimeter targets
    public static final float halfField = 72 * mmPerInch;
    public static final float quadField = 36 * mmPerInch;

    // Vuforia Location Data
    public VuforiaLocalizer vuforia = null;
    public OpenGLMatrix lastLocation = null;
    public OpenGLMatrix currentSkystoneLocation = null;
    public boolean targetVisible = false;
    public float phoneXRotate = 0;
    public float phoneYRotate = 0;
    public float phoneZRotate = 0;

    // Flags for important information that vuforia detects
    public boolean locationFlag = false; // Flag for if we have found where we are
    public boolean depot; // looking for the the side that we are on
    public boolean blueTeam; // sets the team
    boolean skyStoneFlag; // is the skystone visable
    boolean useWebcam; // which camera phone? or webcam?

    // Sets the physical offsets from center of robot for camera
    // EVERYTHING IS IN MILLIMETERS REEEEEEEEEEEE
    //float CAMERA_FORWARD_DISPLACEMENT = 7.0f * mmPerInch;
    //final float CAMERA_VERTICAL_DISPLACEMENT = 6.0f * mmPerInch;
    //final float CAMERA_LEFT_DISPLACEMENT = -6.5f * mmPerInch;
    float CAMERA_FORWARD_DISPLACEMENT = 7.0f;
    final float CAMERA_VERTICAL_DISPLACEMENT = 6.0f;
    final float CAMERA_LEFT_DISPLACEMENT = -6.5f;

    // Center on picture math
    double posPerDeg = 0.0078;
    double distanceFromWall = 14;
    int servoSteps = 101;


    // Load from assets
    VuforiaTrackables targetsSkyStone = null;

    // Split up the above object into separate objects
    VuforiaTrackable stoneTarget = null;
    VuforiaTrackable blueRearBridge = null;
    VuforiaTrackable redRearBridge = null;
    VuforiaTrackable redFrontBridge = null;
    VuforiaTrackable blueFrontBridge = null;
    VuforiaTrackable red1 = null;
    VuforiaTrackable red2 = null;
    VuforiaTrackable front1 = null;
    VuforiaTrackable front2 = null;
    VuforiaTrackable blue1 = null;
    VuforiaTrackable blue2 = null;
    VuforiaTrackable rear1 = null;
    VuforiaTrackable rear2 = null;

    // Hardware Vuforia uses
    //PhoneServoHardware robot = new PhoneServoHardware();
    // Preston's Suggestion:
     Servo phoneServo = null;

    public void init(HardwareMap ahwMap) {
        // Change this if you want to use webcam. If changing mid-program, reinit vuforia.
        setCamera("webcam");

        if(this.useWebcam) {
            webcamName = ahwMap.get(WebcamName.class, "Webcam1");
        }
        phoneServo = ahwMap.get(Servo.class,"phoneServo");

        int cameraMonitorViewId = ahwMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", ahwMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CAMERA_CHOICE;
        if (this.useWebcam) {
            parameters.cameraName = webcamName;
        }

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone");
        initVuforia();

        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);

        // Rotate the "phone" for some reason about front facing or back camera
        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90;
        }

        // Is camera upside down?
        // phoneZRotate = 180; // Maybe

        // Offsets measurements to center of robot
        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT*mmPerInch,
                        CAMERA_LEFT_DISPLACEMENT*mmPerInch,
                        CAMERA_VERTICAL_DISPLACEMENT*mmPerInch)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));


        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }

        // to turn on the light
        //CameraDevice.getInstance().setFlashTorchMode(true);

        // Start looking for assets
        targetsSkyStone.activate();
    }

    public void initVuforia() {
        stoneTarget = this.targetsSkyStone.get(0);
        stoneTarget.setName("Stone Target");
        blueRearBridge = this.targetsSkyStone.get(1);
        blueRearBridge.setName("Blue Rear Bridge");
        redRearBridge = this.targetsSkyStone.get(2);
        redRearBridge.setName("Red Rear Bridge");
        redFrontBridge = this.targetsSkyStone.get(3);
        redFrontBridge.setName("Red Front Bridge");
        blueFrontBridge = this.targetsSkyStone.get(4);
        blueFrontBridge.setName("Blue Front Bridge");
        red1 = this.targetsSkyStone.get(5);
        red1.setName("Red Perimeter 1");
        red2 = this.targetsSkyStone.get(6);
        red2.setName("Red Perimeter 2");
        front1 = this.targetsSkyStone.get(7);
        front1.setName("Front Perimeter 1");
        front2 = this.targetsSkyStone.get(8);
        front2.setName("Front Perimeter 2");
        blue1 = this.targetsSkyStone.get(9);
        blue1.setName("Blue Perimeter 1");
        blue2 = this.targetsSkyStone.get(10);
        blue2.setName("Blue Perimeter 2");
        rear1 = this.targetsSkyStone.get(11);
        rear1.setName("Rear Perimeter 1");
        rear2 = this.targetsSkyStone.get(12);
        rear2.setName("Rear Perimeter 2");

        // Set the position of the Stone Target.  Since it's not fixed in position, assume it's at the field origin.
        // Rotated it to to face forward, and raised it to sit on the ground correctly.
        // This can be used for generic target-centric approach algorithms
        stoneTarget.setLocation(OpenGLMatrix
                .translation(0, 0, stoneZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        //Set the position of the bridge support targets with relation to origin (center of field)
        blueFrontBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, bridgeRotZ)));

        blueRearBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, bridgeRotZ)));

        redFrontBridge.setLocation(OpenGLMatrix
                .translation(-bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, -bridgeRotY, 0)));

        redRearBridge.setLocation(OpenGLMatrix
                .translation(bridgeX, -bridgeY, bridgeZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 0, bridgeRotY, 0)));

        //Set the position of the perimeter targets with relation to origin (center of field)
        red1.setLocation(OpenGLMatrix
                .translation(quadField, -halfField, mmTargetHeight)

                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        red2.setLocation(OpenGLMatrix
                .translation(-quadField, -halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

        front1.setLocation(OpenGLMatrix
                .translation(-halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90)));

        front2.setLocation(OpenGLMatrix
                .translation(-halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 90)));

        blue1.setLocation(OpenGLMatrix
                .translation(-quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        blue2.setLocation(OpenGLMatrix
                .translation(quadField, halfField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));

        rear1.setLocation(OpenGLMatrix
                .translation(halfField, quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        rear2.setLocation(OpenGLMatrix
                .translation(halfField, -quadField, mmTargetHeight)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));
    }

    public void deinit() {
        targetsSkyStone.deactivate();
        this.vuforia = null;
    }

    public  void setCamera(String camera){
        if(camera.equalsIgnoreCase("phone")) {
            this.useWebcam = false;
            return;
        } else if(camera.equalsIgnoreCase("webcam")) {
            this.useWebcam = true;
            return;
        } else {
            this.useWebcam = false;
            return;
        }
    }

    public void setServoAngle(double degrees) {
        phoneServo.setPosition(degrees*posPerDeg);
        return;
    }

    public double getServoAngle() {
        double pos = phoneServo.getPosition();
        return pos/posPerDeg;
    }

    public double getCenterPosition(double inchesFromWall) {
        double cameraDistanceFromWall = Math.sqrt(inchesFromWall*inchesFromWall +
                CAMERA_LEFT_DISPLACEMENT*CAMERA_LEFT_DISPLACEMENT) ;
        double cameraAngleToPictures = Math.asin(inchesFromWall/
                cameraDistanceFromWall)*180.0/Math.PI;
        return cameraAngleToPictures*posPerDeg;
    }

    public void scanTheRoom() throws InterruptedException{
        double servoStepSize = (1.0-0)/(servoSteps-1);
        double dir = 1;
        phoneServo.setPosition(0);
        for(int i = 0;i<2*servoSteps; i++ ){
            if(i>servoSteps){
                dir = -1;
            }
            detectTrackable();
            if(locationFlag){
                break;
            }
            phoneServo.setPosition(phoneServo.getPosition()+dir*servoStepSize);
            sleep(5000/servoSteps);
        }
    }


    public void detectTrackable(){
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);
        setCamera("webcam");

        for (VuforiaTrackable trackable : allTrackables) {
            // seeing the alliance wall using the phone
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                if (trackable.getName().equals("Red Perimeter 1")) {
                    //telemetry.addData("red alliance wall", trackable.getName());
                    // depot side
                    this.depot = false;
                    this.blueTeam = false;
                    this.locationFlag = true;
                    return;
                } else if (trackable.getName().equals("Red Perimeter 2")) {
                    // telemetry.addData("red alliance wall", trackable.getName());
                    //build side
                    this.depot = true;
                    this.blueTeam = false;
                    this.locationFlag = true;
                    return;
                } else if (trackable.getName().equals("Blue Perimeter 1")) {
                    //telemetry.addData(" blue alliance wall", trackable.getName());
                    //depot side
                    this.depot = true;
                    this.blueTeam = true;
                    this.locationFlag = true;
                    return;
                } else if (trackable.getName().equals("Blue Perimeter 2")) {
                    // telemetry.addData("blue alliance wall ", trackable.getName());
                    //build side
                    this.depot = false;
                    this.blueTeam = true;
                    this.locationFlag = true;
                    return;
                }
            }
        }

    }

    public void depotScan() {
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);
        setCamera("webcam");
        for (VuforiaTrackable trackable : allTrackables) {
            // seeing the sky stones
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                //looking for the skystone
                if (trackable.getName().equals("Sky Stones")) {
                    skyStoneFlag = true;
                    currentSkystoneLocation = ((VuforiaTrackableDefaultListener) trackable.getListener()).getFtcCameraFromTarget();
                }
            }
        }
    }

}