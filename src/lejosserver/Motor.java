package lejosserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.simple.JSONObject;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;
import lejosserver.Command.MotorType;
import lejosserver.ErrorMode.ErrorType;

public class Motor {
	private String portName;
	private RegulatedMotor m;
	private MotorThread motorThread;
	private boolean running = false;
	public BlockingQueue<Command> actionQueue = new ArrayBlockingQueue<Command>(1);

	public Motor(Port port, String portName, MotorType motorType) {
		this.portName = portName;
		switch(motorType) {
			case MEDIUM: this.m = new EV3MediumRegulatedMotor(port);break;
			case LARGE: this.m = new EV3LargeRegulatedMotor(port);break;
			default: {
				new ErrorMode(ErrorType.SYSTEM_ERROR, this.getClass().getName());
			}
		}
	}
	
	public void init() {
		motorThread = new MotorThread();
		motorThread.setDaemon(true);
		running = true;
		motorThread.start();
	}
	
	public void executeCmd(Command cmd, PrintWriter pw) throws IOException {
		// closing has to be done from the outside (want to null this instance)
		switch(cmd.cmd) {
			case FORWARD: actionQueue.add(cmd);break;	
			case BACKWARD: actionQueue.add(cmd);break;
			case GETSPEED: getSpeed(pw);break;
			case SETSPEED: setSpeed(cmd.speed);break;
			case STOP: this.m.stop();break;
			case GETTACHO: getTacho(pw);break;
			case RESETTACHO: this.m.resetTachoCount();break;
			case ROTATE: rotate(cmd.rotateDeg);break;
			default: {
				new ErrorMode(ErrorType.SYSTEM_ERROR, this.getClass().getName());
			}
		}
	}
	
	public void rotate(int angle) {
		if (angle != Integer.MIN_VALUE) {
			this.m.rotate(angle);
		} else {
			new ErrorMode(ErrorType.SYSTEM_ERROR, this.getClass().getName());
		}
	}
	
	public void executeAsyncCmd(Command cmd) {
		switch(cmd.cmd) {
			case FORWARD: this.m.forward();break;
			case BACKWARD: this.m.backward();break;
			default: LCD.drawString("Unsupported motor cmd:" + cmd, 0, 4);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getSpeed(PrintWriter pw) throws IOException {
		int speed = this.m.getSpeed();
		JSONObject outputObj = new JSONObject();
		outputObj.put("speed", new Integer(speed));
		outputObj.put("dev", "motor");
		outputObj.put("port", portName);
		StringWriter out = new StringWriter();
		outputObj.writeJSONString(out);
		String jsonOutput = out.toString();
		pw.println(LocalServer.padString(jsonOutput));
		pw.flush();
	}
	
	public void setSpeed(int speed) {
		if (speed > 0) {
			this.m.setSpeed(speed);
		} else {
			new ErrorMode(ErrorType.SYSTEM_ERROR, this.getClass().getName());
		}
	}

	@SuppressWarnings("unchecked")
	public void getTacho(PrintWriter pw) throws IOException {
		int count = this.m.getTachoCount();
		JSONObject outputObj = new JSONObject();
		outputObj.put("tacho", new Integer(count));
		outputObj.put("dev", "motor");
		outputObj.put("port", portName);
		StringWriter out = new StringWriter();
		outputObj.writeJSONString(out);
		String jsonOutput = out.toString();
		pw.println(LocalServer.padString(jsonOutput));
		pw.flush();
	}
	
	private class MotorThread extends Thread {
		@Override
		public void run() {
			while(running) {
				try {
					Command cmd = actionQueue.take();
					executeAsyncCmd(cmd);
				} catch (InterruptedException e) {
					running = false;
					// exit if interrupted
				}
			}
		}
	}

	public void close() {
		running = false;
		m.close();
	}

}