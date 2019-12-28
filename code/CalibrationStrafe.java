package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name="CalibrateStrafe", group="chad")
public class CalibrationStrafe extends LinearOpMode {
    //
    DcMotor frontleft;
    DcMotor frontright;
    DcMotor backleft;
    DcMotor backright;
    //Calculate encoder conversion
    Integer cpr = 28; //counts per rotation
    Integer gearratio = 40 * 80/100;
    Double diameter = 4.125;
    Double cpi = (cpr * gearratio) / (Math.PI * diameter); //counts per inch -> counts per rotation / circumference
    Double meccyBias = 0.65;//adjust until your robot goes 20 inches
    //
    Double conversion = cpi * meccyBias;
    //
    public void runOpMode() {
        //
        frontleft = hardwareMap.dcMotor.get("motor0");
        frontright = hardwareMap.dcMotor.get("motor1");
        backleft = hardwareMap.dcMotor.get("motor2");
        backright = hardwareMap.dcMotor.get("motor3");
        frontright.setDirection(DcMotorSimple.Direction.REVERSE);//If your robot goes backward, switch this from right to left
        backright.setDirection(DcMotorSimple.Direction.REVERSE);//If your robot goes backward, switch this from right to left
        //
        waitForStartify();
        //
        strafeToPosition(20, .4);//Don't change this line, unless you want to calibrate with different speeds
        //
    }

    public void strafeToPosition(double inches, double speed){
        //
        int move = (int)(Math.round(inches * cpi * meccyBias));
        //
        backleft.setTargetPosition(backleft.getCurrentPosition() + move);
        frontleft.setTargetPosition(frontleft.getCurrentPosition() - move);
        backright.setTargetPosition(backright.getCurrentPosition() - move);
        frontright.setTargetPosition(frontright.getCurrentPosition() + move);
        //
        frontleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontright.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backleft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backright.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //
        frontleft.setPower(speed);
        backleft.setPower(speed);
        frontright.setPower(speed);
        backright.setPower(speed);
        //
        while (frontleft.isBusy() && frontright.isBusy() && backleft.isBusy() && backright.isBusy()){}
        frontright.setPower(0);
        frontleft.setPower(0);
        backright.setPower(0);
        backleft.setPower(0);
        return;
    }
    //
    /*
    This function's purpose is simply to drive forward or backward.
    To drive backward, simply make the inches input negative.
     */

    /*
    A tradition within the Thunder Pengwins code, we always start programs with waitForStartify,
    our way of adding personality to our programs.
     */
    public void waitForStartify() {
        waitForStart();
    }
}