package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Comp_TeleOp")
public class Comp_TeleOp extends LinearOpMode {
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    DcMotor Intake;
    DcMotor Outtake;
    CRServo Transfer;
    CRServo Bob;
    CRServo Tim;

    @Override
    public void runOpMode() {

        frontLeft = hardwareMap.get(DcMotor.class, "FL");
        frontRight = hardwareMap.get(DcMotor.class, "FR");
        backLeft = hardwareMap.get(DcMotor.class, "BL");
        backRight = hardwareMap.get(DcMotor.class, "BR");
        Intake = hardwareMap.get(DcMotor.class, "Int");
        Outtake = hardwareMap.get(DcMotor.class, "Out");
        Transfer = hardwareMap.get(CRServo.class, "Trn");
        Bob = hardwareMap.get(CRServo.class, "B");
        Tim = hardwareMap.get(CRServo.class, "T");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        //+
        Outtake.setDirection(DcMotorSimple.Direction.FORWARD);
        //+
        boolean intakeOn = false;
        boolean outtakeOn = false;

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Jeff 2 TeleOp Ready!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Your robot code goes here!

            double drive = -gamepad1.left_stick_y;
            double strafe = -gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            telemetry.addData("RAW Y", gamepad1.left_stick_y);
            telemetry.addData("CALCULATED DRIVE", drive);

            double frontLeftPower = drive + strafe + turn;
            double backLeftPower = drive - strafe + turn;
            double frontRightPower = drive - strafe - turn;
            double backRightPower = drive + strafe - turn;

            double max = Math.max(1.0,
                    Math.max(
                            Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                            Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
                    )
            );

            frontLeftPower /= max;
            frontRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;

            frontLeft.setPower(frontLeftPower);
            frontRight.setPower(frontRightPower);
            backLeft.setPower(backLeftPower);
            backRight.setPower(backRightPower);

            if (gamepad2.right_bumper) {
                intakeOn = true;
            }

            if (gamepad2.left_bumper) {
                intakeOn = false;
            }

            if (gamepad2.dpad_left) {
                Intake.setPower(-0.75);

            }

            if (intakeOn) {
                Intake.setPower(0.75);
            } else {
                Intake.setPower(0.0);
            }

            if (gamepad2.right_trigger > 0.1) {
                outtakeOn = true;
            }

            if (gamepad2.left_trigger > 0.1) {
                outtakeOn = false;
            }

            if (outtakeOn) {
                Outtake.setPower(-0.47);
            } else {
                Outtake.setPower(0.0);
            }

            if (gamepad2.dpad_up) {
                Bob.setPower(1.0);
            } else if (gamepad2.dpad_down) {
                Bob.setPower(0.0);
            }

            if (gamepad2.a) {
                Tim.setPower(1.0);
            } else if (gamepad2.b) {
                Tim.setPower(0.0);
            }

            if (gamepad2.x) {
                Transfer.setPower(-1.0);
            } else if (gamepad2.y) {
                Transfer.setPower(0.0);
            }

            //Telemetry -
            telemetry.addData("Left Stick Y", gamepad1.left_stick_y);
            telemetry.addData("Drive", drive);
            telemetry.addData("Strafe", strafe);
            telemetry.addData("Turn", turn);

            telemetry.addData("FL Power", frontLeftPower);
            telemetry.addData("FR Power", frontRightPower);
            telemetry.addData("BL Power", backLeftPower);
            telemetry.addData("BR Power", backRightPower);

            telemetry.addData("Intake On", intakeOn);
            telemetry.addData("Intake Power", Intake.getPower());
            telemetry.addData("Outtake Power", Outtake.getPower());
            telemetry.addData("Transfer Power", Transfer.getPower());
            telemetry.addData("Bob Power", Bob.getPower());
            telemetry.addData("Tim Power", Tim.getPower());
            telemetry.update();

        }

        // STOP ALL MOTORS AND SERVOS
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);
        Intake.setPower(0.0);
        Outtake.setPower(0.0);
        Transfer.setPower(0.0);
        Bob.setPower(0.0);
        Tim.setPower(0.0);

    }

}



