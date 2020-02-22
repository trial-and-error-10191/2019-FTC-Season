package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Ian Intake Code", group="Test Code")
public class IanIntakeSystemTeleop extends LinearOpMode {
    IanIntakeSystemHardware sm = new IanIntakeSystemHardware();

    // Preference variables
    double thresh = 0.06;
    double SWING_DELAY = 500;
    double DOOR_DELAY = 500;

    // state variables
    boolean debug = true;

    ElapsedTime timeSinceLastSwing = new ElapsedTime();
    ElapsedTime timeSinceLastTrip = new ElapsedTime();

    public void runOpMode() throws InterruptedException {
        sm.init(hardwareMap);

        waitForStart();

        //controls
        boolean liftDown;
        boolean liftUp;
        boolean swingArm;
        boolean toggleDoor;
        boolean doStow;
        boolean doSpin;
        double liftPower;

        timeSinceLastSwing.reset();
        timeSinceLastTrip.reset();
        while (opModeIsActive()){
            liftDown = gamepad2.dpad_down;
            liftUp = gamepad2.dpad_up;
            swingArm = gamepad2.y;
            toggleDoor = gamepad2.a;
            doStow = gamepad2.b;
            doSpin = gamepad2.x;
            liftPower = gamepad2.left_stick_y;

            // control the intake
            if (doSpin) {
                sm.setWheelIntakePower(sm.INTAKE_SPEED);
            }
            else {
                sm.setWheelIntakePower(0);
            }

            // to test and find the lift counts
            if(Math.abs(liftPower) > thresh){
                sm.liftManual(liftPower);
            }
            else {
                sm.liftManual(0);
            }

            // Put stone manipulator in default state
            if(doStow){
                sm.stow(sm.AVG_LIFT_SPEED);
            }

            //to open the door/close the door
            if(toggleDoor && timeSinceLastTrip.milliseconds() > DOOR_DELAY){
                sm.toggleDoor();
                timeSinceLastTrip.reset();
            }

            //to swing the arm/ un swing the arm
            if(swingArm && timeSinceLastSwing.milliseconds() > SWING_DELAY){
                sm.swingTheArm();
                timeSinceLastSwing.reset();
            }

            //to raiseAndLower the elevator
            if (liftUp) {
                sm.raiseAndLower(sm.AVG_LIFT_SPEED);
            }
            else if (liftDown) {
                sm.raiseAndLower(-sm.AVG_LIFT_SPEED);
            }

            telemetry.addData("lift counts", sm.lift.getCurrentPosition());
            telemetry.update();
        }
    }
}