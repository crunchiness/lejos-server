package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejosserver.Command.Mode;
import lejosserver.ErrorMode.ErrorType;

public class ColorSensor extends Sensor {

	private EV3ColorSensor colorSensor;

	public ColorSensor(Port port, String portName) {
		super(portName);
		try {
			colorSensor = new EV3ColorSensor(port);
			setMode(Mode.RGB, "rgb");
		} catch (IllegalArgumentException e) {
			new ErrorMode(ErrorType.NOT_CONNECTED_SENSOR, portName);
		}
	}

	@Override
	public void setMode(Mode mode, String modeName) {
		switch(mode) {
			case RGB: {
				// Measures the RGB color of a surface
				this.mode = colorSensor.getRGBMode();
				this.numberOfValues = 3;
				this.modeName = modeName;
				break;
			}
			case RED: {
				// Measures the intensity of a reflected red
				this.mode = colorSensor.getRedMode();
				this.numberOfValues = 1;
				this.modeName = modeName;
				break;
			}
			case AMBIENT: {
				// Measures the ambient light level
				this.mode = colorSensor.getAmbientMode();
				this.numberOfValues = 1;
				this.modeName = "ambient";
				break;
			}
			default: {
				// TODO Color ID Measures the color ID of a surface getColorIDMode()
				new ErrorMode(ErrorType.SYSTEM_ERROR, this.getClass().getName());
			}
		}
	}

	@Override
	public void close() {
		this.colorSensor.close();
	}
}
