package lejosserver;

import java.io.*;
import java.net.*;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejosserver.Command.CmdType;
import lejosserver.Command.DevType;
import lejosserver.ErrorMode.ErrorType;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LocalServer {
	
	// Motor ports: A, B, C, D
	private static Motor[] motors = {null, null, null, null};
	// Sensor ports: S1, S2, S3, S4
	private static Sensor[] sensors = {null, null, null, null};
	
	private static Camera camera;
	
	public static boolean terminate = false; 
	
	public static void main(String argv[]) throws Exception {
		String clientSentence;
		ServerSocket socket = new ServerSocket(6789);
		LCD.drawString("READY", 0, 4);
		while (!terminate) {
			Socket connectionSocket = socket.accept();
			BufferedReader inReader = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			clientSentence = inReader.readLine();
			DataOutputStream outStream = new DataOutputStream(
					connectionSocket.getOutputStream());
			PrintWriter pw = new PrintWriter(outStream);
			
			LCD.drawString(clientSentence, 0, 4);
			
			JSONObject inputObj = (JSONObject) JSONValue.parse(clientSentence);
			JSONObject contents;
			try {
				contents = (JSONObject) inputObj.get("command");
			} catch (NullPointerException e) {
				LCD.drawString("Bad input: " + clientSentence, 0, 4);
				break;
			}
			Command cmd = new Command(contents);
			
			// Brick commands
			if (cmd.dev == DevType.BRICK) {
				switch(cmd.cmd) {
					case BEEP: Sound.beep();break;
					case BUZZ: Sound.buzz();break;
					case EXIT: terminate = true;break;
					default: LCD.drawString("Unknown cmd: " + cmd.cmdName, 0, 4);
				}

			// Motor commands
			} else if (cmd.dev == DevType.MOTOR) {
				int i = cmd.portIndex;
				if (cmd.cmd == CmdType.INIT) {
					// if initialised already, ignore this command
					if (motors[i] == null) {
						motors[i] = new Motor(cmd.port, cmd.portName, cmd.motorType);
						motors[i].init();
					}
				} else if (cmd.cmd == CmdType.CLOSE) {
					// if motor wasn't initialised, ignore this command
					if (motors[i] != null) {
						motors[i].close();
						motors[i] = null;
					}
				} else if (motors[i] != null){
					motors[i].executeCmd(cmd, pw);
				} else {
					new ErrorMode(ErrorType.NOT_INIT_MOTOR, cmd.cmdName);
				}

			// Sensor commands
			} else if (cmd.dev == DevType.SENSOR) {
				int i = cmd.portIndex;
				if (cmd.cmd == CmdType.INIT) {
					// if initialised already, ignore this command
					if (sensors[i] == null) {
						switch (cmd.sensorType) {
							case COLOR: sensors[i] = new ColorSensor(cmd.port, cmd.portName);break;
							case IR: sensors[i] = new IRSensor(cmd.port, cmd.portName);break;
							case TOUCH: sensors[i] = new TouchSensor(cmd.port, cmd.portName);break;
							default: new ErrorMode(ErrorType.SYSTEM_ERROR, "main loop sensors");break;
						}
					}
				} else if (cmd.cmd == CmdType.CLOSE) {
					// if sensor wasn't initialised, ignore this command
					if (sensors[i] != null) {
						sensors[i].close();
						sensors[i] = null;
					}
				} else if (sensors[i] != null) {
					sensors[i].executeCmd(cmd, pw);
				} else {					
					new ErrorMode(ErrorType.NOT_INIT_SENSOR, cmd.cmdName);
				}
				
			// Camera commands
			} else if (cmd.dev == DevType.CAMERA) {
				if (cmd.cmd == CmdType.INIT) {
					// if initialised already, ignore this command
					if (camera == null) {
						if (cmd.camWidth > 0 && cmd.camHeight > 0) {
							camera = new Camera(cmd.camWidth, cmd.camHeight);
						} else {
							camera = new Camera();
						}
					}
				} else if (cmd.cmd == CmdType.CLOSE) {
					// if sensor wasn't initialised, ignore this command
					if (camera != null) {
						camera.closeCamera();
						camera = null;
					}
				} else if (camera != null) {
					 if (cmd.cmd == CmdType.TAKEPIC) {
						 camera.takePicture(outStream);
					 }
				} else {
					new ErrorMode(ErrorType.NOT_INIT_CAM, cmd.cmdName);
				}

			// Unsupported device
			} else {
				new ErrorMode(ErrorType.SYSTEM_ERROR, "main loop sensors");
			}
		}
		socket.close();
	}

	public static String padString(String str) {
		// pads string with spaces to length 100
		String length = "100";
		return String.format("%1$-" + length + "s", str);
	}
}