
//IAT455 - Workshop week 9

//**********************************************************/
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.lang.String; 

import javax.imageio.ImageIO;

class Week9 extends Frame{  //controlling class
	BufferedImage src1;  
	BufferedImage src1_bright;
	BufferedImage src1_brightGama; 
	
	BufferedImage statueImg;
	BufferedImage backgroundImg; 
	BufferedImage statueMatte; 
	BufferedImage edge_mask;
	
	BufferedImage blurred; 
	BufferedImage colorCorrected; 
	BufferedImage coloredEdges;
	BufferedImage shadedStatue;
	BufferedImage finalResult; 

	int width, width1; 
	int height, height1; 

	public Week9() {
		// constructor
		// Get an image from the specified file in the current directory on the
		// local hard disk.
		try {
			src1 = ImageIO.read(new File("backdoor.jpg")); 
			statueImg = ImageIO.read(new File("statue.jpg"));
			backgroundImg = ImageIO.read(new File("background.jpg")); 
			statueMatte = ImageIO.read(new File("statue_mat0.jpg")); 
			edge_mask = ImageIO.read(new File("edge_mask.jpg")); 

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Week 9 workshop");
		this.setVisible(true);

		width = src1.getWidth();
		height = src1.getHeight(); 
		
		width1 = statueImg.getWidth();
		height1 = statueImg.getHeight();
		
		src1_bright = increaseBrightness(src1, 5); 
		src1_brightGama = gammaIncreaseBrightness(src1, 0.65);
		
		BufferedImage background_copy = copyImg(backgroundImg); 
		//produce copy of image to work around Java exception - see: 
		//http://background-subtractor.googlecode.com/svn-history/r68/trunk/src/imageProcessing/ImageBlurrer.java
		//http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4957775		
		blurred = blur(background_copy);
		
		colorCorrected = colorCorrect(statueImg, blurred);
		
		coloredEdges = combineImages(colorCorrected, edge_mask, Operations.multiply); //TODO: replace the statueImg with proper method call
		
		BufferedImage edgelessStatue = combineImages(statueImg, invert(edge_mask), Operations.multiply); //TODO: replace the statueImg with proper method call
		shadedStatue = combineImages(edgelessStatue, coloredEdges, Operations.add); //TODO: replace the statueImg with proper method call
		
		//finalResult = combineImages(shadedStatue, 
		finalResult = combineImages(combineImages(shadedStatue, statueMatte, Operations.multiply),
									combineImages(backgroundImg, invert(statueMatte), Operations.multiply), 
									Operations.add); // statueImg; //TODO: replace the statueImg with proper method call

		//Anonymous inner-class listener to terminate program
		this.addWindowListener(
				new WindowAdapter(){//anonymous class definition
					public void windowClosing(WindowEvent e){
						System.exit(0);//terminate the program
					}//end windowClosing()
				}//end WindowAdapter
				);//end addWindowListener
	}// end constructor
	
	public BufferedImage combineImages(BufferedImage src1, BufferedImage src2, Operations op) {

		if (src1.getType() != src2.getType()) {
			System.out.println("Source Images should be of the same type");
			return null;
		}
		BufferedImage result = new BufferedImage(src1.getWidth(),
				src1.getHeight(), src1.getType());
		
		//TODO: Complete this code
		for (int i=0; i<result.getWidth(); i++){
			for (int j=0; j<result.getHeight(); j++){
				int rgb_src1 = src1.getRGB(i, j);
				int rgb_src2 = src2.getRGB(i, j);
				
				switch (op){
					case multiply:{
						result.setRGB(i, j, new Color( clip( getRed(rgb_src1)*getRed(rgb_src2) / 255 ),
													   clip( getGreen(rgb_src1)*getGreen(rgb_src2) / 255 ),
													   clip( getBlue(rgb_src1)*getBlue(rgb_src2) / 255 ) ).getRGB());
						break;
					}
					case add:{
						result.setRGB(i, j, new Color( clip( getRed(rgb_src1)+getRed(rgb_src2) ),
								   					   clip( getGreen(rgb_src1)+getGreen(rgb_src2) ),
								   					   clip( getBlue(rgb_src1)+getBlue(rgb_src2) ) ).getRGB());
						break;
					}
//					default:
					
				}
				
//				result.setRGB(i, j, new Color(clip(getRed(rgb)*factor), clip(getGreen(rgb)*factor), clip(getBlue(rgb)*factor)).getRGB());
			}
		}
		
		return result;
	}
	
	public BufferedImage increaseBrightness(BufferedImage src, int factor) {
		BufferedImage result = new BufferedImage(src.getWidth(),
				src.getHeight(), src.getType());


		//TODO: Complete this code
		for (int i=0; i<result.getWidth(); i++){
			for (int j=0; j<result.getHeight(); j++){
				int rgb = src.getRGB(i, j);
				result.setRGB(i, j, new Color(clip(getRed(rgb)*factor), clip(getGreen(rgb)*factor), clip(getBlue(rgb)*factor)).getRGB());
			}
		}
		
		
		return result;
	}
	
	public BufferedImage gammaIncreaseBrightness(BufferedImage src, double gamma) {
		BufferedImage result = new BufferedImage(src.getWidth(),
				src.getHeight(), src.getType());

		//TODO: Complete this code
		for (int i=0; i<result.getWidth(); i++){
			for (int j=0; j<result.getHeight(); j++){
				int rgb = src.getRGB(i, j);
				result.setRGB(i, j, new Color( clip( (int)(Math.pow(getRed(rgb),1.0/gamma)) ), clip( (int)(Math.pow(getGreen(rgb),1.0/gamma)) ), clip( (int)(Math.pow(getBlue(rgb),1.0/gamma)) ) ).getRGB());
			}
		}
		return result;
	}
	
	public BufferedImage blur(BufferedImage img) {

		//TODO: Complete this code
		int width = img.getWidth(); 
		int height = img.getHeight(); 

		WritableRaster wRaster = img.copyData(null);
		BufferedImage copy = new BufferedImage(img.getColorModel(), wRaster, img.isAlphaPremultiplied(), null);

		//apply the operation to each pixel AFTER 1st pixels
		for (int i = 1; i < width-1; i++){ 
			for (int j = 1; j < height-1; j++) {
				int rgb = img.getRGB(i, j);
				copy.setRGB(i, j, 	getEachChannelANDperformOperations(img.getRGB(i-1, j-1), // c1
																	   img.getRGB(i-1, j),	// c2
																	   img.getRGB(i-1, j+1),	// c3
																	   img.getRGB(i, j-1),	// c4
																	   img.getRGB(i, j),	// c5
																	   img.getRGB(i, j+1),	// c6
																	   img.getRGB(i+1, j-1),	// c7
																	   img.getRGB(i+1, j),	// c8
																	   img.getRGB(i+1, j+1))	// c9);
							);
			}
		}
		return copy; //

		
//		return image; //TODO: replace image with the blured image
	}
	
	// Returns the rgb value of the result operations
		private int getEachChannelANDperformOperations(int color1, int color2, int color3, int color4, int color5, int color6, int color7, int color8, int color9){
			// Do operations here
			// color5 will be the middle number in convolve matrix
			// EDIT: There is NO MIDDLE!
			float firstfloat=0.0625f;
			float secondfloat=0.125f;
			// Perform operations
			int red=clip( (int)( firstfloat  * (getRed(color1) + getRed(color3) + getRed(color7) + getRed(color9)) +
					 	   		 secondfloat * (getRed(color2) + getRed(color4) + getRed(color5) + getRed(color6) + getRed(color8)) ) );
			int green=clip( (int)( firstfloat  * (getGreen(color1) + getGreen(color3) + getGreen(color7) + getGreen(color9)) +
						     	   secondfloat * (getGreen(color2) + getGreen(color4) + getGreen(color5) + getGreen(color6) + getGreen(color8)) ) );
			int blue=clip( (int)( firstfloat  * (getBlue(color1) + getBlue(color3) + getBlue(color7) + getBlue(color9)) +
				 	   	    	  secondfloat * (getBlue(color2) + getBlue(color4) + getBlue(color5) + getBlue(color6) + getBlue(color8)) ) );
			
			// Return rgb
			return new Color(red,green,blue).getRGB();
		}
	
	public static BufferedImage copyImg(BufferedImage input) {
		BufferedImage tmp = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < input.getWidth(); x++) {
			for (int y = 0; y < input.getHeight(); y++) {
				tmp.setRGB(x, y, input.getRGB(x, y));
			}
		}
		return tmp;
	}
	
