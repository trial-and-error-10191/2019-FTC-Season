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
    RotateThread rt = null;
    ElbowThread et = null;
    StrafeThread st = null;

    public void init(HardwareMap ahwmap) {
        hwmap = ahwmap;
        driveTrain.init(hwmap);
        vision.init(hwmap);
        arm.init(hwmap);
    }

    public void createDriveThread(double inches, double speed, Telemetry telemetry) {
        dt = new DriveThread(inches,speed,telemetry);
    }

    public void createRotateThread(double degrees, double speed, Telemetry telemetry) {
        rt = new RotateThread(degrees,speed,telemetry);
    }

    public void createElbowThread(int counts, double speed, Telemetry telemetry) {
        et = new ElbowThread(counts,speed,telemetry);
    }
    public void createStrafeThread(double inches, double speed) {
        st = new StrafeThread(inches, speed);
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

    public class RotateThread extends Thread {
        double degrees;
        double speed;
        Telemetry telemetry;

        public RotateThread(double degrees, double speed, Telemetry telemetry) {
            this.degrees = degrees;
            this.speed = speed;
            this.telemetry = telemetry;
        }

        public void run() {
            driveTrain.rotate(this.degrees, this.speed, this.telemetry);
        }

    }
    public class StrafeThread extends Thread {
        double inches;
        double speed;

        public StrafeThread(double inches, double speed) {
            this.inches = inches;
            this.speed = speed;
        }
        public void run() {driveTrain.strafeToPosition(this.inches, this.speed);}
    }


    public class ElbowThread extends Thread {
        int counts;
        double speed;
        Telemetry telemetry;

        public ElbowThread(int counts, double speed, Telemetry telemetry) {
            this.counts = counts;
            this.speed = counts;
        }

        public void run() {
            arm.elbowMove(this.counts,this.speed,this.telemetry);
        }
    }
}
