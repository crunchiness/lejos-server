package lejosserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejosserver.Command.CmdType;
import lejosserver.Command.DevType;
import lejosserver.ErrorMode.ErrorType;

public class LocalServer {

	// Motor ports: A, B, C, D
	private static Motor[] motors = {null, null, null, null};
	// Sensor ports: S1, S2, S3, S4
	private static Sensor[] sensors = {null, null, null, null};

	private static Camera camera;

	private static ServerSocket socket;

	public static boolean terminate = false;

	private static ExitButtonThread exitThread;

	private static class ExitButtonThread extends Thread {
		@Override
		public void run() {
			Button.ESCAPE.waitForPressAndRelease();
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			terminate = true;
		}
	}

	private static RegulatedMotor mB = null, mC = null;

	public static void main(String argv[]) throws Exception {
		String clientSentence;
		Socket connectionSocket;
		socket = new ServerSocket(6789);
		initExitThread();
		LCD.drawString("READY", 0, 4);
		while (!terminate) {
			try {
				connectionSocket = socket.accept();
			} catch (SocketException e) {
				// Shutting down everything via ESCAPE button
				break;
			}
			BufferedReader inReader = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			clientSentence = inReader.readLine();
			DataOutputStream outStream = new DataOutputStream(connectionSocket.getOutputStream());
			PrintWriter pw = new PrintWriter(outStream);
			//PrintWriter pw = new PrintWriter(connectionSocket.getOutputStream(), true);

			Util.drawString(clientSentence);

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
					case LED: Button.LEDPattern(cmd.intParam1);break;
					case PLAYWAV: PlayWAV(cmd.strParam1, cmd.intParam1);break;
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
			} else if  (cmd.dev == DevType.WHEELS) {
				if (mB == null) mB = new EV3LargeRegulatedMotor(MotorPort.B);
				if (mC == null) mC = new EV3LargeRegulatedMotor(MotorPort.C);
				if (cmd.cmd == CmdType.ISMOVING) {
					isMoving(pw, new Boolean(mB.isMoving()));
				}
				else {
					mB.setSpeed(cmd.speed);
					mC.setSpeed(cmd.speed);
					mB.synchronizeWith(new RegulatedMotor[] {mC});
					mB.startSynchronization();
					if (cmd.cmd == CmdType.GO_FORWARD) {
						mB.rotate(cmd.rotateDeg, true);
						mC.rotate(cmd.rotateDeg, true);
					} else if (cmd.cmd == CmdType.GO_BACKWARD) {
						mB.rotate(-cmd.rotateDeg, true);
						mC.rotate(-cmd.rotateDeg, true);
					} else if (cmd.cmd == CmdType.TURN_LEFT) {
						mB.rotate(-cmd.rotateDeg, true);
						mC.rotate(cmd.rotateDeg, true);
					} else if (cmd.cmd == CmdType.TURN_RIGHT) {
						mB.rotate(cmd.rotateDeg, true);
						mC.rotate(-cmd.rotateDeg, true);
					} else if (cmd.cmd == CmdType.STOP) {
						mB.stop();
						mC.stop();
					}
					mB.endSynchronization();
				}
			// Unsupported device
			} else {
				new ErrorMode(ErrorType.SYSTEM_ERROR, "main loop sensors");
			}
		}
		socket.close();
	}

	public static void initExitThread() {
		exitThread = new ExitButtonThread();
		exitThread.setDaemon(true);
		exitThread.start();
	}

	public static String padString(String str) {
		// pads string with spaces to length 100
		String length = "100";
		return String.format("%1$-" + length + "s", str);
	}

	@SuppressWarnings("unchecked")
	private static void isMoving(PrintWriter pw, Boolean isMoving) throws IOException {
		JSONObject outputObj = new JSONObject();
		outputObj.put("ismoving", isMoving);
		outputObj.put("dev", "wheelB");
		StringWriter out = new StringWriter();
		outputObj.writeJSONString(out);
		String jsonOutput = out.toString();
		pw.println(LocalServer.padString(jsonOutput));
		pw.flush();
	}

	static WAVThread _wt = null;
	private static void PlayWAV(String fileName, int vol) {
		java.io.File wavFile = new java.io.File(fileName);
		if (wavFile.exists() && _wt == null) {
			//Sound.playSample(wavFile, vol);
			//WAVThread wt = new WAVThread(wavFile, vol);
			_wt = new WAVThread(wavFile, vol);
			_wt.start();
		}
		else {
			Util.drawString("File not found: " + fileName);
		}
	}

}

class WAVThread extends Thread {
	private java.io.File _file;
	private int _vol;
	public WAVThread(java.io.File wavFile, int vol)
	{
		_file = wavFile;
		_vol = vol;
	}

    public void run() {
    	Sound.playSample(_file, _vol);
    }
}
