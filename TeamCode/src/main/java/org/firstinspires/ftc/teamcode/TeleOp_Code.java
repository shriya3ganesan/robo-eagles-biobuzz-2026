package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp(name = "First Trial TeleOp")
public class TeleOp_Code extends LinearOpMode {

    DcMotor Left;
    DcMotor Right;
    Servo Bob;
    @Override
    public void runOpMode() {

        Left = hardwareMap.get(DcMotor.class, "L");
        Right = hardwareMap.get(DcMotor.class, "R");
        Bob = hardwareMap.get(Servo.class,"B");

        Left.setDirection(DcMotorSimple.Direction.REVERSE);
        //-
        Right.setDirection(DcMotorSimple.Direction.FORWARD);
        //+

        waitForStart();

        while (opModeIsActive()) {
            // Your robot code goes here!

            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double leftPower = drive + turn; // - + - = +
            double rightPower = drive - turn; // + + - = -
            Left.setPower(leftPower);
            Right.setPower(rightPower);

            if (gamepad1.x) {
                Bob.setPosition(1.0);
            }

            if (gamepad1.y) {
                Bob.setPosition(0.0);
            }

            //(
            double max = Math.max(1.0, Math.max(Math.abs(leftPower), Math.abs(rightPower)));

            leftPower /= max;
            rightPower /= max;
            //)


        }
    }

}
