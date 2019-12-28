package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ArmHardware {
    // Physical hardware on the arm
    DcMotor elbow = null;
    DcMotor shoulder = null;
    DcMotor claw = null;
    Servo latch = null;

    boolean buildSiteDown = true;
    // Sensors on the arm
    TouchSensor shoulderStowed = null;
    TouchSensor shoulderOut = null;
    TouchSensor clawZero = null;
    RevTouchSensor elbowTouching;

    // Important math measurements and calculations
    public double elbowCountsPerDegree = 560.0/360.0;//1.56
    public double degreesUntilParallel = 5;
    public int elbowCountsToParallel = (int) Math.round(elbowCountsPerDegree * degreesUntilParallel);

    // Speed Constants
    public static double ELBOW_SPEED_BIAS = 0.5;
    public static double AVG_ELBOW_SPEED = 0.2;
    public static double AVG_SHOULDER_SPEED = 0.75;
    public static double SLOW_SHOULDER_SPEED = 0.1;
    public static double SLOW_ELBOW_SPEED = 0.05;
    public static double AVG_CLAW_SPEED = 0.4;

    // Positions to keep in mind; WARNING: Signs are not correct. This is an absolute value.
    public static int CLOSED_CLAW = 100;
    public static int CLOSE_TO_STOW = 189;
    public static int CLOSE_TO_TOUCH = 274;

    // Hardware Map stuff yay
    HardwareMap hwmap = null;

    // Don't touch the constructor
    public ArmHardware(){

    }

    public void init(HardwareMap ahwmap) {
        hwmap = ahwmap;

        elbow = hwmap.get(DcMotor.class,"elbow");
        shoulder = hwmap.get(DcMotor.class,"shoulder");
        claw = hwmap.get(DcMotor.class,"claw");
        latch = hwmap.get(Servo.class, "SiteMover");

        shoulderStowed = hwmap.get(TouchSensor.class,"shoulderStowedTouch");
        clawZero = hwmap.get(TouchSensor.class,"clawTouch");
        shoulderOut = hwmap.get(TouchSensor.class," ShoulderTouch");
        elbowTouching = hwmap.get(RevTouchSensor.class,"ElbowMag");

        // Set modes of the motors
        elbow.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        shoulder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        claw.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        claw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        shoulder.setDirection(DcMotor.Direction.REVERSE);
        elbow.setDirection(DcMotor.Direction.FORWARD);
        claw.setDirection(DcMotor.Direction.FORWARD);

        elbow.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shoulder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        elbow.setPower(0);
        shoulder.setPower(0);
        claw.setPower(0);

        latch.setPosition(0);

    }

    // Moves the arm
    public void move(double power) {
        // moving the shoulder
        if (power > 0 && shoulderStowed.isPressed()) {
            shoulder.setPower(0);
            shoulder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            elbow.setPower(0);
        }
        else if (power < 0 && shoulderOut.isPressed()) {
            shoulder.setPower(0);
            elbow.setPower(0);
        }
        else {
            elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            shoulder.setPower(power); // Ideally, this moves half as fast as the elbow
            elbow.setPower(-ELBOW_SPEED_BIAS*power); // Reality is that this thing is way too fast
        }
    }


    // Moves the claw
    // positive is closing the claw
    public void grip(double power) {
        if (power < 0 && clawZero.isPressed()) {
            claw.setPower(0);
            claw.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            claw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        else if (power > 0 && claw.getCurrentPosition() >= CLOSED_CLAW) {
            claw.setPower(0);
            claw.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            claw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        else {
            claw.setPower(power);
        }
    }

    // This function puts the arm back into a parallel to the ground initial state
    // .-.-c
    public void lineUp(Telemetry telemetry) {
        if(elbowTouching.isPressed() && shoulderStowed.isPressed()) {
            // If stowed
            elbow.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            elbow.setTargetPosition(elbowCountsToParallel);
            elbow.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            elbow.setPower(AVG_ELBOW_SPEED);
            while(elbow.isBusy()) {
                // Busy waiting
            }
            elbow.setPower(0);
            elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        else if ((!elbowTouching.isPressed() && !shoulderStowed.isPressed()) || (shoulderStowed.isPressed() && !elbowTouching.isPressed())) {
            // If in arbitrary position
            stow(telemetry);
        }
    }

    // This function stows the arm
    // .-.-c
    public void stow(Telemetry telemetry) {
        if (!elbowTouching.isPressed() && !shoulderStowed.isPressed()) {
            // If in arbitrary position
            shoulder.setTargetPosition(0-CLOSE_TO_STOW); // Stow count is 0
            shoulder.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            shoulder.setPower(AVG_SHOULDER_SPEED);
            while(shoulder.isBusy()) {
                // Busy waiting
            }

            shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            shoulder.setPower(SLOW_SHOULDER_SPEED);
            while(!shoulderStowed.isPressed()) {
                // Busy waiting
            }
            shoulder.setPower(0);
            shoulder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            stow(telemetry);
        }
        else if (shoulderStowed.isPressed() && !elbowTouching.isPressed()) {
            // If shoulder is in stowed, but elbow is not
            elbow.setTargetPosition(0-CLOSE_TO_TOUCH);
            elbow.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            elbow.setPower(SLOW_ELBOW_SPEED);
            while(elbow.isBusy()) {
                // Busy waiting
                telemetry.addData("Elbow:",elbow.getCurrentPosition());
                telemetry.update();
            }

            elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            elbow.setPower(-SLOW_ELBOW_SPEED);
            while(!elbowTouching.isPressed()) {
                // Busy waiting
            }
            elbow.setPower(0);
            elbow.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void latch() {
        if (!buildSiteDown) {
            latch.setPosition(0);
            buildSiteDown = true;
        }
        else {
            latch.setPosition(1);
            buildSiteDown = false;
        }
    }

    public void elbowMove(int counts, double power, Telemetry telemetry) {
        elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        elbow.setTargetPosition(counts);
        elbow.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        elbow.setPower(power);
        while (elbow.isBusy()) {
            // Busy waiting
            telemetry.addData("Elbow:",elbow.getCurrentPosition());
            telemetry.update();
        }
        elbow.setPower(0);
    }

    public void shoulderMove(int counts, double power, Telemetry telemetry) {
        shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shoulder.setTargetPosition(counts);
        shoulder.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        shoulder.setPower(power);
        while (shoulder.isBusy()) {
            // Busy waiting
            telemetry.addData("Shoulder:",shoulder.getCurrentPosition());
            telemetry.update();
        }
        shoulder.setPower(0);
        shoulder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

}
