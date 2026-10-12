package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TrialTeleOp")
public class TrialTeleop extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        DcMotor Left;
        DcMotor Right;
        Servo TimmyTommy;

        Left = hardwareMap.get(DcMotor.class, "L");
        Right = hardwareMap.get(DcMotor.class, "R");
        TimmyTommy = hardwareMap.get(Servo.class, "TT");

        waitForStart();


        while (opModeIsActive()) {


            Left.setPower(-gamepad1.left_stick_y);
            Right.setPower(-gamepad1.right_stick_y);

            if (gamepad1.a) {
                TimmyTommy.setPosition(1.0);
            } else if (gamepad1.b) {
                TimmyTommy.setPosition(0.0);
            }

            telemetry.addData("Left Motor Power", Left.getPower());
            telemetry.addData("Right Motor Power", Right.getPower());
            telemetry.addData("Servo Position", TimmyTommy.getPosition());
            telemetry.update();


        }

    }
}