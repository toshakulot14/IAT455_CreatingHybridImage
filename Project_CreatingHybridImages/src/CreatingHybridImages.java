/*File CreatingHybridImages.java

 IAT455 - Course Project
 Creating Hybrid Images
 
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

/** 
 * ---------- Class extends Frame ----------
 **/
class CreatingHybridImages extends Frame {

	// ----------------------- Class Members: -----------------------
	
	// ------ Source Images ------
	// NOTE: replace variable names with more appropriate later on.
	/**
	 * First Source Image, for Edge Detection (Church image on Lab 3).
	 **/
	BufferedImage firstSourceImage;
	
	/**
	 * Second Source Image, for Blur (Animal on grass Image on Lab 9).
	 **/
	BufferedImage secondSourceImage;


	// --- Image results from edge detection and blur: ---
	BufferedImage result_edgeDetection;
	BufferedImage result_blur;
	
	// ---------------------------------------------------------------
	
	
	
	// ----------------------- Class Functions: -----------------------
	/**
	 * Constructor. <br/>
	 * Initializes source images and result images after edge-detection and blur operations.
	 */
	public CreatingHybridImages(){

		// --- Load source images: ---
		// NOTE: replace variable names AND appropriate source images.
		try{
			firstSourceImage = ImageIO.read(new File("church.jpg"));
			secondSourceImage = ImageIO.read(new File("animal.jpg"));
		} catch (Exception e){
			System.out.println("Error: " + e);
		}

		// --- Frame: ---
		this.setTitle("Course Project - Creating Hybrid Images");
		this.setVisible(true);

		// --- Images results from convolve functions: ---
		result_edgeDetection = convolve(firstSourceImage, Operations.edge_detection);
		result_blur = convolve(secondSourceImage, Operations.blur);


		// Anonymous inner-class listener to terminate program
		this.addWindowListener(
				new WindowAdapter(){//anonymous class definition
					public void windowClosing(WindowEvent e){
						System.exit(0);//terminate the program
					}//end windowClosing()
			} //end WindowAdapter
		); //end addWindowListener
	}
	
	/**
	 * Convolve function. <br/><br/>
	 * 
	 * @param src : &nbsp Image source.
	 * @param op : &nbsp <u><i>edge_detection</i></u> or <u><i>blur</i></u>.
	 * @return a {@code BufferedImage} with applied convolve function.
	 */
	public BufferedImage convolve(BufferedImage src, Operations op){
		
		WritableRaster wRaster = src.copyData(null);
		BufferedImage copy = new BufferedImage(src.getColorModel(), wRaster, src.isAlphaPremultiplied(), null);

		// For each pixel:
		for (int i=1; i<src.getWidth()-1; i++){
			for (int j=1; j<src.getHeight()-1; j++){
		
				// Apply convolve operation:
				copy.setRGB(i, j, 	applyConvolveOp(src.getRGB(i-1, j-1), src.getRGB(i-1, j), src.getRGB(i-1, j+1),		// First row
												    src.getRGB(i, j-1),	src.getRGB(i, j), src.getRGB(i, j+1),			// Second row
												    src.getRGB(i+1, j-1), src.getRGB(i+1, j), src.getRGB(i+1, j+1) ,	// Third row
													op)	// Convolve Operation (edge-detection or blur)
							);
			}
		}

		return copy;
	}

