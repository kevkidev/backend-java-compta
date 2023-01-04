package kevkidev.compta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

record Expense(String label, int amount, String comment) {
}

class ExpenseService {
	public void recordExpense(Expense expense) {

	}
}

public class AppConsole {

	static final String H = "h";
	static final String DR = "dr";
	static final String DR_M = "dr:m";
	static final String DR_T = "dr:t";
	static final String DR_A = "dr:a";
	static final String DR_TA = "dr:ta";
	static final String DR_TM = "dr:tm";
	static final String EX = "ex";
	static final String IM = "im";

	static final String MONTHLY_EXPENSES_TITLE = "Monthly Expenses";
	static final String QUARTER_EXPENSES_TITLE = "Quarter Expenses";
	static final String YEARLY_EXPENSES_TITLE = "Yearly Expenses";

	static final String ANNUAL_EXPENSES_TITLE = "Annual Expenses";
	static final String MONTH_EXPENSES_TITLE = "Month Expenses";

	static final boolean COMMENTED_CSV_LINE = true;
	static final boolean VERBOSE = true;
	static final boolean NO_VERBOSE = false;

	private static String lastExportCsvFileName;

	public static void main(String[] args) throws IOException, InterruptedException {

		// liste des depenses
		var monthlyExpenses = new ArrayList<Expense>();
		monthlyExpenses.add(new Expense("Loyer", 50000, "en hausse"));
		monthlyExpenses.add(new Expense("EDF", 7004, ""));
		monthlyExpenses.add(new Expense("Impôts", 10000, "à la source"));

		// list trimestre
		var quarterExpenses = new ArrayList<Expense>();
		quarterExpenses.add(new Expense("Cotisation", 3000, "jan, avr,jui, oct"));

		var yearlyExpenses = new ArrayList<Expense>();
		yearlyExpenses.add(new Expense("Impôts", 0, "mai, juin"));
		yearlyExpenses.add(new Expense("Service 1", 0, "fin le 9/11/2023"));
		yearlyExpenses.add(new Expense("Service 2", 4900, "4.1€/moins"));
		yearlyExpenses.add(new Expense("Vacances", 0, ""));

		var sumMonthExpenses = calculateSum(monthlyExpenses);
		var sumQuarterExpenses = calculateSum(quarterExpenses);
		var sumYearExpenses = calculateSum(yearlyExpenses);

		var annualExpenses = new ArrayList<Expense>();
		annualExpenses.add(new Expense("Chaque mois x 12", sumMonthExpenses * 12, ""));
		annualExpenses.add(new Expense("Chaque trimestre x 3", sumQuarterExpenses * 3, ""));
		annualExpenses.add(new Expense("Chaque année", sumYearExpenses, ""));

		var monthExpenses = new ArrayList<Expense>();
		monthExpenses.add(new Expense("Chaque mois", sumMonthExpenses, ""));
		monthExpenses.add(
				new Expense("A reserver", (sumQuarterExpenses + sumYearExpenses) / 12, "pour trimestre et annuelle"));

		System.out.println("# Start: Compta ");
		BufferedReader obj = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("# h pour l'aide.");

		String command = "";
		do {
			System.out.print("?> ");
			command = obj.readLine();
			// TODO interdire le caractere ';'
			switch (command) {
				case H -> {
					System.out.print("""
							Commandes possibles :
								Afficher les dépenses récurentes :
								dr :  completes,
								dr-m : mensuelles,
								dr-t : trimestrilles,
								dr-a : annuelles,
								dr-ta : Afficher le total des dépenses l'année,
								dr-tm : Afficher le total des dépenses pour le mois.
							""");
				}
				case DR -> {
					System.out.println("Afficher les dépenses récurentes completes");
					continue;
				}
				case DR_M -> {
					System.out.println("Afficher les dépenses récurentes mensuelles");
					displayTable(monthlyExpenses);
				}
				case DR_TA -> {
					System.out.println("------------------------------");
					System.out.println("Total anneé");
					displayBiColTable(annualExpenses);
				}
				case EX -> {
					/*
					 * on export les valeurs calculé en commentaire car elle seront ignoré dans
					 * l'import, ces valeurs seront recalculé lors de l'import pour prendre en
					 * comptes les changement du fichier. Donc il faut par la suite afficher un
					 * avertissement si les valeurs recalculées ne correspondent pas à celle du
					 * fichier
					 */

					// TODO Donc il faut par la suite afficher un avertissement si les valeurs
					// recalculées ne correspondent pas à celle du fichier

					var lines = new ArrayList<String>();
					lines.addAll(convertExpensesToCSVformat(monthlyExpenses, MONTHLY_EXPENSES_TITLE));
					lines.add("#");
					lines.add("#");
					lines.addAll(convertExpensesToCSVformat(quarterExpenses, QUARTER_EXPENSES_TITLE));
					lines.add("#");
					lines.addAll(convertExpensesToCSVformat(yearlyExpenses, YEARLY_EXPENSES_TITLE));
					lines.add("#");
					lines.addAll(convertExpensesToCSVformat(annualExpenses, ANNUAL_EXPENSES_TITLE, COMMENTED_CSV_LINE));
					lines.add("#");
					lines.addAll(convertExpensesToCSVformat(monthExpenses, MONTH_EXPENSES_TITLE, COMMENTED_CSV_LINE));

					lastExportCsvFileName = exportAllToCSV(lines);
					System.out.println();
					readCSV(lastExportCsvFileName, VERBOSE);
				}
				case IM -> {
					runImportProcess(lastExportCsvFileName);
				}
				default -> {

				}
			}
//			System.out.println();
		} while (!command.equals("q"));
		System.out.println("# End");

	}

