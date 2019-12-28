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
    final int LATCH_DELAY = 500;
    final int SWITCH_DELAY = 500;

    // State flags
    boolean isTurbo = false;
    boolean isPrecision = false;

    // Runtime stuff for certain controls
    ElapsedTime timeSinceLastLatch = new ElapsedTime();
    ElapsedTime timeSinceLastModeSwitch = new ElapsedTime();

    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();

        // Display/Functional Variables
        String drivingMode = "Normal";
        double maxSpeed = robot.driveTrain.AVG_SPEED;
        double curSpeed = 0;

        // Driver Variables
        double translationX;
        double translationY;
        double rotation;
        boolean turbo;
        boolean precision;

        // Operator Variables
        boolean openClaw;
        boolean closeClaw;
        boolean stow;
        boolean toggleLatch;
        double moveArm;
        boolean leftservo;
        boolean rightservo;

        // Telemetry Loop
        while(opModeIsActive()) {
            // Change Driver Variables
            translationX = gamepad1.left_stick_x * maxSpeed;
            translationY = -gamepad1.left_stick_y * maxSpeed;
            rotation = gamepad1.right_stick_x * maxSpeed;
            turbo = gamepad1.dpad_up;
            precision = gamepad1.dpad_down;

            // Change Operator Variables
            openClaw = gamepad2.a;
            closeClaw = gamepad2.b;
            stow = gamepad2.dpad_down;
            toggleLatch = gamepad2.x;
            moveArm = gamepad2.left_stick_y;
            leftservo = gamepad2.dpad_left;
            rightservo = gamepad2.dpad_right;

            // Drivetrain Control
            if (turbo && timeSinceLastModeSwitch.milliseconds() >= SWITCH_DELAY) {
                if (isTurbo) {
                    // Do nothing
                } else if (isPrecision) {
                    isPrecision = false;
                    maxSpeed = robot.driveTrain.AVG_SPEED;
                    drivingMode = "Normal";
                } else {
                    isTurbo = true;
                    maxSpeed = robot.driveTrain.MAX_SPEED;
                    drivingMode = "Turbo";
                }
                timeSinceLastModeSwitch.reset();
            }
            if (precision && timeSinceLastModeSwitch.milliseconds() >= SWITCH_DELAY) {
                if (isPrecision) {
                    // Do nothing
                } else if (isTurbo) {
                    isTurbo = false;
                    maxSpeed = robot.driveTrain.AVG_SPEED;
                    drivingMode = "Normal";
                } else {
                    isPrecision = true;
                    maxSpeed = robot.driveTrain.MIN_SPEED;
                    drivingMode = "Precision";
                }
                timeSinceLastModeSwitch.reset();
            }
            robot.driveTrain.travel(translationX, translationY, rotation);
            curSpeed = Math.sqrt(translationX*translationX+translationY*translationY+
                    rotation*rotation);

            // Arm Control
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
            if (toggleLatch && timeSinceLastLatch.milliseconds() >= LATCH_DELAY) {
                robot.arm.latch();
                timeSinceLastLatch.reset();
            }
            if(Math.abs(moveArm) > 0.06) {
                robot.arm.shoulder.setPower(moveArm);
            } else {
                robot.arm.shoulder.setPower(0);
            }

            // Move Servo with Camera
            if(leftservo){
                robot.vision.phoneServo.setPosition(robot.vision.phoneServo.getPosition()+0.001);
            }
            if (rightservo){
                robot.vision.phoneServo.setPosition(robot.vision.phoneServo.getPosition()-0.001);
            }

            telemetry.addData("Driving Mode:", drivingMode);
            telemetry.addData("Desired Speed:",curSpeed);
            telemetry.update();
        }

        // Do we need to do anything on shutdown?
    }
}
