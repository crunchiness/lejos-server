package lejosserver;

import org.json.simple.JSONObject;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;

public class Command {
	public enum CmdType {
		INIT, BEEP, BUZZ, EXIT, FORWARD, BACKWARD, STOP, CLOSE, RESETTACHO, GETSPEED, SETSPEED,
		GETVALUE, SETMODE
	}

	public enum DevType {
		BRICK, MOTOR, SENSOR
	}

	public DevType dev;
	public CmdType cmd;
	public String cmdName;
	public String portName;
	public int intParam;
	public String strParam;
	public Port port;
	public int portIndex;

	public Command(JSONObject command) {
		String dev = (String) command.get("dev");
		if (dev.equals(new String("motor"))) {
			this.dev = DevType.MOTOR;
		} else if (dev.equals(new String("brick"))) {
			this.dev = DevType.BRICK;
		} else if (dev.equals(new String("sensor"))) {
			this.dev = DevType.SENSOR;
		} else {
			// TODO
		}

		Object port = command.get("port");
		if (port != null) {
			portName = (String) port;
			if (port.equals(new String("A"))) {
				this.port = MotorPort.A;
				this.portIndex = 0;
			} else if (port.equals(new String("B"))) {
				this.port = MotorPort.B;
				this.portIndex = 1;
			} else if (port.equals(new String("C"))) {
				this.port = MotorPort.C;
				this.portIndex = 2;
			} else if (port.equals(new String("D"))) {
				this.port = MotorPort.D;
				this.portIndex = 3;
			} else if (port.equals(new String("S1"))) {
				this.port = SensorPort.S1;
				this.portIndex = 0;
			} else if (port.equals(new String("S2"))) {
				this.port = SensorPort.S2;
				this.portIndex = 1;
			} else if (port.equals(new String("S3"))) {
				this.port = SensorPort.S3;
				this.portIndex = 2;
			} else if (port.equals(new String("S4"))) {
				this.port = SensorPort.S4;
				this.portIndex = 3;
			} else {
				// TODO
			}
		}

		this.cmdName = (String) command.get("cmd");
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
		else if (cmdName.equals(new String("getdistance"))) { this.cmd = CmdType.GETVALUE; }
		else if (cmdName.equals(new String("setmode"))) { this.cmd = CmdType.SETMODE; }
		else {
			// TODO
		}

		if (this.cmd == CmdType.SETMODE) {
			// TODO enum modes?
			Object param = command.get("mode");
			if (param != null) {
				this.strParam = (String) param;
			}
		} else if (this.cmd == CmdType.INIT && this.dev == DevType.SENSOR) {
			// TODO enum sensors?
			Object param = command.get("type");
			if (param != null) {
				this.strParam = (String) param;
			}
		}
	}
}