	private static void runImportProcess(final String fileName) throws IOException, InterruptedException {
		if (null == fileName || fileName.isBlank()) {
			System.out.println("No exported file found. Please enter the file name.");
			BufferedReader fileNameReader = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("filename ?> ");
			final var manualFileName = fileNameReader.readLine();
			System.out.println("Trying reading file : " + manualFileName);
			runImportProcess(manualFileName);
		} else {
			importCSV(fileName, VERBOSE);
		}
	}

	private static void displayTable(final List<Expense> data) {
		System.out.println("---------------------------------------------------");
		System.out.println("%s | %s | %s".formatted(addBlankToString("Label", 20), addBlankToString("Amount(int)", 15),
				addBlankToString("Comment", 50)));
		System.out.println("---------------------------------------------------");

		var sum = 0;
		for (var iterator = data.iterator(); iterator.hasNext();) {
			var e = (Expense) iterator.next();
			System.out.println("%s | %s | %s".formatted(addBlankToString(e.label(), 20),
					addBlankToString("" + e.amount(), 15), addBlankToString(e.comment(), 50)));
			sum += e.amount();
		}

		System.out.println("---------------------------------------------------");
		// afficher la somme
		var decimalSum = convertToCurrency(sum);

		System.out.println("%s : %s   %s".formatted(addBlankToStringBefore("Sum", 20, ' '),
				addBlankToString(decimalSum.toString(), 15, ' '), addBlankToString("", 50)));
		System.out.println("---------------------------------------------------");
	}

	private static void displayBiColTable(final List<Expense> data) {
		System.out.println("---------------------------------------------------");
		System.out.println("%s | %s".formatted(addBlankToString("Label", 20), addBlankToString("Amount(int)", 15)));
		System.out.println("---------------------------------------------------");

		var sum = 0;
		for (var iterator = data.iterator(); iterator.hasNext();) {
			var e = (Expense) iterator.next();
			System.out.println(
					"%s | %s".formatted(addBlankToString(e.label(), 20), addBlankToString("" + e.amount(), 15)));
			sum += e.amount();
		}

		System.out.println("---------------------------------------------------");
		// afficher la somme
		var decimalSum = convertToCurrency(sum);

		System.out.println("%s : %s".formatted(addBlankToStringBefore("Sum", 20, ' '),
				ConsoleColors.RED_BOLD + addBlankToString(decimalSum.toString() + ConsoleColors.RESET, 15, ' '),
				addBlankToString("", 50)));
		System.out.println("---------------------------------------------------");
	}

