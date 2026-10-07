package org.firstinspires.ftc.robotcontroller;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
//By JAXON B, Kaius M, Ani G
public class Team_Output {

    LinearOpMode opMode;
    ElapsedTime time = new ElapsedTime();
    private static final double MAXLEVEL = 14;
    public static final double MAXHEIGHT = 34; // Inches; set to 20 inches cap for broken wire
    private static final double DISTANCE_TO_BUILD_ZONE = 1; // what ever distance is from foundation to build zone
    public DcMotor flywheel;
    public Servo gate;

    public Team_Output(LinearOpMode opMode) {

        this.opMode = opMode;
        time.reset();

        flywheel = opMode.hardwareMap.dcMotor.get("fw");
        gate = opMode.hardwareMap.servo.get("g");

        flywheel.setDirection(DcMotorSimple.Direction.FORWARD);
        gate.setDirection(Servo.Direction.FORWARD);


    }

    public void openBasketAuto(double runtime){}

    public void setFlywheel(DcMotor flywheel)
    {
        this.flywheel = flywheel;

        flywheel.setPower(.5);
        time.reset();
        while(time.milliseconds() < opMode.getRuntime() && opMode.opModeIsActive());
    }
}














