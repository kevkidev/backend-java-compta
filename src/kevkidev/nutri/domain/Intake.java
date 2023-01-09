package kevkidev.nutri.domain;

public class Intake implements Cloneable {
	private long id;
	private Aliment aliment;
	private int quantity; // unit = g
	private Aliment calculadedAliment;

	public Intake() {
		super();
	}

	public Intake(long id, Aliment aliment, int quntity) throws CloneNotSupportedException {
		super();
		this.id = id;
		this.aliment = aliment;
		this.quantity = quntity;
		this.calculadedAliment = (Aliment) aliment.clone();
	}

	public void calculateNutriments() {
		calculadedAliment.setEnergy(calculatePercent(aliment.getEnergy()));
		calculadedAliment.setCarbohydrateCount(calculatePercent(aliment.getCarbohydrateCount()));
		calculadedAliment.setFatCount(calculatePercent(aliment.getFatCount()));
		calculadedAliment.setProteinCount(calculatePercent(aliment.getProteinCount()));
	}

	private int calculatePercent(int value) {
		return (int) ((double) value / 100d * (double) quantity);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setAliment(Aliment aliment) {
		this.aliment = aliment;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quntity) {
		this.quantity = quntity;
	}

	public Aliment getCalculadedAliment() {
		return calculadedAliment;
	}

	public void setCalculadedAliment(Aliment calculadedAliment) {
		this.calculadedAliment = calculadedAliment;
	}

	@Override
	public String toString() {
		return "AlimentIntake [aliment=" + aliment + ", quntity=" + quantity + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		var clone = (Intake) super.clone();
		return clone;
	}

}
