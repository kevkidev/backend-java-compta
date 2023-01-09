package kevkidev.compta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import kevkidev.compta.service.CsvExpenseService;
import kevkidev.compta.service.CsvService;
import kevkidev.compta.service.IdGeneratorService;
import kevkidev.nutri.service.AlimentConsoleService;
import kevkidev.nutri.service.IntakeConsoleService;
import kevkidev.nutri.service.MenuConsoleService;

public class AppConsole {

	public static void main(String[] args) throws IOException, InterruptedException, CloneNotSupportedException {
		System.out.println("# Compta/Nutri");

		var idGenerator = new IdGeneratorService();
		var csvService = new CsvService();
		var csvExpensesService = new CsvExpenseService(csvService);
		var alimentConsoleService = new AlimentConsoleService();
		var intakeConsoleService = new IntakeConsoleService(alimentConsoleService);
		var menuConsoleService = new MenuConsoleService();

		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		String command = "";
		do {
			System.out.println("	1: Compta ");
			System.out.println("	2: Nutri ");
			System.out.print("?> ");
			command = input.readLine();

			switch (command) {
				case "1" -> {
					var config = new kevkidev.compta.ComptaConfig();
					config.run();
				}
				case "2" -> {
					var config = new kevkidev.nutri.NutriConfig();
					config.run();
				}
				default -> {
				}
			}
		} while (!command.equals("q"));
		System.out.println("# End");

	}

}
