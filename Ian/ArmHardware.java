package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

// Ian's test code for the arm
public class ArmHardware {

    // Put your Hardware objects here

    // Hardware Map IS Needed
    HardwareMap hwMap           =  null;

    // Put class variables here
    public DcMotor claw;
    public DcMotor shoulder;
    public DcMotor elbow;

    // Sensors on the arm
    public DigitalChannel elbowTouching;
    public TouchSensor shoulderStowed;
    public TouchSensor shoulderOut;
    public TouchSensor clawZero;

    // Preference variables
    public double thresh = 0.06;
    public double elbowCountsPerDegree = 560.0/360.0;//1.56
    public double degreesUntilParallel = 5;
    public int elbowCountsToParallel = (int) Math.round(elbowCountsPerDegree * degreesUntilParallel);
    public static double AVG_ELBOW_SPEED = 0.4;
    public static double AVG_SHOULDER_SPEED = 0.4;
    public static double SLOW_SHOULDER_SPEED = 0.25;
    public static double SLOW_ELBOW_SPEED = 0.2;


    // Don't touch the constructor
    public ArmHardware(){

    }

    // Define your connections between your physical and virtual hardware objects
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        elbow = hwMap.get(DcMotor.class, "elbow");
        shoulder = hwMap.get(DcMotor.class, "shoulder");
        claw = hwMap.get(DcMotor.class, "claw");

        elbowTouching = hwMap.get(DigitalChannel.class,"ElbowMag");
        shoulderStowed = hwMap.get(TouchSensor.class,"shoulderStowedTouch");
        shoulderOut = hwMap.get(TouchSensor.class,"ShoulderTouch");
        clawZero = hwMap.get(TouchSensor.class,"clawTouch");

        elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        claw.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        elbow.setPower(0);
        shoulder.setPower(0);
        claw.setPower(0);

        elbow.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shoulder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    // Moves the arm
    public void move(double power) {
        // moving the shoulder
        if (Math.abs(power) > thresh) {
            shoulder.setPower(power);
            elbow.setPower(power);
        }  else {
            shoulder.setPower(0);
            elbow.setPower(0);
        }
    }

    // This function puts the arm back into a parallel to the ground initial state
    public void lineUp() {
        if(!elbowTouching.getState() && shoulderStowed.isPressed()) {
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
        else if ((elbowTouching.getState() && !shoulderStowed.isPressed()) || (shoulderStowed.isPressed() && elbowTouching.getState())) {
            // If in arbitrary position
            stow();
        }
    }

    // This function stows the arm
    public void stow() {
        if (elbowTouching.getState() && !shoulderStowed.isPressed()) {
            // If in arbitrary position
            shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            shoulder.setPower(SLOW_SHOULDER_SPEED);
            while(!shoulderStowed.isPressed()) {
                // Busy waiting
            }
            shoulder.setPower(0);
            shoulder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            shoulder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            stow();
        }
        else if (shoulderStowed.isPressed() && elbowTouching.getState()) {
            // If shoulder is in stowed, but elbow is not
            elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            elbow.setPower(-SLOW_ELBOW_SPEED);
            while(elbowTouching.getState()) {
                // Busy waiting
            }
            elbow.setPower(0);
            elbow.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            elbow.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}