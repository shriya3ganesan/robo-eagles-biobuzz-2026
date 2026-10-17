package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
@TeleOp(name = "Vien StarterBotWServos Code")
public class VienTeleOpStarterBot extends LinearOpMode{
    DcMotor Left;
    DcMotor Right;
    DcMotor Intake;
    DcMotor Outtake;
    Servo flower1;
    Servo flower2;
    Servo airportDoor;

    @Override
    public void runOpMode() {

        Left = hardwareMap.get(DcMotor.class, "L");
        Right = hardwareMap.get(DcMotor.class, "R");
        Intake = hardwareMap.get(DcMotor.class, "Int");
        Outtake = hardwareMap.get(DcMotor.class, "Out");
        flower1 = hardwareMap.get(Servo.class,"f1");
        flower2 = hardwareMap.get(Servo.class,"f2");
        airportDoor = hardwareMap.get(Servo.class,"Air");

        Left.setDirection(DcMotorSimple.Direction.REVERSE);
        //-
        Right.setDirection(DcMotorSimple.Direction.FORWARD);
        //+
        Intake.setDirection(DcMotorSimple.Direction.REVERSE);
        //+
        Outtake.setDirection(DcMotorSimple.Direction.FORWARD);
        //+

        //default intake and outtake
        boolean intakeOn = false;
        boolean outtakeOn = false;

        waitForStart();

        while (opModeIsActive()) {
            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            double leftPower = drive + turn; // - + - = +
            double rightPower = drive - turn; // + + - = -

            //
            double max = Math.max(1.0, Math.max(Math.abs(leftPower), Math.abs(rightPower)));

            leftPower /= max;
            rightPower /= max;


            // Set power to both motors
            Left.setPower(leftPower);
            Right.setPower(rightPower);


            // Intake is right trigger, outtake is A
            if (gamepad1.right_trigger > 0.1) {
                intakeOn = true;
            }

            if (gamepad1.a) {
                outtakeOn = true;
            }

            //left trigger turns off both
            if (gamepad1.left_trigger > 0.1) {
                intakeOn = false;
                outtakeOn = false;
            }


            // Set power to intake and outtake
            if (intakeOn) {
                Intake.setPower(0.75);
            } else {
                Intake.setPower(0.0);
            }

            if (outtakeOn) {
                Outtake.setPower(0.75);
            } else {
                Outtake.setPower(0.0);
            }

            //servos; flower1 is X flower2 is Y
            if (gamepad1.x) {
                flower1.setPosition(1.0);
            }
            if (gamepad1.y) {
                flower2.setPosition(1.0);
            }

            //left bumper retracts both flower servos
            if (gamepad1.left_bumper){
                flower1.setPosition(0.0);
                flower2.setPosition(0.0);
            }






        }
    }

}