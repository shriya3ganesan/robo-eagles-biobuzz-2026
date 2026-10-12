package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp(name = "First Trial TeleOp and Intake")
public class TeleOp_FullSystem extends LinearOpMode{
    DcMotor Left;
    DcMotor Right;
    DcMotor Intake;
    DcMotor Outtake;
    Servo Bob;
    @Override
    public void runOpMode() {

        Left = hardwareMap.get(DcMotor.class, "L");
        Right = hardwareMap.get(DcMotor.class, "R");
        Intake = hardwareMap.get(DcMotor.class, "Int");
        Outtake = hardwareMap.get(DcMotor.class, "Out");
        Bob = hardwareMap.get(Servo.class,"B");

        Left.setDirection(DcMotorSimple.Direction.REVERSE);
        //-
        Right.setDirection(DcMotorSimple.Direction.FORWARD);
        //+
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        //+
        Outtake.setDirection(DcMotorSimple.Direction.FORWARD);
        //+
        boolean intakeOn = false;
        boolean outtakeOn = false;

        waitForStart();

        while (opModeIsActive()) {
            // Your robot code goes here!

            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double leftPower = drive + turn; // - + - = +
            double rightPower = drive - turn; // + + - = -

            //(
            double max = Math.max(1.0, Math.max(Math.abs(leftPower), Math.abs(rightPower)));

            leftPower /= max;
            rightPower /= max;
            //)

            Left.setPower(leftPower);
            Right.setPower(rightPower);

            if (gamepad1.right_trigger > 0.1) {
                intakeOn = true;
            }

            if (gamepad1.left_trigger > 0.1) {
                intakeOn = false;
            }

            if (intakeOn) {
                Intake.setPower(0.75);
            } else {
                Intake.setPower(0.0);
            }

            if (gamepad1.a) {
                outtakeOn = true;
            }

            if (gamepad1.b) {
                outtakeOn = false;
            }

            if (outtakeOn) {
                Outtake.setPower(0.75);
            } else {
                Outtake.setPower(0.0);
            }

            if (gamepad1.x) {
                Bob.setPosition(1.0);
            }

            if (gamepad1.y) {
                Bob.setPosition(0.0);
            }





        }
    }

}



