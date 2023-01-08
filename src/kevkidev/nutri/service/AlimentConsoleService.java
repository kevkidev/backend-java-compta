package kevkidev.nutri.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kevkidev.compta.common.Util;
import kevkidev.compta.service.IdGeneratorService;
import kevkidev.nutri.domain.Aliment;

public class AlimentConsoleService {

	private final String TITLE_ID = "id";
	private final String TITLE_NAME = "Name";
	private final String TITLE_ENERGY = "Enr (int)";
	private final String TITLE_PROT = "Prot (int)";
	private final String TITLE_CARBO = "Carbo (int)";
	private final String TITLE_FAT = "Fat (int)";

	final int NAME_COL_WIDTH = 20;
	final int ENERGY_COL_WIDTH = TITLE_ENERGY.length();
	final int PROT_COL_WIDTH = TITLE_PROT.length();
	final int CARBO_COL_WIDTH = TITLE_CARBO.length();
	final int ID_COL_WIDTH = 5;
	final int FAT_COL_WIDTH = TITLE_FAT.length();

	public static final String CMD_READ_ALL = "ali";
	public static final String CMD_CREATE = "ali:c";
	public static final String TABLE_COLS_FORMAT = " %s : %s : %s : %s : %s ";

	public void readAll(final BufferedReader input, final List<Aliment> data) {
		System.out.println("Alimetns");
		displayTable(data);
	}

	public void displayTable(final List<Aliment> data) {
		var tableWidth = calculateTableWidth();
		var separator = Util.buildVerticalSeparator(tableWidth);
		var header = TABLE_COLS_FORMAT.formatted(buildHeaderValues());

		System.out.println(separator);
		System.out.println(header);
		System.out.println(separator);

		for (var i = 0; i < data.size(); i++) {
			var value = (Aliment) data.get(i);

			var line = TABLE_COLS_FORMAT.formatted(buildBodyValues(value));
			if (i % 2 != 0) {
				line = Util.invertColor(line);
			}
			System.out.println(line);
		}
		System.out.println(separator);
	}

	public List<String> buildHeaderValues() {
		return new ArrayList<String>(Arrays.asList(Util.addBlankToString(TITLE_ID, ID_COL_WIDTH),
				Util.addBlankToString(TITLE_NAME, 20), Util.addBlankToString(TITLE_ENERGY, 0),
				Util.addBlankToString(TITLE_PROT, 0), Util.addBlankToString(TITLE_CARBO, 0)));
	}

	public List<String> buildBodyValues(final Aliment value) {
		return new ArrayList<String>(Arrays.asList(Util.addBlankToString(String.valueOf(value.getId()), ID_COL_WIDTH),
				Util.addBlankToString(value.getName(), NAME_COL_WIDTH),
				Util.addBlankToString(String.valueOf(value.getEnergy()), TITLE_ENERGY.length()),
				Util.addBlankToString(String.valueOf(value.getProteinCount()), TITLE_PROT.length()),
				Util.addBlankToString(String.valueOf(value.getCarbohydrateCount()), TITLE_CARBO.length()),
				Util.addBlankToString(String.valueOf(value.getFatCount()), TITLE_FAT.length())));
	}

	public int calculateTableWidth() {
		return TITLE_ID.length() + ID_COL_WIDTH + TITLE_NAME.length() + NAME_COL_WIDTH + TITLE_ENERGY.length()
				+ TITLE_PROT.length() + TITLE_CARBO.length() + TITLE_FAT.length();
	}

	public Aliment record(final BufferedReader input, final IdGeneratorService idGenerator) throws IOException {
		System.out.println("Creating aliment...");
		var newAliment = new Aliment();
		newAliment.setId(idGenerator.generateAlimentId());

		System.out.print("	Name ?> ");
		newAliment.setName(input.readLine());

		System.out.print("	Energy (int) [0]?> ");
		var energy = input.readLine();
		newAliment.setEnergy(energy.isBlank() ? 0 : Integer.parseInt(energy));

		System.out.print("	Proteins (int) [0]?> ");
		var prot = input.readLine();
		newAliment.setProteinCount(prot.isBlank() ? 0 : Integer.parseInt(prot));

		System.out.print("	Carbohydrate (int) [0]?> ");
		var carbo = input.readLine();
		newAliment.setCarbohydrateCount(carbo.isBlank() ? 0 : Integer.parseInt(carbo));

		System.out.print("	Fats (int) [0]?> ");
		var fat = input.readLine();
		newAliment.setFatCount(fat.isBlank() ? 0 : Integer.parseInt(fat));

		System.out.println(Util.colorToSuccess("Created : " + newAliment));
		return newAliment;
	}

	public long select(final BufferedReader input) throws IOException {
		System.out.print("	Aliment id ?> ");
		return Long.parseLong(input.readLine());
	}

}
