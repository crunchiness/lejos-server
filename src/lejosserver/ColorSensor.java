package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorSensor extends Sensor {

	private EV3ColorSensor sensor;

	public ColorSensor(Port port, String portName) {
		super(port, portName);
		this.sensor = new EV3ColorSensor(port);
		setMode("rgb");
	}

	@Override
	public void setMode(String name) {
		if (name.equals(new String("rgb"))) {
			// Measures the RGB color of a surface
			this.mode = this.sensor.getRGBMode();
			this.numberOfValues = 3;
			this.modeName = "rgb";
		} else if (name.equals(new String("red"))){
			// Measures the intensity of a reflected red
			this.mode = this.sensor.getRedMode();
			this.numberOfValues = 1;
			this.modeName = "red";
		} else if (name.equals(new String("ambient"))){
			// Measures the ambient light level
			this.mode = this.sensor.getAmbientMode();
			this.numberOfValues = 1;
			this.modeName = "ambient";
		} else {
			// TODO
			// Color ID Measures the color ID of a surface getColorIDMode()
		}
	}
}
