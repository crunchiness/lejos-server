package lejosserver;

import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import lejos.hardware.BrickFinder;
import lejos.hardware.video.Video;

public class Camera {

	private static final int DEFAULT_WIDTH = 160;
	private static final int DEFAULT_HEIGHT = 120;
	private int WIDTH;
	private int HEIGHT;
	private int NUM_PIXELS;
	private int FRAME_SIZE;
	private Video camera;
// TODO camera init command
	public Camera() {
		this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public Camera(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
		NUM_PIXELS = width * height;
		FRAME_SIZE = NUM_PIXELS * 2;
		
		camera = BrickFinder.getDefault().getVideo();
		try {
			camera.open(WIDTH, HEIGHT);
		} catch (IOException e) {
			// TODO what?
			e.printStackTrace();
		}
	}

	public void takePicture() throws IOException {
		byte[] frame = camera.createFrame();
		try {
			camera.grabFrame(frame);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < FRAME_SIZE; i += 4) {
			int y1 = frame[i] & 0xFF;
			int y2 = frame[i + 2] & 0xFF;
			int u = frame[i + 1] & 0xFF;
			int v = frame[i + 3] & 0xFF;
			int rgb1 = convertYUVtoARGB(y1, u, v);
			int rgb2 = convertYUVtoARGB(y2, u, v);
			img.setRGB((i % (WIDTH * 2)) / 2, i / (WIDTH * 2), rgb1);
			img.setRGB((i % (WIDTH * 2)) / 2 + 1, i / (WIDTH * 2), rgb2);
		}
		File f = new File("image.png");
		ImageIO.write(img, "png", f);
		//out.flush();
	}

	private static int convertYUVtoARGB(int y, int u, int v) {
		int c = y - 16;
		int d = u - 128;
		int e = v - 128;
		int r = (298 * c + 409 * e + 128) / 256;
		int g = (298 * c - 100 * d - 208 * e + 128) / 256;
		int b = (298 * c + 516 * d + 128) / 256;
		r = r > 255 ? 255 : r < 0 ? 0 : r;
		g = g > 255 ? 255 : g < 0 ? 0 : g;
		b = b > 255 ? 255 : b < 0 ? 0 : b;
		return 0xff000000 | (r << 16) | (g << 8) | b;
	}
}
