package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorSensor extends Sensor {

	private EV3ColorSensor colorSensor;

	public ColorSensor(Port port, String portName) {
		super(port, portName);
		colorSensor = new EV3ColorSensor(port);
		setMode("rgb");
	}

	@Override
	public void setMode(String name) {
		if (name.equals(new String("rgb"))) {
			// Measures the RGB color of a surface
			mode = colorSensor.getRGBMode();
			numberOfValues = 3;
			modeName = "rgb";
		} else if (name.equals(new String("red"))){
			// Measures the intensity of a reflected red
			mode = colorSensor.getRedMode();
			numberOfValues = 1;
			modeName = "red";
		} else if (name.equals(new String("ambient"))){
			// Measures the ambient light level
			mode = colorSensor.getAmbientMode();
			numberOfValues = 1;
			modeName = "ambient";
		} else {
			// TODO
			// Color ID Measures the color ID of a surface getColorIDMode()
		}
	}

	@Override
	public void close() {
		this.colorSensor.close();
	}
}
