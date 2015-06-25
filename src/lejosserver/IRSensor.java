package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;

public class IRSensor extends Sensor {
	
	private EV3IRSensor sensor;

	public IRSensor(Port port) {
		super(port);
		this.sensor = new EV3IRSensor(port);
		this.mode = this.sensor.getDistanceMode();
	}
	
	@Override
	public void setMode(String name) {
		// TODO Auto-generated method stub
		
	}
	
}