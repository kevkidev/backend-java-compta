package kevkidev.nutri.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import kevkidev.compta.common.Util;
import kevkidev.compta.service.IdGeneratorService;
import kevkidev.nutri.domain.Intake;

public class IntakeConsoleService {

	private final String TITLE_QUANTITY = "Qty (int,g)";
	public static final String CMD_READ_ALL = "itk";
	public static final String CMD_CREATE = "itk:c";

	private final String TABLE_COLS_FORMAT = AlimentConsoleService.TABLE_COLS_FORMAT + ": %s ";
	private AlimentConsoleService alimentConsoleService;

	public IntakeConsoleService(AlimentConsoleService alimentConsoleService) {
		super();
		this.alimentConsoleService = alimentConsoleService;
		System.out.println(TABLE_COLS_FORMAT);
	}

	public void displayTable(final List<Intake> data, final int energySum, final int proteinSum,
			final int carbohydrateSum, final int fatSum, final int quantitySum, final int metaBase,
			final int dailyEnergy) {
		var tableWidth = alimentConsoleService.calculateTableWidth() + TITLE_QUANTITY.length() + 3;
		var separator = Util.buildVerticalSeparator(tableWidth);

		System.out.println(separator);

		var titles = alimentConsoleService.buildHeaderValues();
		titles.add(TITLE_QUANTITY);

		System.out.println(TABLE_COLS_FORMAT.formatted(titles.toArray()));
		System.out.println(separator);

		for (var i = 0; i < data.size(); i++) {
			var value = (Intake) data.get(i);
			var cells = alimentConsoleService.buildBodyValues(value.getCalculadedAliment());
			cells.add(Util.addBlankToString(String.valueOf(value.getQuantity()), TITLE_QUANTITY.length()));

			var line = TABLE_COLS_FORMAT.formatted(cells.toArray());
			if (i % 2 != 0) {
				line = Util.invertColor(line);
			}
			System.out.println(line);
		}
		System.out.println(separator);

		var space1 = Util.addBlankToString("", alimentConsoleService.ID_COL_WIDTH);
		var title = Util.addBlankToStringBefore("Sum", alimentConsoleService.NAME_COL_WIDTH, ' ');
		var energyDouble = (double) energySum / 100d;
		var energySumFormated = Util.addBlankToString(String.valueOf(energyDouble),
				alimentConsoleService.ENERGY_COL_WIDTH);

		if (energyDouble < metaBase) {
			energySumFormated = Util.colorToDanger(energySumFormated);
		} else if (energyDouble > dailyEnergy) {
			energySumFormated = Util.colorToWarnig(energySumFormated);
		}

		var proteinSumFormated = Util.addBlankToString(String.valueOf((double) proteinSum / 100d),
				alimentConsoleService.PROT_COL_WIDTH);
		var carbohydrateSumFormated = Util.addBlankToString(String.valueOf((double) carbohydrateSum / 100d),
				alimentConsoleService.CARBO_COL_WIDTH);
		var fatSumFormated = Util.addBlankToString(String.valueOf((double) fatSum / 100d),
				alimentConsoleService.FAT_COL_WIDTH);
		var qtySumFormated = Util.addBlankToString(String.valueOf(quantitySum), TITLE_QUANTITY.length());

		var line = " %s   %s : %s : %s : %s : %s : %s ".formatted(space1, title, energySumFormated, proteinSumFormated,
				carbohydrateSumFormated, fatSumFormated, qtySumFormated);
		System.out.println(line);
		System.out.println(separator);
	}

	public Intake record(final BufferedReader input, final IdGeneratorService idGenerator) throws IOException {
		System.out.println("Creating intake...");

		var newIntake = new Intake();
		newIntake.setId(idGenerator.generateIntakeId());
		System.out.println("	quantity (g) ?> ");
		newIntake.setQuantity(Integer.parseInt(input.readLine()));

		System.out.println(Util.colorToSuccess("Created : " + newIntake));
		return newIntake;
	}

	public long select(final BufferedReader input) throws IOException {
		System.out.print("	Intake id ?> ");
		return Long.parseLong(input.readLine());
	}

}
