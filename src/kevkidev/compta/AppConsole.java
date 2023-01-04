package kevkidev.compta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import kevkidev.compta.domain.Expense;
import kevkidev.compta.service.CsvService;
import kevkidev.compta.service.ExpenseService;
import kevkidev.compta.util.Common;

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
	static final String IM_S = "im:s";
	static final String IM_M = "im:m";

	public static void main(String[] args) throws IOException, InterruptedException {
		var csvService = new CsvService();
		var expensesService = new ExpenseService(csvService);

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

		var sumMonthExpenses = expensesService.calculateSum(monthlyExpenses);
		var sumQuarterExpenses = expensesService.calculateSum(quarterExpenses);
		var sumYearExpenses = expensesService.calculateSum(yearlyExpenses);

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

					expensesService.export(monthExpenses, quarterExpenses, yearlyExpenses, annualExpenses,
							monthExpenses);
				}
				case IM -> {
					System.out.println("Trying to import last export...");
					expensesService.importCsv();
				}
				case IM_S -> {
					System.out.println("Trying to found existing CSV files...");
					var existingFiles = csvService.findExistingCsvFile();
					if (existingFiles.isEmpty()) {
						System.out.println("Sorry: No existing CSV file found.");
						break;
					}
					var selectedFileName = csvService.selectExistingCsvFile(existingFiles);
					expensesService.importCsv(selectedFileName);
				}
				case IM_M -> {
					var manualFileName = csvService.scanCsvFileName();
					expensesService.importCsv(manualFileName);
				}
				default -> {

				}
			}
		} while (!command.equals("q"));
		System.out.println("# End");

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
		var decimalSum = Common.convertToCurrency(sum);

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
		var decimalSum = Common.convertToCurrency(sum);

		System.out.println("%s : %s".formatted(addBlankToStringBefore("Sum", 20, ' '),
				ConsoleColors.RED_BOLD + addBlankToString(decimalSum.toString() + ConsoleColors.RESET, 15, ' '),
				addBlankToString("", 50)));
		System.out.println("---------------------------------------------------");
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

}
