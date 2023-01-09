package kevkidev.nutri.domain;

public class Aliment implements Cloneable {
	private long id;
	private String name;
	private int energy; // per 100g; unit g*100
	private int proteinCount; // per 100g; unit g*100
	private int carbohydrateCount; // per 100g unit*100
	private int fatCount; // per 100g; unit g*100

	public Aliment() {
		super();
	}

	public Aliment(long id, String name, int energy, int proteinCount, int carbohydrateCount, int fatCount) {
		super();
		this.id = id;
		this.name = name;
		this.energy = energy;
		this.proteinCount = proteinCount;
		this.carbohydrateCount = carbohydrateCount;
		this.fatCount = fatCount;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getProteinCount() {
		return proteinCount;
	}

	public void setProteinCount(int proteinCount) {
		this.proteinCount = proteinCount;
	}

	public int getCarbohydrateCount() {
		return carbohydrateCount;
	}

	public void setCarbohydrateCount(int carbohydrateCount) {
		this.carbohydrateCount = carbohydrateCount;
	}

	public int getFatCount() {
		return fatCount;
	}

	public void setFatCount(int fatCount) {
		this.fatCount = fatCount;
	}

	@Override
	public String toString() {
		return "Aliment [id=" + id + ", name=" + name + ", energy=" + energy + ", proteinCount=" + proteinCount
				+ ", carbohydrateCount=" + carbohydrateCount + ", fatCount=" + fatCount + "]";
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
