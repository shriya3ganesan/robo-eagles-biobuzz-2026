package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
@TeleOp(name = "Jaxon's Trial TeleOp")
public class JaxonTeleOp extends LinearOpMode {

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    @Override
    public void runOpMode() {

        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        waitForStart();

        while (opModeIsActive()) {
double drive = -gamepad1.left_stick_y;
double strafe = gamepad1.left_stick_x;

//THIS IS FOR DRIVETRAIN MOVEMENT :)

if (drive > 0.1 && strafe > 0.1) {
// Forward-right
frontLeft.setPower(0.5);
frontRight.setPower(0);
backRight.setPower(0.5);
backLeft.setPower(0);

} else if (drive > 0.1 && strafe < -0.1) {
// Forward-left
frontRight.setPower(0.5);
frontLeft.setPower(0);
backLeft.setPower(0.5);
backRight.setPower(0);

} else if (drive < -0.1 && strafe > 0.1) {
// Backward-right
frontRight.setPower(-0.5);
frontLeft.setPower(0);
backLeft.setPower(-0.5);
backRight.setPower(0);

} else if (drive < -0.1 && strafe < -0.1) {
// Backward-left
frontLeft.setPower(-0.5);
frontRight.setPower(0);
backRight.setPower(-0.5);
backLeft.setPower(0);

} else if (drive > 0.1){
//forward
frontLeft.setPower(0.5);
frontRight.setPower(0.5);
backLeft.setPower(0.5);
backRight.setPower(0.5);

} else if (drive < -0.1){
//backward
frontLeft.setPower(-0.5);
frontRight.setPower(-0.5);
backLeft.setPower(-0.5);
backRight.setPower(-0.5);

} else if (strafe > 0.1){
//right
frontLeft.setPower(0.5);
frontRight.setPower(-0.5);
backLeft.setPower(-0.5);
backRight.setPower(0.5);

} else if (strafe < -0.1){
//left
frontLeft.setPower(-0.5);
frontRight.setPower(0.5);
backLeft.setPower(0.5);
backRight.setPower(-0.5);

} else {
//stop
frontLeft.setPower(0);
frontRight.setPower(0);
backLeft.setPower(0);
backRight.setPower(0);
        }
    }
}}
