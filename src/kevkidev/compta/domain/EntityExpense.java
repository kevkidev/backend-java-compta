package kevkidev.compta.domain;

public class EntityExpense extends Expense {
	private long id;

	public EntityExpense(long id, String label, int amount, String comment) {
		super(label, amount, comment);
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}