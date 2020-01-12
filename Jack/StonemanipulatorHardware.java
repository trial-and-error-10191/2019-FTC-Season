package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
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
    }


    public void setIntakePower(double p) {
        for(int i = 0; i < intake.length; i++) {
            intake[i].setPower(p);
        }
    }

    public boolean isLoaded(){

        return isLoaded();
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

