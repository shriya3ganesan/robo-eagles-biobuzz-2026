package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "BlueComp_Auto", group = "Autonomous")
public class BlueComp_Auto extends LinearOpMode {

    private DcMotor frontRight;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor backLeft;
    private DcMotor intakeMotor;
    private DcMotor outtakeMotor;
    private CRServo rightServo;
    private CRServo leftServo;
    private CRServo transferSystem;


    @Override
    public void runOpMode() {

        frontRight = hardwareMap.get(DcMotor.class, "fr");
        backRight = hardwareMap.get(DcMotor.class, "br");
        frontLeft = hardwareMap.get(DcMotor.class, "fl");
        backLeft = hardwareMap.get(DcMotor.class, "bl");
        intakeMotor = hardwareMap.get(DcMotor.class, "im");
        outtakeMotor = hardwareMap.get(DcMotor.class, "om");
        rightServo = hardwareMap.get(CRServo.class, "rs");
        leftServo = hardwareMap.get(CRServo.class, "ls");
        transferSystem = hardwareMap.get(CRServo.class, "ts");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        waitForStart();


        if (opModeIsActive()) {

            outtakeMotor.setPower(-0.53);
            sleep(1500);

            transferSystem.setPower(-1);
            sleep(100);

            transferSystem.setPower(0);

            transferSystem.setPower(-1);
            sleep(100);

            transferSystem.setPower(0);

            transferSystem.setPower(-1);
            sleep(100);

            transferSystem.setPower(0);

            transferSystem.setPower(-1);
            sleep(100);

            transferSystem.setPower(0);


            outtakeMotor.setPower(0);

            frontLeft.setPower(1);
            backRight.setPower(1);
            sleep(1200);

            frontRight.setPower(0);
            backLeft.setPower(0);


            frontRight.setPower(1);
            frontLeft.setPower(1);
            backLeft.setPower(1);
            backRight.setPower(1);
            sleep(1400);

            frontLeft.setPower(0.0);
            backLeft.setPower(0.0);
            frontRight.setPower(0.0);
            backRight.setPower(0.0);

            frontRight.setPower(1);
            frontLeft.setPower(0);
            backLeft.setPower(1);
            backRight.setPower(0);
            sleep(200);

            frontLeft.setPower(0.0);
            backLeft.setPower(0.0);
            frontRight.setPower(0.0);
            backRight.setPower(0.0);










        }
    }
}


