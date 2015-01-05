package mnuber.noise;

public class NoiseData implements Comparable<NoiseData> {

	private String label = "";
	private float value = 0f;
	
	NoiseData(String label, float value){
		this.label = label;
		this.value = value;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}

	@Override
	public int compareTo(NoiseData otherData) {
		if (otherData instanceof NoiseData && otherData != null) {
			if(value > ((NoiseData) otherData).getValue())
				return 1;
			else if(value == ((NoiseData) otherData).getValue())
				return 0;
			else
				return -1;
		}
		return -1;

	}
	
	public String toString() {
		return label + "," + value;
	}
	
}