	public BufferedImage colorCorrect(BufferedImage src, BufferedImage bg) {
		BufferedImage result = new BufferedImage(src.getWidth(),
				src.getHeight(), src.getType());

		// Hue:
		float [] hsbvals_statue = new float [3];
		float [] hsbvals_bg = new float[3];
		
		int r,g,b;
		
		//TODO: Complete this code
		for (int i=0; i<result.getWidth(); i++){
			for (int j=0; j<result.getHeight(); j++){
				int rgb_statue = src.getRGB(i, j);
				int rgb_bg = bg.getRGB(i, j);
				Color.RGBtoHSB(getRed(rgb_statue), getGreen(rgb_statue), getBlue(rgb_statue), hsbvals_statue);
				Color.RGBtoHSB(getRed(rgb_bg), getGreen(rgb_bg), getBlue(rgb_bg), hsbvals_bg);
				
				result.setRGB(i, j, Color.HSBtoRGB(hsbvals_bg[0], hsbvals_bg[1], hsbvals_statue[2]));
			}
		}
		

		return result;
	}
	
	public BufferedImage invert(BufferedImage src) {
		BufferedImage result = new BufferedImage(src.getWidth(),
				src.getHeight(), src.getType());

		// Write your code here
		for (int i=0; i < src.getWidth(); i++){
			for (int j=0; j < src.getHeight(); j++){
				int rgb = src.getRGB(i, j);
				result.setRGB(i, j, new Color( clip(255-getRed(rgb)),
											   clip(255-getRed(rgb)),
											   clip(255-getRed(rgb)) ).getRGB() );
			}
		}
		
		return result;
		
	}
	
public BufferedImage over(BufferedImage foreground, BufferedImage matte, BufferedImage background) {
		

		//TODO: Complete this code
	
		return background; //TODO: Replace background here with the result of the over method
	}
	
	
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
	
