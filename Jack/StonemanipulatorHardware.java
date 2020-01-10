package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoControllerEx;
import com.qualcomm.robotcore.hardware.TouchSensor;


public class StonemanipulatorHardware {
    DcMotor lift1;
    DcMotor intake1;
    DcMotor intake2;
    DcMotor extension;
    Servo trapDoor;
    TouchSensor loadingBaySensor;
    ColorSensor stoneDetector;
    int EXTENSION_COUNTS;


    public void setIntakePower(double p) {
        intake1.setPower(p);
        intake2.setPower(p);


    }

    public boolean isloaded(boolean loaded){


        return loaded;
    }



    void setLiftPower(double p) {
        lift1.setPower(p);

    }


    void moveLiftToLevel(double p,int level) {



    }

    void setOuttakePower(double position) {
        trapDoor.setPosition(position);


    }

    void extend(double p) {


    }


    void toggleTrapDoor() {




    }



}

