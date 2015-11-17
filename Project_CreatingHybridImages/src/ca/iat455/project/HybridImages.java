package ca.iat455.project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class HybridImages extends Frame {
	private final static int X_OFFSET = 25;
	private final static int Y_OFFSET = 40;
	private final static int Y_FONT_OFFSET = 5;
	private final static String[] labels = { "Source1", "Source 2", "Original filter 1", "Original filter 2",
			"Original hybrid", "Blur", "Gaussian derivative", "Intermediate step?", "Hybrid" };

	private BufferedImage imgA;
	private BufferedImage imgB;

	private BufferedImage origFilteredImgA;
	private BufferedImage origFilteredImgB;
	private BufferedImage origHybridImg;

	private BufferedImage filteredImgA;
	private BufferedImage filteredImgB1;
	private BufferedImage filteredImgB2;
	private BufferedImage filteredImgB3;
	private BufferedImage hybridImg;

	private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
	private int width, height;

	public HybridImages() {
		loadImages();

		filteredImgA = convolve(imgA, Filters.BLUR);
		filteredImgB1 = convolve(imgB, Filters.GAUSSIAN_DERIV);
		hybridImg = dissolve(filteredImgA, filteredImgB1, 0.5f);

		// Row 1
		images.add(imgA);
		images.add(imgB);

		// Row 2
		images.add(origFilteredImgA);
		images.add(origFilteredImgB);
		images.add(origHybridImg);

		// Row 3
		images.add(filteredImgA);
		images.add(filteredImgB1);
		images.add(filteredImgB2);
//		images.add(filteredImgB3);
		images.add(hybridImg);

		setupWindow();
	} // Constructor
	
	private BufferedImage convolve(BufferedImage img, Filters filter) {
		WritableRaster wRaster = img.copyData(null);
		BufferedImage result = new BufferedImage(img.getColorModel(), wRaster, img.isAlphaPremultiplied(), null);

		int kernelSize = 0; // aka sigma

		switch (filter) {
		case BLUR:
			kernelSize = 7;
			break;
		case GAUSSIAN_DERIV:
			kernelSize = 3;
			break;
		}

		for (int x = kernelSize; x < result.getWidth() - kernelSize; x++) {
			for (int y = kernelSize; y < result.getHeight() - kernelSize; y++) {
				ArrayList<Integer> rgbs = new ArrayList<Integer>();

				switch (filter) {
				case BLUR:
					int[] indicesBlur = { x - 3, x - 2, x - 1, x, x + 1, x + 2, x + 3, y - 3, y - 2, y - 1, y, y + 1,
							y + 2, y + 3 };
					rgbs = getRGBs(img, rgbs, indicesBlur);
				case GAUSSIAN_DERIV:
					int[] indicesEdge = { x - 1, x, x + 1, y - 1, y, y + 1 };
					rgbs = getRGBs(img, rgbs, indicesEdge);
				} // switch

				result.setRGB(x, y, computeRGB(rgbs, filter));
			}
		}
		return result;
	} // convolve

	private ArrayList<Integer> getRGBs(BufferedImage img, ArrayList<Integer> rgbs, int[] indices) {
		int xBegin = 0;
		int xEnd = indices.length / 2 - 1;
		int yBegin = indices.length / 2;
		int yEnd = indices.length - 1;

		for (int x = xBegin; x <= xEnd; x++) {
			for (int y = yBegin; y <= yEnd; y++) {
				rgbs.add(img.getRGB(indices[x], indices[y]));
			}
		}

		return rgbs;
	}

	private int computeRGB(ArrayList<Integer> rgbs, Filters filter) {
		ArrayList<Integer> reds = new ArrayList<Integer>();
		ArrayList<Integer> greens = new ArrayList<Integer>();
		ArrayList<Integer> blues = new ArrayList<Integer>();

		for (int i = 0; i < rgbs.size(); i++) {
			int rgb = rgbs.get(i);
			reds.add(getRed(rgb));
			greens.add(getGreen(rgb));
			blues.add(getBlue(rgb));
		}

		int r = convolveChannel(reds, filter);
		int g = convolveChannel(greens, filter);
		int b = convolveChannel(blues, filter);

		return new Color(r, g, b).getRGB();
	}

	private int convolveChannel(ArrayList<Integer> channel, Filters filter) {
		int c = 0;

		switch (filter) {
		case BLUR:
			for (int i = 0; i < channel.size(); i++) {
				c += channel.get(i);
			}
			return c / channel.size();
		case GAUSSIAN_DERIV:
			int[] sobelFilter = { 1, 2, 1, 0, 0, -0, -1, -2, -1 };
			for (int i = 0; i < channel.size(); i++) {
				c += channel.get(i) * sobelFilter[i];
			}
			return clip(c);
		default:
			return 0;
		}
	} // convolveChannel

	private BufferedImage greyscale(BufferedImage img) {
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
	} // monochrome

	public BufferedImage invert(BufferedImage img) {
		BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());

		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				int rgb = img.getRGB(x, y);
				int r = clip(255 - getRed(rgb));
				int g = clip(255 - getGreen(rgb));
				int b = clip(255 - getBlue(rgb));
				result.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}

		return result;
	}

	private BufferedImage dissolve(BufferedImage imgA, BufferedImage imgB, float mixVal) {
		WritableRaster wRaster = imgA.copyData(null);
		BufferedImage result = new BufferedImage(imgA.getColorModel(), wRaster, imgA.isAlphaPremultiplied(), null);

		for (int x = 0; x < result.getWidth(); x++) {
			for (int y = 0; y < result.getHeight(); y++) {
				int rgbA = imgA.getRGB(x, y);
				int rgbB = imgB.getRGB(x, y);

				// O = (MV * A) + [(1 – MV) * B]
				int r = clip((int) ((mixVal * getRed(rgbA)) + (1 - mixVal) * getRed(rgbB)));
				int g = clip((int) ((mixVal * getGreen(rgbA)) + (1 - mixVal) * getGreen(rgbB)));
				int b = clip((int) ((mixVal * getBlue(rgbA)) + (1 - mixVal) * getBlue(rgbB)));

				result.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}

		return result;
	} // dissolve

	private BufferedImage combine(BufferedImage imgA, BufferedImage imgB, Operations operation) {
		BufferedImage result = new BufferedImage(imgA.getWidth(), imgA.getHeight(), imgA.getType());

		for (int i = 0; i < result.getWidth(); i++)
			for (int j = 0; j < result.getHeight(); j++) {
				int rgb1 = imgA.getRGB(i, j);
				int rgb2 = imgB.getRGB(i, j);

				int r = 0, g = 0, b = 0;

				if (operation == Operations.add) {
					r = getRed(rgb1) + getRed(rgb2);
					g = getGreen(rgb1) + getGreen(rgb2);
					b = getBlue(rgb1) + getBlue(rgb2);

				} else if (operation == Operations.multiply) {
					r = (getRed(rgb1) * getRed(rgb2)) / 255;
					g = (getGreen(rgb1) * getGreen(rgb2)) / 255;
					b = (getBlue(rgb1) * getBlue(rgb2)) / 255;
				}

				result.setRGB(i, j, new Color(clip(r), clip(g), clip(b)).getRGB());
			} // for
		return result;
	} // combineImages

	private void loadImages() {
		try {
			imgA = ImageIO.read(new File("elephant.png"));
			imgB = ImageIO.read(new File("jaguar.png"));
			origFilteredImgA = ImageIO.read(new File("filteredElephant.png"));
			origFilteredImgB = ImageIO.read(new File("filteredJaguar.png"));
			origHybridImg = ImageIO.read(new File("hybrid.png"));

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}

		width = imgB.getWidth();
		height = imgB.getHeight();
	}

	private void setupWindow() {
		setTitle("Hybrid Images");
		setVisible(true);
		setSize(width * 6, height * 6);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	private int getRed(int rgb) {
		return new Color(rgb).getRed();
	}

	private int getGreen(int rgb) {
		return new Color(rgb).getGreen();
	}

	private int getBlue(int rgb) {
		return new Color(rgb).getBlue();
	}

	public void paint(Graphics g) {
		int w = width;
		int h = height;

		g.setColor(Color.BLACK);
		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(f1);

		int x = X_OFFSET;
		int y = Y_OFFSET + Y_FONT_OFFSET;

		for (int i = 0; i < images.size(); i++) {
			g.drawString(labels[i], x, y - Y_FONT_OFFSET);
			g.drawImage(images.get(i), x, y, w, h, this);
			x += w + X_OFFSET;

			if (i == 1 || i == 4) {
				x = X_OFFSET;
				y += h + Y_OFFSET;
			}
		}
	} // paint

	public static void main(String[] args) {
		HybridImages hybridImages = new HybridImages();
		hybridImages.repaint();
	}
} // TestHybrid
