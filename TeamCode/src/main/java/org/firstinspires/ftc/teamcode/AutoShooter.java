package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "AutoShooter", group = "Autonomous")
public class AutoShooter extends LinearOpMode {

    // Control Hub - Drive Motors
    private DcMotor frontRight = null; // Port 0
    private DcMotor backRight = null;  // Port 1
    private DcMotor frontLeft = null;  // Port 2
    private DcMotor backLeft = null;   // Port 3

    // Expansion Hub - Mechanism Motors
    private DcMotor intakeMotor = null;  // Port 0
    private DcMotor outtakeMotor = null; // Port 1

    // Servo Mapping (Control/Expansion Hub Servo Ports)
    private Servo rightServo = null;       // Port 0
    private Servo leftServo = null;        // Port 1
    private CRServo transferSystem = null; // Port 2

    static final double COUNTS_PER_MOTOR_REV = 537.7;    // goBILDA 435 RPM standard resolution
    static final double DRIVE_GEAR_REDUCTION = 1.0;      // 1.0 if direct drive (e.g., standard Strafer chassis)
    static final double WHEEL_DIAMETER_INCHES = 104.0/25.4; // Standard goBILDA 96mm Mecanum Wheel diameter

    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * Math.PI);

    @Override
    public void runOpMode() {

        // ===================================================
        // HARDWARE MAPPING & INITIALIZATION
        // ===================================================
        // Control Hub Motors
        frontRight = hardwareMap.get(DcMotor.class, "motor0"); // CH Port 0
        backRight  = hardwareMap.get(DcMotor.class, "motor1"); // CH Port 1
        frontLeft  = hardwareMap.get(DcMotor.class, "motor2"); // CH Port 2
        backLeft   = hardwareMap.get(DcMotor.class, "motor3"); // CH Port 3

        // Expansion Hub Motors
        intakeMotor  = hardwareMap.get(DcMotor.class, "eh_motor0"); // EH Port 0
        outtakeMotor = hardwareMap.get(DcMotor.class, "eh_motor1"); // EH Port 1

        // Servos
        rightServo     = hardwareMap.get(Servo.class, "right servo");     // Port 0
        leftServo      = hardwareMap.get(Servo.class, "left servo");      // Port 1
        transferSystem = hardwareMap.get(CRServo.class, "transfer");      // Port 2

        // Direction setup: Reverse left side motors so positive power pushes forward
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Active Brake Behavior: Prevents drifting/sliding when power is cut to 0
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reset all 4 drivetrain encoders to a baseline of 0
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Standard Mecanum Auto Ready!");
        telemetry.update();

        // Wait for the driver to press PLAY
        waitForStart();

        // ===================================================
        // AUTONOMOUS ACTIONS SEQUENCE
        // ===================================================
        if (opModeIsActive()) {

            // --- ACTION A: DRIVING FORWARD 1 FOOT ---
            int targetTicks = (int)(12.0 * COUNTS_PER_INCH);

            // Set targets relative to current position
            frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + targetTicks);
            backLeft.setTargetPosition(backLeft.getCurrentPosition() + targetTicks);
            frontRight.setTargetPosition(frontRight.getCurrentPosition() + targetTicks);
            backRight.setTargetPosition(backRight.getCurrentPosition() + targetTicks);

            // Put all motors into Run To Position mode
            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Apply cruise power smoothly
            frontLeft.setPower(0.4);
            backLeft.setPower(0.4);
            frontRight.setPower(0.4);
            backRight.setPower(0.4);

            // Runs loop until ALL motors finish
            while (opModeIsActive() && (frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy())) {
                telemetry.addData("Path", "Mecanum Driving 1 Foot Forward...");
                telemetry.addData("FL Position", "%d / %d", frontLeft.getCurrentPosition(), frontLeft.getTargetPosition());
                telemetry.update();
                sleep(10); // Tiny pause to avoid spamming the CPU
            }

            // Entirely neutralize drivetrain power output
            frontLeft.setPower(0);
            backLeft.setPower(0);
            frontRight.setPower(0);
            backRight.setPower(0);

            // Reset back to standard encoder mode after movement is done
            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // --- ACTION B: SPIN UP THE FLYWHEEL SHOOTER ---
            outtakeMotor.setPower(0.6);
            sleep(1000); // Wait 1 second for flywheel to build up enough speed

            // --- ACTION C: RUN TRANSFER CONTINUOUSLY ---
            rightServo.setPosition(1.0);     // Open/extend right servo
            leftServo.setPosition(1.0);      // Open/extend left servo
            transferSystem.setPower(1.0);    // Spin continuous feeder servo forward

            telemetry.addData("Status", "Shooting active!");
            telemetry.update();

            sleep(4000); // Leave transfer active and shoot continuously for 4 seconds

            // --- ACTION D: SAFE SHUTDOWN BLOCK ---
            outtakeMotor.setPower(0.0);
            transferSystem.setPower(0.0);
            rightServo.setPosition(0.0);
            leftServo.setPosition(0.0);

            telemetry.addData("Status", "Autonomous Routine Finished");
            telemetry.update();
        }
    }

    @Autonomous(name = "AprilTagAutonomous", group = "Linear OpMode")
    public static class AprilTagAutonomous extends LinearOpMode {

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
            // Initialize drivetrain (Strictly retaining your exact hardware configuration mappings)
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

            // Initialize AprilTag Processor & Logitech Webcam (USB Port)
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

            // Advanced Reset: Zeros out the internal position counters completely before match start
            telemetry.addLine("Status: Resetting Encoders...");
            telemetry.update();
            setDrivetrainMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            setDrivetrainMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // Scan for AprilTags during INIT
            while (!isStarted() && !isStopRequested()) {

                List<AprilTagDetection> currentDetections =
                        aprilTag.getDetections();

                for (AprilTagDetection detection : currentDetections) {

                    if (detection instanceof AprilTagSingleDetection) {

                        AprilTagSingleDetection singleDetection =
                                (AprilTagSingleDetection) detection;

                        if (singleDetection.metadata != null) {
                            detectedTagId = singleDetection.id;
                        }
                    }
                }

                telemetry.addLine("Status: Advanced Autonomous Ready");

                if (detectedTagId != -1) {
                    telemetry.addData("Locked AprilTag ID", detectedTagId);
                } else {
                    telemetry.addLine("No AprilTag detected");
                }

                telemetry.update();
                sleep(20);
            }

            if (opModeIsActive()) {
                // STEP 1: Shoot via CR Servo and Outtake simultaneous activation (UNTOUCHED)
                telemetry.addData("Executing: Shoot Sequence | Tag ID", detectedTagId);
                telemetry.update();

                outtake.setPower(-0.54);
                sleep(1500);
                continuousServo.setPower(-1.0); // Active continuous loader feed
                sleep(5000);                   // Exactly 5000 milliseconds of tracking

                outtake.setPower(0.0);         // Disengage shooting modules
                continuousServo.setPower(0.0);

                // Ensure drivetrain is in time-based run mode
                setDrivetrainMode(DcMotor.RunMode.RUN_USING_ENCODER);

                // STEP 2: Move Backwards 325 Milliseconds (UNTOUCHED)
                telemetry.addLine("Executing: Move Backwards (325ms)");
                telemetry.update();
                frontLeft.setPower(-0.5);
                frontRight.setPower(-0.5);
                backLeft.setPower(-0.5);
                backRight.setPower(-0.5);
                sleep(305);

                sleep(50);

                // STEP 3: Strafe Left for 1000 Milliseconds (UNTOUCHED)
                telemetry.addLine("Executing: Strafe Left (300ms)");
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
                frontLeft.setPower(0.0);       // Cut power to all motors
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
         * Advanced Helper function to execute uniform coordinate calculations and precise path operations.
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
}
