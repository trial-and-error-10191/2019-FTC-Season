package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;


public class StonemanipulatorHardware {
    DcMotor lift;
    DcMotor[] intake = new DcMotor[2];
    DcMotor extension;
    DcMotor outtake;
    Servo trapDoor;
    TouchSensor floorSensor;
    ColorSensor stoneDetector;
    int EXTENSION_COUNTS;
    int level;

    HardwareMap hwmap = null;

    public void init(HardwareMap ahwmap) {
        hwmap = ahwmap;

        lift = hwmap.get(DcMotor.class,"lift");
        extension = hwmap.get(DcMotor.class,"extension");
        outtake = hwmap.get(DcMotor.class,"outtake");

        floorSensor = hwmap.get(TouchSensor.class,"floorSensor");
        trapDoor = hwmap.get(Servo.class,"trapdoor");
        stoneDetector = hwmap.get(ColorSensor.class,"stoneDetector");

        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lift.setDirection(DcMotor.Direction.FORWARD);
        extension.setDirection(DcMotor.Direction.FORWARD);
        outtake.setDirection(DcMotorSimple.Direction.FORWARD);

        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lift.setPower(0);
        extension.setPower(0);
        outtake.setPower(0);

        for(int i = 0; i < intake.length; i++) {
            intake[i] = hwmap.get(DcMotor.class,"intake"+i);
            intake[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            intake[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            intake[i].setPower(0);
            if (i % 2 == 0) {
                intake[i].setDirection(DcMotorSimple.Direction.FORWARD);
            }
            else {
                intake[i].setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }

    }




    public void setIntakePower(double p) {
        for(int i = 0; i < intake.length; i++) {
            intake[i].setPower(p);
        }
    }

    public boolean isLoaded(){

        return false;
    }



    public void setLiftPower(double p) {
        lift.setPower(p);

    }


    public void moveLiftToLevel(double p,int level) {

    }

    public void setOuttakePower(double p) {
        outtake.setPower(p);

    }

    public void extend(double p) {
        extension.setPower(p);
    }


    public void toggleTrapDoor(){


    }



}

