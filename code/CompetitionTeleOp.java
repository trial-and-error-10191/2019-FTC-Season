package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Competition Code", group="Test Code")
public class CompetitionTeleOp extends LinearOpMode {
    CompetitionHardware robot = new CompetitionHardware();

    // Preference Variables
    final int DELTA_ELBOW = 50;
    final int DELTA_SHOULDER = 100;
    final double ARM_THRESH = 0.5;

    // State flags
    boolean isTurbo = false;
    boolean isPrecision = false;

    // Runtime stuff for latch
    ElapsedTime timeSinceLastLatch = new ElapsedTime();

    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();

        double maxSpeed = robot.driveTrain.AVG_SPEED;
        while(opModeIsActive()) {
            double translationX = gamepad1.left_stick_x * maxSpeed;
            double translationY = -gamepad1.left_stick_y * maxSpeed;
            double rotation = gamepad1.right_stick_x * maxSpeed;
            boolean turbo = gamepad1.dpad_up;
            boolean precision = gamepad1.dpad_down;

            // double armPower = gamepad2.left_stick_y;
            boolean openClaw = gamepad2.a;
            boolean closeClaw = gamepad2.b;
            boolean stow = gamepad2.dpad_down;
            boolean testElbow = gamepad2.dpad_up;
            boolean toggleLatch = gamepad2.x;
            double moveArm = gamepad2.left_stick_y;

            boolean leftservo = gamepad2.dpad_left;
            boolean rightservo = gamepad2.dpad_right;

            // Drivetrain Control
            if (turbo) {
                if (isTurbo) {
                    // Do nothing
                } else if (isPrecision) {
                    isPrecision = false;
                    maxSpeed = robot.driveTrain.AVG_SPEED;
                } else {
                    isTurbo = true;
                    maxSpeed = robot.driveTrain.MAX_SPEED;
                }
            }
            if (precision) {
                if (isPrecision) {
                    // Do nothing
                } else if (isTurbo) {
                    isTurbo = false;
                    maxSpeed = robot.driveTrain.AVG_SPEED;
                } else {
                    isPrecision = true;
                    maxSpeed = robot.driveTrain.MIN_SPEED;
                }
            }
            robot.driveTrain.travel(translationX, translationY, rotation);

            // Arm Control
           // robot.arm.move(armPower * robot.arm.AVG_SHOULDER_SPEED);
            if (openClaw) {
                robot.arm.grip(robot.arm.AVG_CLAW_SPEED);
            } else if (closeClaw) {
                robot.arm.grip(-robot.arm.AVG_CLAW_SPEED);
            } else {
                robot.arm.grip(0);
            }
            if (stow) {
                robot.arm.stow(telemetry);
            }
            if (toggleLatch && timeSinceLastLatch.milliseconds() >= 500) {
                robot.arm.latch();
                timeSinceLastLatch.reset();
            }
            if(Math.abs(moveArm) > 0.06) {
                robot.arm.shoulder.setPower(moveArm);
            } else {
                robot.arm.shoulder.setPower(0);
            }
            //move servo
            if(leftservo ){
                robot.vision.phoneServo.setPosition(robot.vision.phoneServo.getPosition()+0.001);
            }
            if (rightservo){
                robot.vision.phoneServo.setPosition(robot.vision.phoneServo.getPosition()-0.001);
            }
            telemetry.addData("phoneServoPosition", robot.vision.phoneServo.getPosition());

//            if (testElbow) {
//                robot.arm.elbowMove(robot.arm.shoulder.getCurrentPosition() + 100,0.3, telemetry);
//            }

            /*
            if (Math.abs(moveArm) > ARM_THRESH && !et.isAlive()) {
                if (moveArm > 0) {
                    robot.createElbowThread(robot.arm.elbow.getCurrentPosition()-DELTA_ELBOW,); // opp shoulder
                   // robot.(robot.arm.shoulder.getCurrentPosition()+DELTA_SHOULDER;
                } else {
                    robot.createElbowThread(robot.arm.elbow.getCurrentPosition()+DELTA_ELBOW,); // opp shoulder
                   // robot.createShoulderThread(robot.arm.shoulder.getCurrentPosition()-DELTA_SHOULDER;
                }
                robot.et.start();
            }
            */

            telemetry.update();
        }

        // Do we need to do anything on shutdown?
    }
}
