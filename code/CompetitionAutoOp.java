package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

@Autonomous(name="Auto", group="Test Code")
public class CompetitionAutoOp extends LinearOpMode {
    CompetitionHardware robot = new CompetitionHardware();

    ElapsedTime elapsedDwell = new ElapsedTime();
    double dwellTime = 5000;

    // Movement Variables
    double dir = 1;
    double locationDistance = 18;
    double skystoneScanDistance = 5;

    // Vector Variables
    int hIndex = 0; // Horizontal Dimension of the image
    int dIndex = 2; // Depth dimension of the image

    // Behavior Flags
    boolean movedFlag = false;
    boolean debug = true;
    int hasMoved = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();

        if (debug) {
//            while(!robot.vision.locationFlag && !isStopRequested()) {
//                robot.vision.detectTrackable();
//                telemetry.addData("Location Found",robot.vision.locationFlag);
//                telemetry.update();
//            }
//            robot.vision.deinit();

           sleep(500);
            robot.driveTrain.rotate( 90, 0.45, telemetry);
            sleep(500);
            robot.createDriveThread(14, 0.45, telemetry);
            robot.dt.start();
            while (robot.dt.isAlive()) {

            }
            robot.driveTrain.rotate( 90, 0.45, telemetry);
            // Point camera normal to the robot
            robot.vision.setServoAngle(90);


            // Look for skystones
            elapsedDwell.reset();
            while (!robot.vision.skyStoneFlag && elapsedDwell.milliseconds() < dwellTime) {
                robot.vision.depotScan();
            }

        } else {
            movedFlag = true;
            robot.createDriveThread(-locationDistance, 0.45, telemetry);
            robot.dt.start();
            while (robot.dt.isAlive()) {

            }
            elapsedDwell.reset();
            robot.vision.phoneServo.setPosition(robot.vision.getCenterPosition(locationDistance +
                    robot.vision.CAMERA_FORWARD_DISPLACEMENT));
            while (!robot.vision.locationFlag && elapsedDwell.milliseconds() < dwellTime) {
                robot.vision.detectTrackable();
            }

            if (robot.vision.locationFlag) {
                if (!robot.vision.blueTeam) {
                    dir = -1;
                }
                // Do build site autonomous
                if (!robot.vision.depot) {
                    telemetry.addData("side", robot.vision.depot);
                    telemetry.addData("teamBlue", robot.vision.blueTeam);
                    telemetry.update();

                    //  robot.arm.elbowMove(-100, 0.3);
                    robot.arm.latch();
                    sleep(200);
                    robot.createStrafeThread(11 * dir, 0.45);
                    robot.st.start();
                    while (robot.st.isAlive()) {
                    }
                    sleep(200);
                    // Red side seems to go too far. We need to move away from dead reckoning
                    robot.createDriveThread(robot.vision.blueTeam ? -31 + locationDistance : -29 +
                            locationDistance, 0.4, telemetry);
                    robot.dt.start();
                    while (robot.dt.isAlive()) {
                    }
                    sleep(200);
                    robot.arm.latch();
                    sleep(500);
                    robot.driveTrain.move(34, 0.4, telemetry);
                    robot.arm.latch();
                    robot.driveTrain.strafeToPosition(-55 * dir, 0.45);

                }
                // Do depot site autonomous
                else {
                    // Turn around to look for skystones"
                    telemetry.addData("hi", null);
                    telemetry.update();
                    robot.driveTrain.rotate(180, 0.8, telemetry);
                    sleep(500);
                    // Point camera normal to the robot
                    robot.vision.setServoAngle(90);


                    // Look for skystones
                    elapsedDwell.reset();
                    while (!robot.vision.skyStoneFlag && elapsedDwell.milliseconds() < dwellTime) {
                        robot.vision.depotScan();
                    }

                    // If you didn't find it the first time
                    if (!robot.vision.skyStoneFlag) {
                        // Strafe an amount to look at more stones
                        robot.createStrafeThread(skystoneScanDistance * dir, 0.45);
                        robot.st.start();
                        while (robot.st.isAlive()) {
                            // Busy waiting
                        }

                        // Look for skystones
                        elapsedDwell.reset();
                        while (!robot.vision.skyStoneFlag && elapsedDwell.milliseconds() < dwellTime) {
                            robot.vision.depotScan();
                        }

                        // You've moved once
                        hasMoved = 1;
                    }
                    // If you didn't see it a second time
                    if (!robot.vision.skyStoneFlag) {
                        // Strafe an amount to look at more stones
                        robot.createStrafeThread(skystoneScanDistance * dir, 0.45);
                        robot.st.start();
                        while (robot.st.isAlive()) {
                            // Busy waiting
                        }

                        // Look for skystones
                        elapsedDwell.reset();
                        while (!robot.vision.skyStoneFlag && elapsedDwell.milliseconds() < dwellTime) {
                            robot.vision.depotScan();

                        }

                        // You've moved twice
                        hasMoved = 2;
                    }
                    if (robot.vision.skyStoneFlag) {
                        // Get the vector to the object using vuforia
                        VectorF translation = robot.vision.currentSkystoneLocation.getTranslation();

                        // Line up the center of the robot with the detected skystone
                        robot.createStrafeThread((double) (translation.get(hIndex) -
                                robot.vision.CAMERA_LEFT_DISPLACEMENT) * dir, 0.45);
                        robot.st.start();
                        while (robot.st.isAlive()) {
                            // Busy waiting
                        }
                        sleep(500);
                        // Drive towards the skystone
                        robot.createDriveThread((double) (translation.get(dIndex)-2), 0.45, telemetry);
                        robot.dt.start();
                        sleep(500);
                        while (robot.dt.isAlive()) {
                            // Busy waiting

                        }

                        // TODO: Put in code for obtaining the skystone

                        // Turn 90 degrees to go towards skybridge
                        robot.driveTrain.rotate(dir*90, 0.45, telemetry);
                        sleep(500);
                        // Move towards build site (assumption is that it doesn't move)
                        robot.driveTrain.move(82 + hasMoved*skystoneScanDistance,
                                0.45, telemetry);
                        sleep(500);
                        // Rotate such that outtake is above build site
                        robot.driveTrain.rotate(90 * dir,0.45, telemetry);
                        sleep(500);
                        // TODO: Put in code for dropping the skystone

                        // Rotate towards our parking spot
                        robot.driveTrain.rotate(-90 * dir,0.45, telemetry);
                        sleep(500);
                        // Move to our parking spot
                        robot.driveTrain.move(-48, 0.45, telemetry);
                    }
                    // If you still didn't find one, go park
                    if (!robot.vision.skyStoneFlag) {
                        robot.driveTrain.move(14,0.45, telemetry);
                        sleep(500);
                        robot.driveTrain.rotate(90 * dir, 0.45, telemetry);
                        sleep(500);
                        robot.driveTrain.move(34, 0.45, telemetry);

                    }
                }
                robot.vision.deinit();
            }
        }
    }
}

