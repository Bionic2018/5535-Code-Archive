package org.usfirst.frc.team5535.robot;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.UsbCamera;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
@SuppressWarnings({ "deprecation" })
public class Robot extends IterativeRobot {


	final String defaultAuto = "Str8 8"; // makes a string for going forward 8 sec
	final String customAuto = "Left Vision"; // makes a string for vision
	final String customAuto1 = "Right Vision"; // makes a string for vision
	String autoSelected; // Makes string for auton selection
	SendableChooser<String> chooser = new SendableChooser<>(); /// makes a sendable chooser auton  


	Joystick stick = new Joystick(0);
	private DifferentialDrive DemoDrive;
	Joystick xBox;
	WPI_TalonSRX Left1, Left2, Right1, Right2;
	JoystickButton xBoxa, xBoxselect1, xBoxstart1, xBoxx, xBoxy, xBoxb, xBoxlb, xBoxrb;
	DoubleSolenoid double1, double2;
	Compressor Comp = new Compressor(0);
	Spark Motor1;
	Talon winch;
	NetworkTable table = NetworkTable.getTable("limelight");
	UsbCamera Back_Camera;
	Timer timer;
	double loop, t;



	public enum VisionStage {
		kNoTargetFound,
		kFoundTarget, 
		kCenterOnTarget,
		kDriveForward,
		kDriveForward1,
		kDriveForward2,
		kTurn,
		kTurn1,
		kTurn2,
		kDropCube,
		kDone
	}



	VisionStage currentStage = VisionStage.kNoTargetFound;







	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {

		//sendable chooser
		chooser.addDefault("Str8 8", defaultAuto); // defines the str8 8 to default auto
		chooser.addObject("Left Vision", customAuto); // defines Left Vision to custom auto
		chooser.addObject("Right Vision", customAuto1); // defines Right Vision to customauto1
		SmartDashboard.putData("Auto choices", chooser); // puts the data on smart dashboard

		//winch motor
		winch = new Talon(1);

		//cameras
		Back_Camera = CameraServer.getInstance().startAutomaticCapture();

		//xBox controller
		xBox = new Joystick(0);

		//buttons
		xBoxa = new JoystickButton(xBox, 1);
		xBoxb = new JoystickButton(xBox, 2);
		xBoxx = new JoystickButton(xBox, 3);
		xBoxy = new JoystickButton(xBox, 4);
		xBoxlb = new JoystickButton(xBox, 5);
		xBoxrb = new JoystickButton(xBox, 6);
		xBoxselect1 = new JoystickButton(xBox, 7);
		xBoxstart1 = new JoystickButton(xBox, 8);

		//compressor
		Comp.setClosedLoopControl(false);

		//Talons
		Left1 = new WPI_TalonSRX(1);
		Left2 = new WPI_TalonSRX(2);
		Right1 = new WPI_TalonSRX(4); 
		Right2 = new WPI_TalonSRX(3);

		//Climber 
		Motor1 = new Spark(0);

		//talon groups
		SpeedControllerGroup m_left = new SpeedControllerGroup(Left1, Left2);
		SpeedControllerGroup m_right = new SpeedControllerGroup(Right1, Right2);

		//Drivetrain
		DemoDrive = new DifferentialDrive(m_left, m_right);

		//Solenoids
		double1 = new DoubleSolenoid(0,1);
		double2 = new DoubleSolenoid(2,3);

	}

