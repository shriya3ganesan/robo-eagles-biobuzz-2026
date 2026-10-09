package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp(name = "Color Sensor Test")
public class ColorSensor_FirstDraft extends LinearOpMode {

    ColorSensor George;

    @Override
    public void runOpMode() {

        George = hardwareMap.get(ColorSensor.class, "G");

        boolean isRedAlliance = true;

        waitForStart();

        while (opModeIsActive()) {

            // Store RGB sensor readings in an array
            int[] rgb = {
                    George.red(),
                    George.green(),
                    George.blue()
            };

            boolean OurNectar = false;
            boolean OppNectar = false;

            if (rgb[0] > rgb[2] * 1.5) {
                // Red detected
                if (isRedAlliance) {
                    OurNectar = true;
                }
                else {
                    OppNectar = true;
                }
            }
            else if (rgb[2] > rgb[0] * 1.5) {
                // Blue detected
                if (isRedAlliance) {
                    OppNectar = true;
                }
                else {
                    OurNectar = true;
                }
            }

            // Display results on Driver Station
            telemetry.addData("Red", rgb[0]);
            telemetry.addData("Green", rgb[1]);
            telemetry.addData("Blue", rgb[2]);

            telemetry.addData("Our Nectar", OurNectar);
            telemetry.addData("Opp Nectar", OppNectar);

            telemetry.update();
        }
    }
}