package kevkidev.nutri.domain;

public class HumainProfil {
	private int weight; // kg
	private int height; // cm
	private int age;// an

	public HumainProfil(int weight, int height, int age) {
		super();
		this.weight = weight;
		this.height = height;
		this.age = age;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public double calculateManMetaBase() {
		return 1.083d * Math.pow((double) weight, 0.48d) * Math.pow((double) height / 100d, 0.5)
				* Math.pow((double) age, -0.13d) * 191d;
	}

	@Override
	public String toString() {
		return "HumainProfil [weight=" + weight + ", height=" + height + ", age=" + age + "]";
	}

}
