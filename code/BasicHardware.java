package org.firstinspires.ftc.teamcode;

        import com.qualcomm.robotcore.hardware.DcMotor;
        import com.qualcomm.robotcore.hardware.HardwareMap;
        import com.qualcomm.robotcore.hardware.Servo;
        import com.qualcomm.robotcore.util.ElapsedTime;

public class BasicHardware
{
    // Put your Hardware objects here

    // Hardware Map IS Needed
    HardwareMap hwMap           =  null;

    // Put class variables here

    // Don't touch the constructor
    public BasicHardware(){

    }

    // Define your connections between your physical and virtual hardware objects
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // EX:
        // motor = hwMap.get(DcMotor.class, "motor");

    }

    // Put your functions down here

}