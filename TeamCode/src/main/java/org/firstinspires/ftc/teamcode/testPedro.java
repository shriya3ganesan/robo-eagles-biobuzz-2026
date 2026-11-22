package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import static com.pedropathing.api.Paths.line;

@Autonomous(name = "Pedro Visualizer Auto", group = "Autonomous")
public class testPedro extends OpMode {

    private Follower follower;
    private final ElapsedTime pathTimer = new ElapsedTime();

    // Pedro 3 PoseFactory configured in degrees to match your visualizer
    private final PoseFactory p = PoseFactory.degrees();

    // 1. Define Poses from your Visualizer screenshots
    private final Pose startPose          = p.of(27.92, 11.79, 50);
    private final Pose rightAfterShooting = p.of(8.12, 11.44, 50);
    private final Pose shoot2             = p.of(27.92, 11.79, 50);
    private final Pose toFlower           = p.of(13.95, 46.21, 0);
    private final Pose flower             = p.of(7.85, 46.36, 0);
    private final Pose justAWeeBit        = p.of(10.90, 46.28, 0);
    private final Pose pathwayShoot3      = p.of(31.90, 122.83, 310);
    private final Pose toFlower2          = p.of(47.32, 129.79, 270);
    private final Pose flowerrrr          = p.of(46.93, 132.39, 270);
    private final Pose shooting4          = p.of(31.80, 122.92, 310);
    private final Pose park               = p.of(14.91, 123.02, 270);

    // 2. Define Paths
    private Path path1_RightAfterShooting;
    private Path path2_Shoot2;
    private Path path3_ToFlower;
    private Path path4_Flower;
    private Path path5_JustAWeeBit;
    private Path path6_PathwayShoot3;
    private Path path7_ToFlower2;
    private Path path8_Flowerrrr;
    private Path path9_Shooting4;
    private Path path10_Park;

    // 3. State Machine
    private enum AutoState {
        PATH_1_RIGHT_AFTER_SHOOTING,
        PATH_2_SHOOT_2,
        PATH_3_TO_FLOWER,
        PATH_4_FLOWER,
        PATH_5_JUST_A_WEE_BIT,
        PATH_6_PATHWAY_SHOOT_3,
        PATH_7_TO_FLOWER_2,
        PATH_8_FLOWERRRR,
        PATH_9_SHOOTING_4,
        PATH_10_PARK,
        DONE
    }

    private AutoState currentState = AutoState.PATH_1_RIGHT_AFTER_SHOOTING;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        buildPaths();
    }

    private void buildPaths() {
        // Each segment uses Constant heading interpolation matching your screenshots
        path1_RightAfterShooting = line(startPose, rightAfterShooting)
                .constant(rightAfterShooting);

        path2_Shoot2 = line(rightAfterShooting, shoot2)
                .constant(shoot2);

        path3_ToFlower = line(shoot2, toFlower)
                .constant(toFlower);

        path4_Flower = line(toFlower, flower)
                .constant(flower);

        path5_JustAWeeBit = line(flower, justAWeeBit)
                .constant(justAWeeBit);

        path6_PathwayShoot3 = line(justAWeeBit, pathwayShoot3)
                .constant(pathwayShoot3);

        path7_ToFlower2 = line(pathwayShoot3, toFlower2)
                .constant(toFlower2);

        path8_Flowerrrr = line(toFlower2, flowerrrr)
                .constant(flowerrrr);

        path9_Shooting4 = line(flowerrrr, shooting4)
                .constant(shooting4);

        path10_Park = line(shooting4, park)
                .constant(park);
    }

    @Override
    public void start() {
        pathTimer.reset();
        setState(AutoState.PATH_1_RIGHT_AFTER_SHOOTING);
        follower.follow(path1_RightAfterShooting);
    }

    @Override
    public void loop() {
        // Must be called every loop to run Pedro's vector controller
        follower.update();
        updateStateMachine();

        telemetry.addData("State", currentState);
        telemetry.addData("X", "%.2f", follower.pose().x());
        telemetry.addData("Y", "%.2f", follower.pose().y());
        telemetry.addData("Heading (deg)", "%.1f", Math.toDegrees(follower.pose().heading()));
        telemetry.update();
    }

    private void updateStateMachine() {
        switch (currentState) {
            case PATH_1_RIGHT_AFTER_SHOOTING:
                if (!follower.isBusy()) {
                    follower.follow(path2_Shoot2);
                    setState(AutoState.PATH_2_SHOOT_2);
                }
                break;

            case PATH_2_SHOOT_2:
                if (!follower.isBusy()) {
                    // Add shooting action or timer check here if needed
                    follower.follow(path3_ToFlower);
                    setState(AutoState.PATH_3_TO_FLOWER);
                }
                break;

            case PATH_3_TO_FLOWER:
                if (!follower.isBusy()) {
                    follower.follow(path4_Flower);
                    setState(AutoState.PATH_4_FLOWER);
                }
                break;

            case PATH_4_FLOWER:
                if (!follower.isBusy()) {
                    // Add intake action here if needed
                    follower.follow(path5_JustAWeeBit);
                    setState(AutoState.PATH_5_JUST_A_WEE_BIT);
                }
                break;

            case PATH_5_JUST_A_WEE_BIT:
                if (!follower.isBusy()) {
                    follower.follow(path6_PathwayShoot3);
                    setState(AutoState.PATH_6_PATHWAY_SHOOT_3);
                }
                break;

            case PATH_6_PATHWAY_SHOOT_3:
                if (!follower.isBusy()) {
                    // Add shooting action here if needed
                    follower.follow(path7_ToFlower2);
                    setState(AutoState.PATH_7_TO_FLOWER_2);
                }
                break;

            case PATH_7_TO_FLOWER_2:
                if (!follower.isBusy()) {
                    follower.follow(path8_Flowerrrr);
                    setState(AutoState.PATH_8_FLOWERRRR);
                }
                break;

            case PATH_8_FLOWERRRR:
                if (!follower.isBusy()) {
                    // Add intake action here if needed
                    follower.follow(path9_Shooting4);
                    setState(AutoState.PATH_9_SHOOTING_4);
                }
                break;

            case PATH_9_SHOOTING_4:
                if (!follower.isBusy()) {
                    // Add shooting action here if needed
                    follower.follow(path10_Park);
                    setState(AutoState.PATH_10_PARK);
                }
                break;

            case PATH_10_PARK:
                if (!follower.isBusy()) {
                    setState(AutoState.DONE);
                }
                break;

            case DONE:
                // Routine complete
                break;
        }
    }

    private void setState(AutoState newState) {
        currentState = newState;
        pathTimer.reset();
    }
}

