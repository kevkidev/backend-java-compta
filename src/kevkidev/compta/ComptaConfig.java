package kevkidev.compta;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kevkidev.compta.domain.EntityExpense;
import kevkidev.compta.domain.Expense;
import kevkidev.compta.service.CsvExpenseService;
import kevkidev.compta.service.CsvService;
import kevkidev.compta.service.HelpConsoleService;
import kevkidev.compta.service.IdGeneratorService;
import kevkidev.compta.service.MainExpenseService;

public class ComptaConfig {

	private String command;
	private IdGeneratorService idGenerator;
	private BufferedReader input;
	private CsvService csvService;
	private CsvExpenseService csvExpenseService;

	void run() throws NumberFormatException, IOException, InterruptedException {

		// liste des depenses
		var monthlyExpenses = new ArrayList<Expense>();

		monthlyExpenses.add(new EntityExpense(idGenerator.generateId(), "Loyer", 50000, "+++ mois"));
		monthlyExpenses.add(new EntityExpense(idGenerator.generateId(), "EDF", 6000, ""));
		monthlyExpenses.add(new EntityExpense(idGenerator.generateId(), "Impôts", 0, "à la source"));

		// list trimestre
		var quarterExpenses = new ArrayList<Expense>();
		quarterExpenses.add(new EntityExpense(idGenerator.generateId(), "Cotisation ", 3000, "jan, avr,jui, oct"));

		var yearlyExpenses = new ArrayList<Expense>();
		yearlyExpenses.add(new EntityExpense(idGenerator.generateId(), "Impôts", 0, "mai, juin"));
		yearlyExpenses.add(new EntityExpense(idGenerator.generateId(), "Service 1", 0, "fin le 9/11/2023"));
		yearlyExpenses.add(new EntityExpense(idGenerator.generateId(), "Service 2", 4900, "4.1€/moins"));
		yearlyExpenses.add(new EntityExpense(idGenerator.generateId(), "Vacances", 0, ""));

		var lists = new HashMap<String, List<Expense>>();
		lists.put("monthlyExpenses", monthlyExpenses);
		lists.put("quarterExpenses", quarterExpenses);
		lists.put("yearlyExpenses", yearlyExpenses);
		var mainExpensesService = new MainExpenseService(lists);

		var sumMonthExpenses = mainExpensesService.calculateSum(monthlyExpenses);
		var sumQuarterExpenses = mainExpensesService.calculateSum(quarterExpenses);
		var sumYearExpenses = mainExpensesService.calculateSum(yearlyExpenses);

		var annualExpenses = new ArrayList<Expense>();
		annualExpenses.add(new Expense("Chaque mois x 12", sumMonthExpenses * 12, ""));
		annualExpenses.add(new Expense("Chaque trimestre x 3", sumQuarterExpenses * 3, ""));
		annualExpenses.add(new Expense("Chaque année", sumYearExpenses, ""));

		var monthExpenses = new ArrayList<Expense>();
		monthExpenses.add(new Expense("Chaque mois", sumMonthExpenses, ""));
		monthExpenses.add(
				new Expense("A reserver", (sumQuarterExpenses + sumYearExpenses) / 12, "pour trimestre et annuelle"));

		System.out.println("# Compta started.");
		HelpConsoleService.displayBasicHelp();
		do {
			System.out.print("?> ");
			command = input.readLine();
			// TODO interdire le caractere ';'
			switch (command) {
				case HelpConsoleService.CMD_HELP -> {
					MainExpenseService.displayHelp();
				}
				case MainExpenseService.CMD_READ_ALL_EXPENSES -> {
					System.out.println("Afficher les dépenses récurentes completes");
					continue;
				}
				case MainExpenseService.CMD_READ_MONTLY_EXPENSES -> {
					System.out.println("Afficher les dépenses récurentes mensuelles");
					mainExpensesService.displayTable(monthlyExpenses);
				}
				case MainExpenseService.CMD_READ_YEARLY_EXPENSES_SUM -> {
					System.out.println("------------------------------");
					System.out.println("Total anneé");
					mainExpensesService.displayBiColTable(annualExpenses);
				}
				case CsvExpenseService.CMD_EXPORT_TO_CSV -> {
					/*
					 * on export les valeurs calculé en commentaire car elle seront ignoré dans
					 * l'import, ces valeurs seront recalculé lors de l'import pour prendre en
					 * comptes les changement du fichier. Donc il faut par la suite afficher un
					 * avertissement si les valeurs recalculées ne correspondent pas à celle du
					 * fichier
					 */

					csvExpenseService.export(monthExpenses, quarterExpenses, yearlyExpenses, annualExpenses,
							monthExpenses, idGenerator.getIdCounter());
				}
				case CsvExpenseService.CMD_IMPORT_TO_CSV -> {
					// TODO Donc il faut par la suite afficher un avertissement si les valeurs
					// recalculées ne correspondent pas à celle du fichier imorté

					System.out.println("Trying to import last export...");
					csvExpenseService.importCsv();
				}
				case CsvExpenseService.CMD_IMPORT_TO_CSV_AFTER_SCAN -> {
					System.out.println("Trying to found existing CSV files...");
					var existingFiles = csvService.findExistingCsvFile();
					if (existingFiles.isEmpty()) {
						System.out.println("Sorry: No existing CSV file found.");
						break;
					}
					var selectedFileName = csvService.selectExistingCsvFile(existingFiles, input);
					csvExpenseService.importCsv(selectedFileName);
				}
				case CsvExpenseService.CMD_IMPORT_TO_CSV_AFTER_MANUEL -> {
					System.out.println("No exported file found. Please enter the file name.");
					System.out.print("filename ?> ");
					var manualFileName = input.readLine();
					csvExpenseService.importCsv(manualFileName);
				}
				case MainExpenseService.CMD_CREATE_EXPENSE -> {
					System.out.println("Creating expense...");

					System.out.print("Expense label ?> ");
					var label = input.readLine();

					System.out.print("Expense amount (int) ?> ");
					var amount = Integer.parseInt(input.readLine());

					System.out.print("Expense comment ?> ");
					var comment = input.readLine();
					var newExpense = new Expense(label, amount, comment);

					System.out.print("Expense type [1: MONTHLY, 2: QUARTER, 3: YEARLY] ?> ");
					var type = input.readLine();
					var expensetype = Expense.Type.MONTHLY;

					switch (type) {
						case "1" -> expensetype = Expense.Type.MONTHLY;
						case "2" -> expensetype = Expense.Type.QUARTER;
						case "3" -> expensetype = Expense.Type.YEARLY;
						default -> throw new IllegalArgumentException("Unexpected type: " + type);
					}

					mainExpensesService.record(newExpense, expensetype);
					System.out.println("Created expense : " + newExpense);

				}
				case MainExpenseService.CMD_UPDATE_MONTHLY_EXPENSE -> {
					mainExpensesService.updateExpense(monthlyExpenses, input);
				}
				case MainExpenseService.CMD_UPDATE_QUATER_EXPENSE -> {
					mainExpensesService.updateExpense(quarterExpenses, input);
				}
				case MainExpenseService.CMD_UPDATE_YEARLY_EXPENSE -> {
					mainExpensesService.updateExpense(yearlyExpenses, input);
				}
				case MainExpenseService.CMD_DELETE_YEARLY_EXPENSE -> {
					System.out.println("delete");

				}
				default -> {
				}
			}
		} while (!command.equals("q"));
	}
}
