package kevkidev.nutri.domain;

public class Intake implements Cloneable {
	private long id;
	private Aliment aliment;
	private int quantity; // grammes

	public Intake() {
		super();
	}

	public Intake(long id, Aliment aliment, int quntity) {
		super();
		this.id = id;
		this.aliment = aliment;
		this.quantity = quntity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Aliment getAliment() {
		return aliment;
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