	private static int calculateSum(final List<Expense> data) {
		return data.stream().map(Expense::amount).reduce((a, b) -> a + b).orElse(0);
	}

	private static BigDecimal convertToCurrency(int value) {
		var decimalSum = new BigDecimal((double) value / 100);
		return decimalSum.setScale(2, RoundingMode.HALF_EVEN);
	}

	private static String addBlankToString(final String value, final int maxLenght) {
		return addBlankToString(value, maxLenght, ' ');
	}

	private static String addBlankToString(final String value, final int maxLenght, final char symbol) {
		var newValue = "" + value;
		if (newValue.length() < maxLenght) {
			newValue += symbol;
			return addBlankToString(newValue, maxLenght, symbol);
		}
		return newValue;
	}

	private static String addBlankToStringBefore(final String value, final int maxLenght, final char symbol) {
		var newValue = "" + value;
		if (newValue.length() < maxLenght) {
			newValue = symbol + newValue;
			return addBlankToStringBefore(newValue, maxLenght, symbol);
		}
		return newValue;
	}

	private static String exportAllToCSV(List<String> data) throws FileNotFoundException, InterruptedException {
		var datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
		final var FILE_NAME = "compta-export-csv-" + datetime + ".csv";
		var csvOutputFile = new File(FILE_NAME);

		try (var writer = new PrintWriter(csvOutputFile)) {
			data.forEach(line -> {
				writer.println(line);
			});
			System.out.println("Exported to " + csvOutputFile.getAbsolutePath());
		}
		return FILE_NAME;
	}

	private static String convertExpenseToCSVformat(final Expense value, final boolean commented) {
		var result = value.label() + ";" + value.amount() + ";" + value.comment();
		return commented ? "#" + result : result;
	}

	private static List<String> convertExpensesToCSVformat(final List<Expense> data, final String title,
			final boolean commented) {
		var lines = new ArrayList<String>();
		lines.add("#" + title);
		lines.add("#LABEL;AMOUNT;COMMENT");
		lines.addAll(data.stream().map(e -> convertExpenseToCSVformat(e, commented)).toList());
		return lines;
	}

	private static List<String> convertExpensesToCSVformat(final List<Expense> data, final String title) {
		return convertExpensesToCSVformat(data, title, false);
	}

	private static void readCSV(final String fileName, final boolean verbose, final boolean withImport)
			throws FileNotFoundException, InterruptedException {
//		var fileName = "compta-export-csv-20230103-231916.csv";
		try (Scanner sc = new Scanner(new File(fileName))) {
			System.out.println("CSV file reading ...");
			System.out.println("File: " + fileName + "\n");
			while (sc.hasNextLine()) {
				var currentLine = sc.nextLine();
				if (verbose) {
					System.out.println(currentLine);
				}
				if ('#' == currentLine.charAt(0)) {
					continue;
				}
				if (withImport) {
					convertCSVLineToExpense(currentLine, verbose);
				}
			}
			System.out.println("\nCSV file closed.\n");
		} catch (FileNotFoundException e) {
			System.out.println("File \"" + fileName + "\" not found.");
		}
	}

	private static void importCSV(final String fileName, final boolean verbose)
			throws FileNotFoundException, InterruptedException {
		readCSV(fileName, verbose, true);
	}

	private static void readCSV(final String fileName, final boolean verbose)
			throws FileNotFoundException, InterruptedException {
		readCSV(fileName, verbose, false);
	}

	private static List<Expense> convertCSVLineToExpense(final String line, final boolean verbose)
			throws FileNotFoundException, InterruptedException {
//		var fileName = "compta-export-csv-20230103-231916.csv";
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

}
