package kevkidev.compta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kevkidev.compta.common.Util;
import kevkidev.compta.domain.EntityExpense;
import kevkidev.compta.domain.Expense;
import kevkidev.compta.service.CsvExpenseService;
import kevkidev.compta.service.CsvService;
import kevkidev.compta.service.IdGeneratorService;
import kevkidev.compta.service.MainExpenseService;
import kevkidev.nutri.domain.Aliment;
import kevkidev.nutri.domain.HumainProfil;
import kevkidev.nutri.domain.Intake;
import kevkidev.nutri.domain.Menu;
import kevkidev.nutri.service.AlimentConsoleService;
import kevkidev.nutri.service.IntakeConsoleService;
import kevkidev.nutri.service.MenuConsoleService;

public class AppConsole {
	static final String CMD_HELP = "h";
	static final String CMD_READ_ALL_EXPENSES = "r";
	static final String CMD_READ_MONTLY_EXPENSES = "rm";
	static final String CMD_READ_QUARTER_EXPENSES = "rq";
	static final String CMD_READ_YEARLY_EXPENSES = "ry";

	static final String CMD_READ_MONTLY_EXPENSES_SUM = "rms";
	static final String CMD_READ_YEARLY_EXPENSES_SUM = "rys";

	static final String CMD_EXPORT_TO_CSV = "e";

	static final String CMD_IMPORT_TO_CSV = "i";
	static final String CMD_IMPORT_TO_CSV_AFTER_SCAN = "is";
	static final String CMD_IMPORT_TO_CSV_AFTER_MANUEL = "im";

	static final String CMD_CREATE_EXPENSE = "c";

	static final String CMD_UPDATE_MONTHLY_EXPENSE = "um";
	static final String CMD_UPDATE_QUATER_EXPENSE = "uq";
	static final String CMD_UPDATE_YEARLY_EXPENSE = "uy";

	static final String CMD_DELETE_MONTHLY_EXPENSE = "dm";
	static final String CMD_DELETE_QUATER_EXPENSE = "dq";
	static final String CMD_DELETE_YEARLY_EXPENSE = "dy";

	static final int SEDENTATRY_ACTIVITY_LEVEL = 120; // unit= unit*100; no sport
	static final int LIGHT_ACTIVE_ACTIVITY_LEVEL = 140; // unit= unit*100; 1-3 times /week
	static final int MODERATE_ACTIVE_ACTIVITY_LEVEL = 160; // unit= unit*100; sport 3-5 times/week
	static final int VERY_ACTIVE_ACTIVITY_LEVEL = 170; // unit= unit*100; sport 6-7 times/week
	static final int EXTREME_ACTIVE_ACTIVITY_LEVEL = 190; // unit= unit*100; sport every day and hard job

