package kevkidev.compta.service;

import java.util.List;
import java.util.Map;

import kevkidev.compta.domain.Expense;

public class MainExpenseService {
	Map<String, List<Expense>> data;

	public static final String MONTHLY_LIST_NAME = "monthlyExpenses";
	public static final String QUARTER_LIST_NAME = "quarterExpenses";
	public static final String YEARLY_LIST_NAME = "yearlyExpenses";

	public enum Type {
		MONTHLY, QUARTER, YEARLY
	}

	public MainExpenseService(final Map<String, List<Expense>> data) {
		this.data = data;
	}

	public int calculateSum(final List<Expense> data) {
		return data.stream().map(Expense::amount).reduce((a, b) -> a + b).orElse(0);
	}

	public void recordExpence(final Expense expense, final Type type) {

		switch (type) {
			case MONTHLY -> data.get(MONTHLY_LIST_NAME).add(expense);
			case QUARTER -> data.get(QUARTER_LIST_NAME).add(expense);
			case YEARLY -> data.get(YEARLY_LIST_NAME).add(expense);
			default -> {
				System.out.println("Unknow expense type.");
			}
		}
	}
}
