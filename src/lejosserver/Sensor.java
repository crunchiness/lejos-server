package lejosserver;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.SensorMode;

public abstract class Sensor {
	public Port port;
	public SensorMode mode;
	public String modeName;
	public int numberOfValues;
	private String portName;

	public Sensor(Port port, String portName) {
		this.port = port;
		this.portName = portName;
		this.numberOfValues = 1; // how many values fetchSample returns depends on sensor mode
	}
	
	abstract public void setMode(String name);
	

	@SuppressWarnings("unchecked")
	public String getValue() throws IOException {
		
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
		return LocalServer.padString(jsonOutput);
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
	public String getMode() throws IOException {
		
		// Put values into JSON
		JSONObject outputObj = new JSONObject();
		outputObj.put("dev", "sensor");
		outputObj.put("mode", modeName);
		outputObj.put("port", portName);
		StringWriter out = new StringWriter();
		outputObj.writeJSONString(out);
		String jsonOutput = out.toString();
		
		// Pad since MATLAB is expecting 100 byte reply
		return LocalServer.padString(jsonOutput);
	}
}
