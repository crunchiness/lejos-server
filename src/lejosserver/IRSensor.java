package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;

public class IRSensor extends Sensor {
	
	private EV3IRSensor irSensor;

	public IRSensor(Port port, String portName) {
		super(port, portName);
		this.irSensor = new EV3IRSensor(port);
		setMode("distance");
	}
	
	@Override
	public void setMode(String name) {
		if (name.equals(new String("distance"))) {
			mode = this.irSensor.getDistanceMode();
			numberOfValues = 1;
			modeName = "distance";
		} else {
			// TODO
		}
	}

	@Override
	public void close() {
		irSensor.close();
	}
	
}