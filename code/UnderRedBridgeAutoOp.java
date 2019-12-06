package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Red Under Bridge", group="Test Code")
public class UnderRedBridgeAutoOp extends LinearOpMode {
    CompetitionHardware robot = new CompetitionHardware();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        waitForStart();

//        robot.createRotateThread(robot.driveTrain.getHeading()+90,0.4, telemetry);
//        robot.rt.start();
//        while(robot.rt.isAlive()) {
//            // Busy waiting
//        }
        sleep(20000);
        robot.createDriveThread(32,0.4,telemetry);
        robot.dt.start();
        while(robot.dt.isAlive()) {
            // Busy waiting
        }
    }
}