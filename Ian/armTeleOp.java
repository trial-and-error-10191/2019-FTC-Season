package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="arm teleop", group="Iterative Opmode")
// to test the Armhard ware
public class armTeleOp extends LinearOpMode {

    // Put your robot here
    armHardware robot = new armHardware();

    // Put your class variables here
    public double power = 0.5;
    public double rewop = -0.5;
    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        waitForStart();

        double left;
        double right;

        while (opModeIsActive()){
            left = gamepad1.left_stick_y;
            right = gamepad1.right_stick_y;

            //one stick controlling it
            if(Math.abs(left) >= 0.06){
                robot.setPowerToArm( left/2, false,false,true,false);
            }
            else{
                robot.setPowerToArm( left/2 , false,false,false,true);
            }

//            // both the stick are controlling it
//            if(Math.abs(left) >= 0.06) {
//                robot.setPowerToArm(left/2, true, false,false ,false);
//            }
//            else {
//                robot.setPowerToArm(right/2, false, true,false, false);
//            }
//

            if (gamepad1.a)
                robot.setPowerClaw(power,gamepad1.a);
            else if (gamepad1.b) {
                robot.setPowerClaw(rewop, gamepad1.b);
            }

        }
    }

}