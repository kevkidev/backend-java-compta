package kevkidev.compta.domain;

public class Expense {
	private String label;
	private int amount;
	private String comment;

	public static enum Type {
		MONTHLY, QUARTER, YEARLY
	}

	public Expense(String label, int amount, String comment) {
		super();
		this.label = label;
		this.amount = amount;
		this.comment = comment;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "Expense [label=" + label + ", amount=" + amount + ", comment=" + comment + "]";
	}

}