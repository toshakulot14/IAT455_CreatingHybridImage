
/*File ImageBasics.java

 IAT455 - Workshop week 3
 Basic Image Manipulation
 
 Starter code
 **********************************************************/
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

class ImageManipulation extends Frame { 
	BufferedImage testImage; 
	BufferedImage testImage1;

	BufferedImage brightnessImage; 
	BufferedImage RGBmultiplyImage; 
	BufferedImage invertImage;
	BufferedImage contrastImage;
	BufferedImage monochrome1Image;
	BufferedImage monochrome2Image;
	BufferedImage edgeDetectionImage;

	int width; // width of the image
	int height; // height of the image
	
	int width1; // width of the image
	int height1; // height of the image

	public ImageManipulation() {
		// constructor
		// Get an image from the specified file in the current directory on the
		// local hard disk.
		try {
			testImage = ImageIO.read(new File("bird1.jpg"));
			testImage1 = ImageIO.read(new File("church.jpg"));

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Week 3 workshop - Basic image manipulation");
		this.setVisible(true);

		width = testImage.getWidth();
		height = testImage.getHeight();
		
		width1 = testImage1.getWidth();
		height1 = testImage1.getHeight();

		brightnessImage = filterImage(testImage, Filters.brightness);
		RGBmultiplyImage = filterImage(testImage, Filters.RGBmultiply);
		invertImage = filterImage(testImage, Filters.invert);
		contrastImage = filterImage(testImage, Filters.contrast); 
		monochrome1Image = filterImage(testImage, Filters.monochrome_average); 
		monochrome2Image = filterImage(testImage, Filters.monochrome_perceptual);

		edgeDetectionImage = convolve(testImage1); 

		//Anonymous inner-class listener to terminate program
		this.addWindowListener(
				new WindowAdapter(){//anonymous class definition
					public void windowClosing(WindowEvent e){
						System.exit(0);//terminate the program
					}//end windowClosing()
				}//end WindowAdapter
				);//end addWindowListener
	}// end constructor

	public BufferedImage filterImage (BufferedImage img, Filters filt)
	//produce the result image for each operation
	{
		int width = img.getWidth(); 
		int height = img.getHeight(); 

		WritableRaster wRaster = img.copyData(null);
		BufferedImage copy = new BufferedImage(img.getColorModel(), wRaster, img.isAlphaPremultiplied(), null);

		//apply the operation to each pixel
		for (int i = 0; i < width; i++){ 
			for (int j = 0; j < height; j++) {
				int rgb = img.getRGB(i, j);
				copy.setRGB(i, j, filterPixel(rgb, filt));}}
		return copy; 
	}
		
	public int filterPixel(int rgb, Filters filt) 
	{ //operation to be applied to each pixel
				
		int alpha = (rgb >>> 24) & 0xff;
		int red = (rgb >>> 16) & 0xff;
		int green = (rgb >>> 8) & 0xff;
		int blue = rgb & 0xff; 
		Color c;
		
		switch (filt) {
		case brightness:  //O = I*2
			//write code
			red*=2.0;
			if (red>255){
				red=255;
			}
			green*=2.0;
			if (green>255){
				green = 255;
			}
			blue*=2.0;
			if (blue>255){
				blue = 255;
			}
			c = new Color(red, green, blue);
			return c.getRGB();

		case RGBmultiply: //R=R*0.1, G=G*1.25, B=B*1
			//write code
			red*=0.1;
			if (red>255){
				red=255;
			}
			green*=1.25;
			if (green>255){
				green = 255;
			}
//			blue*=2.0;
//			if (blue>255){
//				blue = 255;
//			}
			c = new Color(red, green, blue);
			return c.getRGB();

		case invert: //O=1=I
			//write code
			// 255 for not normalized rgb
			red=255-red;
			if (red>255){
				red=255;
			}
			green=255-green;
			if (green>255){
				green = 255;
			}
			blue=255-blue;
			if (blue>255){
				blue = 255;
			}
			c = new Color(red, green, blue);
			return c.getRGB();
			
		case contrast: //O=(I-0.33)*3
			//write code
			// 0.33 unnormalized is 84.2
			red=(int)((red-84.2)*3);
			if (red>255){
				red=255;
			} else if (red<0) {
				red=0;
			}
			green=(int)((green-84.2)*3);
			if (green>255){
				green = 255;
			} else if (green<0) {
				green=0;
			}
			blue=(int)((blue-84.2)*3);
			if (blue>255){
				blue = 255;
			} else if (blue<0) {
				blue=0;
			}
			c = new Color(red, green, blue);
			return c.getRGB();

		case monochrome_average: //average R, G, B
			//write code
			// 
			int ave=(int)((red+green+blue)/3.0);
			if (ave>255){
				ave = 255;
			} else if (ave<0) {
				ave=0;
			}
			return new Color(ave, ave, ave).getRGB();

		case monochrome_perceptual: //human eye perception values
			//write code
			// 0.309=78.795, 0.609=155.295, 0.082=20.91
			int ave_better=(int)(red*0.309+green*0.609+blue*0.082);
			if (ave_better>255){
				ave_better = 255;
			} else if (ave_better<0) {
				ave_better=0;
			}
			return new Color(ave_better, ave_better, ave_better).getRGB();
			
		case blank_image:
			return rgb | 0xFFFFFFFF;
			
		default:
			return rgb | 0xFFFFFFFF;
		}
	}
	
	// Edge detection algorithm - spatial filtering by implementing the moving window manually
	public BufferedImage convolve(BufferedImage img) {
		//write algorithm to perform edge detection based on spatial convolution, as described in lecture/textbook
		//return a Bufferedimage = edgeDetectionImage
		WritableRaster wRaster = img.copyData(null);
		BufferedImage copy = new BufferedImage(img.getColorModel(), wRaster, img.isAlphaPremultiplied(), null);
		
		//apply the operation to each pixel
		for (int i = 1; i < width1-1; i++){ 
			for (int j = 1; j < height1-1; j++) {
				
				// Get a 3x3 matrix -> kernel
				
				// Long way:
//				int c1 = img.getRGB(i-1, j-1);
//				int c2 = img.getRGB(i-1, j);
//				int c3 = img.getRGB(i-1, j+1);
//				int c4 = img.getRGB(i, j-1);
//				int c5 = img.getRGB(i, j);
//				int c6 = img.getRGB(i, j+1);
//				int c7 = img.getRGB(i+1, j-1);
//				int c8 = img.getRGB(i+1, j);
//				int c9 = img.getRGB(i+1, j+1);
				
				// Shorter way:
				// Get all channels of kernel..
				// ..Perform required operation(s) on each channel of each pixels in the kernel:
				// Can be done in one function, in one line:
				int rgb = getEachChannelANDperformOperations(img.getRGB(i-1, j-1), // c1
						img.getRGB(i-1, j),	// c2
						img.getRGB(i-1, j+1),	// c3
						img.getRGB(i, j-1),	// c4
						img.getRGB(i, j),	// c5
						img.getRGB(i, j+1),	// c6
						img.getRGB(i+1, j-1),	// c7
						img.getRGB(i+1, j),	// c8
						img.getRGB(i+1, j+1));	// c9
						
		
//						int rgb = getEachChannel(c1,c2,c3,c4,c5,c6,c7,c8,c9);
						
						
						copy.setRGB(i, j, rgb);}}
				return copy; 
		
		//return filterImage(testImage, Filters.blank_image); //remove this line, when finished with the algorithm
	}

	// Returns the rgb value of the result operations
	private int getEachChannelANDperformOperations(int color1, int color2, int color3, int color4, int color5, int color6, int color7, int color8, int color9){
		// Do operations here
		// color5 will be the middle number in convolve matrix
		int middle=8;
		int borderconvolvematrix=-1;
		// Perform operations
		int red=(middle*getRed(color5)) + borderconvolvematrix*(getRed(color1) + getRed(color2) + getRed(color3) + getRed(color4) + getRed(color6) + getRed(color7) + getRed(color8) + getRed(color9));
		int blue=(middle*getBlue(color5)) + borderconvolvematrix*(getBlue(color1) + getBlue(color2) + getBlue(color3) + getBlue(color4) + getBlue(color6) + getBlue(color7) + getBlue(color8) + getBlue(color9));
		int green=(middle*getGreen(color5)) + borderconvolvematrix*(getGreen(color1) + getGreen(color2) + getGreen(color3) + getGreen(color4) + getGreen(color6) + getGreen(color7) + getGreen(color8) + getGreen(color9));
		
		// CHeck boundary of red, green, and blue
		if (red>255){
			red = 255;
		} else if (red<0) {
			red=0;
		}
		if (blue>255){
			blue = 255;
		} else if (blue<0) {
			blue=0;
		}
		if (green>255){
			green = 255;
		} else if (green<0) {
			green=0;
		}
		// Return rgb
		return new Color(red,green,blue).getRGB();
	}
	
	// GetRed() function
	private int getRed(int rgb){
		return new Color(rgb).getRed();
	}
	
	// GetGreen() function
	private int getGreen(int rgb){
		return new Color(rgb).getGreen();
	}
	
	// GetBlue() function
	private int getBlue(int rgb){
		return new Color(rgb).getBlue();
	}
	
public void paint(Graphics g) {
	
	//if working with different images, this may need to be adjusted
	int w = width / 3; 
	int h = height / 3;

	this.setSize(w * 5 + 300, h * 3 + 150);

	g.drawImage(testImage,25,50,w, h,this);
    g.drawImage(brightnessImage, 25+ w+ 25, 50, w, h,this);
    g.drawImage(RGBmultiplyImage, 25+w*2+50, 50, w, h,this);
    g.drawImage(invertImage, 25+w*3+75, 50, w, h,this);
    g.drawImage(contrastImage,w*4+125,50,w, h,this);
    
    g.drawImage(monochrome1Image, 25, h+30+250, w, h,this);
    g.drawImage(monochrome2Image, 25+ w+ 25, h+30+250, w, h,this);	
    
    g.setColor(Color.BLACK);
    Font f1 = new Font("Verdana", Font.PLAIN, 13); 
    g.setFont(f1); 
    g.drawString("Original image", 25, 45); 
    g.drawString("Brightness x2.0", 50+w, 45); 
    g.drawString("RGB Multiply 0.1, 1.25, 1", 72+2*w, 45); 
    g.drawString("Invert", 100+3*w, 45);
    g.drawString("Contrast", 125+4*w,45);
    
    g.drawString("Monochrome 1", 25, 45+h+220); 
    g.drawString("Monochrome 2", 50+w, 45+h+220); 	
    
    g.drawString("Monochrome 1 - based on averaging red, green, blue", 15, h+h/2+60); 
    g.drawString("Monochrome 2 - based on human perception of colors:", 15, h+h/2+90);
    g.drawString("R*0.309+G*0.609+B*0.082", 15, h+h/2+60+60);
    
    g.drawString("Edge detection - based on spatial convolution", w*2+170, 20+h+100);
    
    g.drawImage(testImage1, w*2+150, 50+h+100, width1/2, height1/2,this);
    g.drawImage(edgeDetectionImage, w*2+180+width1/2, 50+h+100, width1/2, height1/2,this);
}
// =======================================================//

public static void main(String[] args) {

	ImageManipulation img = new ImageManipulation();// instantiate this object
	img.repaint();// render the image

}// end main
}
