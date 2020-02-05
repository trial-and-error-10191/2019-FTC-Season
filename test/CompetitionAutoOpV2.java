package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Auto V2", group="Test Code")
public class CompetitionAutoOpV2 extends LinearOpMode {
    CompetitionHardware robot = new CompetitionHardware();

    ElapsedTime elapsedDwell = new ElapsedTime();
    double dwellTime = 2500;

    // Movement Variables
    double dir = 1;
    double locationDistance = 11;
    double skystoneScanDistance1 = 18;
    double skystoneScanDistance2 = 6;
    double giveUpDistanceToWall = 3;
    double giveUpAlignHorz = robot.vision.ROBOT_WIDTH/2 - 2; // Fudge a bit because we aren't centered
    double skystoneAlignHorz = robot.vision.ROBOT_WIDTH/2 + 6; // Fudge a bit so we don't hit stone
    double giveUpAlingVert = 16;

    // Behavior Flags
    boolean movedFlag = false;
    boolean debug = false;
    int hasMoved = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();

        if (debug) {
            elapsedDwell.reset();
            robot.vision.setServoAngle(90);
            while (!robot.vision.skyStoneFlag && elapsedDwell.milliseconds() < dwellTime) {
                robot.vision.detectSkystone(telemetry);
                telemetry.addData("skystone", robot.vision.skyStoneFlag);
                telemetry.update();
            }
            double distance = robot.vision.getRobotOffsetFromSkystone();
            robot.createStrafeThread(distance, robot.driveTrain.AVG_SPEED,telemetry);
            robot.st.start();
            while (robot.st.isAlive()) {
                // Busy waiting
            }
            sleep(500);

            // Do evasive maneuvers to get skystone
            alignWithSkystone(false);
            pickUpSkystone();

        } else {
            // Determine where we are in the arena
            findOurselves();

            // If we know where we are
            if (robot.vision.locationFlag) {
                telemetry.addLine("found myself");
                telemetry.update();

                robot.vision.setServoAngle(90); // Point camera normal to the robot

                // If you are on red team, reverse your directions for left and right
                if (!robot.vision.blueTeam) {
                    this.dir = -1;
                }

                telemetry.addData("Depot Side", robot.vision.depot);
                telemetry.addData("Blue Team", robot.vision.blueTeam);
                telemetry.update();

                // If you are not at depot, do build site autonomous
                if (!robot.vision.depot) {
                    doFoundationRoute();
                }
                // If you are at depot side, do its autonomous
                else {
                    doDepotRoute();
                }
            }
        }
        robot.vision.deinit();
    }




    // Sets up state variables for autonomous based on what picture is detected
    public void findOurselves() {
        // Back up to see the picture
        telemetry.addLine("Trying to find myself");
        telemetry.update();
        robot.createDriveThread(-this.locationDistance, robot.driveTrain.AVG_SPEED, telemetry);
        robot.dt.start();
        while (robot.dt.isAlive()) {

        }

        // Look for our location
        elapsedDwell.reset();
        robot.vision.phoneServo.setPosition(robot.vision.getCenterPosition(locationDistance +
                robot.vision.CAMERA_FORWARD_DISPLACEMENT));
        while (!robot.vision.locationFlag && elapsedDwell.milliseconds() < this.dwellTime) {
            robot.vision.detectLocation();
        }
    }

    // Accomplishes the route we have planned for foundation side autonomous
    public void doFoundationRoute() {
        // Lift up the Foundation latch
        robot.arm.latch();

        // Move to center of Foundation
        if (robot.vision.blueTeam) {
            robot.createStrafeThread(22.5 * dir, robot.driveTrain.AVG_SPEED,telemetry);
        }
        else {
            robot.createStrafeThread(17.5 * dir, robot.driveTrain.AVG_SPEED,telemetry);
        }

        robot.st.start();
        while (robot.st.isAlive()) {
        }
        sleep(200);

        // Move to the Foundation
        // TODO: Fix this, weight imbalance has been changed. Distances should be same
        robot.createDriveThread( -28 , robot.driveTrain.AVG_SPEED, telemetry);
        robot.dt.start();
        while (robot.dt.isAlive()) {
        }
        sleep(600);

        // Put the latch down to grab the foundation
        robot.arm.latch();
        sleep(600);

        // Move foundation to build site
        telemetry.addLine("Moving to build site");
        telemetry.update();
        if (robot.vision.blueTeam) {
            robot.driveTrain.move(43, robot.driveTrain.AVG_SPEED, telemetry);
        }
        else {
            robot.driveTrain.move(50, robot.driveTrain.AVG_SPEED, telemetry);
        }


        // Release foundation
        robot.arm.latch();

        // Go park under skybridge
        telemetry.addLine("parking");
        telemetry.update();
        robot.driveTrain.strafeToPosition(-55 * dir, robot.driveTrain.AVG_SPEED,telemetry);
    }

    // Accomplishes the route we have planned for depot side autonomous
    public void doDepotRoute() {
        // Turn around to look for stones
        robot.driveTrain.rotate(  robot.driveTrain.getHeading()+180, robot.driveTrain.ROT_SPEED, telemetry);

        // Scan for stones, grab it, and back up
        sleep(500);
        scanForSkystone();

        // Drop stone off at foundation, and park under bridge
        if (robot.vision.skyStoneFlag) {
            dropOffSkystone();
            park();
        }
        // If you still didn't find one, last one is skystone
        else {
            giveUpAndGrabAStone();
        }

        sleep(500);

        // Move back to starting position
        robot.driveTrain.move(34, robot.driveTrain.AVG_SPEED, telemetry);

        // Set our robot back to a detection state
        resetFlags();

        // Scan for stones, grab it, and back up
        sleep(500);
        scanForSkystone();

        // Drop stone off at foundation, and park under bridge
        if (robot.vision.skyStoneFlag) {
            dropOffSkystone();
            park();
        }
        // If you still didn't find one, last one is skystone
        else {
            giveUpAndGrabAStone();
        }
    }

    // Default behavior if no skystone is detected, i.e., grab the last stone
    public void giveUpAndGrabAStone() {
        // Assume a distance to a stone
        robot.driveTrain.move(20, robot.driveTrain.AVG_SPEED, telemetry);
        sleep(500);

        // Do evasive maneuvers to get skystone
        alignWithSkystone(true);

        pickUpSkystone();

        dropOffSkystone();

        park();
    }

    // Sweeps through the stones looking for a skystone. If detected, robot will grab stone and back up
    public void scanForSkystone() {
        // Turn around to look for skystones
        telemetry.addLine("Looking for skystones");
        telemetry.update();

        // If on red team, strafe to a side
        if(!robot.vision.blueTeam) {
            robot.createStrafeThread(16, robot.driveTrain.AVG_SPEED,telemetry);
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
            robot.createStrafeThread(skystoneScanDistance1 * dir, robot.driveTrain.AVG_SPEED,telemetry);
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
            robot.createStrafeThread(skystoneScanDistance2 * dir, robot.driveTrain.AVG_SPEED,telemetry);
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

            // Line up the center of the robot
            telemetry.addLine("Centering Skystone");
            telemetry.update();
            double distance = robot.vision.getRobotOffsetFromSkystone();
            robot.createStrafeThread(distance, 0.45,telemetry);
            robot.st.start();
            while (robot.st.isAlive()) {
                // Busy waiting
            }
            sleep(500);

            // Do evasive maneuvers to get skystone
            alignWithSkystone(false);

            // Pick up the skystone
            pickUpSkystone();

            // Drive away from skystone
            telemetry.addLine("Backing up to move to build site");
            telemetry.update();
            robot.createDriveThread(-12, 0.45, telemetry);
            robot.dt.start();
            while (robot.dt.isAlive()) {
                // Busy waiting
            }
        }
    }

    // Places a skystone in foundation starting from backed up from grabbing a detected stone
    public void dropOffSkystone() {
        // Turn 90 degrees to go towards skybridge
        robot.driveTrain.rotate(dir * robot.driveTrain.getHeading() + 90, robot.driveTrain.ROT_SPEED, telemetry);
        sleep(200);

        // Move towards build site (assumption is that it doesn't move)
        if (this.hasMoved == 2) {
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

        // Drop the stone, stacking is hard
        robot.arm.openClaw();
    }

    // Picks up a skystone using a moment arm
    public void pickUpSkystone() {
        robot.arm.shoulderMove(robot.arm.SHOULDER_AT_90,robot.arm.AVG_SHOULDER_SPEED,telemetry);
        robot.arm.clawMove(robot.arm.CLOSED_CLAW, robot.arm.AVG_CLAW_SPEED, 1500);
        robot.arm.toggleHold();
        sleep(300);
        robot.arm.stow(telemetry);
    }

    // TODO: Positions robot to pick up the skystone on the skinny side
    public void alignWithSkystone(boolean gaveUp) {
        if (gaveUp) {
            // At this point we are almost at the wall
            // Drive to the wall
            robot.createDriveThread(dir*giveUpDistanceToWall, 0.45, telemetry);
            robot.dt.start();
            while (robot.dt.isAlive()) {
                // Busy waiting
            }
            sleep(500);

            // Get ready to crash into stones to the left/right of the skystone
            if (hasMoved == 0) {
                robot.driveTrain.strafeToPosition(dir*giveUpAlignHorz,robot.driveTrain.MIN_SPEED,telemetry);
            }
            else {
                robot.driveTrain.strafeToPosition(-dir*giveUpAlignHorz,robot.driveTrain.MIN_SPEED,telemetry);
            }


            // drive toward stone
            robot.createDriveThread(giveUpAlingVert, 0.45, telemetry);
            robot.dt.start();
            while (robot.dt.isAlive()) {
                // Busy waiting
            }
            sleep(500);


        } else {
            // At this point, we are centered on a skystone
            // Get ready to crash into stones to the left/right of the skystone
            robot.driveTrain.strafeToPosition(-dir*skystoneAlignHorz,robot.driveTrain.MIN_SPEED,telemetry);

            // Drive towards the skystone
            double distance = robot.vision.getDistanceToSkystone()+robot.vision.ROBOT_LENGTH/2;
            robot.createDriveThread(distance, 0.45, telemetry);
            robot.dt.start();
            while (robot.dt.isAlive()) {
                // Busy waiting
            }
            sleep(500);
        }
        // Rotate towards the skystone
        robot.driveTrain.rotate(robot.driveTrain.getHeading()-dir*90,robot.driveTrain.ROT_SPEED,telemetry);

        // Pick up the skystone
        pickUpSkystone();

        // Reverse movements to set us back into expected position for dropping off skystone
        // Reverse rotation
        robot.driveTrain.rotate(robot.driveTrain.getHeading()+dir*90,robot.driveTrain.ROT_SPEED,telemetry);

        // Reverse the increased distance to skystone
        robot.createDriveThread(-robot.vision.ROBOT_LENGTH/2, 0.45, telemetry);
        robot.dt.start();
        while (robot.dt.isAlive()) {
            // Busy waiting
        }
        sleep(500);
    }

    // Moves the robot from just after placing a skystone to parking under the skybridge
    public void park() {
        // Back up to park
        robot.driveTrain.move(-7, robot.driveTrain.AVG_SPEED, telemetry);
        sleep(200);

        // Rotate towards our parking spot
        telemetry.update();
        telemetry.addLine("Parking");
        robot.driveTrain.rotate(robot.driveTrain.getHeading() + 90 * dir, robot.driveTrain.ROT_SPEED, telemetry);
        sleep(500);

        // Move to our parking spot
        robot.driveTrain.move(-48, robot.driveTrain.AVG_SPEED, telemetry);
    }

    // Resets state flags and other variables to just before detecting skystones
    public void resetFlags() {
        robot.vision.skyStoneFlag = false;
        this.hasMoved = 0;
    }
}


