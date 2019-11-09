package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="Competition Code", group="Test Code")
public class CompetitionTeleOp extends LinearOpMode {
    CompetitionHardware robot = new CompetitionHardware();

    // State flags
    boolean isTurbo = false;
    boolean isPrecision = false;

    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();

        double maxSpeed = robot.driveTrain.AVG_SPEED;
        while(opModeIsActive()) {
            double translationX = gamepad1.left_stick_x*maxSpeed;
            double translationY = -gamepad1.left_stick_y*maxSpeed;
            double rotation = gamepad1.right_stick_x*maxSpeed;
            boolean turbo = gamepad1.dpad_up;
            boolean precision = gamepad1.dpad_down;

            double armPower = gamepad2.left_stick_y;
            boolean openClaw = gamepad2.a;
            boolean closeClaw = gamepad2.b;
            boolean stow = gamepad2.dpad_down;

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
            robot.driveTrain.travel(translationX,translationY,rotation);

            // Arm Control
            robot.arm.move(armPower*robot.arm.AVG_SHOULDER_SPEED);
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

            telemetry.addData("Elbow:",robot.arm.elbow.getCurrentPosition());
            telemetry.addData("Shoulder:",robot.arm.shoulder.getCurrentPosition());
            telemetry.update();
        }

        // Do we need to do anything on shutdown?
    }
}
