package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

// Ian's test code for the arm
public class armHardware {

    // Put your Hardware objects here

    // Hardware Map IS Needed
    HardwareMap hwMap           =  null;

    // Put class variables here
    public DcMotor claw;
    public DcMotor shoulder;
    public DcMotor elbow;

    // Don't touch the constructor
    public armHardware(){

    }

    // Define your connections between your physical and virtual hardware objects
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // EX:
        // motor = hwMap.get(DcMotor.class, "motor");
        elbow = hwMap.get(DcMotor.class, "elbow");
        shoulder = hwMap.get(DcMotor.class, "shoulder");
        claw = hwMap.get(DcMotor.class, "claw");

        elbow.setPower(0);
        shoulder.setPower(0);
        claw.setPower(0);

        elbow.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shoulder.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


    }
    public void setPowerToArm (double p,boolean Shoulder, boolean Elbow,boolean up, boolean down){
        // exstinding and retractiong, controling with one stick
        if(up){
            shoulder.setPower(p);
            elbow.setPower(-p);
        }
        else if (down){
            shoulder.setPower(p);
            elbow.setPower(-p);
        }
        else {
            shoulder.setPower(0);
            elbow.setPower(0);
        }

//        //controling it bu both of the sticks
//        if(Shoulder){
//            shoulder.setPower(p);
//        }
//        else if (Elbow){
//            elbow.setPower(p);
//        }
//        else {
//            shoulder.setPower(0);
//            elbow.setPower(0);
//        }
//

    }
    public void setPowerClaw(double p , boolean a){
        if (a){
            claw.setPower(p);
        }
        else {
            claw.setPower(0);
        }
    }

    // Put your functions down here

}