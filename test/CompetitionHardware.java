package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class CompetitionHardware {
    // Hardware map imports from the phone
    HardwareMap hwmap = null;

    // Physical Hardware Objects
    DrivetrainHardware driveTrain = new DrivetrainHardware();
    VuforiaHardware vision = new VuforiaHardware();
    ArmHardware arm = new ArmHardware();

    // Threads
    DriveThread dt = null;

    public void init(HardwareMap ahwmap) {
        hwmap = ahwmap;
        driveTrain.init(hwmap);
        vision.init(hwmap);
        arm.init(hwmap);
    }

    public void createDriveThread(double inches, double speed, Telemetry telemetry) {
        dt = new DriveThread(inches,speed,telemetry);
    }

    public class DriveThread extends Thread {
        double inches;
        double speed;
        Telemetry telemetry;

        public DriveThread(double inches, double speed, Telemetry telemetry) {
            this.inches = inches;
            this.speed = speed;
            this.telemetry = telemetry;
        }

        public void run() {
            driveTrain.move(this.inches, this.speed, this.telemetry);
        }

    }
}
