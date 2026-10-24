package org.firstinspires.ftc.teamcode;

//imports
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous (name= "Vien Mechanum Auto", group = "Autonomous")
public class VienMechanumAuto extends LinearOpMode {

    //runtime
    private ElapsedTime runtime = new ElapsedTime();

    //assigning wheel motors
    private DcMotor backLeft = null;
    private DcMotor backRight = null;
    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;

    //assigning intake and outake
    private DcMotor outake = null;
    private DcMotor intake = null;

    //flower servos
    private CRServo leftFlower = null;
    private CRServo rightFlower = null;

    //airport door(transfer)
    private CRServo airportDoor = null;


    //104 mm mechanum wheels
    //Double check COUNTS_PER_MOTOR_REV var
    static final double COUNTS_PER_MOTOR_REV = 537.7;
    static final double DRIVE_GEAR_REDUCTION = 1.0;


    static final double WHEEL_DIAMETER_INCHES = 104.0 / 25.4;

    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.14159);


    // shooter and transfer
    public void shooter() {

        outake.setPower(0.7);
        sleep(1500);

        for (int i = 0; i < 4 && opModeIsActive(); i++) {

            airportDoor.setPower(1.0);
            sleep(400);

            airportDoor.setPower(0.0);
            sleep(500);
        }

        airportDoor.setPower(0.0);
        outake.setPower(0.0);
    }

    public void encoderDrive ( double speed,
                               double backLeftInches,
                               double backRightInches,
                               double frontLeftInches,
                               double frontRightInches,
                               double sleep     ) {
        //Reset encoder to 0
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //set new target
        int newBLTarget = backLeft.getCurrentPosition() + (int) (backLeftInches * COUNTS_PER_INCH);
        int newBRTarget = backRight.getCurrentPosition() + (int) (backRightInches * COUNTS_PER_INCH);
        int newFLTarget = frontLeft.getCurrentPosition() + (int) (frontLeftInches * COUNTS_PER_INCH);
        int newFRTarget = frontRight.getCurrentPosition() + (int) (frontRightInches * COUNTS_PER_INCH);

        backLeft.setTargetPosition(newBLTarget);
        backRight.setTargetPosition(newBRTarget);
        frontLeft.setTargetPosition(newFLTarget);
        frontRight.setTargetPosition(newFRTarget);

        // Run to positionn
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // reset the timeout time and start motion.
        runtime.reset();
        backLeft.setPower(Math.abs(speed));
        backRight.setPower(Math.abs(speed));
        frontLeft.setPower(Math.abs(speed));
        frontRight.setPower(Math.abs(speed));

        while (opModeIsActive() && (runtime.seconds() < sleep)
                && (backLeft.isBusy()
                && backRight.isBusy()
                && frontLeft.isBusy()
                && frontRight.isBusy())) {

            // telemetry
            telemetry.addData("Running to", " %7d :%7d",
                    newBLTarget,
                    newBRTarget,
                    newFLTarget,
                    newFRTarget);
            telemetry.addData("Currently at", " at %7d :%7d",
                    backLeft.getCurrentPosition(),
                    backRight.getCurrentPosition(),
                    frontLeft.getCurrentPosition(),
                    frontRight.getCurrentPosition());
            telemetry.update();
        }

        //run using encoder
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


    }

    @Override
    public void runOpMode() {

        //HW map
        //wheels
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        backRight = hardwareMap.get(DcMotor.class, "br");
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        frontRight = hardwareMap.get(DcMotor.class, "fr");
        //intake and outake
        outake = hardwareMap.get(DcMotor.class, "out");
        intake = hardwareMap.get(DcMotor.class, "in");
        //flowers
        rightFlower = hardwareMap.get(CRServo.class,"f1");
        leftFlower = hardwareMap.get(CRServo.class,"f2");
        //transfer
        airportDoor = hardwareMap.get(CRServo.class, "trn");


        //set directions
        //wheels
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);

        //intake & outake
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        outake.setDirection(DcMotorSimple.Direction.FORWARD);

        //Wheel brake
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();


        if (opModeIsActive()) {
            //Movement code
            //move forward
            encoderDrive(0.75, 12, 12, 12, 12, 2.0);
            //shoot
            shooter();
            //move backwards
            encoderDrive(0.75, -12, -12, -12, -12, 2.0);

            // Stop wheels
            backLeft.setPower(0);
            backRight.setPower(0);
            frontLeft.setPower(0);
            frontRight.setPower(0);


        }

        }

    }