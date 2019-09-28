/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Pushbot.
 * See PushbotTeleopTank_Iterative and others classes starting with "Pushbot" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:        "left_drive"
 * Motor channel:  Right drive motor:        "right_drive"
 * Motor channel:  Manipulator drive motor:  "left_arm"
 * Servo channel:  Servo to open left claw:  "left_hand"
 * Servo channel:  Servo to open right claw: "right_hand"
 */
public class PrestonBot
{
    // Actual Hardware
    public DcMotor[] motors = new DcMotor[4];

    // Measurements and Conversion Factors
    double wheelDiameter = 4; // inches
    double wheelCirc = Math.PI*wheelDiameter;
    double countsPerRev = 7; // Output shaft diameter gotten from the website
    double driveGearReductions = (19.2/1) + (80.0/100); // internal gears + external gears
    double countsPerInch = (countsPerRev*driveGearReductions)/(wheelCirc);

    /* local OpMode members. */
    HardwareMap hwMap           =  null;

    /* Constructor */
    public PrestonBot(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        for(int i = 0; i < motors.length; i++) {
            motors[i] = hwMap.get(DcMotor.class,"motor"+i);
            if(i%2 == 0) {
                motors[i].setDirection(DcMotor.Direction.FORWARD);
            } else {
                motors[i].setDirection(DcMotor.Direction.REVERSE);
            }
            motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    public void driveInInches(double power, double inches, Telemetry telemetry) {
        // Reset encoders because I don't like seeing big numbers\
        for(int i = 0; i < motors.length; i++) {
            motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        // Convert to counts
        int counts = (int) Math.round(inches*countsPerInch);

        // Set target position by adding counts to current position (should be 0)
        int direction = power > 0 ? 1 : -1;
        setTargetPosition(direction*counts);

        // Set Mode of Drivetrain to RUN_TO_POSITION
        setDrivetrainRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set Drivetrain Power
        setPowerToDrivetrain(Math.abs(power),false);

        while(drivetrainIsBusy()) {
            telemetry.addData("Destination",convertCountsToInches(counts));
            telemetry.addData("Inches Traveled",convertCountsToInches(motors[0].getCurrentPosition()));
            telemetry.update();
        }

        // Stop Drivetrain
        setPowerToDrivetrain(0,false);

        // Turn back to RUN_USING_ENCODERS
        setDrivetrainRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setPowerToDrivetrain(double power, boolean isRotating) {
        if (isRotating) {
            for(int i = 0; i < motors.length; i++) {
                if(i%2 == 0) {
                    motors[i].setPower(power);
                } else {
                    motors[i].setPower(-power);
                }
            }
        } else {
            for(int i = 0; i < motors.length; i++) {
                motors[i].setPower(power);
            }
        }
    }

    public void setDrivetrainRunMode(DcMotor.RunMode mode) {
        for(int i=0; i < motors.length; i++) {
            motors[i].setMode(mode);
        }
    }

    public void setTargetPosition(int additionalCounts) {
        for(int i = 0; i < motors.length; i++) {
            motors[i].setTargetPosition(motors[i].getCurrentPosition()+additionalCounts);
        }
    }

    public boolean drivetrainIsBusy() {
        boolean result = true;
        for(int i = 0; i < motors.length; i++) {
            result &= motors[i].isBusy();
        }
        return result;
    }

    public double convertCountsToInches(int counts) {
        return counts/countsPerInch;
    }

 }

