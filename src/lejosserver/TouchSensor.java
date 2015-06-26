package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;

public class TouchSensor extends Sensor {

	private EV3TouchSensor touchSensor;

	public TouchSensor(Port port, String portName) {
		super(port, portName);
		touchSensor = new EV3TouchSensor(port);
		setMode("touch");
	}

	@Override
	public void setMode(String name) {
		if (name.equals(new String("touch"))) {
			mode = touchSensor.getTouchMode();
			numberOfValues = 1;
			modeName = "touch";
		} else {
			// TODO
		}
	}

	@Override
	public void close() {
		this.touchSensor.close();
	}

}
