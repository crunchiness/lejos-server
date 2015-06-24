package lejosserver;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.json.simple.JSONObject;

import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

public class Motor {
	
	private Port port;
	private RegulatedMotor m;
	private MotorThread motorThread;
	public BlockingQueue<Command> actionQueue = new ArrayBlockingQueue<Command>(1);

	public Motor(Port port) {
		this.port = port;
		this.m = new EV3LargeRegulatedMotor(this.port);
	}
	
	public void init() {
		motorThread = new MotorThread();
		motorThread.setDaemon(true);
		motorThread.start();
	}
	
	public void executeAsyncCmd(Command cmd) {
		switch(cmd.cmd) {
			case FORWARD: this.m.forward();break;
			case BACKWARD: this.m.backward();break;
			case STOP: this.m.stop();break;
			case RESETTACHO: this.m.resetTachoCount();break;
			default: LCD.drawString("Unsupported motor cmd:" + cmd, 0, 4);
		}
//		} else if (cmd.equals(new String("gettacho"))) {
//			this.m.getTachoCount();
//		} else if (cmd.equals(new String("rotate"))) {
//			this.m.rotate(angle);
	}

	public void setAcceleration(int acceleration) {
		this.m.setAcceleration(acceleration);
	}

	@SuppressWarnings("unchecked")
	public String getSpeed() throws IOException {
		int speed = this.m.getSpeed();
		JSONObject outputObj = new JSONObject();
		outputObj.put("speed", new Integer(speed));
		outputObj.put("dev", "motor");
		outputObj.put("port", port.toString()); //TODO fix
		StringWriter out = new StringWriter();
		outputObj.writeJSONString(out);
		String jsonOutput = out.toString();
		return LocalServer.padString(jsonOutput);
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
					Command cmd = actionQueue.take();
					executeAsyncCmd(cmd);
				} catch (InterruptedException e) {
					// exit if interrupted
				}
			}
		}
	}
}