	public static void main(String[] args) throws IOException, InterruptedException, CloneNotSupportedException {
		var profil = new HumainProfil(110, 180, 42);
		final var MB = (int) profil.calculateManMetaBase();
		final var PROT = profil.getWeight();
		final var DAILY_ENERGY = (double) MODERATE_ACTIVE_ACTIVITY_LEVEL / 100d * (double) MB;

		var idGenerator = new IdGeneratorService();

		var csvService = new CsvService();
		var csvExpensesService = new CsvExpenseService(csvService);
		var alimentConsoleService = new AlimentConsoleService();
		var intakeConsoleService = new IntakeConsoleService(alimentConsoleService);
		var menuConsoleService = new MenuConsoleService();

		// liste aliments
		var aliments = new ArrayList<Aliment>();
		var ali1 = new Aliment(idGenerator.generateAlimentId(), "Haricots rouges", 80, 3, 5, 6);
		aliments.add(ali1);
		var ali2 = new Aliment(idGenerator.generateAlimentId(), "Riz blanc", 15100, 290, 3110, 140);
		aliments.add(ali2);
		var ali3 = new Aliment(idGenerator.generateAlimentId(), "Haricots verts", 3200, 170, 440, 10);
		aliments.add(ali3);
		var avocat = new Aliment(idGenerator.generateAlimentId(), "Avocat", 20500, 156, 83, 2060);
		aliments.add(avocat);

		var itk1 = new Intake(idGenerator.generateIntakeId(), ali1, 10000);
		itk1.calculateNutriments();
		var itk2 = new Intake(idGenerator.generateIntakeId(), ali2, 250);
		itk2.calculateNutriments();
		var itk3 = new Intake(idGenerator.generateIntakeId(), avocat, 100);

		var menus = new ArrayList<Menu>();
		var menu1 = new Menu(idGenerator.generateMenuId(), "Riz/Hricots Rouge", new ArrayList<>());
		menu1.getIntakes().add(itk1);
		menu1.getIntakes().add(itk2);
		menu1.getIntakes().add(itk3);
		menus.add(menu1);

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
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("# h : help");
		System.out.println("# q : quit");

		String command = "";
		do {
			System.out.print("?> ");
			command = input.readLine();
			// TODO interdire le caractere ';'
			switch (command) {
				case CMD_HELP -> {
					final var RIGHT_SPACE_SIZE = 5;
					var part1 = """
#Help
	h : display the command list

	Afficher les dépenses récurentes
		%s: read complete list 
		%s: read monthly list
		%s: read quater list
		%s: read yearly list
		%s: read yearly sum
	 	%s: read montly sum
""".formatted(addBlankToString(CMD_READ_ALL_EXPENSES, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_READ_MONTLY_EXPENSES, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_READ_QUARTER_EXPENSES, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_READ_YEARLY_EXPENSES, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_READ_YEARLY_EXPENSES_SUM, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_READ_MONTLY_EXPENSES_SUM, RIGHT_SPACE_SIZE));
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
""".formatted(addBlankToString(CMD_CREATE_EXPENSE, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_UPDATE_MONTHLY_EXPENSE, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_UPDATE_QUATER_EXPENSE, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_UPDATE_YEARLY_EXPENSE, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_DELETE_MONTHLY_EXPENSE, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_DELETE_QUATER_EXPENSE, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_DELETE_YEARLY_EXPENSE, RIGHT_SPACE_SIZE));

					var part3 = """
	Import/Export CSV
		%s: export all data to one csv
		%s: import the last exported csv of the running instance
		%s: scan existing CSV files and let you select one to import it
		%s: entre the file's name to import
""".formatted(addBlankToString(CMD_EXPORT_TO_CSV, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_IMPORT_TO_CSV, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_IMPORT_TO_CSV_AFTER_SCAN, RIGHT_SPACE_SIZE),
							addBlankToString(CMD_IMPORT_TO_CSV_AFTER_MANUEL, RIGHT_SPACE_SIZE));

					System.out.println(part1);
					System.out.println(part2);
					System.out.println(part3);
				}
				case CMD_READ_ALL_EXPENSES -> {
					System.out.println("Afficher les dépenses récurentes completes");
					continue;
				}
				case CMD_READ_MONTLY_EXPENSES -> {
					System.out.println("Afficher les dépenses récurentes mensuelles");
					displayTable(monthlyExpenses);
				}
				case CMD_READ_YEARLY_EXPENSES_SUM -> {
					System.out.println("------------------------------");
					System.out.println("Total anneé");
					displayBiColTable(annualExpenses);
				}
				case CMD_EXPORT_TO_CSV -> {
					/*
					 * on export les valeurs calculé en commentaire car elle seront ignoré dans
					 * l'import, ces valeurs seront recalculé lors de l'import pour prendre en
					 * comptes les changement du fichier. Donc il faut par la suite afficher un
					 * avertissement si les valeurs recalculées ne correspondent pas à celle du
					 * fichier
					 */

					csvExpensesService.export(monthExpenses, quarterExpenses, yearlyExpenses, annualExpenses,
							monthExpenses, idGenerator.getIdCounter());
				}
				case CMD_IMPORT_TO_CSV -> {
					// TODO Donc il faut par la suite afficher un avertissement si les valeurs
					// recalculées ne correspondent pas à celle du fichier imorté

					System.out.println("Trying to import last export...");
					csvExpensesService.importCsv();
				}
				case CMD_IMPORT_TO_CSV_AFTER_SCAN -> {
					System.out.println("Trying to found existing CSV files...");
					var existingFiles = csvService.findExistingCsvFile();
					if (existingFiles.isEmpty()) {
						System.out.println("Sorry: No existing CSV file found.");
						break;
					}
					var selectedFileName = csvService.selectExistingCsvFile(existingFiles, input);
					csvExpensesService.importCsv(selectedFileName);
				}
				case CMD_IMPORT_TO_CSV_AFTER_MANUEL -> {
					System.out.println("No exported file found. Please enter the file name.");
					System.out.print("filename ?> ");
					var manualFileName = input.readLine();
					csvExpensesService.importCsv(manualFileName);
				}
				case CMD_CREATE_EXPENSE -> {
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

					mainExpensesService.recordExpence(newExpense, expensetype);
					System.out.println("Created expense : " + newExpense);

				}
				case CMD_UPDATE_MONTHLY_EXPENSE -> {
					updateExpense(monthlyExpenses, input);
				}
				case CMD_UPDATE_QUATER_EXPENSE -> {
					updateExpense(quarterExpenses, input);
				}
				case CMD_UPDATE_YEARLY_EXPENSE -> {
					updateExpense(yearlyExpenses, input);
				}
				case CMD_DELETE_YEARLY_EXPENSE -> {
					System.out.println("delete");

				}
				case AlimentConsoleService.CMD_READ_ALL -> {
					alimentConsoleService.readAll(input, aliments);
				}
				case IntakeConsoleService.CMD_CREATE -> {
					var aliment = alimentConsoleService.record(input, idGenerator);
					aliments.add(aliment);
				}
				case MenuConsoleService.CMD_READ_ALL -> {
					menuConsoleService.readAll(input, menus);
				}
				case MenuConsoleService.CMD_READ -> {
					var menuId = menuConsoleService.select(input);
					var menu = menus.stream().filter(e -> e.getId() == menuId).findFirst().get();
					System.out.println(
							"Reading menu : " + Util.colorToPrimary(menu.getName()) + " contains aliments ...");
					System.out.println(Util.colorToPrimary("Goal: MB : " + MB + " kcal, DAILY_ENERGY : " + DAILY_ENERGY
							+ " kcal; PROT: " + PROT + " g"));
					var energySum = menu.getIntakes().stream().map(e -> e.getCalculadedAliment().getEnergy())
							.reduce((a, b) -> a + b).get();
					var proteinSum = menu.getIntakes().stream().map(e -> e.getCalculadedAliment().getProteinCount())
							.reduce((a, b) -> a + b).get();
					var carbohydrateSum = menu.getIntakes().stream()
							.map(e -> e.getCalculadedAliment().getCarbohydrateCount()).reduce((a, b) -> a + b).get();
					var fatSum = menu.getIntakes().stream().map(e -> e.getCalculadedAliment().getFatCount())
							.reduce((a, b) -> a + b).get();
					var quantitySum = menu.getIntakes().stream().map(e -> e.getQuantity()).reduce((a, b) -> a + b)
							.get();

					intakeConsoleService.displayTable(menu.getIntakes(), energySum, proteinSum, carbohydrateSum, fatSum,
							quantitySum, (int) MB, (int) DAILY_ENERGY);
				}
				case MenuConsoleService.CMD_CREATE -> {
					var menu = menuConsoleService.record(input, idGenerator);
					menus.add(menu);
				}
				case MenuConsoleService.CMD_INFLATE -> {
					System.out.println("Adding intake to menu ...");
					var menuId = menuConsoleService.select(input);
					var alimentId = alimentConsoleService.select(input);
					var aliment = aliments.stream().filter(e -> e.getId() == alimentId).findFirst().get();
					var intake = intakeConsoleService.record(input, idGenerator);
					intake.setAliment(aliment);
					intake.calculateNutriments();

					var menu = menus.stream().filter(e -> e.getId() == menuId).findFirst().get();
					menu.getIntakes().add(intake);
					System.out.println(Util.colorToSuccess("Intake added to menu."));
				}
				default -> {
				}
			}
		} while (!command.equals("q"));
		System.out.println("# End");

	}

	private static void updateExpense(List<Expense> list, BufferedReader input)
			throws NumberFormatException, IOException {
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

	private static void displayTable(final List<Expense> data) {
		System.out.println("---------------------------------------------------");
		System.out.println("%s | %s | %s | %s".formatted(addBlankToString("id", 5), addBlankToString("Label", 20),
				addBlankToString("Amount(int)", 15), addBlankToString("Comment", 50)));
		System.out.println("---------------------------------------------------");

		var sum = 0;
		for (var iterator = data.iterator(); iterator.hasNext();) {
			var e = (EntityExpense) iterator.next();
			System.out.println("%s | %s | %s | %s".formatted(addBlankToString(String.valueOf(e.getId()), 5),
					addBlankToString(e.getLabel(), 20), addBlankToString("" + e.getAmount(), 15),
					addBlankToString(e.getComment(), 50)));
			sum += e.getAmount();
		}

		System.out.println("---------------------------------------------------");
		// afficher la somme
		var decimalSum = Util.convertToCurrency(sum);

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
					"%s | %s".formatted(addBlankToString(e.getLabel(), 20), addBlankToString("" + e.getAmount(), 15)));
			sum += e.getAmount();
		}

		System.out.println("---------------------------------------------------");
		// afficher la somme
		var decimalSum = Util.convertToCurrency(sum);

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
