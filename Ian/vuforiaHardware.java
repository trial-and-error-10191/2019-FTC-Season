package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.CameraDevice;

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

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;

// hardware

public class vuforiaHardware {
    // IMPORTANT:  For Phone Camera, set 1) the camera source and 2) the orientation, based on how your phone is mounted:
    // 1) Camera Source.  Valid choices are:  BACK (behind screen) or FRONT (selfie side)
    // 2) Phone Orientation. Choices are: PHONE_IS_PORTRAIT = true (portrait) or PHONE_IS_PORTRAIT = false (landscape)
    //
    // NOTE: If you are running on a CONTROL HUB, with only one USB WebCam, you must select CAMERA_CHOICE = BACK; and PHONE_IS_PORTRAIT = false;
    //
    public static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    public static final boolean PHONE_IS_PORTRAIT = true;

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    public static final String VUFORIA_KEY =
            "Acd3Mhv/////AAABmQHTPP6MLkaAuT4ajCpWMLFIAsffT0PglAjW5YBhoEBRGmKeJcOmf37joiF+BKOuseAqCQ+Dq6THvITLD+L/v5UI/RaEka+Egq7V+JYnS26F1HnEGFG0pYR6TxQksLltAKf7HvyKRgfZLwtRSGPvA8/Pvu936WpjlRDOizksUUMQ8+iaM/aPUKGrlswF8QrzncCcmCGOSq+HwwHCJH6pJSQK7HTgGmg5TMLkXK5Q5D3OILskNBUI8LVrAEzWY7mDZVGpYekIBeb4IoNu7tShpgOmj4Sx0VLoT5eidMHOzN+BAoTkoZkawqGqWIHHwykX0cLcxeh8hWhHhN2n56mbv/57u/h9eUUdXzNZikOhPRff";

    //math
    // Since ImageTarget trackables use mm to specifiy their dimensions, we must use mm for all the physical dimension.
    // We will define some constants and conversions here
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

    // Class Members
    public OpenGLMatrix lastLocation = null;
    public VuforiaLocalizer vuforia = null;
    public boolean targetVisible = false;
    public float phoneXRotate = 0;
    public float phoneYRotate = 0;
    public float phoneZRotate = 0;

    // Flag for if we have found where we are
    public boolean locationFlag = false;
    //looking for the the side that we are on
    public boolean depot;
    // sets the team
    public boolean blueTeam;
    // is the skystone visable
    boolean skyStoneVis;
    // which camra phone? or webcam?
    boolean webCam;
    boolean phoneCam;


    // Load from assets
    VuforiaTrackables targetsSkyStone = null;
    // webcam
    WebcamName webcamName = null;


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
    phoneServoHardware robot = new phoneServoHardware();

    // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();


    public void init(HardwareMap ahwMap) {

        // Create a transformation matrix describing where the phone is on the robot.
        //
        // NOTE !!!!  It's very important that you turn OFF your phone's Auto-Screen-Rotation option.
        // Lock it into Portrait for these numbers to work.
        //
        // Info:  The coordinate frame for the robot looks the same as the field.
        // The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
        // Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
        //
        // The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
        // pointing to the LEFT side of the Robot.
        // The two examples below assume that the camera is facing forward out the front of the robot.

        // We need to rotate the camera around it's long axis to bring the correct camera forward

        // For convenience, gather together all the trackable objects in one easily-iterable collection */


        int cameraMonitorViewId = ahwMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", ahwMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CAMERA_CHOICE;
        parameters.cameraName = webcamName;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone");

        webcamName = ahwMap.get(WebcamName.class, "Webcam 1");


        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);

        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90;
        }

        // Next, translate the camera lens to where it is on the robot.
        // In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
        final float CAMERA_FORWARD_DISPLACEMENT = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot center
        final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT = 0;     // eg: Camera is ON the robot's center line


        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));


        /**  Let all the trackable listeners know where the phone is.  */
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }
        // to turn on the light
        CameraDevice.getInstance().setFlashTorchMode(true);

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
    public  void setCamera(String camera){
        if(camera.equalsIgnoreCase("phone")) {
            this.phoneCam = true;
            return;
        } else if(camera.equalsIgnoreCase("webcam")) {
            this.webCam = true;
            return;
        } else {
            this.phoneCam = false;
            this.webCam = false;
            return;
        }
    }

    public void scanTheRoom() {
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        setCamera("phone");
        for (VuforiaTrackable trackable : allTrackables) {
            // seeing the alliance wall using the phone
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                if (trackable.getName().equals("Red Perimeter 1")) {
                    //telemetry.addData("red alliance wall", trackable.getName());
                    // depot side
                    depot = false;
                    blueTeam = false;
                    this.locationFlag = true;
                    return;
                } else if (trackable.getName().equals("Red Perimeter 2")) {
                    // telemetry.addData("red alliance wall", trackable.getName());
                    //build side
                    depot = true;
                    blueTeam = false;
                    this.locationFlag = true;
                    return;
                } else if (trackable.getName().equals("Blue Perimeter 1")) {
                    //telemetry.addData(" blue alliance wall", trackable.getName());
                    //depot side
                    depot = true;
                    blueTeam = true;
                    this.locationFlag = true;
                    return;
                } else if (trackable.getName().equals("Blue Perimeter 2")) {
                    // telemetry.addData("blue alliance wall ", trackable.getName());
                    //build side
                    depot = false;
                    blueTeam = true;
                    this.locationFlag = true;
                    return;
                }
            }
        }
    }

    public void depotScan() {
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        setCamera("webcam");
        //robot.turnServo(START_POINT);
        //add a sleep(1 sec);
        //robot.turnServo(MID_POINT);
        for (VuforiaTrackable trackable : allTrackables) {
            // seeing the sky stones
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                //looking for the skystone
                if (trackable.getName().equals("Sky Stones")) {
                  skyStoneVis = true;

                }
            }
        }
    }


}
//
//            // using the webcam
//            else {
//                if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
//                    if (trackable.getName().equals("Front Perimeter 1")) {
//                        //  telemetry.addData("red depot", trackable.getName());
//                    }
//                    else if (trackable.getName().equals("Front Perimeter 2")) {
//                        //  telemetry.addData("blue depot", trackable.getName());
//                    }
//                    else if (trackable.getName().equals("Rear Perimeter 2")) {
//                        // telemetry.addData("build side/ red side", trackable.getName());
//                    }
//                    else if (trackable.getName().equals("Rear Perimeter 1")) {
//                        // telemetry.addData("build side / blue side", trackable.getName());
//                    }
//                    else {
//                        // telemetry.addLine("Sky Stones");
//                    }
//                    if (trackable.getName().equals("Sky Stones")) {
//                        //move the arm
//                    }
//                }
//            }
//
//



