package lejosserver;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3TouchSensor;
import lejosserver.Command.Mode;
import lejosserver.ErrorMode.ErrorType;

public class TouchSensor extends Sensor {

	private EV3TouchSensor touchSensor;

	public TouchSensor(Port port, String portName) {
		super(portName);
		touchSensor = new EV3TouchSensor(port);
		setMode(Mode.TOUCH, "touch");
	}

	@Override
	public void setMode(Mode mode, String modeName) {
		switch(mode) {
			case TOUCH: {
				this.mode = touchSensor.getTouchMode();
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
		this.touchSensor.close();
	}

}
