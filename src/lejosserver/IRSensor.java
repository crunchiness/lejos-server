package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;

public class IRSensor extends Sensor {
	
	private EV3IRSensor sensor;

	public IRSensor(Port port) {
		super(port);
		this.sensor = new EV3IRSensor(port);
		setMode("distance");
	}
	
	@Override
	public void setMode(String name) {
		if (name.equals(new String("distance"))) {
			this.mode = this.sensor.getDistanceMode();
			this.numberOfValues = 1;
			this.modeName = "distance";
		} else {
			// TODO
		}
	}
	
}