	/**
	 * This function is run once each time the robot enters autonomous mode
	 */
	@Override
	public void autonomousInit() {

		//timer
		t = 0.0; // sets up a control double
		timer.reset(); //resets timer
		timer.start(); //starts timer
		loop = 2.0; //control

		//sets current stage
		currentStage = VisionStage.kNoTargetFound;

		//sendable chooser
		autoSelected = chooser.getSelected(); // Gets whats selected from smrt dash
		System.out.println("Auto selected: " + autoSelected); // prints out autonnomus selected


	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {	

		//FMS data
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();

		//set limelight led/camera

		table.putNumber( "ledMode", 0);
		table.putNumber( "camMode", 0);

		//limelight table
		double targetx = table.getNumber("tx", 0); //Horizontal correction
		double targeta = table.getNumber("ta", 0); //Distance correction
		double targetv = table.getNumber("tv", 0); //Wether there is a target or not

		//stages
		/*
		kNoTargetFound,
		kFoundTarget, 
		kCenterOnTarget,
		kDriveForward,
		kDriveForward1,
		kDriveForward2,
		kTurn,
		kTurn1,
		kTurn2,
		kDropCube,
		kDone
		 */


		//start of sendable chooser
		switch(autoSelected) { 

		//str8 8 choice
		case defaultAuto:

			if(timer.get() > 1 && timer.get() < 6 && loop == 2.0  ){
				DemoDrive.arcadeDrive(.6, 0);
			}
			else if (timer.get() > 6) {

				loop = 5.6; //sets control loop
				DemoDrive.arcadeDrive(0, 0);

			}
			break; //ends first choice

			//left Vision choice
		case customAuto:

			if(gameData.length() > 0)
			{
				if(gameData.charAt(0) == 'L')
				{
					System.out.println("Left");
					//movement w/o vision



					//vision processing
					if (currentStage == VisionStage.kNoTargetFound) 
					{
						System.out.println( "No target found");


						if (targetv == 1) {
							currentStage = VisionStage.kCenterOnTarget;
						}
					}

					if (currentStage == VisionStage.kCenterOnTarget) {

						System.out.println("Centering on target");


						if (targetx > 1) {
							DemoDrive.arcadeDrive(.6, .35);

						}

						if (targetx < -2.5) {
							DemoDrive.arcadeDrive(.6, -.35);
						}

						if(targetx >= -10 && targetx <= 10 && targetv == 1) {

							currentStage = VisionStage.kDriveForward;
						}

					}

					if (currentStage == VisionStage.kDriveForward) {
						System.out.println("Driving Forward");


						if (targeta < 10)  
						{  DemoDrive.arcadeDrive(.6, -.01);

						}

						if (targetx < -2.5) {
							DemoDrive.arcadeDrive(.6, -.35);


						}
						if (targetx > 1) {
							DemoDrive.arcadeDrive(.6, .35);
						}

						if (targeta >= 10 && targeta <= 30 && targetx >= -10 && targetx <= 10 && targetv == 1) {

							currentStage = VisionStage.kDropCube;
						}

					}


					if (currentStage == VisionStage.kDropCube) {
						System.out.println("Dropping cube");

						if (targetv == 1 && targeta >= 10)
							double2.set(DoubleSolenoid.Value.kForward);

						currentStage = VisionStage.kDone;




					}
					if (currentStage == VisionStage.kDone) {
						System.out.println("Done");


						table.putNumber( "camMode", 1);
						table.putNumber( "ledMode", 2);

						Timer.delay(3);
					} 
					else {

						currentStage = VisionStage.kTurn1;

						System.out.println("Right");

						//movement w/o vision
						if (currentStage == VisionStage.kTurn1) {

							DemoDrive.arcadeDrive(0, .5);

							if (timer.get() > 3) {

								DemoDrive.arcadeDrive(0, 0);

								currentStage = VisionStage.kDriveForward1;
							}

							if(currentStage == VisionStage.kDriveForward1) {

								DemoDrive.arcadeDrive(.6, 0);

								if (timer.get() > 8) {

									DemoDrive.arcadeDrive(0,  0);

									currentStage = VisionStage.kTurn2;

								}

								if (currentStage == VisionStage.kTurn2) {

									DemoDrive.arcadeDrive(0, -.5);

									if (targetv == 1) {

										DemoDrive.arcadeDrive(0, 0);

										currentStage = VisionStage.kCenterOnTarget;


									}

								}

							}



							//vision processing

							if (currentStage == VisionStage.kCenterOnTarget) {

								System.out.println("Centering on target");


								if (targetx > 1) {
									DemoDrive.arcadeDrive(.6, .35);

								}

								if (targetx < -2.5) {
									DemoDrive.arcadeDrive(.6, -.35);
								}

								if(targetx >= -10 && targetx <= 10 && targetv == 1) {

									currentStage = VisionStage.kDriveForward;
								}

							}

							if (currentStage == VisionStage.kDriveForward) {
								System.out.println("Driving Forward");


								if (targeta < 10)  
								{  DemoDrive.arcadeDrive(.6, -.01);


								}
								if (targetx > 1) {
									DemoDrive.arcadeDrive(.6, .35);

								}

								if (targetx < -2.5) {
									DemoDrive.arcadeDrive(.6, -.35);
								}

								if (targeta >= 10 && targeta <= 30 && targetx >= -10 && targetx <= 10 && targetv == 1) {

									currentStage = VisionStage.kDropCube;
								}

							}


							if (currentStage == VisionStage.kDropCube) {
								System.out.println("Dropping cube");

								if (targetv == 1 && targeta >= 10)
									double2.set(DoubleSolenoid.Value.kForward);

								currentStage = VisionStage.kDone;




							}
							if (currentStage == VisionStage.kDone) {
								System.out.println("Done");


								table.putNumber( "camMode", 1);
								table.putNumber( "ledMode", 2);

								Timer.delay(3);
								break; //ends second choice


							}
						}
					}
				}
			}

			//Vision Right
		case customAuto1:

			if(gameData.length() > 0)
			{
				if(gameData.charAt(0) == 'L')
				{
					System.out.println("Right");

					currentStage = VisionStage.kTurn1;

					//movement w/o vision
					if (currentStage == VisionStage.kTurn1) {

						DemoDrive.arcadeDrive(0, -.5);

						if (timer.get() > 3) {

							DemoDrive.arcadeDrive(0, 0);

							currentStage = VisionStage.kDriveForward1;
						}

						if(currentStage == VisionStage.kDriveForward1) {

							DemoDrive.arcadeDrive(.6, 0);

							if (timer.get() > 8) {

								DemoDrive.arcadeDrive(0,  0);

								currentStage = VisionStage.kTurn2;

							}

							if (currentStage == VisionStage.kTurn2) {

								DemoDrive.arcadeDrive(0, .5);

								if (targetv == 1) {

									DemoDrive.arcadeDrive(0, 0);

									currentStage = VisionStage.kCenterOnTarget;


								}

							}

						}



						//vision processing

						if (currentStage == VisionStage.kCenterOnTarget) {

							System.out.println("Centering on target");


							if (targetx > 1) {
								DemoDrive.arcadeDrive(.6, .35);

							}

							if (targetx < -2.5) {
								DemoDrive.arcadeDrive(.6, -.35);
							}

							if(targetx >= -10 && targetx <= 10 && targetv == 1) {

								currentStage = VisionStage.kDriveForward;
							}

						}

						if (currentStage == VisionStage.kDriveForward) {
							System.out.println("Driving Forward");


							if (targeta < 10)  
							{  DemoDrive.arcadeDrive(.6, -.01);


							}
							if (targetx > 1) {
								DemoDrive.arcadeDrive(.6, .35);

							}

							if (targetx < -2.5) {
								DemoDrive.arcadeDrive(.6, -.35);
							}

							if (targeta >= 10 && targeta <= 30 && targetx >= -10 && targetx <= 10 && targetv == 1) {

								currentStage = VisionStage.kDropCube;
							}

						}


						if (currentStage == VisionStage.kDropCube) {
							System.out.println("Dropping cube");

							if (targetv == 1 && targeta >= 10)
								double2.set(DoubleSolenoid.Value.kForward);

							currentStage = VisionStage.kDone;




						}
						if (currentStage == VisionStage.kDone) {
							System.out.println("Done");


							table.putNumber( "camMode", 1);
							table.putNumber( "ledMode", 2);

							Timer.delay(3);




						}

						else {

							//vision processing
							if (currentStage == VisionStage.kNoTargetFound) 
							{
								System.out.println( "No target found");


								if (targetv == 1) {
									currentStage = VisionStage.kCenterOnTarget;
								}
							}

							if (currentStage == VisionStage.kCenterOnTarget) {

								System.out.println("Centering on target");


								if (targetx > 1) {
									DemoDrive.arcadeDrive(.6, .35);

								}

								if (targetx < -2.5) {
									DemoDrive.arcadeDrive(.6, -.35);
								}

								if(targetx >= -10 && targetx <= 10 && targetv == 1) {

									currentStage = VisionStage.kDriveForward;
								}

							}

							if (currentStage == VisionStage.kDriveForward) {
								System.out.println("Driving Forward");


								if (targeta < 10)  
								{  DemoDrive.arcadeDrive(.6, -.01);

								}

								if (targetx < -2.5) {
									DemoDrive.arcadeDrive(.6, -.35);


								}
								if (targetx > 1) {
									DemoDrive.arcadeDrive(.6, .35);
								}

								if (targeta >= 10 && targeta <= 30 && targetx >= -10 && targetx <= 10 && targetv == 1) {

									currentStage = VisionStage.kDropCube;
								}

							}


							if (currentStage == VisionStage.kDropCube) {
								System.out.println("Dropping cube");

								if (targetv == 1 && targeta >= 10)
									double2.set(DoubleSolenoid.Value.kForward);

								currentStage = VisionStage.kDone;




							}
							if (currentStage == VisionStage.kDone) {
								System.out.println("Done");


								table.putNumber( "camMode", 1);
								table.putNumber( "ledMode", 2);

								Timer.delay(3);


								break;
							}
						}

						break;

					}
				}
			}
		}
	}
	/**
	 * This function is called once each time the robot enters tele-operated
	 * mode
	 */
	@Override
	public void teleopInit() {

		currentStage = VisionStage.kDone;
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {

		//limelight camera/led set
		table.putNumber( "ledMode", 1);
		table.putNumber( "camMode", 1);

		//set stage
		currentStage = VisionStage.kDone;


		//Drivesticks
		DemoDrive.tankDrive((xBox.getRawAxis(1)* -.8), (xBox.getRawAxis(5)* -.85));
		System.out.println(Left2.getSensorCollection()); 

		//Buttons

		//compressor on						
		if (xBoxstart1.get()) {
			Comp.setClosedLoopControl(true);


		}
		//compressor off		
		else if (xBoxselect1.get()) {
			Comp.setClosedLoopControl(false);

		}
		//Arms up
		else if (xBoxa.get()) {

			double1.set(DoubleSolenoid.Value.kForward);

		}
		//Arms down
		else if (xBoxb.get()) {

			double1.set(DoubleSolenoid.Value.kReverse);
		}
		//Winch windup
		else if (xBoxx.get()) {

			winch.set(1);
		}
		//unused
		else if (xBoxy.get()) {


			winch.set(-1);


		}
		//Flysection down
		else if (xBox.getRawAxis(2) == 1.0 || xBox.getRawAxis(2) > 0) {
			Motor1.set(-.8);
		}
		//Flysection up
		else if (xBox.getRawAxis(3) == 1.0 || xBox.getRawAxis(2) > 0) {
			Motor1.set(1);

		}
		else if (xBoxlb.get()) {
			//Arms Close
			double2.set(DoubleSolenoid.Value.kForward);

		}

		else if (xBoxrb.get()) {
			//Arms Open
			double2.set(DoubleSolenoid.Value.kReverse);

		}


		else {
			//off until used
			double1.set(DoubleSolenoid.Value.kOff);
			double2.set(DoubleSolenoid.Value.kOff);
			Motor1.set(0);
			winch.set(0);
		}


	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {	}
}
