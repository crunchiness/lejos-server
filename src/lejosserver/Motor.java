package lejosserver;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

public class Motor {
	
	private Port port;
	private RegulatedMotor m;
	private MotorThread motorThread;
	public BlockingQueue<String> actionQueue = new ArrayBlockingQueue<String>(1);

	public Motor(String which) {
		Port port = null;
		switch (which) {
			case "A": port = MotorPort.A;break;
			case "B": port = MotorPort.B;break;
			case "C": port = MotorPort.C;break;
			case "D": port = MotorPort.D;break;
		}
		this.port = port;
		this.m = new EV3LargeRegulatedMotor(this.port);
	}
	
	public void init() {
		motorThread = new MotorThread();
		motorThread.setDaemon(true);
		motorThread.start();
	}
	
	public void executeCmd(String cmd) {
		if (cmd.equals(new String("forward"))) {
			this.m.forward();
		} else if (cmd.equals(new String("backward"))) {
			this.m.backward();
		} else if (cmd.equals(new String("stop"))) {
			this.m.stop();
		} else if (cmd.equals(new String("close"))) {
			this.m.close();
//		} else if (cmd.equals(new String("gettacho"))) {
//			this.m.getTachoCount();
		} else if (cmd.equals(new String("resettacho"))) {
			this.m.resetTachoCount();
//		} else if (cmd.equals(new String("rotate"))) {
//			this.m.rotate(angle);
		} else {
			LCD.drawString("Unsupported motor cmd:" + cmd, 0, 4);
		}
	}

	public void setAcceleration(int acceleration) {
		this.m.setAcceleration(acceleration);
	}

	public int getSpeed() {
		return this.m.getSpeed();
	}

	public void setSpeed(int speed) {
		this.m.setSpeed(speed);
	}

	public int getRotationSpeed() {
		return this.m.getRotationSpeed();
	}

	public float getMaxSpeed() {
		return this.m.getMaxSpeed();
	}
	
	private class MotorThread extends Thread {
		@Override
		public void run() {
			while(true) {
				try {
					String cmd = actionQueue.take();
					executeCmd(cmd);
				} catch (InterruptedException e) {
					// exit if interrupted
				}
			}
		}
	}
}