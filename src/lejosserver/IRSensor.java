package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejosserver.Command.Mode;
import lejosserver.ErrorMode.ErrorType;

public class IRSensor extends Sensor {
	
	private EV3IRSensor irSensor;

	public IRSensor(Port port, String portName) {
		super(portName);
		this.irSensor = new EV3IRSensor(port);
		setMode(Mode.DISTANCE, "distance");
	}
	
	@Override
	public void setMode(Mode mode, String modeName) {
		switch(mode) {
			case DISTANCE: {
				this.mode = irSensor.getDistanceMode();
				this.numberOfValues = 1;
				this.modeName = modeName;
				break;
			}
			default: {
				new ErrorMode(ErrorType.SYSTEM_ERROR, this.getClass().getName());
			}
		}
	}

	@Override
	public void close() {
		irSensor.close();
	}
	
}