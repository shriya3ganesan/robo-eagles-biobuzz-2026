package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Drivetrain {
    DcMotor fl;
    DcMotor fr;
    DcMotor bl;
    DcMotor br;

    ElapsedTime runtime = new ElapsedTime();
    private LinearOpMode opMode;

    public Drivetrain(LinearOpMode opMode) throws InterruptedException {
        this.opMode = opMode;

        fl = this.opMode.hardwareMap.dcMotor.get("fl");
        fr = this.opMode.hardwareMap.dcMotor.get("fr");
        bl = this.opMode.hardwareMap.dcMotor.get("bl");
        br = this.opMode.hardwareMap.dcMotor.get("br");
        //forwards
        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.FORWARD);
        //backwards+
        /*
        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.REVERSE);
        //Left strafe
        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        bl.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.REVERSE);
        //Right Strafe
        fl.setDirection(DcMotor.Direction.FORWARD);
        fr.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        br.setDirection(DcMotor.Direction.FORWARD);
        */





        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    } public double getEncoderAverage() {
        double count = 4.0;
        if(fr.getCurrentPosition() == 0)
        {
            count--;
        }
        if(fl.getCurrentPosition() == 0)
        {
            count--;
        }
        if(br.getCurrentPosition() == 0)
        {
            count--;
        }
        if(bl.getCurrentPosition() == 0)
        {
            count--;
        }
        if(count == 0)
        {
            return 0;
        }
        return (fl.getCurrentPosition() + fr.getCurrentPosition()
                + br.getCurrentPosition() + bl.getCurrentPosition()) / count;
    }
    private double getStrafeEncoderAverage(double direction) {

        double count = 4.0;
        double average = 0;

        if(fr.getCurrentPosition() == 0)
        {
            count--;
        }
        if(fl.getCurrentPosition() == 0)
        {
            count--;
        }
        if(br.getCurrentPosition() == 0)
        {
            count--;
        }
        if(bl.getCurrentPosition() == 0)
        {
            count--;
        }
        if(direction > 0)
        {
            average = (((-1*fl.getCurrentPosition() + fr.getCurrentPosition()
                    + -1*br.getCurrentPosition() + bl.getCurrentPosition())) ) / count;
        }
        else if(direction < 0)
        {
            average = (((fl.getCurrentPosition() + -1*fr.getCurrentPosition()
                    + br.getCurrentPosition() + -1*bl.getCurrentPosition())))  / count;
        }
        return average;
    }

    public void resetEncoders() {

        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        opMode.idle();

        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        opMode.idle();

        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        opMode.idle();

        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        opMode.idle();

        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        opMode.idle();

        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        opMode.idle();

        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        opMode.idle();

        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        opMode.idle();

    }
    public void setMotorsPower(double power)
    {

        fl.setPower(power);
        fr.setPower(power);
        br.setPower(power);
        bl.setPower(power);
    }

    public void setStrafePower(double power)
    {
        fr.setPower(-power);
        br.setPower(power);
        fl.setPower(power);
        bl.setPower(-power);
    }

    public void snowWhite () {

        fr.setPower(0);
        fl.setPower(0);
        br.setPower(0);
        bl.setPower(0);
    }

    public double getTargetPercentile(double reading) {
        return Math.abs(getEncoderAverage() / reading);
    }

    public void forwards(double power) {
        setMotorsPower(Math.abs(power));

    }

    public void backwards(double power) {
        setMotorsPower(-Math.abs(power));

    }public void encoderMove(double power, double distance, double runtime)
        {
            resetEncoders();
            ElapsedTime time = new ElapsedTime();

            double initEncoder = getEncoderAverage();

            time.reset();



            while (Math.abs(getEncoderAverage() - initEncoder) < distance && time.seconds() < runtime && opMode.opModeIsActive()) {
                setMotorsPower(power);

                opMode.telemetry.addData("Encoder distance left", (distance - getEncoderAverage()));
                opMode.telemetry.update();

            }
            snowWhite();

        }

        public void strafeMove(double target, double power, double timeout)
        {

            ElapsedTime runtime = new ElapsedTime();
            resetEncoders();

            double averageStrafe = 0.0;
            double speed = power * .6;


            while(Math.abs(averageStrafe) < target && runtime.seconds() < timeout && opMode.opModeIsActive())
            {

                setStrafePower(speed);
                averageStrafe = getStrafeEncoderAverage(power);


                if (Math.abs(speed) < Math.abs(power)){
                    speed = speed * 1.05;
                }

            }


    }
}
