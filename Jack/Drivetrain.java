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


public class Drivetrain
{
    public DcMotor DcMotor1 = null;
    public DcMotor DcMotor2 = null;
    public DcMotor DcMotor3 = null;
    public DcMotor DcMotor4 = null;

    double MIN_SPEED = 0.20;
    double MAX_SPEED = 0.80;

    HardwareMap hwMap =  null;

    public Drivetrain(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        DcMotor1 = hwMap.get(DcMotor.class, "motor1");
        DcMotor2 = hwMap.get(DcMotor.class, "motor2");
        DcMotor3 = hwMap.get(DcMotor.class, "motor3");
        DcMotor4 = hwMap.get(DcMotor.class, "motor4");

        DcMotor1.setDirection(DcMotor.Direction.FORWARD);
        DcMotor3.setDirection(DcMotor.Direction.FORWARD);
        DcMotor2.setDirection(DcMotor.Direction.REVERSE);
        DcMotor4.setDirection(DcMotor.Direction.REVERSE);

        DcMotor1.setPower(0);
        DcMotor2.setPower(0);
        DcMotor3.setPower(0);
        DcMotor4.setPower(0);

        DcMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        DcMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        DcMotor3.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        DcMotor4.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);




    }

    public void setPowerToDriveTrain(double doDrive, boolean doRotate, boolean doStrafe) {
        if (doRotate = true) {
            double FL = 0.0;
            double BL = 0.0;
            double FR = -0.0;
            double BR = -0.0;
            DcMotor1.setPower(FL);
            DcMotor2.setPower(BL);
            DcMotor3.setPower(-FR);
            DcMotor4.setPower(-BR);
        } else if (doStrafe = true) {
            double FL = 0.0;
            double BL = 0.0;
            double FR = 0.0;
            double BR = 0.0;
            DcMotor1.setPower(FL);
            DcMotor2.setPower(BL);
            DcMotor3.setPower(FR);
            DcMotor4.setPower(BR);
        } else {
            double FL = 0.0;
            double BL = -0.0;
            double FR = -0.0;
            double BR = 0.0;
            DcMotor1.setPower(FL);
            DcMotor2.setPower(-BL);
            DcMotor3.setPower(-FR);
            DcMotor4.setPower(BR);
        }

    }

    public boolean drivetraisBusy () {

        return (DcMotor1.isBusy() && DcMotor2.isBusy() && DcMotor3.isBusy() && DcMotor4.isBusy());

    }


    public void setDrivetrainMode(DcMotor.RunMode mode) {
            DcMotor1.setMode(mode);
            DcMotor2.setMode(mode);
            DcMotor3.setMode(mode);
            DcMotor4.setMode(mode);

    }
    void driveInInches(double in, double p) {

        }
    void driveinDegress(double deg, double p) {


        }


    }




