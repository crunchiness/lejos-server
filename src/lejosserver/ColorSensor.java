package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorSensor extends Sensor {

	private EV3ColorSensor sensor;

	public ColorSensor(Port port) {
		super(port);
		this.sensor = new EV3ColorSensor(port);
		setMode("RGB");
	}

	@Override
	public void setMode(String name) {
		if (name.equals(new String("RGB"))) {
			this.mode = this.sensor.getRGBMode();
			this.numberOfValues = 3;
		}
	}
}
