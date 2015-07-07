package lejosserver;

import org.json.simple.JSONObject;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejosserver.ErrorMode.ErrorType;

/**
 * @author Ingvaras Merkys
 * A class that represents a command to robot. Initialised from JSON string.
 */
public class Command {
	public enum CmdType {
		INIT, BEEP, BUZZ, EXIT, FORWARD, BACKWARD, STOP, CLOSE, GETTACHO, RESETTACHO, GETSPEED, SETSPEED,
		GETVALUE, SETMODE, GETMODE, TAKEPIC, ROTATE
	}

	public enum DevType {
		BRICK, MOTOR, SENSOR, CAMERA
	}
	
	public enum SensorType {
		COLOR, IR, TOUCH
	}
	
	public enum MotorType {
		MEDIUM, LARGE
	}

	public enum Mode {
		RGB, RED, AMBIENT, DISTANCE, TOUCH, COLORID
	}
	
	public DevType dev;
	public String devName;
	
	public CmdType cmd;
	public String cmdName;
	
	public SensorType sensorType;
	public String sensorName;
	
	public MotorType motorType;
	public String motorName;
	
	public Mode sensorMode;
	public String modeName;
	
	public String portName;
	public Port port;
	public int portIndex;
	
	public int camWidth = Integer.MIN_VALUE;
	public int camHeight = Integer.MIN_VALUE;
	public int speed = Integer.MIN_VALUE;
	public int rotateDeg = Integer.MIN_VALUE;
	public boolean isAsync;
	
	/**
	 * Parses JSON object into Command.
	 * @param command	JSON command object
	 */
	public Command(JSONObject command) {
		// TODO what if casts fail
		parseDev(command);
		parseCmd(command);
		if (dev == DevType.MOTOR || dev == DevType.SENSOR) {
			parsePort(command);
		}
		if (dev == DevType.SENSOR && cmd == CmdType.INIT) {
			parseSensorType(command);
		}
		if (dev == DevType.MOTOR && cmd == CmdType.INIT) {
			parseMotorType(command);
		}
		if (dev == DevType.CAMERA && cmd == CmdType.INIT) {
			parseCamWidth(command);
			parseCamHeight(command);
		}
		if (cmd == CmdType.SETMODE) {
			parseSensorMode(command);
		}
		if (cmd == CmdType.ROTATE) {
			parseRotateDeg(command);
			parseIsAsync(command);
		}
		if (cmd == CmdType.SETSPEED) {
			parseSpeed(command);
		}
	}
	
	private void parseIsAsync(JSONObject command) {
		try {
			Long isAsync = (Long) command.get("is_async");
			if (isAsync != null) {
				this.isAsync = isAsync > 0;
			} else {
				new ErrorMode(ErrorType.MISSING_CMD_VALUE, "is_async");
			}
		} catch (ClassCastException e) {
			new ErrorMode(ErrorType.EXPECTED_INT, "is_async");
		}
	}

	private void parseDev(JSONObject command) {
		devName = (String) command.get("dev");
		if (devName.equals(new String("motor"))) { dev = DevType.MOTOR; }
		else if (devName.equals(new String("brick"))) {	dev = DevType.BRICK; }
		else if (devName.equals(new String("sensor"))) { dev = DevType.SENSOR;	}
		else if (devName.equals(new String("camera"))) { dev = DevType.CAMERA;	}
		else {
			new ErrorMode(ErrorType.UNKNOWN_DEV, devName);
		}
	}
	
	private void parsePort(JSONObject command) {
		portName = (String) command.get("port");
		if (portName.equals(new String("A"))) {
			this.port = MotorPort.A;
			this.portIndex = 0;
		} else if (portName.equals(new String("B"))) {
			this.port = MotorPort.B;
			this.portIndex = 1;
		} else if (portName.equals(new String("C"))) {
			this.port = MotorPort.C;
			this.portIndex = 2;
		} else if (portName.equals(new String("D"))) {
			this.port = MotorPort.D;
			this.portIndex = 3;
		} else if (portName.equals(new String("S1"))) {
			this.port = SensorPort.S1;
			this.portIndex = 0;
		} else if (portName.equals(new String("S2"))) {
			this.port = SensorPort.S2;
			this.portIndex = 1;
		} else if (portName.equals(new String("S3"))) {
			this.port = SensorPort.S3;
			this.portIndex = 2;
		} else if (portName.equals(new String("S4"))) {
			this.port = SensorPort.S4;
			this.portIndex = 3;
		} else {
			new ErrorMode(ErrorType.UNKNOWN_PORT, portName);
		}
	}
	
