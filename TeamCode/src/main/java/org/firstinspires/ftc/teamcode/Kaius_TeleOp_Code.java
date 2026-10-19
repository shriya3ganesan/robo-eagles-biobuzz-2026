package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Kaius_TeleOp")
public class Kaius_TeleOp_Code extends LinearOpMode {

    DcMotor Right;
    DcMotor Left;
    DcMotor Intake;
    DcMotor Outtake;
    CRServo One;
    CRServo Two;
    CRServo Three;

    @Override
    public void runOpMode() {

        Right = hardwareMap.get(DcMotor.class, "right_drive");
        Left = hardwareMap.get(DcMotor.class, "left_drive");
        Intake = hardwareMap.get(DcMotor.class, "intake");
        Outtake = hardwareMap.get(DcMotor.class, "outtake");
        One = hardwareMap.get(CRServo.class, "servo_one");
        Two = hardwareMap.get(CRServo.class, "servo_two");
        Three = hardwareMap.get(CRServo.class, "servo_three");

        Left.setDirection(DcMotorSimple.Direction.REVERSE);
        Right.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        boolean intakeOn = false;
        boolean outtakeOn = false;


        waitForStart();


        while (opModeIsActive()) {

            // Example basic tank drive control using the gamepads
            double drive = -gamepad1.left_stick_y;
            double turn  = -gamepad1.right_stick_y;

            Left.setPower(drive);
            Right.setPower(turn);

            telemetry.addData("Status", "Running");
            telemetry.update();

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

            if (gamepad1.left_bumper) {
                outtakeOn = true;
            }

            if (gamepad1.right_bumper) {
                outtakeOn = false;
            }

            if (outtakeOn) {
                Outtake.setPower(1.0);
            } else {
                Outtake.setPower(0.0);
            }

            if (gamepad1.x) {
                One.setPower(1.0);
            }

            if (gamepad1.y) {
                One.setPower(0.0);
            }

            if (gamepad1.a) {
                Two.setPower(1.0);
            }

            if (gamepad1.b) {
                Two.setPower(0.0);
            }

            if (gamepad1.left_stick_button) {
                Three.setPower(1.0);
            }

            if (gamepad1.right_stick_button) {
                Three.setPower(0.0);
            }
        }
    }
}