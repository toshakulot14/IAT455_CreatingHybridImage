package ca.iat455.project;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * This class shows three different sets of hybrid images, using different source
 * images with varying combinations of shape and alignment.
 * 
 * @author Melissa Wang & Andy Tang
 */
public class HybridComparison extends HybridAbstractClass {	
	private static final long serialVersionUID = 1L;
	
	// Constants for output display
	private final static Dimension PANEL_SIZE = new Dimension(1000, 1300);
	private final static int LABEL_Y_OFFSET = 50;
	private final static int IMAGE_X_OFFSET = 10;
	private final static int IMAGE_Y_OFFSET = 60;
	private final static String[] SOURCE_IMAGE_NAMES = new String[] { "lion", "tiger", "lion", "tiger2", "car", "tiger" };
	private final static String[] filters = {"Sobel", "Prewitt", "Sharpen", "Emboss", "Edge Detection"};

	// Fields for output display
	private JPanel panelCenter;
	private JPanel panelWest;
	private JPanel panelNorth;
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public HybridComparison() {
		loadImages(SOURCE_IMAGE_NAMES);
		createHybridImages();
		drawImages();
		setupWindow();
	} // Constructor
	
	@Override
	protected void createHybridImages() {
		for (int i = 0; i < inputImages.size(); i += 2) {
			BufferedImage img1 = inputImages.get(i);
			BufferedImage img2 = inputImages.get(i + 1);
			
			boolean showProcess = false;
			boolean showCustom = false;
			boolean showCustomProcess = false;
			ArrayList<BufferedImage> processImages = createHybridImage(img1, img2, high_pass, dissolve_value, showProcess, showCustom, showCustomProcess);
			
			// Add images
			outputImages.addAll(processImages);
			BufferedImage hybridImg = processImages.get(processImages.size() - 1);
			for (int j = 0; j < 3; j++) {
				outputImages.add(hybridImg);
			}
		}
	} // createHybridImages
		
