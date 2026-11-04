package org.firstinspires.ftc;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "ShootingAuto", group = "Autonomous")
public class ShootingAuto extends LinearOpMode {

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
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        waitForStart();


        if (opModeIsActive()) {

            outtakeMotor.setPower(-0.53);
            sleep(1500);

            transferSystem.setPower(-1);
            sleep(3000);

            outtakeMotor.setPower(0);
            transferSystem.setPower(0);




        }
    }
}

