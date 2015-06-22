package org.lejos.ev3.example;

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
			JSONObject contents = (JSONObject) obj.get("command");
			String cmd = (String) contents.get("cmd");

			if (cmd.equals(new String("beep"))) {
				Sound.beep();
				//System.out.println("Beep.");
			} else if (cmd.equals(new String("buzz"))) {
				Sound.buzz();
				//System.out.println("Buzz.");
			} else if (cmd.equals(new String("motor"))) {
				String which = (String) contents.get("which");
				long degrees = (long) contents.get("degrees");
				Port port = null;
				switch (which) {
					case "A": port = MotorPort.A;break;
					case "B": port = MotorPort.B;break;
					case "C": port = MotorPort.C;break;
					case "D": port = MotorPort.D;break;
				}
				RegulatedMotor m = new EV3LargeRegulatedMotor(port);
				m.rotate((int) degrees);
				m.close();
				//System.out.println("Motor " + which + " " + Long.toString(degrees) + " degrees.");
			} else if (cmd.equals(new String("exit"))) {
				//System.out.println("Exit.");
				break;
			} else {
				LCD.drawString("Unknown cmd: " + cmd, 0, 4);
			}
		}
	}
}