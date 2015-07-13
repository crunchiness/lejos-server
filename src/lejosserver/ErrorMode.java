package lejosserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * @author Ingvaras Merkys Purpose of this class is to provide a useful error
 *         message (via EV3 screen) and allow for quitting the application by
 *         clicking the button.
 */
public class ErrorMode {
	
	public enum ErrorType {
		UNKNOWN_DEV, UNKNOWN_PORT, UNKNOWN_COMMAND, UNKNOWN_SENSOR, UNKNOWN_MOTOR, UNKNOWN_SENSOR_MODE, MISSING_CMD_VALUE, SYSTEM_ERROR, NOT_INIT_CAM, NOT_INIT_MOTOR, NOT_INIT_SENSOR, NOT_CONNECTED_CAM, NOT_CONNECTED_MOTOR, NOT_CONNECTED_SENSOR, FULLQUEUE, EXPECTED_INT
	}

	private static final Map<ErrorType, String> errorStringMap;
	static {
		Map<ErrorType, String> map = new HashMap<ErrorType, String>();
		map.put(ErrorType.UNKNOWN_DEV, "Unknown device '%s'.");
		map.put(ErrorType.UNKNOWN_PORT, "Unknown port '%s'.");
		map.put(ErrorType.UNKNOWN_COMMAND, "Unknown command '%s'.");
		map.put(ErrorType.UNKNOWN_SENSOR, "Unknown sensor type '%s'.");
		map.put(ErrorType.UNKNOWN_MOTOR, "Unknown motor type '%s'.");
		map.put(ErrorType.UNKNOWN_SENSOR_MODE, "Unknown sensor mode '%s'.");
		map.put(ErrorType.MISSING_CMD_VALUE, "Value for '%s' not provided.");
		map.put(ErrorType.NOT_INIT_CAM, "Cmd '%s'. Camera not init.");
		map.put(ErrorType.NOT_INIT_MOTOR, "Cmd '%s'. Motor not init.");
		map.put(ErrorType.NOT_INIT_SENSOR, "Cmd '%s'. Sensor not init.");
		map.put(ErrorType.NOT_CONNECTED_CAM, "No camera connected.");
		map.put(ErrorType.NOT_CONNECTED_MOTOR, "Motor not connected to '%s' ().");
		map.put(ErrorType.NOT_CONNECTED_MOTOR, "No motor / unexpected device at port '%s'.");
		map.put(ErrorType.NOT_CONNECTED_SENSOR, "No sensor / unexpected device at port '%s'.");
		map.put(ErrorType.FULLQUEUE, "Repeated 'rotate' received. Motor at port '%s'.");
		map.put(ErrorType.EXPECTED_INT, "Expected integer value for '%s'.");
		// User should never see this:
		map.put(ErrorType.SYSTEM_ERROR, "System error in '%s'.");
		errorStringMap = Collections.unmodifiableMap(map);
	}

	public ErrorMode(ErrorType err) {
		this(err, "");
	}

	public ErrorMode(ErrorType err, String param) {
		String errStr = "Error:" + String.format(errorStringMap.get(err), param);
		Sound.buzz();
		Util.drawString(errStr);
		buttonListener();
	}

	public void buttonListener() {
		Button.ESCAPE.waitForPressAndRelease();
		LocalServer.terminate = true;
	}
}