	public void paint(Graphics g){
		int w = width/2; //door image
		int h = height/2;
		
		int w1 = width1/2; //statue
		int h1 = height1/2;
				
		this.setSize(w*5 +100,h*4+50);
		
		g.setColor(Color.BLACK);
	    Font f1 = new Font("Verdana", Font.PLAIN, 13);  
	    g.setFont(f1); 
		
		g.drawImage(src1,20,50,w, h,this);
	    g.drawImage(src1_bright, 50+w, 50, w, h,this);
	    g.drawImage(src1_brightGama, 80+w*2, 50, w, h,this);

	    g.drawImage(statueImg, 150+w*3, 50, w1, h1,this);
	    g.drawImage(backgroundImg, 150+w*3+w1+40, 50, w1, h1,this);
	    
	    g.drawImage(statueMatte,150+w*3, 50+h1+70,w1, h1,this);
	    g.drawImage(edge_mask, 150+w*3+w1+40, 50+h1+70, w1, h1,this);

	    g.drawImage(blurred,30,50+h+180,w1, h1,this);
	    g.drawString("Blurred background", 30, 50+h+170); 
	    
	    g.drawImage(colorCorrected,30+w1+30,50+h+180,w1, h1,this);
	    g.drawString("Color corrected", 30+w1+30, 50+h+170); 
	    
	    g.drawImage(coloredEdges,30+w1*2+60,50+h+180,w1, h1,this);
	    g.drawString("Colored Edges", 30+w1*2+60, 50+h+170);
	    
	    g.drawImage(shadedStatue,30+w1*3+90,50+h+180,w1, h1,this);
	    g.drawString("Shaded Statue", 30+w1*3+90, 50+h+170);
	    
	    g.drawImage(finalResult,30+w1*4+120,50+h+180,w1, h1,this);
	    g.drawString("Final Result", 30+w1*4+120, 50+h+170);
	    
	    g.drawString("Dark image", 20, 40); 
	    g.drawString("Increased brightness", 50+w, 40);
	    g.drawString("Increased brightness-Gamma", 80+w*2, 40);
	    
	    g.drawString("Statue Image", 150+w*3, 40); 
	    g.drawString("Background Image", 150+w*3+w1+40, 40);
	    
	    g.drawString("Statue - Matte", 150+w*3, 50+h1+60); 
	    g.drawString("Edge Matte", 150+w*3+w1+40, 50+h1+60);  
	    
	}
	//=======================================================//

  public static void main(String[] args){
	
    Week9 img = new Week9();//instantiate this object
    img.repaint();//render the image
	
  }//end main
}
//=======================================================//