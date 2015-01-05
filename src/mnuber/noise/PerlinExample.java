package mnuber.noise;

/**Perlin Example
 * 
 * A simple implementation of PerlinNoise1D
 * @author mnuber
 */
public class PerlinExample{


	public static void main(String[] args) {
		PerlinNoise1D saleExamples = new PerlinNoise1D("Sales ($)");
		saleExamples.createWeeklySample(30, 45000);
		saleExamples.printSample();
		saleExamples.exportSample("sales.txt");
		
	}
}
