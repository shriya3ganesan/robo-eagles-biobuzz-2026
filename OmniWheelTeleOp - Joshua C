package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo; // Standard Servo for 300-degree servos

@TeleOp(name = "Omni Two-Motor TeleOp with Standard and CR Servos", group = "TeleOp")
public class OmniTeleOp extends LinearOpMode {

    // Declare hardware variables
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor intakeMotor = null;
    private DcMotor outtakeMotor = null;

    // Declare 2 standard 300-degree servos and 1 full-rotation (CR) servo
    private Servo servo1 = null;
    private Servo servo2 = null;
    private CRServo servo3 = null;

    @Override
    public void runOpMode() {
        // Initialize the hardware variables.
        leftDrive   = hardwareMap.get(DcMotor.class, "a");
        rightDrive  = hardwareMap.get(DcMotor.class, "b");
        intakeMotor = hardwareMap.get(DcMotor.class, "c");
        outtakeMotor = hardwareMap.get(DcMotor.class, "d");

        // Hardware map servo1 and servo2 as standard Servo, servo3 as CRServo
        servo1      = hardwareMap.get(Servo.class, "servo1");
        servo2      = hardwareMap.get(Servo.class, "servo2");
        servo3      = hardwareMap.get(CRServo.class, "servo3");

        // Reverse one of the drive motors so positive power moves both forward
        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);

        // Adjust these if your intake/outtake spin backwards
        intakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        outtakeMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // ==========================================
            // DRIVETRAIN CONTROL (Arcade Drive Style)
            // ==========================================
            double drive = -gamepad1.left_stick_y;
            double turn  =  gamepad1.right_stick_x;

            double leftPower  = drive + turn;
            double rightPower = drive - turn;

            double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
            if (max > 1.0) {
                leftPower  /= max;
                rightPower /= max;
            }

            leftDrive.setPower(leftPower);
            rightDrive.setPower(rightPower);

            // ==========================================
            // INTAKE CONTROL (Gamepad 1 Triggers)
            // ==========================================
            if (gamepad1.right_trigger > 0.1) {
                intakeMotor.setPower(gamepad1.right_trigger);
            } else if (gamepad1.left_trigger > 0.1) {
                intakeMotor.setPower(-gamepad1.left_trigger);
            } else {
                intakeMotor.setPower(0.0);
            }

            // ==========================================
            // OUTTAKE CONTROL (Gamepad 1 Buttons A/B)
            // ==========================================
            if (gamepad1.a) {
                outtakeMotor.setPower(0.6);
            } else if (gamepad1.b) {
                outtakeMotor.setPower(-1.0);
            } else {
                outtakeMotor.setPower(0.0);
            }

            // ==========================================
            // 300-DEGREE SERVO 1 (Gamepad 1 Bumpers)
            // ==========================================
            // Press Right Bumper for max position (1.0), Left Bumper for min position (0.0)
            if (gamepad1.right_bumper) {
                servo1.setPosition(1.0);
            } else if (gamepad1.left_bumper) {
                servo1.setPosition(0.0);
            }
            // Note: Standard servos hold their last commanded position when buttons are released.

            // ==========================================
            // 300-DEGREE SERVO 2 (Gamepad 1 D-Pad Up/Down)
            // ==========================================
            // Press D-Pad Up for max position (1.0), D-Pad Down for min position (0.0)
            if (gamepad1.dpad_up) {
                servo2.setPosition(1.0);
            } else if (gamepad1.dpad_down) {
                servo2.setPosition(0.0);
            }

            // ==========================================
            // FULL ROTATION SERVO 3 (Gamepad 1 Buttons X/Y)
            // ==========================================
            if (gamepad1.y) {
                servo3.setPower(1.0);
            } else if (gamepad1.x) {
                servo3.setPower(-1.0);
            } else {
                servo3.setPower(0.0); // Stop when released
            }

            // ==========================================
            // TELEMETRY
            // ==========================================
            telemetry.addData("Drive Motors", "Left (%.2f), Right (%.2f)", leftPower, rightPower);
            telemetry.addData("Intake Power", intakeMotor.getPower());
            telemetry.addData("Outtake Power", outtakeMotor.getPower());
            telemetry.addData("Servo Positions", "S1: %.2f, S2: %.2f", servo1.getPosition(), servo2.getPosition());
            telemetry.addData("CR Servo Power", "S3: %.2f", servo3.getPower());
            telemetry.update();
        }
    }
}
