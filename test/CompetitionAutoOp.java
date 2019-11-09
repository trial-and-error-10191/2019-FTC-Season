package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="Auto", group="Test Code")
public class CompetitionAutoOp extends LinearOpMode {
    CompetitionHardware robot = new CompetitionHardware();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        waitForStart();
        robot.arm.buildSiteMover();
       //robot.createDriveThread(24, 0.4, telemetry);
       // robot.dt.start();

        while(!robot.vision.locationFlag) {
            robot.vision.scanTheRoom();
        }

        if (robot.vision.depot = true) {
           // robot.driveTrain.move(16, 0.4, telemetry);
            //robot.arm.buildSiteMover();
            //robot.driveTrain.move(-40, 0.4, telemetry);
            telemetry.addLine("yah");
            telemetry.update();
        }
        else if(robot.vision.depot = false) {
            telemetry.addLine("nah");
        }
    }
}
