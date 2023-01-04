package kevkidev.compta.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kevkidev.compta.domain.Expense;
import kevkidev.compta.service.CsvService.CSVToObjectConverter;

public class ExpenseService {

	private CsvService csvService;
	public CSVToObjectConverter<Expense> convertCSVLineToExpense;

	public ExpenseService(CsvService csvService) {
		this.csvService = csvService;
		convertCSVLineToExpense = (String line, boolean verbose) -> {
			return convertCSVLineToExpense(line, verbose);
		};
	}

	private static List<Expense> convertCSVLineToExpense(final String line, final boolean verbose) {
		var expenses = new ArrayList<Expense>();
		var items = line.split(";");
		var label = items[0];
		var amount = Integer.parseInt(items[1]);
		var comment = (items.length >= 3) ? items[2] : "";
		var expense = new Expense(label, amount, comment);

		if (verbose) {
			System.out.println("imported-> " + expense);
		}

		return expenses;
	}

	private String convertExpenseToCSVformat(final Expense value, final boolean commented) {
		var result = value.label() + ";" + value.amount() + ";" + value.comment();
		return commented ? "#" + result : result;
	}

	private List<String> convertExpensesToCSVformat(final List<Expense> data, final String title,
			final boolean commented) {
		var lines = new ArrayList<String>();
		lines.add("#" + title);
		lines.add("#LABEL;AMOUNT;COMMENT");
		lines.addAll(data.stream().map(e -> convertExpenseToCSVformat(e, commented)).toList());
		return lines;
	}

	private List<String> convertExpensesToCSVformat(final List<Expense> data, final String title) {
		return convertExpensesToCSVformat(data, title, false);
	}

	public void export(final List<Expense> monthlyExpenses, final List<Expense> quarterExpenses,
			final List<Expense> yearlyExpenses, final List<Expense> annualExpenses, final List<Expense> monthExpenses)
			throws FileNotFoundException, InterruptedException {

		final String MONTHLY_EXPENSES_TITLE = "Monthly Expenses";
		final String QUARTER_EXPENSES_TITLE = "Quarter Expenses";
		final String YEARLY_EXPENSES_TITLE = "Yearly Expenses";
		final String ANNUAL_EXPENSES_TITLE = "Annual Expenses";
		final String MONTH_EXPENSES_TITLE = "Month Expenses";

		var lines = new ArrayList<String>();
		lines.addAll(convertExpensesToCSVformat(monthlyExpenses, MONTHLY_EXPENSES_TITLE));
		lines.add("#");
		lines.addAll(convertExpensesToCSVformat(quarterExpenses, QUARTER_EXPENSES_TITLE));
		lines.add("#");
		lines.addAll(convertExpensesToCSVformat(yearlyExpenses, YEARLY_EXPENSES_TITLE));
		lines.add("#");
		lines.addAll(convertExpensesToCSVformat(annualExpenses, ANNUAL_EXPENSES_TITLE, CsvService.COMMENTED_CSV_LINE));
		lines.add("#");
		lines.addAll(convertExpensesToCSVformat(monthExpenses, MONTH_EXPENSES_TITLE, CsvService.COMMENTED_CSV_LINE));

		csvService.exportAllToCSV(lines);
		csvService.readCSV(CsvService.VERBOSE);
	}

	public int calculateSum(final List<Expense> data) {
		return data.stream().map(Expense::amount).reduce((a, b) -> a + b).orElse(0);
	}

	public void importCsv(String fileName) throws IOException, InterruptedException {
		csvService.importCsv(fileName, convertCSVLineToExpense);
	}

	public void importCsv() throws IOException, InterruptedException {
		csvService.importCsv(convertCSVLineToExpense);
	}
}
