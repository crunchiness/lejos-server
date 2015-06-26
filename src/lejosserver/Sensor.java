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

	public Sensor(Port port) {
		this.port = port;
		this.numberOfValues = 1; // how many values fetchSample returns depends on sensor mode
	}
	
	abstract public void setMode(String name);
//	public String getMode() {
//		JSONObject outputObj = new JSONObject();
//		outputObj.put("value", modeName);
//		outputObj.put("dev", "sensor");
//		outputObj.put("mode", modeName);
//		outputObj.put("port", port.toString()); //TODO fix
//		StringWriter out = new StringWriter();
//		outputObj.writeJSONString(out);
//		String jsonOutput = out.toString();
//		
//		// Pad since MATLAB is expecting 100 byte reply
//		return LocalServer.padString(jsonOutput);
//		
//		
//		return modeName;
//	}
	

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
		outputObj.put("port", port.toString()); //TODO fix
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
}
