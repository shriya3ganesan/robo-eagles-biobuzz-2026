//1. Have it have 4 dc motors for each wheel (front and back) %
//2. Have it have 2 servo motors for transfer %
//3. Have it have 1 Outtake Motor (Dc), and 1 Inttake Motor (Dc)
//4. Have each control mapped and symmetric to a specific control on the gamepad.

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name= "Vien Test Code")
public class VienTestCode extends LinearOpMode{
    DcMotor backLeft;
    DcMotor backRight;
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor outake;
    DcMotor intake;
    Servo transfer1;
    Servo transfer2;

    @Override
    public void runOpMode(){
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        //outake
        outake = hardwareMap.get(DcMotor.class, "out");
        intake = hardwareMap.get(DcMotor.class, "in");
        //servo transfer
        transfer1 = hardwareMap.get(Servo.class, "t1");
        transfer2 = hardwareMap.get(Servo.class, "t2");

        //set direction
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Default intake/outtake:On/off
         boolean intakeOn = false;
         boolean outakeOn = false;
        //

        waitForStart();

        while(opModeIsActive()){
            //Buttons>

            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.left_stick_x;

            double backLeftPower = drive + turn;
            double backRightPower = drive - turn;
            double frontLeftPower = drive + turn;
            double frontRightPower = drive - turn;
            //

            //
            double max = Math.max(1.0, Math.max(
                    Math.max(
                            Math.abs(backLeftPower),
                    Math.abs(frontLeftPower)),
                    Math.max(
                            Math.abs(backRightPower),
                    Math.abs(frontRightPower))));

            backLeftPower /= max;
            backRightPower /= max;
            frontLeftPower /= max;
            frontRightPower /= max;
            //

            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);
            frontRight.setPower(frontRightPower);
            frontLeft.setPower(frontLeftPower);

            //INTAKE
            if (gamepad1.right_trigger > 1.0) {
                intakeOn = true;
            }

            if (intakeOn){
                intake.setPower(0.75);
            } else{
                intake.setPower(0.0);
            }

            //OUTAKE
            if (gamepad1.a) {
                outakeOn = true;
            }

            if (gamepad1.b) {
                outakeOn = false;
            }

            if (outakeOn) {
                outake.setPower(0.75);
            } else{
                outake.setPower(0.0);
            }

            //BOB THE SERVO
            if (gamepad1.x){
                transfer1.setPosition(1.0);
            } else {
                transfer1.setPosition(0.0);
            }

            if (gamepad1.y){
                transfer2.setPosition(1.0);
            } else {
                transfer2.setPosition(0.0);
            }

        }

    }
}
