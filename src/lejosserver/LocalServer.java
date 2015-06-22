package lejosserver;

import java.io.*;
import java.net.*;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class LocalServer {
	
	public static void main(String argv[]) throws Exception {
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
				LCD.drawString("Unknown command: " + clientSentence, 0, 4);
				break;
			}
			String cmd = (String) contents.get("cmd");

			if (cmd.equals(new String("init"))) {
				
			} else if (cmd.equals(new String("beep"))) {
				Sound.beep();
			} else if (cmd.equals(new String("buzz"))) {
				Sound.buzz();
			} else if (cmd.equals(new String("motor"))) {
				String which = (String) contents.get("which");
				long degrees = (long) contents.get("degrees");
				(new Thread(new EV3LargeMotorAction(which, degrees))).start();
			} else if (cmd.equals(new String("exit"))) {
				break;
			} else {
				LCD.drawString("Unknown cmd: " + cmd, 0, 4);
			}
		}
	}
}