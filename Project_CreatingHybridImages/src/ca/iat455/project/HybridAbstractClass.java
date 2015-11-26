package ca.iat455.project;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * This class provides the fields and methods to produce a hybrid image and
 * display them in a window.
 * 
 * @author Melissa Wang
 */
public class HybridAbstractClass extends JFrame {
	private static final long serialVersionUID = 1L;
	
	// Fields for output display
	protected ArrayList<BufferedImage> inputImages = new ArrayList<BufferedImage>();
	protected ArrayList<BufferedImage> outputImages = new ArrayList<BufferedImage>();
	protected int width, height;
	
	///////////////////////////////////////// Process /////////////////////////////////////////
	
	public BufferedImage convolve(BufferedImage img, Filters filter) {
		WritableRaster wRaster = img.copyData(null);
		BufferedImage result = new BufferedImage(img.getColorModel(), wRaster, img.isAlphaPremultiplied(), null);

		// Determine kernel size
		int kernelSize;
		switch (filter) {
		case LOW_FREQ:
			kernelSize = 7;
			break;
		case HIGH_FREQ:
			kernelSize = 3;
			break;
		default:
			kernelSize = 0;
			break;
		} // switch

		// Apply spatial convolution to image
		int edge = kernelSize / 2;
		for (int x = edge; x < result.getWidth() - edge; x++) {
			for (int y = edge; y < result.getHeight() - edge; y++) {
				ArrayList<Integer> rgbs = new ArrayList<Integer>();

				switch (filter) {
				case LOW_FREQ:
					int[] indicesBlur = { x-3, x-2, x-1, x, x+1, x+2, x+3,
										  y-3, y-2, y-1, y, y+1, y+2, y+3 };
					rgbs = getRGBs(img, rgbs, indicesBlur);
					break;
				case HIGH_FREQ:
					int[] indicesEdge = { x-1, x, x+1,
										  y-1, y, y+1 };
					rgbs = getRGBs(img, rgbs, indicesEdge);
					break;
				default:
					break;
				} // switch

				result.setRGB(x, y, computeRGB(rgbs, filter));
			}
		}
		return result;
	} // convolve

	public ArrayList<Integer> getRGBs(BufferedImage img, ArrayList<Integer> rgbs, int[] indices) {
		// Determine the boundaries between x and y in the array parameter
		int xBegin = 0;
		int xEnd = indices.length / 2 - 1;
		int yBegin = indices.length / 2;
		int yEnd = indices.length - 1;

		// Get the RGB values of the kernel's pixels
		for (int x = xBegin; x <= xEnd; x++) {
			for (int y = yBegin; y <= yEnd; y++) {
				rgbs.add(img.getRGB(indices[x], indices[y]));
			}
		}

		return rgbs;
	} // getRGBs

	public int computeRGB(ArrayList<Integer> rgbs, Filters filter) {
		ArrayList<Integer> reds = new ArrayList<Integer>();
		ArrayList<Integer> greens = new ArrayList<Integer>();
		ArrayList<Integer> blues = new ArrayList<Integer>();

		// Separate all of the kernel pixels' channels
		for (int i = 0; i < rgbs.size(); i++) {
			int rgb = rgbs.get(i);
			reds.add(getRed(rgb));
			greens.add(getGreen(rgb));
			blues.add(getBlue(rgb));
		}

		// Apply filter to each channel
		int r = convolveChannel(reds, filter);
		int g = convolveChannel(greens, filter);
		int b = convolveChannel(blues, filter);

		return new Color(r, g, b).getRGB();
	} // computeRGB

	public int convolveChannel(ArrayList<Integer> channel, Filters filter) {
		int c = 0;

		switch (filter) {
		case LOW_FREQ:
			// Blur: Average the pixels
			for (int i = 0; i < channel.size(); i++) {
				c += channel.get(i);
			}
			return c / channel.size();
		case HIGH_FREQ:
			// Gaussian derivative? Multiply kernel with Sobel (edge-emphasizing) filter
			int[] sobelFilter = { 1, 2, 1, 0, 0, -0, -1, -2, -1 };
			for (int i = 0; i < channel.size(); i++) {
				c += channel.get(i) * sobelFilter[i];
			}
			return clip(c);
		default:
			return 0;
		}
	} // convolveChannel

	public BufferedImage grayscale(BufferedImage img) {
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				int rgb = img.getRGB(x, y);
				float[] hsv = new float[3];
				Color.RGBtoHSB(getRed(rgb), getGreen(rgb), getBlue(rgb), hsv);
				result.setRGB(x, y, Color.HSBtoRGB(0, 0, hsv[2]));
			}
		}

		return result;
	} // grayscale

	public BufferedImage dissolve(BufferedImage imgA, BufferedImage imgB, float mixVal) {
		WritableRaster wRaster = imgA.copyData(null);
		BufferedImage result = new BufferedImage(imgA.getColorModel(), wRaster, imgA.isAlphaPremultiplied(), null);

		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				int rgbA = imgA.getRGB(x, y);
				int rgbB = imgB.getRGB(x, y);

				// O = (MV * A) + [(1 � MV) * B]
				int r = clip((int) ((mixVal * getRed(rgbA)) + (1 - mixVal) * getRed(rgbB)));
				int g = clip((int) ((mixVal * getGreen(rgbA)) + (1 - mixVal) * getGreen(rgbB)));
				int b = clip((int) ((mixVal * getBlue(rgbA)) + (1 - mixVal) * getBlue(rgbB)));

				result.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}

		return result;
	} // dissolve
	
	public BufferedImage brighten(BufferedImage img, float factor) {
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

		for (int i = 0; i < result.getWidth(); i++) {
			for (int j = 0; j < result.getHeight(); j++) {
				int rgb = img.getRGB(i, j);
				int r = clip((int)(getRed(rgb) * factor));
				int g = clip((int)(getGreen(rgb) * factor));
				int b = clip((int)(getBlue(rgb) * factor));
				result.setRGB(i, j, new Color(r, g, b).getRGB());
			}
		}

		return result;
	} // brighten
	
	///////////////////////////////////////// Helper /////////////////////////////////////////

	protected int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	protected int getRed(int rgb) {
		return new Color(rgb).getRed();
	}

	protected int getGreen(int rgb) {
		return new Color(rgb).getGreen();
	}

	protected int getBlue(int rgb) {
		return new Color(rgb).getBlue();
	}
	
	///////////////////////////////////////// Setup /////////////////////////////////////////
	
	protected void loadImages(String[] imgNames, boolean isJPG) {
		try {
			String extension = (isJPG) ? ".jpg" : ".png";
			
			for (String name : imgNames) {
				File file = new File(name + extension);
				BufferedImage img = ImageIO.read(file);
				inputImages.add(img);
			}
		} catch (Exception e) {
			System.out.println("Cannot load image");
		}

		BufferedImage img = inputImages.get(0);
		width = img.getWidth();
		height = img.getHeight();
	} // loadImages
	
	///////////////////////////////////////// Display /////////////////////////////////////////
	
	protected void setupWindow(String title) {
		setTitle(title);
		setMinimumSize(new Dimension(1200, 700));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	} // setupWindow
} // Window
