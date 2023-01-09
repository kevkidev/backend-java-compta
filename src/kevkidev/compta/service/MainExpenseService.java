package kevkidev.compta.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import kevkidev.compta.ConsoleColors;
import kevkidev.compta.common.Util;
import kevkidev.compta.domain.EntityExpense;
import kevkidev.compta.domain.Expense;

public class MainExpenseService {
	public static final String MONTHLY_LIST_NAME = "monthlyExpenses";

	public static final String QUARTER_LIST_NAME = "quarterExpenses";
	public static final String YEARLY_LIST_NAME = "yearlyExpenses";

	public static final String CMD_READ_ALL_EXPENSES = "r";
	public static final String CMD_READ_MONTLY_EXPENSES = "rm";
	public static final String CMD_READ_QUARTER_EXPENSES = "rq";
	public static final String CMD_READ_YEARLY_EXPENSES = "ry";

	public static final String CMD_UPDATE_MONTHLY_EXPENSE = "um";
	public static final String CMD_UPDATE_QUATER_EXPENSE = "uq";
	public static final String CMD_UPDATE_YEARLY_EXPENSE = "uy";

	public static final String CMD_DELETE_MONTHLY_EXPENSE = "exp:dm";
	public static final String CMD_DELETE_QUATER_EXPENSE = "exp:dq";
	public static final String CMD_DELETE_YEARLY_EXPENSE = "exp:dy";

	public static final String CMD_READ_MONTLY_EXPENSES_SUM = "rms";
	public static final String CMD_READ_YEARLY_EXPENSES_SUM = "rys";

	public static final String CMD_CREATE_EXPENSE = "c";

	public static void displayHelp() {
		final var RIGHT_SPACE_SIZE = 5;

		var part1 = """
				Afficher les dépenses récurentes
				%s: read complete list
				%s: read monthly list
				%s: read quater list
				%s: read yearly list
				%s: read yearly sum
				%s: read montly sum
				""".formatted(Util.addBlankToString(MainExpenseService.CMD_READ_ALL_EXPENSES, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_READ_MONTLY_EXPENSES, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_READ_QUARTER_EXPENSES, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_READ_YEARLY_EXPENSES, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_READ_YEARLY_EXPENSES_SUM, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_READ_MONTLY_EXPENSES_SUM, RIGHT_SPACE_SIZE));

		var part2 = """
				Editer dépenses
				%s: create expense

				%s: update montly expenses
				%s: update quater expenses
				%s: update yearly expenses

				%s: delete montly expenses
				%s: delete quater expensesmenu.getIntakes().stream().map(e -> e.getAliment().getEnergy())
								.reduce(
				%s: delete yearly expenses
				""".formatted(Util.addBlankToString(MainExpenseService.CMD_CREATE_EXPENSE, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_UPDATE_MONTHLY_EXPENSE, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_UPDATE_QUATER_EXPENSE, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_UPDATE_YEARLY_EXPENSE, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_DELETE_MONTHLY_EXPENSE, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_DELETE_QUATER_EXPENSE, RIGHT_SPACE_SIZE),
				Util.addBlankToString(MainExpenseService.CMD_DELETE_YEARLY_EXPENSE, RIGHT_SPACE_SIZE));

		System.out.println(part1);
		System.out.println(part2);
	}

	Map<String, List<Expense>> data;

	public MainExpenseService(final Map<String, List<Expense>> data) {
		this.data = data;
	}

	public int calculateSum(final List<Expense> data) {
		return data.stream().map(Expense::getAmount).reduce((a, b) -> a + b).orElse(0);
	}

	public void displayBiColTable(final List<Expense> data) {
		System.out.println("---------------------------------------------------");
		System.out.println(
				"%s | %s".formatted(Util.addBlankToString("Label", 20), Util.addBlankToString("Amount(int)", 15)));
		System.out.println("---------------------------------------------------");

		var sum = 0;
		for (var iterator = data.iterator(); iterator.hasNext();) {
			var e = iterator.next();
			System.out.println("%s | %s".formatted(Util.addBlankToString(e.getLabel(), 20),
					Util.addBlankToString("" + e.getAmount(), 15)));
			sum += e.getAmount();
		}

		System.out.println("---------------------------------------------------");
		// afficher la somme
		var decimalSum = Util.convertToCurrency(sum);

		System.out.println("%s : %s".formatted(Util.addBlankToStringBefore("Sum", 20, ' '),
				ConsoleColors.RED_BOLD + Util.addBlankToString(decimalSum.toString() + ConsoleColors.RESET, 15, ' '),
				Util.addBlankToString("", 50)));
		System.out.println("---------------------------------------------------");
	}

	public void displayTable(final List<Expense> data) {
		System.out.println("---------------------------------------------------");
		System.out.println(
				"%s | %s | %s | %s".formatted(Util.addBlankToString("id", 5), Util.addBlankToString("Label", 20),
						Util.addBlankToString("Amount(int)", 15), Util.addBlankToString("Comment", 50)));
		System.out.println("---------------------------------------------------");

		var sum = 0;
		for (var iterator = data.iterator(); iterator.hasNext();) {
			var e = (EntityExpense) iterator.next();
			System.out.println("%s | %s | %s | %s".formatted(Util.addBlankToString(String.valueOf(e.getId()), 5),
					Util.addBlankToString(e.getLabel(), 20), Util.addBlankToString("" + e.getAmount(), 15),
					Util.addBlankToString(e.getComment(), 50)));
			sum += e.getAmount();
		}

		System.out.println("---------------------------------------------------");
		// afficher la somme
		var decimalSum = Util.convertToCurrency(sum);

		System.out.println("%s : %s   %s".formatted(Util.addBlankToStringBefore("Sum", 20, ' '),
				Util.addBlankToString(decimalSum.toString(), 15, ' '), Util.addBlankToString("", 50)));
		System.out.println("---------------------------------------------------");
	}

	public void record(final Expense expense, final Expense.Type type) {

		switch (type) {
			case MONTHLY -> data.get(MONTHLY_LIST_NAME).add(expense);
			case QUARTER -> data.get(QUARTER_LIST_NAME).add(expense);
			case YEARLY -> data.get(YEARLY_LIST_NAME).add(expense);
			default -> {
				System.out.println("Unknow expense type.");
			}
		}
	}

	public void updateExpense(List<Expense> list, BufferedReader input) throws NumberFormatException, IOException {
		System.out.println("Updating a montly expense...");

		System.out.println("Expense id ?> ");
		var id = Long.parseLong(input.readLine());
		var expense = (EntityExpense) list.stream().filter(e -> ((EntityExpense) e).getId() == id).findFirst().get();
		System.out.println("Updateting: " + expense);

		System.out.print("Expense label [%s]?> ".formatted(expense.getLabel()));
		var label = input.readLine();

		System.out.print("Expense amount (int) [%s]?> ".formatted(expense.getAmount()));
		var amount = input.readLine();

		System.out.print("Expense comment [%s]?> ".formatted(expense.getComment()));
		var comment = input.readLine();

		System.out.println("Confirm (y|n)");
		var confirm = input.readLine();

		if (confirm.equals("y")) {
			if (!label.isBlank()) {
				expense.setLabel(label);
			}
			if (!amount.isBlank()) {
				expense.setAmount(Integer.parseInt(amount));
			}
			if (!amount.isEmpty()) {
				expense.setComment(comment);
			}
			System.out.println("Updating successed.");
		} else {
			System.out.println("Updating canceled.");
		}
	}

}
