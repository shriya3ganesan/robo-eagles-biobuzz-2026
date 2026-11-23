package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "AprilTagAutonomous", group = "Linear OpMode")
public class AprilTagAutonomous extends LinearOpMode {

    // Drivetrain motors
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    // Intake and outtake motors
    private DcMotor intake;
    private DcMotor outtake;

    // Continuous rotation servos
    private CRServo continuousServo;
    private CRServo servo1;
    private CRServo servo2;

    // Vision variables
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private int detectedTagId = -1; // -1 means no tag seen yet

    // Advanced Encoder Tuning Constants
    static final double COUNTS_PER_MOTOR_REV = 537.7; // Ticks for standard GoBILDA 19.2:1 / Yellow Jacket motor
    static final double DRIVE_GEAR_REDUCTION = 1.0;   // 1.0 if wheels are direct-driven without external gears
    static final double WHEEL_DIAMETER_INCHES = 3.77953; // Standard 96mm Mecanum Wheel diameter in inches
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

    @Override
    public void runOpMode() {
        // Initialize drivetrain
        frontLeft = hardwareMap.get(DcMotor.class, "a");
        frontRight = hardwareMap.get(DcMotor.class, "b");
        backLeft = hardwareMap.get(DcMotor.class, "c");
        backRight = hardwareMap.get(DcMotor.class, "d");

        // Initialize intake and outtake
        intake = hardwareMap.get(DcMotor.class, "int");
        outtake = hardwareMap.get(DcMotor.class, "out");

        // Initialize continuous rotation servos
        continuousServo = hardwareMap.get(CRServo.class, "CR");
        servo1 = hardwareMap.get(CRServo.class, "S1");
        servo2 = hardwareMap.get(CRServo.class, "S2");

        // Initialize AprilTag Processor & Logitech Webcam
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTag)
                .build();

        // Drivetrain directions
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        // Brake drivetrain immediately on target arrival to kill drift
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reset encoders
        telemetry.addLine("Status: Resetting Encoders...");
        telemetry.update();
        setDrivetrainMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDrivetrainMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Scan for AprilTags continuously during INIT without referencing AprilTagDetection symbols
        while (!isStarted() && !isStopRequested()) {
            List<?> currentDetections = aprilTag.getDetections();

            if (!currentDetections.isEmpty()) {
                Object firstTag = currentDetections.get(0);
                try {
                    detectedTagId = firstTag.getClass().getField("id").getInt(firstTag);
                } catch (Exception ignored) {
                }
            }

            telemetry.addLine("Status: Advanced Autonomous Ready");
            if (detectedTagId != -1) {
                telemetry.addData("Locked AprilTag ID", detectedTagId);
            } else {
                telemetry.addLine("Scanning for AprilTag...");
            }
            telemetry.update();
            sleep(20);
        }

        if (opModeIsActive()) {
            // STEP 1: Shoot via CR Servo and Outtake simultaneous activation
            telemetry.addData("Executing: Shoot Sequence | Tag ID", detectedTagId);
            telemetry.update();

            outtake.setPower(-0.54);
            sleep(1500);
            continuousServo.setPower(-1.0); // Active continuous loader feed
            sleep(5000);

            outtake.setPower(0.0);         // Disengage shooting modules
            continuousServo.setPower(0.0);

            // Ensure drivetrain is in encoder-assisted run mode
            setDrivetrainMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // STEP 2: Move Backwards
            telemetry.addLine("Executing: Move Backwards (305ms)");
            telemetry.update();
            frontLeft.setPower(-0.5);
            frontRight.setPower(-0.5);
            backLeft.setPower(-0.5);
            backRight.setPower(-0.5);
            sleep(305);

            sleep(50);

            // STEP 3: Strafe Left
            telemetry.addLine("Executing: Strafe Left (1000ms)");
            telemetry.update();
            frontLeft.setPower(-0.5);
            frontRight.setPower(0.5);
            backLeft.setPower(0.5);
            backRight.setPower(-0.5);
            sleep(1000);

            sleep(50);

            // STEP 4: Final Safety Stop
            telemetry.addLine("Status: Autonomous Path Complete");
            telemetry.update();
            frontLeft.setPower(0.0);
            frontRight.setPower(0.0);
            backLeft.setPower(0.0);
            backRight.setPower(0.0);
        }

        // Safely shut down camera stream when OpMode finishes
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    /**
     * Helper function to change all drivetrain motor run modes simultaneously
     */
    private void setDrivetrainMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    /**
     * Helper function to execute encoder-based driving in inches
     */
    public void encoderDrive(double speed, double leftFrontInches, double rightFrontInches,
                             double leftBackInches, double rightBackInches, double timeoutS) {
        int newLeftFrontTarget;
        int newRightFrontTarget;
        int newLeftBackTarget;
        int newRightBackTarget;

        if (opModeIsActive()) {
            newLeftFrontTarget = frontLeft.getCurrentPosition() + (int)(leftFrontInches * COUNTS_PER_INCH);
            newRightFrontTarget = frontRight.getCurrentPosition() + (int)(rightFrontInches * COUNTS_PER_INCH);
            newLeftBackTarget = backLeft.getCurrentPosition() + (int)(leftBackInches * COUNTS_PER_INCH);
            newRightBackTarget = backRight.getCurrentPosition() + (int)(rightBackInches * COUNTS_PER_INCH);

            frontLeft.setTargetPosition(newLeftFrontTarget);
            frontRight.setTargetPosition(newRightFrontTarget);
            backLeft.setTargetPosition(newLeftBackTarget);
            backRight.setTargetPosition(newRightBackTarget);

            setDrivetrainMode(DcMotor.RunMode.RUN_TO_POSITION);

            frontLeft.setPower(Math.abs(speed));
            frontRight.setPower(Math.abs(speed));
            backLeft.setPower(Math.abs(speed));
            backRight.setPower(Math.abs(speed));

            long startTime = System.currentTimeMillis();

            while (opModeIsActive() &&
                    ((System.currentTimeMillis() - startTime) < (timeoutS * 1000)) &&
                    (frontLeft.isBusy() && frontRight.isBusy() && backLeft.isBusy() && backRight.isBusy())) {

                telemetry.addData("Target FL/FR", "%7d :%7d", newLeftFrontTarget, newRightFrontTarget);
                telemetry.addData("Actual FL/FR", "%7d :%7d", frontLeft.getCurrentPosition(), frontRight.getCurrentPosition());
                telemetry.update();
            }

            frontLeft.setPower(0);
            frontRight.setPower(0);
            backLeft.setPower(0);
            backRight.setPower(0);

            setDrivetrainMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}