	/**
	 * ApplyConvolveOp function: <br/>
	 * Using 3x3 Matrix.
	 * 
	 * @param pixel1 : top-left pixel.
	 * @param pixel2 : top-middle pixel.
	 * @param pixel3 : top-right pixel.
	 * @param pixel4 : middle-left pixel.
	 * @param pixel5 : middle-middle (center) pixel. 
	 * @param pixel6 : middle-right pixel.
	 * @param pixel7 : bottom-left pixel.
	 * @param pixel8 : bottom-middle pixel.
	 * @param pixel9 : bottom-right pixel.
	 * @param op : edge-detection or blur.
	 * @return an {@code int} with appropriate RGB value.
	 */
	private int applyConvolveOp(int pixel1, int pixel2, int pixel3, 
								int pixel4, int pixel5, int pixel6, 
								int pixel7, int pixel8, int pixel9,
								Operations op){
		
		// pixel5 will be the middle number in convolve matrix.

		int red, green, blue;		
		
		switch(op){
			case edge_detection:{
				int middle=8;
				int borderconvolvematrix=-1;
				// Perform operations
				red=clip( (middle*getRed(pixel5)) + borderconvolvematrix*(getRed(pixel1) + getRed(pixel2) + getRed(pixel3) + getRed(pixel4) + getRed(pixel6) + getRed(pixel7) + getRed(pixel8) + getRed(pixel9)) );
				blue=clip( (middle*getBlue(pixel5)) + borderconvolvematrix*(getBlue(pixel1) + getBlue(pixel2) + getBlue(pixel3) + getBlue(pixel4) + getBlue(pixel6) + getBlue(pixel7) + getBlue(pixel8) + getBlue(pixel9)) );
				green=clip( (middle*getGreen(pixel5)) + borderconvolvematrix*(getGreen(pixel1) + getGreen(pixel2) + getGreen(pixel3) + getGreen(pixel4) + getGreen(pixel6) + getGreen(pixel7) + getGreen(pixel8) + getGreen(pixel9)) );
	
				// Return rgb
				return new Color(red,green,blue).getRGB();
			}
			case blur:{
				float firstfloat=0.0625f;
				float secondfloat=0.125f;
				red=clip( (int)( firstfloat  * (getRed(pixel1) + getRed(pixel3) + getRed(pixel7) + getRed(pixel9)) +
						 	     secondfloat * (getRed(pixel2) + getRed(pixel4) + getRed(pixel5) + getRed(pixel6) + getRed(pixel8)) ) );
				green=clip( (int)( firstfloat  * (getGreen(pixel1) + getGreen(pixel3) + getGreen(pixel7) + getGreen(pixel9)) +
							       secondfloat * (getGreen(pixel2) + getGreen(pixel4) + getGreen(pixel5) + getGreen(pixel6) + getGreen(pixel8)) ) );
				blue=clip( (int)( firstfloat  * (getBlue(pixel1) + getBlue(pixel3) + getBlue(pixel7) + getBlue(pixel9)) +
					 	   	      secondfloat * (getBlue(pixel2) + getBlue(pixel4) + getBlue(pixel5) + getBlue(pixel6) + getBlue(pixel8)) ) );

				// Return rgb
				return new Color(red,green,blue).getRGB();
			}
			default:
				return 0;
		}
		
		// Return rgb
		// return new Color(red,green,blue).getRGB();
	}


	// --------- HELPER FUNCTIONS ---------
	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	protected int getRed(int pixel) {
		return (pixel >>> 16) & 0xFF;
	}

	protected int getGreen(int pixel) {
		return (pixel >>> 8) & 0xFF;
	}

	protected int getBlue(int pixel) {
		return pixel & 0xFF;
	}
	//--------------------------------------

	
	/**
	 * Paint window method.
	 */
	public void paint(Graphics g){
		
		// Resized First Source Image:
		int w_firstSrcImg = (int)(firstSourceImage.getWidth()*0.40);
		int h_firstSrcImg = (int)(firstSourceImage.getHeight()*0.40);
		// Resized Second Source Image:
		int w_secondSrcImg = secondSourceImage.getWidth();
		int h_secondSrcImg = secondSourceImage.getHeight();

		// Window Size:
		this.setSize(w_firstSrcImg+30+w_secondSrcImg+300, 50+h_firstSrcImg+25+h_secondSrcImg+50);
		
		// Draw First Source Images and Edge-Detection:
		g.drawImage(firstSourceImage, 25, 50, w_firstSrcImg, h_firstSrcImg, this);
		g.drawImage(result_edgeDetection, 25+w_firstSrcImg+30, 50, w_firstSrcImg, h_firstSrcImg, this);

		// Draw Second Source Image and Blur:
		g.drawImage(secondSourceImage, 25, 50+h_firstSrcImg+35, w_secondSrcImg, h_secondSrcImg, this);
		g.drawImage(result_blur, 25+w_secondSrcImg+35, 50+h_firstSrcImg+35, w_secondSrcImg, h_secondSrcImg, this);
		
		// Text:
		g.setColor(Color.BLACK);
		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(f1);
		g.drawString("Original first image:", 25, 45);
		g.drawString("Edge-Detection image:", 25+w_firstSrcImg+30, 45);
		g.drawString("Original second image:", 25, 50+h_firstSrcImg+30);
		g.drawString("Blurred image:", 25+w_firstSrcImg+30, 50+h_firstSrcImg+30);
		
	}
	
	// ---------------------------------------------------------------
	
	
	
	// ---------------------------- MAIN: ----------------------------
	/**
	 * Main Function
	 **/
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CreatingHybridImages img = new CreatingHybridImages();
		img.repaint();		

	}
	// ---------------------------------------------------------------

	
	
} // End CreateHybridImages class.


// Fin.
