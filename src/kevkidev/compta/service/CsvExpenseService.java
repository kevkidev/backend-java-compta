package kevkidev.compta.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kevkidev.compta.domain.Expense;
import kevkidev.compta.domain.EntityExpense;
import kevkidev.compta.service.CsvService.CSVToObjectConverter;

public class CsvExpenseService {

	private CsvService csvService;
	public CSVToObjectConverter<Expense> convertCSVLineToExpense;

	public CsvExpenseService(CsvService csvService) {
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
		var result = "%s;%s;%s".formatted(value.getLabel(), value.getAmount(), value.getComment());
		return commented ? "#" + result : result;
	}

	private String convertExpenseToCSVformat(final EntityExpense value, final boolean commented) {
		var result = "%s;%s;%s;%s".formatted(value.getId(), value.getLabel(), value.getAmount(), value.getComment());
		return commented ? "#" + result : result;
	}

	private List<String> convertExpensesToCSVformat(final List<Expense> data, final String title,
			final boolean commented) {
		var lines = new ArrayList<String>();
		lines.add("#" + title);
		final var COL_NAMES = "LABEL;AMOUNT;COMMENT";
		if (data.get(0) instanceof EntityExpense) {
			lines.add("#ID;" + COL_NAMES);
			lines.addAll(data.stream().map(e -> convertExpenseToCSVformat((EntityExpense) e, commented)).toList());
		} else {
			lines.add(COL_NAMES);
			lines.addAll(data.stream().map(e -> convertExpenseToCSVformat(e, commented)).toList());
		}
		return lines;
	}

	private List<String> convertExpensesToCSVformat(final List<Expense> data, final String title) {
		return convertExpensesToCSVformat(data, title, false);
	}

	public void export(final List<Expense> monthlyExpenses, final List<Expense> quarterExpenses,
			final List<Expense> yearlyExpenses, final List<Expense> annualExpenses, final List<Expense> monthExpenses,
			final long lastGeneratedId) throws FileNotFoundException, InterruptedException {

		final String MONTHLY_EXPENSES_TITLE = "Monthly_Expenses";
		final String QUARTER_EXPENSES_TITLE = "Quarter_Expenses";
		final String YEARLY_EXPENSES_TITLE = "Yearly_Expenses";
		final String ANNUAL_EXPENSES_TITLE = "Annual_Expenses";
		final String MONTH_EXPENSES_TITLE = "Month_Expenses";
		final String LAST_GENERATED_ID_TITLE = "LAST_GENERATED_ID";

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
		lines.add("#");
		lines.add("#" + LAST_GENERATED_ID_TITLE);
		lines.add(String.valueOf(lastGeneratedId));

		csvService.exportAllToCSV(lines);
		csvService.readCSV(CsvService.VERBOSE);
	}

	public void importCsv(String fileName) throws IOException, InterruptedException {
		csvService.importCsv(fileName, convertCSVLineToExpense);
	}

	public void importCsv() throws IOException, InterruptedException {
		csvService.importCsv(convertCSVLineToExpense);
	}
}
