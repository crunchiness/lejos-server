package lejosserver;

import org.json.simple.JSONObject;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

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
		RGB, RED, AMBIENT, DISTANCE, TOUCH
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
	
	public int camWidth = Integer.MIN_VALUE; // TODO default?
	public int camHeight = Integer.MIN_VALUE; // TODO default?
	public int speed = Integer.MIN_VALUE;
	public int rotateDeg = Integer.MIN_VALUE;
	
	/**
	 * Parses JSON object into Command.
	 * @param command	JSON command object
	 */
	public Command(JSONObject command) {
		// TODO what if casts fail
		// Parse "dev"
		devName = (String) command.get("dev");
		if (devName.equals(new String("motor"))) { dev = DevType.MOTOR; }
		else if (devName.equals(new String("brick"))) {	dev = DevType.BRICK; }
		else if (devName.equals(new String("sensor"))) { dev = DevType.SENSOR;	}
		else if (devName.equals(new String("camera"))) { dev = DevType.CAMERA;	}
		else {
			// TODO
		}

		// Parse "port"
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
			// TODO
		}

		// Parse "cmd"
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
			// TODO
		}

		// Parse "sensor_type"
		sensorName = (String) command.get("sensor_type");
		if (sensorName.equals(new String("color"))) { sensorType = SensorType.COLOR; }
		else if (sensorName.equals(new String("ir"))) { sensorType = SensorType.IR; }
		else if (sensorName.equals(new String("touch"))) { sensorType = SensorType.TOUCH; }
		else {
			// TODO
		}
		
		// Parse "motor_type"
		motorName = (String) command.get("motor_type");
		if (motorName.equals(new String("medium"))) { motorType = MotorType.MEDIUM; }
		else if (motorName.equals(new String("large"))) {motorType = MotorType.LARGE; }
		else {
			// TODO
		}
		
		// Parse "mode" (sensor mode)
		modeName = (String) command.get("mode");
		if (modeName.equals(new String("rgb"))) { sensorMode = Mode.RGB; }
		else if (modeName.equals(new String("red"))) { sensorMode = Mode.RED; }
		else if (modeName.equals(new String("ambient"))) { sensorMode = Mode.AMBIENT; }
		else if (modeName.equals(new String("distance"))) { sensorMode = Mode.DISTANCE; }
		else if (modeName.equals(new String("touch"))) { sensorMode = Mode.TOUCH; }
		else {
			// TODO
		}
		
		// Parse "cam_width"
		Long widthObj = (Long) command.get("cam_width");
		if (widthObj != null) {
			camWidth = (int) (long) widthObj;
		} else {
			// TODO
		}
		
		// Parse "cam_height"
		Long heightObj = (Long) command.get("cam_height");
		if (heightObj != null) {
			camHeight = (int) (long) heightObj;
		} else {
			// TODO
		}
		
		// Parse "speed"
		Long speedObj = (Long) command.get("speed");
		if (speedObj != null) {
			speed = (int) (long) speedObj;
		} else {
			// TODO
		}
		
		// Parse "rotate_deg"
		Long rotateDegObj = (Long) command.get("rotate_deg");
		if (rotateDegObj != null) {
			rotateDeg  = (int) (long) rotateDegObj;
		} else {
			// TODO
		}
		// TODO check for required?
	}
}
