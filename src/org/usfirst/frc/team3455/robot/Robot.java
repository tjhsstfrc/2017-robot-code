package org.usfirst.frc.team3455.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive class.
 */
public class Robot extends SampleRobot {
	
    RobotDrive robotDrive;
    Joystick driveStick;
    Joystick chainStick;

    // Channels for the wheels
//    final int frontLeftChannel	= 4;
//    final int rearLeftChannel	= 2;
//    final int frontRightChannel	= 3;
//    final int rearRightChannel	= 1;
    
    Talon frontLeft;
    Talon frontRight;
    Talon backLeft;
    Talon backRight;
    Talon chainCim1;
    Talon chainCim2;
    
    Encoder chainEncoder;
    
    // The channel on the driver station that the joystick is connected to
    final int driveJoystickChannel	= 0;
    final int chainJoystickChannel = 1;
    
    int autoCount = 0;
    double drivePowerMultiplier = 1.0;


    public Robot() {
//    	robotDrive = new RobotDrive(frontLeftChannel, rearLeftChannel, frontRightChannel, rearRightChannel);
//    	robotDrive.setExpiration(0.1);
//        robotDrive.setInvertedMotor(MotorType.kFrontLeft, true);	// invert the left side motors
//    	robotDrive.setInvertedMotor(MotorType.kRearLeft, true);		// you may need to change or remove this to match your robot
    	frontLeft = new Talon(2);
    	frontRight = new Talon(5);
    	backLeft = new Talon(3);
    	backRight = new Talon(4);
    	chainCim1 = new Talon(0);
    	chainCim2 = new Talon(1);
        driveStick = new Joystick(driveJoystickChannel);
        chainStick = new Joystick(chainJoystickChannel);
        
        chainEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k1X);
    	//winchEncoder.setMaxPeriod(.1);
    	chainEncoder.setMinRate(10);
    	//winchEncoder.setDistancePerPulse(5);
    	chainEncoder.setReverseDirection(true);
    	chainEncoder.setSamplesToAverage(7);     
    }
    public void autonomous() {  
    	autoCount = 0;
        while (isAutonomous() && isEnabled()) {
        	if(autoCount<30){
            frontLeft.set(-.3);
            frontRight.set(-.3);
            backLeft.set(-.3);
            backRight.set(-.3);
            autoCount++;
        	}
        	else if(autoCount<65){
        		frontLeft.set(.3);
                frontRight.set(-.3);
                backLeft.set(.3);
                backRight.set(-.3);
                autoCount++;
        	}
        	else if(autoCount<95){
        		frontLeft.set(.3);
                frontRight.set(.3);
                backLeft.set(.3);
                backRight.set(.3);
                autoCount++;
        	}
        	else{
        		frontLeft.set(0);
        		frontRight.set(0);
        		backLeft.set(0);
        		backRight.set(0);
        	}
            Timer.delay(0.05);
        }
    } 

    /**
     * Runs the motors with Mecanum drive.
     */
    public void operatorControl() {
    	double ctrlThresh = .2;
    	double minPower = .05;
    	double maxPower = .75;
    	double recip = (1 - ctrlThresh);
    	double mult = (maxPower - minPower);
    	double forward;
    	double strafe;
    	double rotate;
    	double fAxis;
    	double sAxis;
    	double rAxis;
    	double fDir;
    	double sDir;
    	double rDir;
    	double frontLeftPower;
    	double frontRightPower;
    	double backLeftPower;
    	double backRightPower;
    	double chainPower;
    
    	
        //robotDrive.setSafetyEnabled(false);
        while (isOperatorControl() && isEnabled()) {
        	
        ctrlThresh = .2;
   	     recip = 1-ctrlThresh;
   	     fAxis = driveStick.getRawAxis(1);
         sAxis = driveStick.getRawAxis(0);
         rAxis = -1 * driveStick.getRawAxis(4);

   	     fDir = fAxis/Math.abs(fAxis);
   	     sDir = sAxis/Math.abs(sAxis);
   	     rDir = rAxis/Math.abs(rAxis);
   	     
   	     forward = 0;
   	     rotate = 0;
   	     strafe = 0;
   	     
   	     if(Math.abs(fAxis) > ctrlThresh)
   		{forward = (fAxis-ctrlThresh*fDir)/recip;}
   	     if(Math.abs(sAxis) > ctrlThresh)
   		{strafe = (sAxis-ctrlThresh*sDir)/recip;}
   	     if(Math.abs(rAxis) > ctrlThresh)
   		{rotate = (rAxis-ctrlThresh*rDir)/recip;}
              
   	     
   	     frontLeftPower = (-1*forward - rotate + strafe);
   	     fDir = frontLeftPower/Math.abs(frontLeftPower);
                frontLeftPower = (Math.abs(frontLeftPower*mult) + minPower) * fDir;
               
                frontRightPower = (forward - rotate + strafe);
                fDir = frontRightPower/Math.abs(frontRightPower);
                frontRightPower = (Math.abs(frontRightPower*mult) + minPower) * fDir;

                backLeftPower = (-1*forward - rotate - strafe);
                fDir = backLeftPower/Math.abs(backLeftPower);
                backLeftPower = (Math.abs(backLeftPower*mult) + minPower) * fDir;

                backRightPower = (forward - rotate - strafe);
                fDir = backRightPower/Math.abs(backRightPower);
                backRightPower = (Math.abs(backRightPower*mult) + minPower) * fDir;

                frontRight.set(frontRightPower*drivePowerMultiplier);
                frontLeft.set(frontLeftPower*drivePowerMultiplier);
               backRight.set(backRightPower*drivePowerMultiplier);
                backLeft.set(backLeftPower*drivePowerMultiplier); 

               chainPower = chainStick.getRawAxis(1);
               if(Math.abs(chainPower) > .05){
               chainCim1.set(-1*chainPower);
               chainCim2.set(-1*chainPower);
               }
               
               if(driveStick.getRawButton(2)){
            	   
            	   drivePowerMultiplier = 0.3;
               }
               if(driveStick.getRawButton(1)){
            	   
               drivePowerMultiplier = 1.0;
               }
        	
            
            Timer.delay(0.02);	// wait to avoid hogging CPU cycles
        }
    }
    
}
