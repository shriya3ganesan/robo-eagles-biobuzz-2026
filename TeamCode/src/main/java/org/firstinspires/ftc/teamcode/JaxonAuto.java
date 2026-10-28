package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
@Autonomous(name = "Jeff Auto")
public class JaxonAuto extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;
    DcMotor Intake;
    DcMotor Outtake;
    CRServo Transfer;
    CRServo Bob;
    CRServo Tim;

    // Encoder ticks per wheel revolution
// Verify this value against your actual drivetrain motors
    static final double TICKS_PER_REV = 537.7;

    // Actual wheel diameter: 104 mm
    static final double WHEEL_DIAMETER = 104.0 / 25.4;

    // Calculate encoder ticks per inch
    static final double TICKS_PER_INCH =
            TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER);

    public void stopDrive() {

        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);
    }

    public boolean driveInches(double inches, double power) {

        if (!opModeIsActive()) return false;

        // STEP 1: Reset encoders
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // STEP 2: Calculate target ticks
        int ticks = (int) Math.round(inches * TICKS_PER_INCH);

        // STEP 3: Set targets
        frontLeft.setTargetPosition(ticks);
        frontRight.setTargetPosition(ticks);
        backLeft.setTargetPosition(ticks);
        backRight.setTargetPosition(ticks);

        // STEP 4: Enable RUN_TO_POSITION
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // STEP 5: Apply motor power
        frontLeft.setPower(Math.abs(power));
        frontRight.setPower(Math.abs(power));
        backLeft.setPower(Math.abs(power));
        backRight.setPower(Math.abs(power));

        // STEP 6: Wait until targets or timeout
        long startTime = System.currentTimeMillis();

        while (opModeIsActive()
                && (frontLeft.isBusy() || frontRight.isBusy()
                || backLeft.isBusy() || backRight.isBusy())
                && System.currentTimeMillis() - startTime < 5000) {
            idle();
        }

        // STEP 7: Check whether targets were reached
        boolean reachedTarget =
                opModeIsActive()
                        && !frontLeft.isBusy()
                        && !frontRight.isBusy()
                        && !backLeft.isBusy()
                        && !backRight.isBusy();

        // STEP 8: Display results
        telemetry.addData("Target Ticks", ticks);
        telemetry.addData("Front Left", frontLeft.getCurrentPosition());
        telemetry.addData("Front Right", frontRight.getCurrentPosition());
        telemetry.addData("Back Left", backLeft.getCurrentPosition());
        telemetry.addData("Back Right", backRight.getCurrentPosition());
        telemetry.addData("Reached Target", reachedTarget);
        telemetry.update();

        // STEP 9: Stop drivetrain
        stopDrive();

        // STEP 10: Restore motor modes
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // STEP 11: Return the result
        return reachedTarget;
    }

    public void shootFour() {

        if (!opModeIsActive()) return;

        Outtake.setPower(0.6);
        sleep(1500);

        for (int i = 0; i < 4 && opModeIsActive(); i++) {

            Transfer.setPower(1.0);
            sleep(400);

            Transfer.setPower(0.0);
            sleep(500);
        }

        Transfer.setPower(0.0);
        Outtake.setPower(0.0);
    }

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
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        Intake.setDirection(DcMotorSimple.Direction.FORWARD);
        //+
        Outtake.setDirection(DcMotorSimple.Direction.FORWARD);
        //+

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Jeff Auto Ready!");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Your robot code goes here!

            // STEP 1: Forward one foot
            boolean reachedShootingPosition = driveInches(-12, 0.3);

            // STEP 2: Shoot only if the robot reached its target
            if (reachedShootingPosition && opModeIsActive()) {

                sleep(300);

                if (opModeIsActive()) {
                    shootFour();
                }

                sleep(300);

                // STEP 3: Return to starting position
                if (opModeIsActive()) {
                    driveInches(12, 0.3);
                }
            }
        }

// STEP 4: Stop everything
        stopDrive();
        Intake.setPower(0.0);
        Outtake.setPower(0.0);
        Transfer.setPower(0.0);
        Bob.setPower(0.0);
        Tim.setPower(0.0);
    }
}