	private void drawImages() {
		panelCenter = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Set image size and position values
                int labelIndex = 0;
                int w = width;
        		int h = height;
        		int x = IMAGE_X_OFFSET;
        		int y = IMAGE_Y_OFFSET;
        		
        		// Set labels
        		Font font = new Font("Verdana", Font.PLAIN, 20);
        		g.setFont(font);
        		String[] labels = {  "Similar shape and alignment (Source 1 & 2)",
									 "Similar shape, different alignment (Source 3 & 4)",
									 "Different shape and alignment (Source 5 & 6)" };

        		for (int i = 0; i < outputImages.size(); i++) {
        			g.setColor(Color.BLACK);
        			if (i % 4 == 0) {
        				g.drawString(labels[labelIndex], x, y - (LABEL_Y_OFFSET / 4));
        				labelIndex++;
        			}
        			g.drawImage(outputImages.get(i), x, y, w, h, this);
        			
        			// Set position to next image in row
        			x += w + IMAGE_X_OFFSET;
        			
        			// Set next image half the size of previous image
        			w /= 2;
    				h /= 2;
    				
    				// Reset values to draw next row of images
        			// And draw row-separating line
        			if (i == 3 || i == 7) {
        				w = width;
        				h = height;
        				x = IMAGE_X_OFFSET;
        				y += h + (IMAGE_Y_OFFSET / 2);
        				g.setColor(Color.GRAY);
        				g.drawLine(0, y, getWidth(), y);
        				y += IMAGE_Y_OFFSET;
        			}
        		} // for
            } // paintComponent
        }; // JPanel
	} // drawImages

	private JPanel drawSourceImages() {
		return new JPanel(){
			private static final long serialVersionUID = 1L;

			@Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	   
	            int w = width;
	    		int h = height;
	    		int x = 0;
	    		int y = 0;
	
	    		//make labels
	    		Font font = new Font("Verdana", Font.PLAIN, 14);
        		g.setFont(font);
        		ArrayList<String> labels = new ArrayList<String>();
        		for (int i=1; i <= inputImages.size(); i++){
        			labels.add("Source " + i);
        		}
	    		
        		//loop through inputs, and render them
	    		for (int i = 0; i < inputImages.size(); i++) {
		    		g.setColor(Color.BLACK);
		    		
	    			// Draw labels and images
	    			int padding = 20;
	    			g.drawImage(inputImages.get(i), x, y+padding+(i*20)+5, w/3, h/3, this);
	    			g.drawString(labels.get(i), x, y+padding+(i*20));

	    			// Draw separating line
	    			if(i%2 == 1){
		    			g.setColor(Color.GRAY);
		    			Graphics2D g2d = (Graphics2D) g;
		    			g2d.setStroke(new BasicStroke(3));
		    			g.drawLine(0, y+padding+(i*20)+20+h/3, getWidth(), y+padding+(i*20)+20+h/3);	    				
	    			}
	    			y += h/3 + 20;
	    			
	    		} //for
			} // paintComponent	
			
		};
	}

	///////////////////////////////////////// Setup /////////////////////////////////////////
	
	private void setupWindow() {
		
		/**** Set up North Border ****/
		
		// Create panel to hold buttons
		JPanel northNestedPanel = new JPanel(new FlowLayout());
		
		// Create browse buttons and add to subpanel
		ArrayList<JButton> btnList = new ArrayList<JButton>();
		for(int i=0; i < inputImages.size(); i++){
			btnList.add(createBrowseBtn(inputImages, i));
			northNestedPanel.add(btnList.get(i));
		}
		
		// Create text field and button for dissolve then add to subpanel
		JTextField textField = new JTextField(10);
		JButton dissolveBtn = createDissolveBtn(textField);
		northNestedPanel.add(textField);		
		northNestedPanel.add(dissolveBtn);
		
		// Create combobox for filter selection
		
		JComboBox<String> filterSelector = createComboBox(filters);
		
		/**** Add components to BorderLayout ****/
		
		// Add North Border with Browse buttons and filter Combobox
		panelNorth = new JPanel(new BorderLayout());
		panelNorth.add(northNestedPanel, BorderLayout.NORTH);
		panelNorth.add(filterSelector, BorderLayout.CENTER);
		add(panelNorth, BorderLayout.NORTH);
		
		// Add West Border
		panelWest = drawSourceImages();
		panelWest.setPreferredSize(new Dimension(width/3+10, 1250));
		JScrollPane scrollPaneWest = new JScrollPane(panelWest);
		add(scrollPaneWest, BorderLayout.WEST);
		
		// Add Center Border
		panelCenter.setPreferredSize(PANEL_SIZE);
		JScrollPane scrollPaneCenter = new JScrollPane(panelCenter);
		add(scrollPaneCenter, BorderLayout.CENTER);
		
		super.setupWindow("Hybrid Image Comparison");
	} // setupWindow


	private JButton createBrowseBtn(ArrayList<BufferedImage> list, int inputImagesIndex) {
		JButton browseBtn = new JButton("Source " + (inputImagesIndex+1));
		
		browseBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//create file chooser object
				JFileChooser fc = new JFileChooser();
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg");
				fc.setFileFilter(filter);
				
				int result = fc.showOpenDialog(null);
				
				//try reading the file
				if (result == JFileChooser.APPROVE_OPTION) {
				    File file = fc.getSelectedFile(); 
				    try {
						BufferedImage newInput = ImageIO.read(file);
						
						//if new image is wrong dimensions, display pop up
						if(newInput.getWidth() == width || newInput.getHeight() == height){
							//store input into list of inputimages at the ith position corresponding with ith button
							list.set(inputImagesIndex, newInput);
							
							String msg = file.getName() + " read successfully";
							JOptionPane.showMessageDialog(null, msg);
							update();							
						} else {
							String msg = "Dimensions of the image do not match. Images must be " + height + "x" + width;
							JOptionPane.showMessageDialog(null, msg);
						} //end if
						
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "Error reading image.");
						e1.printStackTrace();
					}  //end try catch
				    
				}//end reading file

			}//end anonymous listener
		});
		
		return browseBtn;
	} // createBrowseBtn
	
	
	private JButton createDissolveBtn(JTextField textField) {
		JButton dissolveBtn = new JButton("Change dissolve");
		
		dissolveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					float tempMixVal = Float.parseFloat(textField.getText());
					
					if (tempMixVal >= 0 && tempMixVal <= 1) {
						setDissolveValue(tempMixVal);
					} else {
						JOptionPane.showMessageDialog(null, "Value must be a float from 0 to 1");
					}
					update();
					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Invalid input, try again.");
					e.printStackTrace();
				} //end try
			} //end anonymous actionlistener
			
		});
		
		return dissolveBtn;
	}
	
	
	private JComboBox<String> createComboBox(String[] filters) {
		JComboBox<String> filterSelector = new JComboBox<String>(filters);
		
		filterSelector.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				String selection = (String) filterSelector.getSelectedItem();
				
				//check which filter to use
				float[] filter = new float[9];
				
				if (selection.equals(filters[0])) {
					float[] tmp = {1, 2, 1 , 0, 0 ,0, -1, -2, -1};	//sobel					
					filter = tmp;
				} else if(selection.equals(filters[1])) {
					float[] tmp = {-1, 0, 1, -1, 0, 1, -1, 0, 1};	//prewitt
					filter = tmp;
				} else if(selection.equals(filters[2])) {
					float[] tmp = {0, -1 ,0, -1, 4, -1, 0, -1, 0};	//sharpen
					filter = tmp;
				} else if(selection.equals(filters[3])) {
					float[] tmp = {-2, -1, 0, -1, 1, 0, 0, 1, 2};	//emboss
					filter = tmp;
				} else if(selection.equals(filters[4])) {
					float[] tmp = {-1, -1, -1, -1, 8, -1, -1, -1, -1};	//edge
					filter = tmp;
				} else {
					//if nothing matches, do not filter
					JOptionPane.showMessageDialog(null, "Whoops, there is an unhandled filter. Select another filter.");
				} //end selection
				
				//set filter and redraw using new filter
				setFilter(filter);
				update();
			
			}//anonymous listener	
		}); //fileSelector
		
		return filterSelector;
	}
	
	
	private void update(){
		outputImages = new ArrayList<BufferedImage>();	//reset output images
		createHybridImages();
		panelWest.revalidate();
		panelWest.repaint();
		panelCenter.revalidate();
		panelCenter.repaint();
	}
}