package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "AI_Trial_Camera")
public class AI_Trial_Camera extends LinearOpMode {
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    DcMotor Intake;
    DcMotor Outtake;
    CRServo Transfer;
    CRServo Bob;
    CRServo Tim;

    // Vision variables
    AprilTagProcessor aprilTag;
    VisionPortal visionPortal;

    // Tuning constants
    static final double DESIRED_DISTANCE_INCHES = 36.0;
    static final double MAX_VALID_RANGE_INCHES = 120.0;

    static final double DRIVE_GAIN  = 0.035;
    static final double STRAFE_GAIN = 0.030;
    static final double TURN_GAIN   = 0.025;

    // False-detection filtering state
    int lastSeenId = -1;
    int consecutiveFrames = 0;
    boolean hasRumbledForLock = false;

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

        // Initialize AprilTag Processor & Logitech Webcam at 1280x720 (720p)
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(1280, 720))
                .addProcessor(aprilTag)
                .build();

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

        telemetry.addData("Status", "Jeff 2 TeleOp + 720p Auto-Align Ready!");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // 1. Scan & filter AprilTags
            int confirmedTagId = -1;
            double tagRange = 0.0;
            double tagBearing = 0.0;
            double tagYaw = 0.0;

            List<?> currentDetections = aprilTag.getDetections();
            if (!currentDetections.isEmpty()) {
                Object det = currentDetections.get(0);
                try {
                    Object metadata = det.getClass().getField("metadata").get(det);
                    int id = det.getClass().getField("id").getInt(det);
                    Object ftcPose = det.getClass().getField("ftcPose").get(det);

                    if (metadata != null && ftcPose != null) {
                        double r = ftcPose.getClass().getField("range").getDouble(ftcPose);
                        double b = ftcPose.getClass().getField("bearing").getDouble(ftcPose);
                        double y = ftcPose.getClass().getField("yaw").getDouble(ftcPose);

                        if (r <= MAX_VALID_RANGE_INCHES) {
                            if (id == lastSeenId) {
                                consecutiveFrames++;
                            } else {
                                lastSeenId = id;
                                consecutiveFrames = 1;
                            }

                            if (consecutiveFrames >= 3) {
                                confirmedTagId = id;
                                tagRange = r;
                                tagBearing = b;
                                tagYaw = y;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            } else {
                lastSeenId = -1;
                consecutiveFrames = 0;
                hasRumbledForLock = false;
            }

            if (confirmedTagId != -1 && !hasRumbledForLock) {
                gamepad1.rumble(250);
                hasRumbledForLock = true;
            }

            // 2. Drivetrain control: Auto-Align (Hold D-Pad Right) vs Manual Sticks
            double drive;
            double strafe;
            double turn;

            if (gamepad1.dpad_right && confirmedTagId != -1) {
                double rangeError = tagRange - DESIRED_DISTANCE_INCHES;

                drive  = (Math.abs(rangeError) > 1.5) ? rangeError * DRIVE_GAIN : 0.0;
                strafe = (Math.abs(tagYaw) > 2.0)     ? -tagYaw * STRAFE_GAIN   : 0.0;
                turn   = (Math.abs(tagBearing) > 2.0) ? -tagBearing * TURN_GAIN : 0.0;

                drive  = Math.max(-0.45, Math.min(0.45, drive));
                strafe = Math.max(-0.45, Math.min(0.45, strafe));
                turn   = Math.max(-0.45, Math.min(0.45, turn));
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

            // 3. Gamepad 2 Controls
            if (gamepad2.right_bumper) {
                intakeOn = true;
            }

            if (gamepad2.left_bumper) {
                intakeOn = false;
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
                Outtake.setPower(-0.50);
            } else {
                Outtake.setPower(0.0);
            }

            if (gamepad2.dpad_up) {
                Bob.setPower(1.0);
            } else if (gamepad2.dpad_down) {
                Bob.setPower(0.0);
            }
//hi
            if (gamepad2.a) {
                Tim.setPower(1.0);
            } else if (gamepad2.b) {
                Tim.setPower(0.0);
            }

            if (gamepad2.x) {
                Transfer.setPower(1.0);
            } else if (gamepad2.y) {
                Transfer.setPower(0.0);
            }

            // 4. Telemetry (Driver Hub Display)
            if (confirmedTagId != -1) {
                telemetry.addData(">>> LOCKED TAG ID", confirmedTagId);
                telemetry.addData("Live Range (in)", "%.1f (Target: %.1f)", tagRange, DESIRED_DISTANCE_INCHES);
                telemetry.addData("Side Angle (Yaw)", "%.1f deg", tagYaw);
                telemetry.addData("Aim Angle (Bearing)", "%.1f deg", tagBearing);
                if (gamepad1.dpad_right) {
                    telemetry.addLine("STATUS: AUTO-ALIGNING...");
                } else {
                    telemetry.addLine("TIP: Hold D-Pad Right to Auto-Align!");
                }
            } else {
                telemetry.addLine("Camera Status: Scanning (No valid tag)");
            }

            telemetry.addData("Intake On", intakeOn);
            telemetry.addData("Outtake Power", Outtake.getPower());
            telemetry.update();
        }

        // Stop all motors and servos
        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);
        Intake.setPower(0.0);
        Outtake.setPower(0.0);
        Transfer.setPower(0.0);
        Bob.setPower(0.0);
        Tim.setPower(0.0);

        if (visionPortal != null) {
            visionPortal.close();
        }
    }
}

