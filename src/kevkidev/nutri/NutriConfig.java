package kevkidev.nutri;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import kevkidev.compta.common.Util;
import kevkidev.compta.service.HelpConsoleService;
import kevkidev.compta.service.IdGeneratorService;
import kevkidev.nutri.domain.Aliment;
import kevkidev.nutri.domain.HumainProfil;
import kevkidev.nutri.domain.Intake;
import kevkidev.nutri.domain.Menu;
import kevkidev.nutri.service.AlimentConsoleService;
import kevkidev.nutri.service.IntakeConsoleService;
import kevkidev.nutri.service.MenuConsoleService;

public class NutriConfig {

	static final int SEDENTATRY_ACTIVITY_LEVEL = 120; // unit= unit*100; no sport

	static final int LIGHT_ACTIVE_ACTIVITY_LEVEL = 140; // unit= unit*100; 1-3 times /week
	static final int MODERATE_ACTIVE_ACTIVITY_LEVEL = 160; // unit= unit*100; sport 3-5 times/week
	static final int VERY_ACTIVE_ACTIVITY_LEVEL = 170; // unit= unit*100; sport 6-7 times/week
	static final int EXTREME_ACTIVE_ACTIVITY_LEVEL = 190; // unit= unit*100; sport every day and hard job

	private IdGeneratorService idGenerator;
	private String command;
	private AlimentConsoleService alimentConsoleService;
	private BufferedReader input;
	private MenuConsoleService menuConsoleService;
	private IntakeConsoleService intakeConsoleService;

	public void run() throws CloneNotSupportedException, IOException {
		var profil = new HumainProfil(110, 180, 42);
		final var MB = (int) profil.calculateManMetaBase();
		final var PROT = profil.getWeight();
		final var DAILY_ENERGY = MODERATE_ACTIVE_ACTIVITY_LEVEL / 100d * MB;

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

		HelpConsoleService.displayBasicHelp();
		do {
			System.out.print("?> ");
			command = input.readLine();
			// TODO interdire le caractere ';'
			switch (command) {

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
							quantitySum, MB, (int) DAILY_ENERGY);
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
	}

}
