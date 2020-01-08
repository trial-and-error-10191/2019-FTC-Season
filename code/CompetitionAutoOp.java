package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

@Autonomous(name="Auto", group="Test Code")
public class CompetitionAutoOp extends LinearOpMode {
    CompetitionHardware robot = new CompetitionHardware();

    ElapsedTime elapsedDwell = new ElapsedTime();
    double dwellTime = 2500;

    // Movement Variables
    double dir = 1;
    double locationDistance = 11;
    double skystoneScanDistance1 = 18;
    double skystoneScanDistance2 = 6;

    // Behavior Flags
    boolean movedFlag = false;
    boolean debug = false;
    int hasMoved = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();
        //cool

        if (debug) {
            robot.driveTrain.strafeToPosition(20, 0.45);
            sleep(500);
            robot.driveTrain.strafeToPosition(-20, 0.45);
        } else {
            // Back up to see the picture
            telemetry.addLine("Trying to find myself");
            telemetry.update();
            robot.createDriveThread(-locationDistance, 0.45, telemetry);
            robot.dt.start();
            while (robot.dt.isAlive()) {

            }

            // Look for our location
            elapsedDwell.reset();
            robot.vision.phoneServo.setPosition(robot.vision.getCenterPosition(locationDistance +
                    robot.vision.CAMERA_FORWARD_DISPLACEMENT));
            while (!robot.vision.locationFlag && elapsedDwell.milliseconds() < dwellTime) {
                robot.vision.detectLocation();
            }

            // If we know where we are
            if (robot.vision.locationFlag) {
                telemetry.addLine("found myself");
                telemetry.update();

                // If you are on red team, reverse your directions for left and right
                if (!robot.vision.blueTeam) {
                    dir = -1;
                }

                telemetry.addData("Depot Side", robot.vision.depot);
                telemetry.addData("Blue Team", robot.vision.blueTeam);
                telemetry.update();

                // If you are not at depot, do build site autonomous
                if (!robot.vision.depot) {
                    // Lift up the Foundation latch
                    robot.arm.latch();

                    // Move to center of Foundation
                    telemetry.addLine("Moving to foundation");
                    telemetry.update();
                    robot.createStrafeThread(17.5 * dir, 0.45);
                    robot.st.start();
                    while (robot.st.isAlive()) {
                    }
                    sleep(200);

                    // Move to the Foundation
                    // TODO: Fix this, weight imbalance has been changed. Distances should be same
                    robot.createDriveThread( -30 , 0.4, telemetry);
                    robot.dt.start();
                    while (robot.dt.isAlive()) {
                    }
                    sleep(200);

                    // Put the latch down to grab the foundation
                    robot.arm.latch();
                    sleep(200);

                    // Move foundation to build site
                    telemetry.addLine("Moving to build site");
                    telemetry.update();
                    robot.driveTrain.move(34, 0.4, telemetry);

                    // Release foundation
                    robot.arm.latch();

                    // Go park under skybridge
                    telemetry.addLine("parking");
                    telemetry.update();
                    robot.driveTrain.strafeToPosition(-55 * dir, 0.45);
                }
                // If you are at depot side, do its autonomous
                else {
                    // Turn around to look for skystones
                    telemetry.addLine("Looking for skystones");
                    telemetry.update();
                    robot.vision.setServoAngle(90); // Point camera normal to the robot
                    robot.driveTrain.rotate(  robot.driveTrain.getHeading()+180, robot.driveTrain.ROT_SPEED, telemetry);

                    // If on red team, strafe to a side
                    if(!robot.vision.blueTeam) {
                        robot.createStrafeThread(16, 0.45);
                        robot.st.start();
                        while (robot.st.isAlive()) {
                        }
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
                        robot.createStrafeThread(skystoneScanDistance1 * dir, 0.45);
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

                        // You've moved once
                        hasMoved = 1;
                    }
                    // If you didn't see it a second time
                    if (!robot.vision.skyStoneFlag) {
                        // Strafe an amount to look at more stones (should be at the wall)
                        robot.createStrafeThread(skystoneScanDistance2 * dir, 0.45);
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
                        telemetry.addLine("found skystone");
                        telemetry.update();
                        robot.vision.detectSkystone(telemetry);

                        // Line up the edge opposite the camera with the detected skystone
                        telemetry.addLine("Centering Skystone");
                        telemetry.update();
                        double distance = robot.vision.blueTeam ? robot.vision.getEdgeOffsetFromSkystone() :
                                robot.vision.getCameraOffsetFromSkystone();
                        robot.createStrafeThread(distance, 0.45);
                        robot.st.start();
                        while (robot.st.isAlive()) {
                            // Busy waiting
                        }
                        sleep(500);

                        // Drive towards the skystone
                        robot.createDriveThread(robot.vision.getDistanceToSkystone(), 0.45, telemetry);
                        robot.dt.start();
                        while (robot.dt.isAlive()) {
                            // Busy waiting
                        }
                        sleep(500);

                        // TODO: Put in code for obtaining the skystone

                        // Drive away from skystone
                        telemetry.addLine("Moving build site");
                        telemetry.update();
                        robot.createDriveThread(-12, 0.45, telemetry);
                        robot.dt.start();
                        while (robot.dt.isAlive()) {
                            // Busy waiting
                        }
                        sleep(500);

                        // Turn 90 degrees to go towards skybridge
                        robot.driveTrain.rotate(dir * robot.driveTrain.getHeading() + 90, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(200);

                        // Move towards build site (assumption is that it doesn't move)
                        if (hasMoved == 2) {
                            robot.driveTrain.move(82 + skystoneScanDistance1 + skystoneScanDistance2,
                                    0.45, telemetry);
                        }
                        else {
                            robot.driveTrain.move(82 + hasMoved * skystoneScanDistance1,
                                    0.45, telemetry);
                        }

                        // Rotate such that outtake is above build site
                        robot.driveTrain.rotate(robot.driveTrain.getHeading() - 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move towards build site (assumption is that it doesn't move)
                        robot.driveTrain.move(20, 0.45, telemetry);
                        sleep(200);

                        // TODO: Put in code for dropping the skystone

                        // Back up to park
                        robot.driveTrain.move(-7, 0.45, telemetry);
                        sleep(200);

                        // Rotate towards our parking spot
                        telemetry.update();
                        telemetry.addLine("Parking");
                        robot.driveTrain.rotate(robot.driveTrain.getHeading() + 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move to our parking spot
                        robot.driveTrain.move(-48, 0.45, telemetry);
                    }
                    // If you still didn't find one, last one is skystone
                    if (!robot.vision.skyStoneFlag) {
                        // Assume a distance to a stone
                        robot.driveTrain.move(20, 0.45, telemetry);
                        sleep(500);

                        // TODO: Put in code for grabbing the skystone

                        // Back up to drive towards foundation
                        robot.driveTrain.move(-12, 0.45, telemetry);
                        sleep(500);

                        // Rotate towards foundation
                        robot.driveTrain.rotate(-90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Drive towards foundation
                        robot.driveTrain.move(82 + skystoneScanDistance1 + skystoneScanDistance2, 0.45, telemetry);

                        // Rotate to face foundation
                        robot.driveTrain.rotate(robot.driveTrain.getHeading() - 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move towards foundation (assumption is that it doesn't move)
                        robot.driveTrain.move(20, 0.45, telemetry);
                        sleep(200);

                        // TODO: Put in code for dropping the skystone

                        // Back up to park
                        robot.driveTrain.move(-7, 0.45, telemetry);
                        sleep(200);

                        // Rotate towards our parking spot
                        robot.driveTrain.rotate(robot.driveTrain.getHeading() + 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
                        sleep(500);

                        // Move to our parking spot
                        robot.driveTrain.move(-48, 0.45, telemetry);

                    }
                }
            }
            robot.vision.deinit();
        }
    }
}

