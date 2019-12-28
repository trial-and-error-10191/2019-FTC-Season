package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;

@Autonomous(name="Auto", group="Test Code")
public class CompetitionAutoOp extends LinearOpMode {
    CompetitionHardware robot = new CompetitionHardware();

    ElapsedTime elapsedDwell = new ElapsedTime();
    double dwellTime = 2000;

    // Movement Variables
    double dir = 1;
    double locationDistance = 11;
    double skystoneScanDistance = 12;

    // Behavior Flags
    boolean movedFlag = false;
    boolean debug = false;
    int hasMoved = 0;


    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();

        if (debug) {



        } else {

            robot.vision.setServoAngle(70);
            movedFlag = true;
            telemetry.addLine("Trying to find myself");
            robot.createDriveThread(-locationDistance, 0.45, telemetry);
            robot.dt.start();
            while (robot.dt.isAlive()) {

            }
            elapsedDwell.reset();
            //robot.vision.scanTheRoom();
          //  robot.vision.phoneServo.setPosition(robot.vision.getCenterPosition(locationDistance +
            //
            //        robot.vision.CAMERA_FORWARD_DISPLACEMENT));
            while (!robot.vision.locationFlag && elapsedDwell.milliseconds() < dwellTime) {
                robot.vision.detectLocation();
            }

            if (robot.vision.locationFlag) {
                telemetry.update();
                telemetry.addLine("found myself");
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
                    telemetry.update();
                    telemetry.addLine("Moving to build site");
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
                    telemetry.update();
                    telemetry.addLine("Moving build site");
                    robot.driveTrain.move(34, 0.4, telemetry);
                    robot.arm.latch();
                    telemetry.update();
                    telemetry.addLine("parking");
                    robot.driveTrain.strafeToPosition(-55 * dir, 0.45);

                }
                // Do depot site autonomous
                else if (robot.vision.blueTeam && robot.vision.depot){
                    // Turn around to look for skystones
                    telemetry.update();
                    telemetry.addLine("Looking for skystones");
                    robot.vision.setServoAngle(90); // Point camera normal to the robot
                    robot.driveTrain.rotate(180, robot.driveTrain.ROT_SPEED, telemetry);
                    sleep(500);

                    // Look for skystones
                    elapsedDwell.reset();
                    while (!robot.vision.skyStoneFlag && elapsedDwell.milliseconds() < dwellTime) {
                        robot.vision.detectSkystone(telemetry);
                        telemetry.addData("skystone", robot.vision.skyStoneFlag);
                        telemetry.update();
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
                            telemetry.addData("skystone", robot.vision.skyStoneFlag);
                            telemetry.update();
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
                            robot.vision.detectSkystone(telemetry);
                            telemetry.addData("skystone", robot.vision.skyStoneFlag);
                            telemetry.update();
                        }

                        // You've moved twice
                        hasMoved = 2;
                    }
                    if (robot.vision.skyStoneFlag) {
                        // Update Skystone location
                        telemetry.update();
                        telemetry.addLine("found skystone");
                        robot.vision.detectSkystone(telemetry);

                        // Line up the center of the robot with the detected skystone
                        telemetry.update();
                        telemetry.addLine("Moving skystones");
                        robot.createStrafeThread(robot.vision.getRobotOffsetFromSkystone(), 0.45);
                        robot.st.start();
                        while (robot.st.isAlive()) {
                            // Busy waiting
                        }
                        sleep(500);

                        // Drive towards the skystone
                        robot.createDriveThread(robot.vision.getDistanceToSkystone(), 0.45, telemetry);
                        robot.dt.start();
                        sleep(500);
                        while (robot.dt.isAlive()) {
                            // Busy waiting
                        }

                        // TODO: Put in code for obtaining the skystone

                        // Drive away from skystone
                        telemetry.update();
                        telemetry.addLine("Moving build site");
                        robot.createDriveThread(-12, 0.45, telemetry);
                        robot.dt.start();
                        sleep(500);
                        while (robot.dt.isAlive()) {
                            // Busy waiting

                        }

                        // Turn 90 degrees to go towards skybridge
                        robot.driveTrain.rotate(dir * -90, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(200);

                        // Move towards build site (assumption is that it doesn't move)
                        robot.driveTrain.move(82 + hasMoved * skystoneScanDistance,
                                0.45, telemetry);
                        sleep(200);



                        // Rotate such that outtake is above build site
                        robot.driveTrain.rotate(robot.driveTrain.getHeading() - 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move towards build site (assumption is that it doesn't move)
                        robot.driveTrain.move(20, 0.45, telemetry);
                        sleep(200);
                        robot.driveTrain.move(-7, 0.45, telemetry);
                        sleep(200);

                        // TODO: Put in code for dropping the skystone

                        // Rotate towards our parking spot
                        telemetry.update();
                        telemetry.addLine("Parking");
                        robot.driveTrain.rotate(robot.driveTrain.getHeading() + 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move to our parking spot
                        robot.driveTrain.move(-48, 0.45, telemetry);
                    }
                    // If you still didn't find one, go park
                    if (!robot.vision.skyStoneFlag) {
                        telemetry.update();
                        telemetry.addLine("No skyston, Parking");
                        robot.driveTrain.move(14, 0.45, telemetry);
                        sleep(500);
                        robot.driveTrain.move(-12, 0.45, telemetry);
                        sleep(500);
                        robot.driveTrain.rotate(-90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);
                        robot.driveTrain.move(34+skystoneScanDistance*hasMoved, 0.45, telemetry);

                    }
                }
                else {
                    // Turn around to look for skystones
                    robot.vision.setServoAngle(90); // Point camera normal to the robot
                    robot.driveTrain.rotate(180, robot.driveTrain.ROT_SPEED, telemetry);
                    sleep(500);

                    robot.createStrafeThread(16, 0.45);
                    robot.st.start();
                    while (robot.st.isAlive()) {

                    }
                    // Look for skystones
                    elapsedDwell.reset();
                    while (!robot.vision.skyStoneFlag && elapsedDwell.milliseconds() < dwellTime) {
                        robot.vision.detectSkystone(telemetry);
                        telemetry.addData("skystone", robot.vision.skyStoneFlag);
                        telemetry.update();
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
                            telemetry.addData("skystone", robot.vision.skyStoneFlag);
                            telemetry.update();
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
                            robot.vision.detectSkystone(telemetry);
                            telemetry.addData("skystone", robot.vision.skyStoneFlag);
                            telemetry.update();
                        }

                        // You've moved twice
                        hasMoved = 2;
                    }
                    if (robot.vision.skyStoneFlag) {
                        // Update Skystone location
                        robot.vision.detectSkystone(telemetry);

                        // Line up the center of the robot with the detected skystone
                        VectorF translation = robot.vision.currentSkystoneLocation.getTranslation();
                        robot.createStrafeThread(translation.get(robot.vision.hIndex)/25.4, 0.45);
                        robot.st.start();
                        while (robot.st.isAlive()) {
                            // Busy waiting
                        }
                        sleep(500);

                        // Drive towards the skystone
                        robot.createDriveThread(robot.vision.getDistanceToSkystone(), 0.45, telemetry);
                        robot.dt.start();
                        sleep(500);
                        while (robot.dt.isAlive()) {
                            // Busy waiting
                        }

                        // TODO: Put in code for obtaining the skystone

                        // Drive away from skystone
                        robot.createDriveThread(-12, 0.45, telemetry);
                        robot.dt.start();
                        sleep(500);
                        while (robot.dt.isAlive()) {
                            // Busy waiting

                        }

                        // Turn 90 degrees to go towards skybridge
                        robot.driveTrain.rotate(dir * -90, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move towards build site (assumption is that it doesn't move)
                        robot.driveTrain.move(82 + hasMoved * skystoneScanDistance,
                                0.45, telemetry);
                        sleep(500);

                        // Rotate such that outtake is above build site
                        robot.driveTrain.rotate(robot.driveTrain.getHeading() - 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move towards build site (assumption is that it doesn't move)
                        robot.driveTrain.move(20, 0.45, telemetry);
                        sleep(200);
                        robot.driveTrain.move(-7, 0.45, telemetry);
                        sleep(200);

                        // TODO: Put in code for dropping the skystone

                        // Rotate towards our parking spot
                        robot.driveTrain.rotate(robot.driveTrain.getHeading() + 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move to our parking spot
                        robot.driveTrain.move(-48, 0.45, telemetry);
                    }
                    // If you still didn't find one, go park
                    if (!robot.vision.skyStoneFlag) {
                        robot.driveTrain.move(14, 0.45, telemetry);
                        sleep(500);
                        robot.driveTrain.move(-12, 0.45, telemetry);
                        sleep(500);
                        robot.driveTrain.rotate(-90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);
                        robot.driveTrain.move(34+skystoneScanDistance*hasMoved, 0.45, telemetry);

                    }
                }
            }
            robot.vision.deinit();
        }
    }
}

