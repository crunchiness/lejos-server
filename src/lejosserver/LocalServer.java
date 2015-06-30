package lejosserver;

import java.io.*;
import java.net.*;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejosserver.Command.CmdType;
import lejosserver.Command.DevType;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LocalServer {
	
	// Motor ports: A, B, C, D
	private static Motor[] motors = {null, null, null, null};
	// Sensor ports: S1, S2, S3, S4
	private static Sensor[] sensors = {null, null, null, null};
	
	private static Camera camera;
	
	public static void main(String argv[]) throws Exception {
		boolean terminate = false;
		String clientSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);
		LCD.drawString("READY", 0, 4);
		while (!terminate) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			clientSentence = inFromClient.readLine();
			DataOutputStream outToClient = new DataOutputStream(
					connectionSocket.getOutputStream());
			PrintWriter pw = new PrintWriter(outToClient);
			
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
				if (cmd.cmd == CmdType.INIT && motors[i] == null) {
					motors[i] = new Motor(cmd.port, cmd.portName, cmd.motorType);
					motors[i].init();
				} else if (motors[i] != null){
					motors[i].executeCmd(cmd, pw);
				} else {
					// TODO either repeated init or command on non inited motor
				}

			// Sensor commands
			} else if (cmd.dev == DevType.SENSOR) {
				int i = cmd.portIndex;
				if (cmd.cmd == CmdType.INIT && sensors[i] == null) {
					switch (cmd.sensorType) {
						case COLOR: sensors[i] = new ColorSensor(cmd.port, cmd.portName);break;
						case IR: sensors[i] = new IRSensor(cmd.port, cmd.portName);break;
						case TOUCH: sensors[i] = new TouchSensor(cmd.port, cmd.portName);
						default: // TODO
					}
				} else if (cmd.cmd == CmdType.CLOSE) {
					//TODO should not throw error if sensor wasn't initialized
					sensors[i].close();
					sensors[i] = null;
				} else if (sensors[i] != null) {
					sensors[i].executeCmd(cmd, pw);
				} else {					
					// TODO either repeated init or command on non inited sensor
				}
				
			// Camera commands
			} else if (cmd.dev == DevType.CAMERA) {
				if (cmd.cmd == CmdType.INIT && camera == null) {
					if (cmd.camWidth > 0 && cmd.camHeight > 0) {
						camera = new Camera(cmd.camWidth, cmd.camHeight);
					} else {
						camera = new Camera();
					}
				} else if (cmd.cmd == CmdType.TAKEPIC) {
					camera.takePicture(outToClient);
				} else if (cmd.cmd == CmdType.CLOSE) {
					camera.closeCamera();
					camera = null;
				}
			// Unsupported device
			} else {
				// TODO
			}
		}
		welcomeSocket.close();
	}

	public static String padString(String str) {
		// pads string with spaces to length 100
		String length = "100";
		return String.format("%1$-" + length + "s", str);
	}
}