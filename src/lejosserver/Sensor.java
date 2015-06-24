package lejosserver;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;

public class Sensor {
	private Port port;
	private EV3IRSensor s;
	private SensorMode distanceMode;
	public Sensor(Port port) {
		this.port = port;
		this.s = new EV3IRSensor(port);
		this.distanceMode = this.s.getDistanceMode();
	}
	
	@SuppressWarnings("unchecked")
	public String getDistance() throws IOException {
		float[] sample = new float[1];
		distanceMode.fetchSample(sample, 0);
		
		JSONObject outputObj = new JSONObject();
		
		outputObj.put("distance", sample[0]);
		outputObj.put("dev", "sensor");
		outputObj.put("port", port.toString()); //TODO fix
		StringWriter out = new StringWriter();
		outputObj.writeJSONString(out);
		String jsonOutput = out.toString();
		
		return LocalServer.padString(jsonOutput);
	}
	
}