package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

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
}
