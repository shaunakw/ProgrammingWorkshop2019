package com.team254.frc2019.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import com.team254.frc2019.Constants;
import com.team254.frc2019.Kinematics;
import com.team254.lib.geometry.Twist2d;
import com.team254.lib.util.DriveSignal;
import com.team254.lib.util.Util;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class Drive extends Subsystem {
    private PeriodicIO mPeriodicIO = new PeriodicIO();

    private static Drive mInstance = null;

    private final XboxController mController;
    private CANSparkMax mRightMaster, mRightSlave, mLeftMaster, mLeftSlave;

    private Drive() {
        mController = new XboxController(Constants.kXboxControllerPort);

        mRightMaster = new CANSparkMax(Constants.kDriveRightMasterId, MotorType.kBrushless);
        mRightSlave = new CANSparkMax(Constants.kDriveRightSlaveId, MotorType.kBrushless);
        mLeftMaster = new CANSparkMax(Constants.kDriveLeftMasterId, MotorType.kBrushless);
        mLeftSlave = new CANSparkMax(Constants.kDriveLeftSlaveId, MotorType.kBrushless);

        mRightSlave.follow(mRightMaster);
        mLeftSlave.follow(mLeftMaster);
    }

    public static Drive getInstance() {
        if (mInstance == null) {
            mInstance = new Drive();
        }
        return mInstance;
    }

    private static class PeriodicIO {
        double right_demand;
        double left_demand;
    }

    public synchronized void setOpenLoop(DriveSignal signal) {
        mPeriodicIO.right_demand = signal.getRight();
        mPeriodicIO.left_demand = signal.getLeft();
    }

    public boolean isHighGear() {
        return false;
    }

    @Override
    public synchronized void readPeriodicInputs() {
        double throttle = mController.getY(Hand.kLeft);
        double turn = mController.getX(Hand.kRight);
        setCheesyishDrive(throttle, turn, false);
    }

    public synchronized void setCheesyishDrive(double throttle, double wheel, boolean quickTurn) {
        if (Util.epsilonEquals(throttle, 0.0, 0.04)) {
            throttle = 0.0;
        }

        if (Util.epsilonEquals(wheel, 0.0, 0.035)) {
            wheel = 0.0;
        }

        final double kWheelGain = 0.05;
        final double kWheelNonlinearity = 0.05;
        final double denominator = Math.sin(Math.PI / 2.0 * kWheelNonlinearity);
        // Apply a sin function that's scaled to make it feel better.
        if (!quickTurn) {
            wheel = Math.sin(Math.PI / 2.0 * kWheelNonlinearity * wheel);
            wheel = Math.sin(Math.PI / 2.0 * kWheelNonlinearity * wheel);
            wheel = wheel / (denominator * denominator) * Math.abs(throttle);
        }

        wheel *= kWheelGain;
        DriveSignal signal = Kinematics.inverseKinematics(new Twist2d(throttle, 0.0, wheel));
        double scaling_factor = Math.max(1.0, Math.max(Math.abs(signal.getLeft()), Math.abs(signal.getRight())));
        setOpenLoop(new DriveSignal(signal.getLeft() / scaling_factor, signal.getRight() / scaling_factor));
    }

    @Override
    public synchronized void writePeriodicOutputs() {
        mRightMaster.set(mPeriodicIO.right_demand);
        mLeftMaster.set(mPeriodicIO.left_demand);
    }

    @Override
    public void stop() {
        setOpenLoop(DriveSignal.NEUTRAL);
    }

    @Override
    public boolean checkSystem() {
        return true;
    }

    @Override
    public void outputTelemetry() {

    }
}