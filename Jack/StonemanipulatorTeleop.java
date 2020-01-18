package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

//gamepad2 as op gamepad1 as driver
public class StonemanipulatorTeleop extends LinearOpMode {
    StonemanipulatorHardware sm = new StonemanipulatorHardware();
    double movesticklevel = gamepad2.right_stick_y;


    public void runOpMode() {
        sm.init(hardwareMap);
        waitForStart();


        boolean uplevel;
        boolean downlevel;
        while(opModeIsActive()){
            uplevel = gamepad2.dpad_up;
            downlevel = gamepad2.dpad_down;



            if(uplevel == true) {


            }

        }

    }

}

