package org.usfirst.frc.team3455.robot;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Encoder;

/**
 * This is a demo program showing the use of the CameraServer class. With start
 * automatic capture, there is no opportunity to process the image. Look at the
 * IntermediateVision sample for how to process the image before sending it to
 * the FRC PC Dashboard.
 */

public class Robot extends IterativeRobot {
	
	Encoder encoder;
	NetworkTable table;
	double data = 0.0;

	//RobotDrive myDrive;
	Joystick first, second;
	
	// Channels for the wheels
	// final int frontLeftChannel = 4;
	// final int rearLeftChannel = 2;
	// final int frontRightChannel = 3;
	// final int rearRightChannel = 1;

	Talon frontLeft;
	Talon frontRight;
	Talon backLeft;
	Talon backRight;

	double scalingFactorEncoder = 2.5;
	double errorFix = 0.05;
	
	double yAxis1;
	double yAxis2;
	double leftPower;
	double rightPower;
	
	// update every 5 milliseconds
	double kUpdatePeriod = 0.005;

	public void robotInit() {
		
		table = NetworkTable.getTable("SmartDashboard");
    	CameraServer.getInstance().startAutomaticCapture();
		
    	encoder = new Encoder(1, 2, true, EncodingType.k4X);
		// CameraServer.getInstance().startAutomaticCapture();
		/*
		 * new Thread(() -> { UsbCamera camera =
		 * CameraServer.getInstance().startAutomaticCapture();
		 * camera.setResolution(640, 480);
		 * 
		 * CvSink cvSink = CameraServer.getInstance().getVideo(); CvSource
		 * outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);
		 * 
		 * Mat source = new Mat(); Mat output = new Mat();
		 * 
		 * while(!Thread.interrupted()) { cvSink.grabFrame(source);
		 * outputStream.putFrame(source); } }).start();
		 * NetworkTable.setServerMode(); NetworkTable.setTeam(3455); table =
		 * NetworkTable.getTable("SmartDashboard");
		 */
    	encoder.setSamplesToAverage(5);
    	encoder.setDistancePerPulse(1.0/360 * scalingFactorEncoder);

    	first = new Joystick(1);
    	second = new Joystick(0);

		frontLeft = new Talon(5);
		frontRight = new Talon(8);
		backLeft = new Talon(6);
		backRight = new Talon(7);
		
		frontLeft.setInverted(true);
		backLeft.setInverted(true);
		
		
	}
	
	public void autonomousInit() {
		encoder.reset();
//		
	}
	
	public void autonomousPeriodic() {
		table.putNumber("encoder", encoder.getDistance());
		if(encoder.getDistance() < 1.0 - errorFix) {
			tankDrive(-0.3, -0.3);
		} else {
			tankDrive(0.0, 0.0);
		}
	}

	public void teleopInit() {
		encoder.reset();
	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		while (isOperatorControl() && isEnabled()) {
			
//			if(first.getRawButton(0)) {
//				Thread cameraThread = new Thread(() -> {
//					double turn = table.getNumber("Turning Value", 99.9);
//					boolean turning = table.getBoolean("Turning", false);
//			
//					while(turning){
//						frontLeft.set(turn);
//						frontRight.set(-turn);
//						backLeft.set(turn);
//						backRight.set(-turn);
//						turning = table.getBoolean("Turning", false);
//						turn = table.getNumber("Turning Value", 99.9);
//					}
//		        });
//				cameraThread.setName("cameraThread");
//				
//				if(getThreadByName("cameraThread") != null) {
//					cameraThread.start();
//				}
//			}
//			
			
			table.putNumber("encoder", encoder.getDistance());
			yAxis1 = first.getRawAxis(1);
			yAxis2 = first.getRawAxis(5);
			table.putNumber("Y1", yAxis1);
    		table.putNumber("Y2", yAxis2);
    		tankDrive(yAxis1, yAxis2);
    		Timer.delay(0.01);
    		// wait to avoid hogging CPU cycles
		}
	}

	public void tankDrive(double leftValue, double rightValue) {

		// square the inputs (while preserving the sign) to increase fine
		// control while permitting
		// full power
		leftValue = limit((leftValue));
		rightValue = limit((rightValue));
		
		frontLeft.set(thresh(leftValue));
		backLeft.set(thresh(leftValue));
		frontRight.set(thresh(rightValue));
		backRight.set(thresh(rightValue));
		
	}

	protected static double limit(double num) {
		if (num > 1.0) {
			return 1.0;
		}
		if (num < -1.0) {
			return -1.0;
		}
		return num;
	}
	
	protected static double thresh(double num) {
		if(num < 0.25 && num > -0.25) {
			return 0.0;
		}
		return num;
	}
	
	public Thread getThreadByName(String threadName) {
	    for (Thread t : Thread.getAllStackTraces().keySet()) {
	        if (t.getName().equals(threadName)) return t;
	    }
	    return null;
	}

	public void testPeriodic() {
		
	}

}