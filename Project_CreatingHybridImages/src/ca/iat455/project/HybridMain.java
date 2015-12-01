package ca.iat455.project;

/**
 * This class instantiates a HybridDisplay object (which displays a window of
 * the hybrid image process).
 * 
 * @author Melissa Wang & Andy Tang
 */
public class HybridMain {
	public static void main(String[] args) {
		HybridProcess hybridProcess = new HybridProcess();
		hybridProcess.repaint();
		
		HybridComparison hybridComparison = new HybridComparison();
		hybridComparison.repaint();
	}
} // HybridMain
