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

    static final double TICKS_PER_REV = 537.7;
    static final double WHEEL_DIAMETER = 4.0;
    static final double TICKS_PER_INCH =
            TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER);

    public void moveForward(double power, long time) {

        if (!opModeIsActive()) return;

        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);

        sleep(time);

        stopDrive();
    }

    public void stopDrive() {

        frontLeft.setPower(0.0);
        frontRight.setPower(0.0);
        backLeft.setPower(0.0);
        backRight.setPower(0.0);

    }


    public void driveInches(double inches, double power) {

        // Reset encoder measurements
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        int ticks = (int) Math.round(inches * TICKS_PER_INCH);

        // Set target distance
        frontLeft.setTargetPosition(ticks);
        frontRight.setTargetPosition(ticks);
        backLeft.setTargetPosition(ticks);
        backRight.setTargetPosition(ticks);

        // Tell motors to travel to their targets
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        frontLeft.setPower(Math.abs(power));
        frontRight.setPower(Math.abs(power));
        backLeft.setPower(Math.abs(power));
        backRight.setPower(Math.abs(power));

        // Safety timeout
        long startTime = System.currentTimeMillis();

        while (opModeIsActive()
                && (frontLeft.isBusy() || frontRight.isBusy()
                || backLeft.isBusy() || backRight.isBusy())
                && System.currentTimeMillis() - startTime < 5000) {
            idle();
        }

        stopDrive();

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void shootFour() {

        // Spin up the shooter
        Outtake.setPower(0.6);
        sleep(1500);

        // Feed game pieces into shooter
        if (opModeIsActive()) {
            Transfer.setPower(1.0);
            sleep(2000);
        }

        // Stop feeding and shooting
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
        boolean intakeOn = false;
        boolean outtakeOn = false;

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        if (opModeIsActive()) {
            // Your robot code goes here!

            // STEP 1: Forward one foot
            driveInches(12, 0.3);

            sleep(300);

            // STEP 2: Shoot four game pieces
            if (opModeIsActive()) {
                shootFour();
            }

            sleep(300);

            // STEP 3: Backward one foot
            if (opModeIsActive()) {
                driveInches(-12, 0.3);
            }

            // STEP 4: Stop everything
            stopDrive();
            Intake.setPower(0.0);
            Outtake.setPower(0.0);
            Transfer.setPower(0.0);
        }

        }
    }

