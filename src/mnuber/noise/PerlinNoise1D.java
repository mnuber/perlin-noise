package mnuber.noise;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**PerlinNoise1D
 * 
 * A one-dimensional implementation of Perlin noise used to generate mock values for a dataset
 * Examples include sales data, investment returns, vod sales, etc.  which can be 
 * Used in a model or for values in testing, or just to populate a database with mock data.
 * 
 * @author mnuber
 * 
 */
class PerlinNoise1D{

	private int dataSize = 0;
	private String dataField = "";
	private NoiseData[] dataSet = null;
	
	
	/**PerlinNoise1D
	 * 
	 * A one-dimensional implementation of Perlin noise used to generate mock values for a dataset
	 * Examples include sales data, investment returns, vod sales, etc.  which can be 
	 * Used in a model or for values in testing, or just to populate a database with mock data.
	 * 
	 * 
	 * @param dataField The name of the column of the sample to be produced
	 */
	public PerlinNoise1D(String dataField) {
		this.dataField = dataField;
	}
	
	
	/**Creates a 52-week sample with the specified seed and avg
	 * 
	 * @param seed The seed for the noise generation
	 * @param avg The avg expected data result
	 */
	public void createWeeklySample(int seed, int avg){
		this.dataSize = 52;
		this.dataSet = new NoiseData[dataSize];
		generateSample(dataSize, "W", seed, avg);
	}
	
	/**Creates a 12-month sample with the specified seed and avg
	 * 
	 * @param seed The seed for the noise generation
	 * @param avg The avg expected data result
	 */
	public void createMonthlySample(int seed, int avg){
		this.dataSize = 12;
		this.dataSet = new NoiseData[dataSize];
		generateSample(dataSize, "Y2015M", seed, avg);
	}
	
	/**Create Custom Sample
	 * 
	 * Creates a sample with a the specified parameters
	 * 
	 * @param seed The seed for the noise generation
	 * @param avg The avg expected data result
	 * @param label for each row
	 * @param size the size of the dataSet
	 */
	public void createCustomSample(int seed, int avg , String label, int size){
		this.dataSize = size;
		this.dataSet = new NoiseData[dataSize];
		generateSample(dataSize, label, seed, avg);
	}
	
	
	
	/**Print Sample
	 * 
	 * Prints the sample in rows to the console
	 */
	public void printSample(){
		for(NoiseData d : dataSet) {
			System.out.println(d);
		}
	}
	
	/**Export Sample
	 * 
	 * Exports the sample in a specified file, in CSV format
	 * 
	 * @param filename of the file to be created
	 */
	public void exportSample(String filename){
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(new File(filename)));
			writer.write("Data,"+ this.dataField);
			writer.newLine();
			for(NoiseData d : dataSet) {
				writer.write(d.toString());
				writer.newLine();
				writer.flush();	
			}
			
			writer.close();

		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
	
	/**Noise1D
	 * 
	 * Generates consistent noise for a each seed.  It's similar to a random function 
	 * except each seed produces the exact same value for noise.
	 * 
	 * @param seed for 'random'
	 * @return noise value
	 */
	private float noise1D(int seed) {
		/*
		 * Avoids divide by zero error in next step
		 */
		seed +=255;
		/*
		 * The seed is added to seed^2 then divided by seed + the number of bits in seed
		 * Dividing by the bit count produces extra variance for numbers that are near one another
		 * (15 and 16 have bit counts of 4 and 1, respectively)
		 */
		seed += (seed*Integer.bitCount(seed+1)/(Integer.bitCount(seed)*Integer.bitCount(seed)))%Integer.MAX_VALUE;
		/*
		 * Using prime numbers prevents patterns in similar input
		 */
		float noise = (float) ((54041041.0*seed)/(54041041.0 + 1-seed/9539483.0));
		return (float) (noise);
	}

	/**Offset
	 * 
	 * Since the last element and the first element of the array should be interpolated together,
	 * (i.e. December 31st and January 1st would have a similar data)
	 * It's important that we don't exceed the bounds of the array when we look for adjacent
	 * Indices on either end.  
	 * 
	 * @param offset the amount to offset the array
	 * @return the index of the next element, offset by the size of the array
	 */
	protected int overlap(int offset) {
		return (dataSize + offset)%dataSize;
	}
	
	
	/**Mean Interpolation
	 * 
	 *  The easiest way to interpolate two values.
	 * 
	 * @param x the first value to interpolate
	 * @param y the value of the next index
	 * @return the mean value of the inputs
	 */
	public float meanInterpolation(float x, float y) {
		return (x + y)/2;
	}

	
	/**Sort the dataSet highest to lowest
	 * 
	 */
	public void sort() {
		for(int j = 0; j < dataSet.length-1; j++){
			NoiseData temp;
			for(int i = 0; i < dataSet.length-1; i++){
				if(dataSet[i].getValue() < dataSet[i+1].getValue()){
					temp = dataSet[i];
					dataSet[i] = dataSet[i+1];
					dataSet[i+1] = temp;
				}
			}
		}

	}
	

	/**Generate Sample
	 * 
	 * Generates a data samples with the specified parameters 
	 * 
	 * @param dataSize The size of the dataSet to create
 	 * @param label for each roww
	 * @param seed The seed for the noise generation
	 * @param avg The avg expected data result
	 */
	private void generateSample(int dataSize, String label, int seed, int avg) {
		for(int i = 0; i < dataSize; i++) {
			dataSet[i] = new NoiseData(label + "" + (i+1), noise1D(seed*i));
		}
		normalizeAndScaleData(avg);

		for(int i = 0; i < dataSize; i++) {
			for(int j = 0; j < 4; j++) //interpolate three times
				dataSet[i].setValue(meanInterpolation(dataSet[i].getValue(), dataSet[overlap(i+1)].getValue()));
		}

	}


	/**Normalize and Scale Data
	 * 
	 * Normalizes data from a 0.5-1.5 and then scales the results so that the expected average is known.
	 * @param scale The average expected data value
	 */
	protected void normalizeAndScaleData(int scale) {
			sort();
			float normalMin = dataSet[0].getValue();
			float normalMax = dataSet[dataSize-1].getValue();
			for (int i = 0; i < dataSize; i++) {
				dataSet[i].setValue((float) ((((dataSet[i].getValue() - normalMin)/
						(normalMax - normalMin)) +.5) * scale));
			}
	}
}