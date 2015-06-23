package lejosserver;

import java.io.*;
import java.net.*;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LocalServer {
	
	// Motor ports: A, B, C, D
	private Motor[] motors = {null, null, null, null};
	// Sensor ports: 1, 2, 3, 4
	private Sensor[] sensors = {null, null, null, null};
	
	public LocalServer() {}
	
	public static void main(String argv[]) throws Exception {
		LocalServer server = new LocalServer();
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);
		LCD.drawString("OK", 0, 4);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(
					connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			LCD.drawString("Received: " + clientSentence, 0, 4);
			
			JSONObject obj = (JSONObject) JSONValue.parse(clientSentence);
			JSONObject contents;
			try {
				contents = (JSONObject) obj.get("command");
			} catch (NullPointerException e) {
				LCD.drawString("Bad input: " + clientSentence, 0, 4);
				break;
			}

			String dev = (String) contents.get("dev");
			String cmd = (String) contents.get("cmd");
			
			// Brick commands
			if (dev.equals(new String("brick"))) {
				if (cmd.equals(new String("beep"))) {
					Sound.beep();
				} else if (cmd.equals(new String("buzz"))) {
					Sound.buzz();
				} else if (cmd.equals(new String("exit"))) {
					break;
				} else {
					LCD.drawString("Unknown cmd: " + cmd, 0, 4);
				}

			// Motor commands
			} else if (dev.equals(new String("motor"))) {
				String port = (String) contents.get("port");
				if (cmd.equals(new String("init"))) {
					server.init_motor(port);
				} else {
					int portIndex = getPortIndex(port);
					if (portIndex == -1) {
						LCD.drawString("Bad motor port: " + port, 0, 4);
						//break;
					}
					try {
						server.motors[portIndex].actionQueue.add(cmd);
					} catch(IllegalStateException e) {
						// TODO queue is full
					}
				}

			// Unsupported device
			} else {
				LCD.drawString("Unsupported device: " + dev, 0, 4);
			}
		}
	}

	private static int getPortIndex(String port) {
		int portIndex;
		if (port.equals(new String("A"))) { portIndex = 0; }
		else if (port.equals(new String("B"))) { portIndex = 1; }
		else if (port.equals(new String("C"))) { portIndex = 2; }
		else if (port.equals(new String("D"))) { portIndex = 3; }
		else { portIndex = -1; }
		return portIndex;
	}
	
	private void init_motor(String port) {
		int portIndex = getPortIndex(port);
		if (portIndex == -1) {
			LCD.drawString("Bad motor port: " + port, 0, 4);
			return;
		}
		if (motors[portIndex] == null) {
			motors[portIndex] = new Motor(port);
			motors[portIndex].init();
		} else {
			LCD.drawString("Motor already init", 0, 4);
		}
	}
}