	private void parseCmd(JSONObject command) {
		cmdName = (String) command.get("cmd");
		if (cmdName.equals(new String("init"))) { this.cmd = CmdType.INIT; }
		else if (cmdName.equals(new String("beep"))) { this.cmd = CmdType.BEEP; }
		else if (cmdName.equals(new String("buzz"))) { this.cmd = CmdType.BUZZ; }
		else if (cmdName.equals(new String("exit"))) { this.cmd = CmdType.EXIT; }
		else if (cmdName.equals(new String("forward"))) { this.cmd = CmdType.FORWARD; }
		else if (cmdName.equals(new String("backward"))) { this.cmd = CmdType.BACKWARD; }
		else if (cmdName.equals(new String("stop"))) { this.cmd = CmdType.STOP; }
		else if (cmdName.equals(new String("close"))) { this.cmd = CmdType.CLOSE; }
		else if (cmdName.equals(new String("resettacho"))) { this.cmd = CmdType.RESETTACHO; }
		else if (cmdName.equals(new String("getspeed"))) { this.cmd = CmdType.GETSPEED; }
		else if (cmdName.equals(new String("setspeed"))) { this.cmd = CmdType.SETSPEED; }
		else if (cmdName.equals(new String("getvalue"))) { this.cmd = CmdType.GETVALUE; }
		else if (cmdName.equals(new String("setmode"))) { this.cmd = CmdType.SETMODE; }
		else if (cmdName.equals(new String("getmode"))) { this.cmd = CmdType.GETMODE; }
		else if (cmdName.equals(new String("takepic"))) { this.cmd = CmdType.TAKEPIC; }
		else if (cmdName.equals(new String("gettacho"))) { this.cmd = CmdType.GETTACHO; }
		else if (cmdName.equals(new String("rotate"))) { this.cmd = CmdType.ROTATE; }
		else {
			new ErrorMode(ErrorType.UNKNOWN_COMMAND, cmdName);
		}
	}
	
	private void parseSensorType(JSONObject command) {
		sensorName = (String) command.get("sensor_type");
		if (sensorName.equals(new String("color"))) { sensorType = SensorType.COLOR; }
		else if (sensorName.equals(new String("ir"))) { sensorType = SensorType.IR; }
		else if (sensorName.equals(new String("touch"))) { sensorType = SensorType.TOUCH; }
		else {
			new ErrorMode(ErrorType.UNKNOWN_SENSOR, sensorName);
		}
	}
	
	private void parseMotorType(JSONObject command) {
		motorName = (String) command.get("motor_type");
		if (motorName.equals(new String("medium"))) { motorType = MotorType.MEDIUM; }
		else if (motorName.equals(new String("large"))) {motorType = MotorType.LARGE; }
		else {
			new ErrorMode(ErrorType.UNKNOWN_MOTOR, motorName);
		}
	}
	
	private void parseSensorMode(JSONObject command) {
		modeName = (String) command.get("mode");
		if (modeName.equals(new String("rgb"))) { sensorMode = Mode.RGB; }
		else if (modeName.equals(new String("red"))) { sensorMode = Mode.RED; }
		else if (modeName.equals(new String("ambient"))) { sensorMode = Mode.AMBIENT; }
		else if (modeName.equals(new String("distance"))) { sensorMode = Mode.DISTANCE; }
		else if (modeName.equals(new String("touch"))) { sensorMode = Mode.TOUCH; }
		else if (modeName.equals(new String("colorid"))) { sensorMode = Mode.COLORID; }
		else {
			new ErrorMode(ErrorType.UNKNOWN_SENSOR_MODE, modeName);
		}
	}
	
	private void parseCamWidth(JSONObject command) {
		try {
			Long widthObj = (Long) command.get("cam_width");
			if (widthObj != null) {
				camWidth = (int) (long) widthObj;
			} else {
				new ErrorMode(ErrorType.MISSING_CMD_VALUE, "cam_width");
			}
		} catch (ClassCastException e) {
			new ErrorMode(ErrorType.EXPECTED_INT, "cam_width");
		}
	}
	
	private void parseCamHeight(JSONObject command) {
		try {
			Long heightObj = (Long) command.get("cam_height");
			if (heightObj != null) {
				camHeight = (int) (long) heightObj;
			} else {
				new ErrorMode(ErrorType.MISSING_CMD_VALUE, "cam_height");
			}
		} catch (ClassCastException e) {
			new ErrorMode(ErrorType.EXPECTED_INT, "cam_height");
		}
	}
	
	private void parseSpeed(JSONObject command) {
		try {
			Long speedObj = (Long) command.get("speed");
			if (speedObj != null) {
				speed = (int) (long) speedObj;
			} else {
				new ErrorMode(ErrorType.MISSING_CMD_VALUE, "speed");
			}			
		} catch (ClassCastException e) {
			new ErrorMode(ErrorType.EXPECTED_INT, "speed");
		}
	}
	
	private void parseRotateDeg(JSONObject command) {
		try {
			Long rotateDegObj = (Long) command.get("rotate_deg");
			if (rotateDegObj != null) {
				rotateDeg  = (int) (long) rotateDegObj;
			} else {
				new ErrorMode(ErrorType.MISSING_CMD_VALUE, "rotate_deg");
			}
		} catch (ClassCastException e) {
			new ErrorMode(ErrorType.EXPECTED_INT, "rotate_deg");
		}
	}
}
