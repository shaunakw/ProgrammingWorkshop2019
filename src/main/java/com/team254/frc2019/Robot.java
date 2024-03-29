/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team254.frc2019;

import com.team254.frc2019.loops.Looper;
import com.team254.frc2019.subsystems.Drive;

import edu.wpi.first.wpilibj.TimedRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private final Looper mEnabledLooper = new Looper();
    private final Looper mDisabledLooper = new Looper();

    private final SubsystemManager mSubsystemManager = SubsystemManager.getInstance();

    private final Drive mDrive = Drive.getInstance();

    @Override
    public void robotInit() {
        mSubsystemManager.setSubsystems(mDrive);
        mSubsystemManager.registerEnabledLoops(mEnabledLooper);
        mSubsystemManager.registerDisabledLoops(mDisabledLooper);
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void autonomousInit() {
        mDrive.stop();

        mDisabledLooper.stop();
        mEnabledLooper.start();
    }

    @Override
    public void teleopInit() {
        mDisabledLooper.stop();
        mEnabledLooper.start();
    }

    @Override
    public void teleopPeriodic() {

    }

    @Override
    public void disabledInit() {
        mDrive.stop();

        mEnabledLooper.stop();
        mDisabledLooper.start();
    }
}
