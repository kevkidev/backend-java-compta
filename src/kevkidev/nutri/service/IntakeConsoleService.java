package kevkidev.nutri.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import kevkidev.compta.common.Util;
import kevkidev.compta.service.IdGeneratorService;
import kevkidev.nutri.domain.Intake;

public class IntakeConsoleService {

	private final String TITLE_QUANTITY = "Qty (int)";
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
			final int carbohydrateSum) {
		var tableWidth = alimentConsoleService.calculateTableWidth() + TITLE_QUANTITY.length();
		var separator = Util.buildVerticalSeparator(tableWidth);

		System.out.println(separator);

		var titles = alimentConsoleService.buildHeaderValues();
		titles.add(TITLE_QUANTITY);

		System.out.println(TABLE_COLS_FORMAT.formatted(titles.toArray()));
		System.out.println(separator);

		for (var i = 0; i < data.size(); i++) {
			var value = (Intake) data.get(i);
			var cells = alimentConsoleService.buildBodyValues(value.getAliment());
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
		var energySumFormated = Util.addBlankToString(String.valueOf(energySum),
				alimentConsoleService.ENERGY_COL_WIDTH);
		var proteinSumFormated = Util.addBlankToString(String.valueOf(proteinSum),
				alimentConsoleService.PROT_COL_WIDTH);
		var carbohydrateSumFormated = Util.addBlankToString(String.valueOf(carbohydrateSum),
				alimentConsoleService.CARBO_COL_WIDTH);

		var line = " %s   %s : %s : %s : %s ".formatted(space1, title, energySumFormated, proteinSumFormated,
				carbohydrateSumFormated);
		System.out.println(line);
		System.out.println(separator);
	}

	public Intake record(final BufferedReader input, final IdGeneratorService idGenerator) throws IOException {
		System.out.println("Creating intake...");

		var newIntake = new Intake();
		newIntake.setId(idGenerator.generateIntakeId());
		newIntake.setQuantity(Integer.parseInt(input.readLine()));

		System.out.println(Util.colorToSuccess("Created : " + newIntake));
		return newIntake;
	}

	public long select(final BufferedReader input) throws IOException {
		System.out.print("	Intake id ?> ");
		return Long.parseLong(input.readLine());
	}

}
