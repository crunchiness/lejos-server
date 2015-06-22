package lejosserver;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

public class EV3MediumMotorAction implements Action, Runnable {

	private Port port;
	private int degrees;
	
	public EV3MediumMotorAction(String which, long degrees) {
		Port port = null;
		switch (which) {
			case "A": port = MotorPort.A;break;
			case "B": port = MotorPort.B;break;
			case "C": port = MotorPort.C;break;
			case "D": port = MotorPort.D;break;
		}
		this.port = port;
		this.degrees = (int) degrees;
	}
	
	@Override
	public void run() {
		RegulatedMotor m = new EV3MediumRegulatedMotor(this.port);
		m.rotate(this.degrees);
		//TODO remove
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.close();
	}

}
