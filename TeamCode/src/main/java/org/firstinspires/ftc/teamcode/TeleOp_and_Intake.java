package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp(name = "First Trial TeleOp and Intake")
public class TeleOp_and_Intake extends LinearOpMode{
    DcMotor Left;
    DcMotor Right;
    DcMotor Intake;
    Servo Bob;
    @Override
    public void runOpMode() {

        Left = hardwareMap.get(DcMotor.class, "L");
        Right = hardwareMap.get(DcMotor.class, "R");
        Intake = hardwareMap.get(DcMotor.class, "Int");
        Bob = hardwareMap.get(Servo.class,"B");

        Left.setDirection(DcMotorSimple.Direction.REVERSE);
        //-
        Right.setDirection(DcMotorSimple.Direction.FORWARD);
        //+
        Intake.setDirection(DcMotorSimple.Direction.FORWARD);

        boolean intakeOn = false;

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

            if (gamepad1.x) {
                Bob.setPosition(1.0);
            }

            if (gamepad1.y) {
                Bob.setPosition(0.0);
            }





        }
    }

}


