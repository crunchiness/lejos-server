package lejosserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.simple.JSONObject;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejosserver.Command.CmdType;

public class Wheels {
	private static RegulatedMotor mB = null, mC = null;
	private static MotorPort _mp1, _mp2;

	public Wheels(MotorPort port1, MotorPort port2) {
		_mp1 = port1;
		_mp2 = port2;
	}

	public void executeCmd(Command cmd, PrintWriter pw) throws IOException {
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
				mB.rotate(cmd.rotateDeg, true);
				mC.rotate(-cmd.rotateDeg, true);
			} else if (cmd.cmd == CmdType.TURN_RIGHT) {
				mB.rotate(-cmd.rotateDeg, true);
				mC.rotate(cmd.rotateDeg, true);
			} else if (cmd.cmd == CmdType.STOP) {
				mB.stop();
				mC.stop();
			}
			mB.endSynchronization();
		}
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

}