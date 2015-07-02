package lejosserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

/**
 * @author Ingvaras Merkys Purpose of this class is to provide a useful error
 *         message (via EV3 screen) and allow for quitting the application by
 *         clicking the button.
 */
public class ErrorMode {

	private int CHARS_PER_LINE = 17;
	private int LINE_HEIGHT = 1;
	
	public enum ErrorType {
		UNKNOWN_DEV, UNKNOWN_PORT, UNKNOWN_COMMAND, UNKNOWN_SENSOR, UNKNOWN_MOTOR, UNKNOWN_SENSOR_MODE, MISSING_CMD_VALUE, SYSTEM_ERROR, NOT_INIT_CAM, NOT_INIT_MOTOR, NOT_INIT_SENSOR, NOT_CONNECTED_CAM
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
		map.put(ErrorType.NOT_CONNECTED_CAM, "Camera not connected?");
		// User should never see this:
		map.put(ErrorType.SYSTEM_ERROR, "System error in '%s'.");
		errorStringMap = Collections.unmodifiableMap(map);
	}

	public ErrorMode(ErrorType err) {
		this(err, "");
	}

	public ErrorMode(ErrorType err, String param) {
		String errStr = String.format(errorStringMap.get(err), param);
		Sound.buzz();
		drawError(errStr);
		buttonListener();
	}

	public void drawError(String err) {
		LCD.clear();
		int strLen = err.length();
		for (int i = 0; i*CHARS_PER_LINE < strLen; i++) {
			String subStr = err.substring(i*CHARS_PER_LINE);
			LCD.drawString(subStr, 0, i*LINE_HEIGHT);
		}
	}
	public void buttonListener() {
		Button.ESCAPE.waitForPressAndRelease();
		LocalServer.terminate = true;
	}
}
