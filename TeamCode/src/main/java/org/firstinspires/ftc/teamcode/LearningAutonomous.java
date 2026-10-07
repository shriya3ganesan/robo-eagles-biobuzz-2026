package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;


@Autonomous(name = "LearningAutonomous", group = "Learning")
public class LearningAutonomous extends LinearOpMode {
    private DcMotor backleftMotor;

    private DcMotor backRightMotor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;

    @Override
    public void runOpMode() {

        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        backleftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");


        if (opModeIsActive()) {

            for (int i = 0; i < 30; i++) {
                backleftMotor.setPower(.4);
                backRightMotor.setPower(.4);
                frontRightMotor.setPower(.4);
                frontLeftMotor.setPower(.4);
                sleep(4000);

                backleftMotor.setPower(-.4);
                backRightMotor.setPower(-.4);
                frontRightMotor.setPower(-.4);
                frontLeftMotor.setPower(-.4);
                sleep(4000);



            }

        }
    }
}
