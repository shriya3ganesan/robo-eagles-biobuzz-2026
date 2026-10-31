//1. Have it have 4 dc motors for each wheel (front and back) %
//2. Have it have 2 servo motors for transfer %
//3. Have it have 1 Outtake Motor (Dc), and 1 Inttake Motor (Dc)
//4. Have each control mapped and symmetric to a specific control on the gamepad.

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name= "Vien TeleOp")
public class VienTeleOp extends LinearOpMode{
    DcMotor backLeft;
    DcMotor backRight;
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor outtake;
    DcMotor intake;
    CRServo flowerL;
    CRServo flowerR;
    CRServo transfer;

    @Override
    public void runOpMode(){
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        //outake
        outtake = hardwareMap.get(DcMotor.class, "out");
        intake = hardwareMap.get(DcMotor.class, "in");
        //servo flower
        flowerL = hardwareMap.get(CRServo.class, "floL");
        flowerR = hardwareMap.get(CRServo.class, "floR");
        //transfer
        transfer = hardwareMap.get(CRServo.class, "trn");

        //set direction
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Mecanum TeleOp Ready");
        telemetry.update();

        // Default intake/outtake:On/off
         boolean intakeOn = false;
         boolean outakeOn = false;
        //

        waitForStart();

        while(opModeIsActive()){
            //Buttons>
            double drive = -gamepad1.left_stick_y;   // Forward / Backward
            double strafe = gamepad1.left_stick_x;  // Left / Right Strafing
            double turn = gamepad1.right_stick_x;   // Rotation / Turning (moved to right stick for better control)

            // Mecanum wheel power formulas
            double frontLeftPower  = drive + strafe + turn;
            double backLeftPower   = drive - strafe + turn;
            double frontRightPower = drive - strafe - turn;
            double backRightPower  = drive + strafe - turn;

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

            //GP 1=
            //INTAKE>>
            if (gamepad1.right_trigger > 0.5) {
                intakeOn = true;
            }
            if (gamepad1.left_trigger > 0.5) {
                intakeOn = false;
            }

            if (intakeOn) {
                intake.setPower(0.75);
            } else{
                intake.setPower(0.0);
            }

            //OUTAKE>>
            if (gamepad1.right_bumper) {
                outakeOn = true;
            }

            if (gamepad1.left_bumper) {
                outakeOn = false;
            }

            if (outakeOn) {
                outtake.setPower(0.75);
                transfer.setPower(0.3);
            } else{
                outtake.setPower(0.0);
                transfer.setPower(0.0);
            }

            //Flower Left>>
            if (gamepad1.x) {
                flowerL.setPower(0.5);
            }

            if (gamepad1.y) {
                flowerL.setPower(0);
            }

            //Flower Right>>
            if (gamepad1.a){
                flowerR.setPower(0.5);
            }
            if (gamepad1.b){
                flowerR.setPower(0);
            }

        }

    }
}
