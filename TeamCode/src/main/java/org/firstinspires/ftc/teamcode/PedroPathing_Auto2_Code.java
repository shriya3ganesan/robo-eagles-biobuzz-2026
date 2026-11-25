package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.JaxonB_pedro.Constants;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.paths.Path;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "RoboEagles Pedro Auto")
public class PedroPathing_Auto2_Code extends OpMode {

    // PEDRO PATHING
    private Follower follower;

    private final PoseFactory p = PoseFactory.degrees();

    // ROBOT HARDWARE
    private DcMotor intake;
    private DcMotor outtake;
    private CRServo transfer;

    // MECHANISM POWERS
    private static final double INTAKE_POWER = 0.75;
    private static final double OUTTAKE_POWER = -0.53;
    private static final double TRANSFER_POWER = 1.0;

    // SHOOTING TIMING
    private static final long SHOOT_TIME_MS = 3500;
    private static final long SPINUP_MS = 800;

    private int state = 0;
    private long shootStart = 0;

    // COORDINATES (X, Y, Heading in degrees)

    private final Pose shoot2 =
            p.of(27.92, 11.79, 50);

    private final Pose afterShooting =
            p.of(8.12, 11.44, 50);

    private final Pose toFlower =
            p.of(13.95, 46.21, 0);

    private final Pose flower =
            p.of(7.85, 46.36, 0);

    private final Pose weeBit =
            p.of(10.90, 46.28, 0);

    private final Pose toFlower2 =
            p.of(47.32, 129.79, 270);

    private final Pose flower2 =
            p.of(46.93, 132.39, 270);

    private final Pose pathwayShoot3 =
            p.of(31.90, 122.83, 310);

    private final Pose shooting4 =
            p.of(31.80, 122.92, 310);

    private final Pose park =
            p.of(14.91, 123.02, 270);


    // PATH CREATION

    private Path makePath(Pose from, Pose to) {
        return Paths.line(from, to)
                .linear(from, to);
    }

    private void driveTo(Pose from, Pose to) {
        follower.follow(makePath(from, to));
    }


    // INTAKE MECHANISM

    private void intakeOn() {
        intake.setPower(INTAKE_POWER);
    }

    private void intakeOff() {
        intake.setPower(0);
    }


    // SHOOTING MECHANISM

    private void startShooting() {
        intakeOff();

        outtake.setPower(OUTTAKE_POWER);
        transfer.setPower(0);

        shootStart = System.currentTimeMillis();
    }

    private boolean updateShooting() {

        long elapsed =
                System.currentTimeMillis() - shootStart;

        if (elapsed >= SHOOT_TIME_MS) {
            stopShooting();
            return true;
        }

        if (elapsed >= SPINUP_MS) {
            transfer.setPower(TRANSFER_POWER);
        }

        return false;
    }

    private void stopShooting() {
        outtake.setPower(0);
        transfer.setPower(0);
    }

    private void stopMechanisms() {
        intakeOff();
        stopShooting();
    }


    // INITIALIZATION

    @Override
    public void init() {

        follower = Constants.create(hardwareMap);

        follower.setPose(shoot2);
        follower.holdEnd.set(true);

        intake = hardwareMap.get(
                DcMotor.class, "Int");

        outtake = hardwareMap.get(
                DcMotor.class, "Out");

        transfer = hardwareMap.get(
                CRServo.class, "Trn");

        intake.setDirection(
                DcMotor.Direction.FORWARD);

        outtake.setDirection(
                DcMotor.Direction.FORWARD);

        intake.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        outtake.setZeroPowerBehavior(
                DcMotor.ZeroPowerBehavior.BRAKE);

        stopMechanisms();

        telemetry.addLine("RoboEagles Pedro Auto Ready");
        telemetry.update();
    }


    // START AUTONOMOUS

    @Override
    public void start() {

        state = 0;
        startShooting();
    }


    // AUTONOMOUS STATE MACHINE

    @Override
    public void loop() {

        follower.update();

        switch (state) {

            case 0: // SHOOT 2

                if (updateShooting()) {
                    driveTo(shoot2, afterShooting);
                    state = 1;
                }

                break;


            case 1: // RIGHT AFTER SHOOTING

                if (!follower.isBusy()) {
                    intakeOn();
                    driveTo(afterShooting, toFlower);
                    state = 2;
                }

                break;


            case 2: // TO FLOWER

                if (!follower.isBusy()) {
                    driveTo(toFlower, flower);
                    state = 3;
                }

                break;


            case 3: // FLOWER

                if (!follower.isBusy()) {
                    driveTo(flower, weeBit);
                    state = 4;
                }

                break;


            case 4: // JUST A WEE BIT

                if (!follower.isBusy()) {
                    intakeOff();
                    driveTo(weeBit, toFlower2);
                    state = 5;
                }

                break;


            case 5: // TO FLOWER 2

                if (!follower.isBusy()) {
                    intakeOn();
                    driveTo(toFlower2, flower2);
                    state = 6;
                }

                break;


            case 6: // FLOWERRRR

                if (!follower.isBusy()) {
                    intakeOff();
                    driveTo(flower2, pathwayShoot3);
                    state = 7;
                }

                break;


            case 7: // PATHWAY SHOOT 3

                if (!follower.isBusy()) {
                    driveTo(pathwayShoot3, shooting4);
                    state = 8;
                }

                break;


            case 8: // SHOOTING 4 POSITION

                if (!follower.isBusy()) {
                    startShooting();
                    state = 9;
                }

                break;


            case 9: // SHOOT FOR 3.5 SECONDS

                if (updateShooting()) {
                    driveTo(shooting4, park);
                    state = 10;
                }

                break;


            case 10: // PARK

                if (!follower.isBusy()) {
                    stopMechanisms();
                    follower.stop();
                    follower.update();
                    state = 11;
                }

                break;


            case 11: // FINISHED

                break;
        }


        // TELEMETRY

        telemetry.addData("State", state);

        telemetry.addData(
                "X", follower.pose().x());

        telemetry.addData(
                "Y", follower.pose().y());

        telemetry.addData(
                "Heading",
                Math.toDegrees(follower.pose().heading()));

        telemetry.addData(
                "Intake Power", intake.getPower());

        telemetry.addData(
                "Shooter Power", outtake.getPower());

        telemetry.update();
    }


    // STOP AUTONOMOUS

    @Override
    public void stop() {

        stopMechanisms();

        if (follower != null) {
            follower.stop();
            follower.update();
        }
    }
}
