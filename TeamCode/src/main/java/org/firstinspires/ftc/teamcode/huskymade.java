package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp(name = "HuskyLens")
public class huskymade extends LinearOpMode {
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    DcMotor Intake;
    DcMotor Outtake;
    CRServo Transfer;
    CRServo Bob;
    CRServo Tim;

    // HuskyLens Vision
    HuskyLens huskyLens;

    // Tuning constants (HuskyLens screen is 320x240)
    static final int SCREEN_CENTER_X = 160;
    static final int TARGET_TAG_WIDTH_PX = 45; // TODO: Measure block.width at your exact shooting spot!

    // Tolerances for "Perfect Shot Angle"
    static final int X_TOLERANCE_PX = 12;      // How close to center (left/right)
    static final int WIDTH_TOLERANCE_PX = 6;   // How close to target distance

    // Auto-align gains (tuned for pixel values)
    static final double DRIVE_GAIN  = 0.015;
    static final double TURN_GAIN   = 0.004;

    // False-detection filtering state
    int lastSeenId = -1;
    int consecutiveFrames = 0;
    boolean hasRumbledForShot = false;

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

        // Initialize HuskyLens on I2C (Must be named "huskylens" in Robot Config, on I2C Bus 1, 2, or 3)
        huskyLens = hardwareMap.get(HuskyLens.class, "huskylens");
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.TAG_RECOGNITION);

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        Outtake.setDirection(DcMotorSimple.Direction.FORWARD);

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

        telemetry.addData("Status", "Jeff 2 TeleOp + HuskyLens Ready!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // 1. Scan & filter HuskyLens AprilTags
            HuskyLens.Block confirmedBlock = null;
            HuskyLens.Block[] blocks = huskyLens.blocks();

            if (blocks.length > 0) {
                // Grab the first detected tag (Note: must be learned on HuskyLens, ID > 0)
                HuskyLens.Block b = blocks[0];

                if (b.id > 0) {
                    if (b.id == lastSeenId) {
                        consecutiveFrames++;
                    } else {
                        lastSeenId = b.id;
                        consecutiveFrames = 1;
                    }

                    if (consecutiveFrames >= 3) {
                        confirmedBlock = b;
                    }
                }
            } else {
                lastSeenId = -1;
                consecutiveFrames = 0;
            }

            // 2. Check if we are at the "Perfect Shot Angle"
            boolean isPerfectShotAngle = false;
            int xError = 0;
            int widthError = 0;

            if (confirmedBlock != null) {
                xError = confirmedBlock.x - SCREEN_CENTER_X;
                widthError = TARGET_TAG_WIDTH_PX - confirmedBlock.width;

                if (Math.abs(xError) <= X_TOLERANCE_PX && Math.abs(widthError) <= WIDTH_TOLERANCE_PX) {
                    isPerfectShotAngle = true;
                }
            }

            // Rumble once when entering the perfect shot zone
            if (isPerfectShotAngle && !hasRumbledForShot) {
                gamepad1.rumble(300);
                gamepad2.rumble(300);
                hasRumbledForShot = true;
            } else if (!isPerfectShotAngle) {
                hasRumbledForShot = false;
            }

            // 3. Drivetrain control: Auto-Align (Hold D-Pad Right) vs Manual Sticks
            double drive;
            double strafe;
            double turn;

            if (gamepad1.dpad_right && confirmedBlock != null) {
                // Drive forward/backward based on tag pixel width, turn based on X center
                drive  = (Math.abs(widthError) > WIDTH_TOLERANCE_PX) ? widthError * DRIVE_GAIN : 0.0;
                turn   = (Math.abs(xError) > X_TOLERANCE_PX)         ? -xError * TURN_GAIN     : 0.0;
                strafe = 0.0; // HuskyLens 2D cannot measure yaw angle to strafe-square

                drive = Math.max(-0.40, Math.min(0.40, drive));
                turn  = Math.max(-0.40, Math.min(0.40, turn));
            } else {
                drive = -gamepad1.left_stick_y;
                strafe = gamepad1.left_stick_x;
                turn = gamepad1.right_stick_x;

                if (Math.abs(drive) < 0.05) drive = 0;
                if (Math.abs(strafe) < 0.05) strafe = 0;
                if (Math.abs(turn) < 0.05) turn = 0;
            }

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

            // 4. Gamepad 2 Controls (Intake & Outtake)
            if (gamepad2.right_bumper) intakeOn = true;
            if (gamepad2.left_bumper) intakeOn = false;
            Intake.setPower(intakeOn ? 0.75 : 0.0);

            if (gamepad2.right_trigger > 0.1) outtakeOn = true;
            if (gamepad2.left_trigger > 0.1) outtakeOn = false;
            Outtake.setPower(outtakeOn ? 1.0 : 0.0);

            // 5. Telemetry Status for Driver Station
            if (isPerfectShotAngle) {
                telemetry.addLine(">>> PERFECT ANGLE TO SHOOT! <<<");
            } else if (confirmedBlock != null) {
                telemetry.addLine("Tag Locked - Adjusting...");
            } else {
                telemetry.addLine("Searching for AprilTag...");
            }

            if (confirmedBlock != null) {
                telemetry.addData("Tag ID", confirmedBlock.id);
                telemetry.addData("Tag X (Target 160)", confirmedBlock.x);
                telemetry.addData("Tag Width (Target " + TARGET_TAG_WIDTH_PX + ")", confirmedBlock.width);
            }
            telemetry.update();
        }
    }
}
