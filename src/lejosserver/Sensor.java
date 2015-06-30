package lejosserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lejos.hardware.sensor.SensorMode;
import lejosserver.Command.Mode;

public abstract class Sensor {
	public SensorMode mode;
	public String modeName;
	public int numberOfValues;
	private String portName;

	public Sensor(String portName) {
		this.portName = portName;
		this.numberOfValues = 1; // how many values fetchSample returns depends on sensor mode
	}
	
	abstract public void setMode(Mode sensorMode, String modeName);
	abstract public void close();
	

	@SuppressWarnings("unchecked")
	public void getValue(PrintWriter pw) throws IOException {
		
		float[] sample = new float[this.numberOfValues];
		mode.fetchSample(sample, 0);
		
		JSONArray valueList = new JSONArray();
		valueList.addAll(toList(sample));
		
		// Put values into JSON
		JSONObject outputObj = new JSONObject();
		outputObj.put("value", valueList);
		outputObj.put("dev", "sensor");
		outputObj.put("mode", modeName);
		outputObj.put("port", portName);
		StringWriter out = new StringWriter();
		outputObj.writeJSONString(out);
		String jsonOutput = out.toString();
		
		// Pad since MATLAB is expecting 100 byte reply
		pw.println(LocalServer.padString(jsonOutput));
		pw.flush();
	}
	
	private static List<Float> toList(float[] array) {
		int size = array.length;
		List<Float> list = new ArrayList<Float>(size);
		for(int i = 0; i < size; i++) {
			list.add(array[i]);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public void getMode(PrintWriter pw) throws IOException {
		
		// Put values into JSON
		JSONObject outputObj = new JSONObject();
		outputObj.put("dev", "sensor");
		outputObj.put("mode", modeName);
		outputObj.put("port", portName);
		StringWriter out = new StringWriter();
		outputObj.writeJSONString(out);
		String jsonOutput = out.toString();
		
		// Pad since MATLAB is expecting 100 byte reply
		pw.println(LocalServer.padString(jsonOutput));
		pw.flush();
	}

	public void executeCmd(Command cmd, PrintWriter pw) throws IOException {
		switch(cmd.cmd) {
			case GETVALUE: getValue(pw);break;
			case GETMODE: getMode(pw);break;
			case SETMODE: setMode(cmd.sensorMode, cmd.modeName);break;
			case CLOSE: break;
			default: //TODO
		}
	}

}
