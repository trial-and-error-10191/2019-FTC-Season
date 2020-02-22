package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class IanIntakeSystemHardware {
    // Hardware Objects
    DcMotor[] intake = new DcMotor[2];
    DcMotor lift;
    Servo swing;
    Servo door;

    // Sensors
    TouchSensor floorZero = null;

    // Map connecting hardware to objects
    HardwareMap hwmap = null;

    // State Variables
    int level = 0;
    boolean armSwung = false;
    boolean doorOpen = false;
    boolean auto = true;

    // Preference Variables
    double INTAKE_SPEED = 0.5;
    double MIN_LIFT_SPEED = 0.5;
    double AVG_LIFT_SPEED = 0.75;
    double DOOR_OPEN = 1;
    double DOOR_CLOSED = 0;
    double ARM_OUT = 1;
    double ARM_IN = 0;

    // Measured Variables
    int deltaLevel = 100; // Counts
    int rotateLevel = 50;

    public void init(HardwareMap ahwmap){

        hwmap = ahwmap;

        lift = hwmap.get(DcMotor.class, "lift");
        swing = hwmap.get(Servo.class,"swing");
        door = hwmap.get(Servo.class, "door");
        floorZero = hwmap.get(TouchSensor.class,"floorZero");

        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setDirection(DcMotor.Direction.FORWARD);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setPower(0);

        for(int i = 0; i < intake.length; i++){
            intake[i] = hwmap.get(DcMotor.class,"intake"+i);
            intake[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            intake[i].setPower(0);
            if (i % 2 == 0) {
                intake[i].setDirection(DcMotorSimple.Direction.FORWARD);
            }
            else {
                intake[i].setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }
    }


    public void liftManual(double power){
        if(floorZero.isPressed() && power < 0){
            lift.setPower(0);
            lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            return;
        }
        else if(Math.abs(power) > 0.06){
            lift.setPower(power);
        }
        else {
            lift.setPower(0);
        }

        auto = false;

    }

    public void setLiftPosition(double power, int pos) {
        lift.setTargetPosition(pos);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(power);
        while(lift.isBusy()){}
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setPower(0);
    }

    public void stow(double power) {
        double p = Math.abs(power);
        int condition = binaryToDigit(doorOpen,armSwung,lift.getCurrentPosition() > rotateLevel);
        switch(condition) {
            case(1):
                setLiftPosition(-p,rotateLevel);
                stow(power);
            case(2):
                setLiftPosition(p,rotateLevel);
                swingTheArm();
                stow(power);
            case(3):
                swingTheArm();
                setLiftPosition(-p,rotateLevel);
                stow(power);
            case(4):
                toggleDoor();
                stow(power);
            case(5):
                toggleDoor();
                setLiftPosition(-p,rotateLevel);
                stow(power);
            case(6):
                toggleDoor();
                setLiftPosition(p,rotateLevel);
                swingTheArm();
                stow(power);
            case(7):
                toggleDoor();
                swingTheArm();
                setLiftPosition(-p,rotateLevel);
                stow(power);
            default:
                // Move towards sensor
                lift.setPower(-MIN_LIFT_SPEED);
                while(!floorZero.isPressed()) {}
                lift.setPower(0);
                lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                auto = true;
        }
    }

    public void raiseAndLower(double power) {
        int dir;
        // to get the direction we are going to
        if(power < 0){
             dir = -1;
        }
        else {
            dir = 1;
        }

        // to check if the zero button is pressed
        if(floorZero.isPressed() && power < 0){
            lift.setPower(0);
            lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            return;
        }

        // to switch to auto
        if (!auto){
            stow(power);
        }
        else {
            // to tell what level we are on
            if(dir == 1){
                level++;
            }
            else {
                level--;
            }
            // to run the motors to our target position
            setLiftPosition(power,lift.getCurrentPosition() + deltaLevel*dir);
            auto = true;
        }
    }

    // setting up the power to the wheel intake
    public void setWheelIntakePower(double power){
         for (int i = 0; i < intake.length; i++) {
             intake[i].setPower(power);
         }
    }

    // if you want to swing the arm out
    public void swingTheArm(){
        if(armSwung){
            swing.setPosition(ARM_IN);
            armSwung = false;
        }
        else{
            swing.setPosition(ARM_OUT);
            armSwung = true;
        }
    }

    // to open the claw
    public void toggleDoor(){
        if(doorOpen){
            door.setPosition(DOOR_CLOSED);
            doorOpen = false;
        }
        else{
            door.setPosition(DOOR_OPEN);
            doorOpen = true;
        }
   }

    // Compare two arrays
    public int binaryToDigit(boolean a, boolean b, boolean c) {
        int aa = a ? 1 : 0;
        int bb = b ? 1 : 0;
        int cc = c ? 1 : 0;
        return aa*(int)Math.pow(2,2) + bb*2 + cc;
    }
}
