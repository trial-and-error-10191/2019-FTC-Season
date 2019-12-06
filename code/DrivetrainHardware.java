package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class DrivetrainHardware {

    // Hardware Variables
    DcMotor[] motors = new DcMotor[4];

    //Gyro Variables
    public BNO055IMU imu;
    public Orientation angles = new Orientation();
    public Acceleration gravity = new Acceleration();
    public int correctHeading = 1; // 1: First Angle; 2: Second Angle; 3: Third Angle

    // Preference Variables
    static final double     TURN_SPEED              = 0.5;     // Nominal half speed for better accuracy.
    static final double     HEADING_THRESHOLD       = 1 ;      // As tight as we can make it with an integer gyro
    static final double     P_TURN_COEFF            = 0.1;     // Larger is more responsive, but also less stable
    static final double MAX_SPEED = 0.85;
    static final double AVG_SPEED = 0.6;
    static final double MIN_SPEED = 0.45;

    // Drivetrain coolios variables
    double thresh  = 0.06;
    double encCountsPerRev = 28 * 19.2 * 80 / 100; // electrical * internal * externaly
    double wheelDiameter = 4.125;
    double wheelCircumference = Math.PI * wheelDiameter;
    double countsPerInch = encCountsPerRev / wheelCircumference;

    // Center of the wheels
    double w_x = 7.5; // inches
    double w_y = 6; // inches

    // Calibration Stuff
    double bias = 1.05;
    double conversion = countsPerInch * bias;
    double meccyBias = 1;
    double meccyConversion = countsPerInch * meccyBias;

    public ElapsedTime runtime = new ElapsedTime();

    // Hardware Map Variables
    HardwareMap hwmap = null;


    public DrivetrainHardware(){
    }//Constructor

    public void init(HardwareMap ahwmap){
        hwmap = ahwmap;

        for (int i = 0; i < motors.length; i++){
            motors[i] = hwmap.get(DcMotor.class, "motor" + i);
            if (i % 2 == 0) { // even
                motors[i].setDirection(DcMotor.Direction.REVERSE);
            } else {
                motors[i].setDirection(DcMotor.Direction.FORWARD);
            }
            motors[i].setPower(0);
            motors[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        imuInit();
    }

    public void imuInit() {
        BNO055IMU.Parameters imuParameters = new BNO055IMU.Parameters();

        this.imu = hwmap.get(BNO055IMU.class, "imu");
        imuParameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        imuParameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        imuParameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        imuParameters.loggingEnabled      = true;
        imuParameters.loggingTag          = "IMU";
        imuParameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        this.imu.initialize(imuParameters);
    }

    public void travel(double translationX, double translationY, double rotation) {
        // Calculate velocities
        double v0 = translationY + translationX + rotation;
        double v1 = translationY - translationX - rotation;
        double v2 = translationY - translationX + rotation;
        double v3 = translationY + translationX - rotation;

        // See what the max is
        double maximum = Math.max(Math.max(Math.max(Math.abs(v0),Math.abs(v1)),Math.abs(v2)),Math.abs(v3));

        // If maximum is greater than available power, scale everything by maximum
        if(maximum > 1) {
            v0 = v0/maximum;
            v1 = v1/maximum;
            v2 = v2/maximum;
            v3 = v3/maximum;
        }

        // Set power to their velocities
        motors[0].setPower(v0);
        motors[1].setPower(v1);
        motors[2].setPower(v2);
        motors[3].setPower(v3);
    }

    public float getHeading() {
        angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        if (correctHeading == 1) {
            return angles.firstAngle;
        } else if (correctHeading == 2) {
            return angles.secondAngle;
        } else { // Using third angle by default
            return angles.thirdAngle;
        }
    }

    public void setDrivetrainSpeed(double p) {
        for(int i = 0; i < motors.length; i++) {
            motors[i].setPower(p);
        }
    }

    public boolean isDrivetrainBusy() {
        boolean result = true;
        for(int i = 0; i < motors.length; i++) {
            result = result&motors[i].isBusy();
        }
        return result;
    }

    /*
    This function's purpose is simply to drive forward or backward.
    To drive backward, simply make the inches input negative.
     */
    public void move(double inches, double speed, Telemetry telemetry) {
        //
        if (inches < 5) {
            int move = (int) (Math.round(inches * conversion));

            for(int i = 0; i < motors.length; i++) {
                motors[i].setTargetPosition(motors[i].getCurrentPosition()+move);
                motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }

            setDrivetrainSpeed(speed);
            while (isDrivetrainBusy()) {
                telemetry.addData("motor0", motors[0].getCurrentPosition());
                telemetry.addData("motor1", motors[1].getCurrentPosition());
                telemetry.addData("motor2", motors[2].getCurrentPosition());
                telemetry.addData("motor3", motors[3].getCurrentPosition());
                telemetry.update();
            }

            setDrivetrainSpeed(0);
        } else {
            int move1 = (int) (Math.round((inches - 5) * conversion));
            int movefl2 = motors[0].getCurrentPosition() + (int) (Math.round(inches * conversion));
            int movefr2 = motors[1].getCurrentPosition() + (int) (Math.round(inches * conversion));
            int movebl2 = motors[2].getCurrentPosition() + (int) (Math.round(inches * conversion));
            int movebr2 = motors[3].getCurrentPosition() + (int) (Math.round(inches * conversion));

            for(int i = 0; i < motors.length; i++) {
                motors[i].setTargetPosition(motors[i].getCurrentPosition()+move1);
                motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
            }

            setDrivetrainSpeed(speed);
            while (isDrivetrainBusy()) {
            }

            motors[0].setTargetPosition(movefl2);
            motors[1].setTargetPosition(movefr2);
            motors[2].setTargetPosition(movebl2);
            motors[3].setTargetPosition(movebr2);

            setDrivetrainSpeed(0.1);
            //
            while (isDrivetrainBusy()) {
                telemetry.addData("motor0", motors[0].getCurrentPosition());
                telemetry.addData("motor1", motors[1].getCurrentPosition());
                telemetry.addData("motor2", motors[2].getCurrentPosition());
                telemetry.addData("motor3", motors[3].getCurrentPosition());
                telemetry.update();
            }
            setDrivetrainSpeed(0);
        }
        return;
    }

    public void strafeToPosition(double inches, double speed){
        //
        int move = (int)(Math.round(inches * meccyConversion));
        //
        motors[2].setTargetPosition(motors[2].getCurrentPosition() - move);
        motors[0].setTargetPosition(motors[0].getCurrentPosition() + move);
        motors[3].setTargetPosition(motors[3].getCurrentPosition() + move);
        motors[1].setTargetPosition(motors[1].getCurrentPosition() - move);
        //
        motors[0].setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motors[1].setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motors[2].setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motors[3].setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //
        motors[0].setPower(speed);
        motors[2].setPower(speed);
        motors[1].setPower(speed);
        motors[3].setPower(speed);
        //
        while (motors[0].isBusy() && motors[1].isBusy() && motors[2].isBusy() && motors[3].isBusy()){}
        motors[1].setPower(0);
        motors[0].setPower(0);
        motors[3].setPower(0);
        motors[2].setPower(0);
        return;
    }

    /**
     *  Method to spin on central axis to point in a new direction.
     *  Move will stop if either of these conditions occur:
     *  1) Move gets to the heading (angle)
     *  2) Driver stops the opmode running.
     *
     * @param speed Desired speed of turn.
     * @param angle      Absolute Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     */
    public void rotate(double angle, double speed, Telemetry telemetry) {

        // keep looping while we are still active, and not on heading.
        while (!onHeading(speed, angle, P_TURN_COEFF, telemetry)) {
            // Update telemetry & Allow time for other processes to run.
            telemetry.update();
        }
}

    /**
     * Perform one cycle of closed loop heading control.
     *
     * @param speed     Desired speed of turn.
     * @param angle     Absolute Angle (in Degrees) relative to last gyro reset.
     *                  0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                  If a relative angle is required, add/subtract from current heading.
     * @param PCoeff    Proportional Gain coefficient
     * @return
     */
    boolean onHeading(double speed, double angle, double PCoeff, Telemetry telemetry) {
        double   error ;
        double   steer ;
        boolean  onTarget = false ;
        double leftSpeed;
        double rightSpeed;

        // determine turn power based on +/- error
        error = getError(angle);

        if (Math.abs(error) <= HEADING_THRESHOLD) {
            steer = 0.0;
            leftSpeed  = 0.0;
            rightSpeed = 0.0;
            onTarget = true;
        }
        else {
            steer = getSteer(error, PCoeff);
            rightSpeed  = speed * steer;
            leftSpeed   = -rightSpeed;
        }

        // Send desired speeds to motors.
        for(int i = 0; i < motors.length; i++) {
            if(i%2==0) {
                motors[i].setPower(leftSpeed);
            } else {
                motors[i].setPower(rightSpeed);
            }
        }

        // Display it for the driver.
        telemetry.addData("Target", "%5.2f", angle);
        telemetry.addData("Err/St", "%5.2f/%5.2f", error, steer);
        telemetry.addData("Speed.", "%5.2f:%5.2f", leftSpeed, rightSpeed);

        return onTarget;
    }

    /**
     * getError determines the error between the target angle and the robot's current heading
     * @param   targetAngle  Desired angle (relative to global reference established at last Gyro Reset).
     * @return  error angle: Degrees in the range +/- 180. Centered on the robot's frame of reference
     *          +ve error means the robot should turn LEFT (CCW) to reduce error.
     */
    public double getError(double targetAngle) {

        double robotError;

        // calculate error in -179 to +180 range  (
        //robotError = targetAngle - gyro.getIntegratedZValue();
        robotError = targetAngle - getHeading();
        while (robotError > 180)  robotError -= 360;
        while (robotError <= -180) robotError += 360;
        return robotError;
    }

    /**
     * returns desired steering force.  +/- 1 range.  +ve = steer left
     * @param error   Error angle in robot relative degrees
     * @param PCoeff  Proportional Gain Coefficient
     * @return
     */
    public double getSteer(double error, double PCoeff) {
        return Range.clip(error * PCoeff, -1, 1);
    }




        }

