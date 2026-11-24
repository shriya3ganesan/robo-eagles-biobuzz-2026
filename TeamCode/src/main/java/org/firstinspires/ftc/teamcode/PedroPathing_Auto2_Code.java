package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.JaxonB_pedro.Constants;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "RoboEagles Pedro Auto")
public class PedroPathing_Auto2_Code extends OpMode {

    // PEDRO PATHING
    private Follower follower;

    // ROBOT HARDWARE
    private DcMotor intake;
    private DcMotor outtake;
    private CRServo transfer;

    // Mechanism powers - tune these
    private static final double INTAKE_POWER = 0.75;
    private static final double OUTTAKE_POWER = -0.53;
    private static final double TRANSFER_POWER = 1.0;

    // Shooting timing
    private static final long SHOOT_TIME_MS = 3500;
    private static final long SPINUP_MS = 800;

    private int state = 0;
    private long shootStart = 0;

    // Coordinates taken from screenshots (X, Y, Heading in radians)
    private final Pose shoot2 = new Pose(27.92, 11.79, Math.toRadians(50));
    private final Pose afterShooting = new Pose(8.12, 11.44, Math.toRadians(50));
    private final Pose toFlower = new Pose(13.95, 46.21, Math.toRadians(0));
    private final Pose flower = new Pose(7.85, 46.36, Math.toRadians(0));
    private final Pose weeBit = new Pose(10.90, 46.28, Math.toRadians(0));
    private final Pose toFlower2 = new Pose(47.32, 129.79, Math.toRadians(270));
    private final Pose flower2 = new Pose(46.93, 132.39, Math.toRadians(270));
    private final Pose pathwayShoot3 = new Pose(31.90, 122.83, Math.toRadians(310));
    private final Pose shooting4 = new Pose(31.80, 122.92, Math.toRadians(310));
    private final Pose park = new Pose(14.91, 123.02, Math.toRadians(270));

    private Path makePath(Pose from, Pose to) {
        Path path = new Path(new BezierLine(from, to));
        path.setLinearHeadingInterpolation(from.getHeading(), to.getHeading());
        return path;
    }

    private void driveTo(Pose from, Pose to) {
        follower.followPath(makePath(from, to), true);
    }

    // Intake mechanism
    private void intakeOn() {
        intake.setPower(INTAKE_POWER);
    }

    private void intakeOff() {
        intake.setPower(0);
    }

    // Shooter with 800 ms spin-up
    private void startShooting() {
        intakeOff();
        outtake.setPower(OUTTAKE_POWER);
        transfer.setPower(0);
        shootStart = System.currentTimeMillis();
    }

    private boolean updateShooting() {
        long elapsed = System.currentTimeMillis() - shootStart;
        if (elapsed >= SPINUP_MS) {
            transfer.setPower(TRANSFER_POWER);
        }
        if (elapsed >= SHOOT_TIME_MS) {
            stopShooting();
            return true;
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

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setStartingPose(shoot2);

        intake = hardwareMap.get(DcMotor.class, "Int");
        outtake = hardwareMap.get(DcMotor.class, "Out");
        transfer = hardwareMap.get(CRServo.class, "Trn");

        intake.setDirection(DcMotor.Direction.FORWARD);
        outtake.setDirection(DcMotor.Direction.FORWARD);

        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outtake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        stopMechanisms();
        telemetry.addLine("RoboEagles Pedro Auto Ready");
        telemetry.update();
    }

    @Override
    public void start() {
        state = 0;
        startShooting();
    }

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

            case 4: // JUTS A WEE BIT
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
                    follower.breakFollowing();
                    state = 11;
                }
                break;

            case 11: // FINISHED
                break;
        }

        telemetry.addData("State", state);
        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Intake Power", intake.getPower());
        telemetry.addData("Shooter Power", outtake.getPower());
        telemetry.update();
    }

    @Override
    public void stop() {
        stopMechanisms();
        follower.breakFollowing();
    }
}