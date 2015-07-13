package lejosserver;

import lejos.hardware.lcd.LCD;

public class Util {
	private static int CHARS_PER_LINE = 17;
	private static int LINE_HEIGHT = 1;
	public static void drawString(String str) {
		LCD.clear();
		int strLen = str.length();
		for (int i = 0; i*CHARS_PER_LINE < strLen; i++) {
			int from = i*CHARS_PER_LINE;
			int to = i*CHARS_PER_LINE + CHARS_PER_LINE;
			String subStr;
			try {
				subStr = str.substring(from, to);
			} catch (StringIndexOutOfBoundsException e) {
				subStr = str.substring(from);
			}
			LCD.drawString(subStr, 0, i*LINE_HEIGHT);
		}
	}
}
