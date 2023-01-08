package kevkidev.nutri.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kevkidev.compta.common.Util;
import kevkidev.compta.service.IdGeneratorService;
import kevkidev.nutri.domain.Menu;

public class MenuConsoleService {

	public static final String CMD_READ_ALL = "men";
	public static final String CMD_READ = "men:r";
	public static final String CMD_CREATE = "men:c";
	public static final String CMD_INFLATE = "men:i";

	public void readAll(final BufferedReader input, final List<Menu> data) {
		System.out.println("Alimetns");
		displayTable(data);
	}

	private void displayTable(final List<Menu> data) {
		final String TITLE_ID = "id";
		final int TITLE_ID_RIGHT_SPACES = 5;
		final String TITLE_NAME = "Name";
		final int TITLE_NAME_RIGHT_SPACES = 20;

		var tableColsFormat = " %s : %s ";
		var tableWidth = TITLE_ID.length() + TITLE_ID_RIGHT_SPACES + TITLE_NAME.length() + TITLE_NAME_RIGHT_SPACES;
		var separator = Util.buildVerticalSeparator(tableWidth);
		var header = tableColsFormat.formatted(Util.addBlankToString(TITLE_ID, 5),
				Util.addBlankToString(TITLE_NAME, 20));

		System.out.println(separator);
		System.out.println(header);
		System.out.println(separator);

		for (var i = 0; i < data.size(); i++) {
			var value = (Menu) data.get(i);
			var line = tableColsFormat.formatted(Util.addBlankToString(String.valueOf(value.getId()), 5),
					Util.addBlankToString(value.getName(), 20));
			if (i % 2 != 0) {
				line = Util.invertColor(line);
			}
			System.out.println(line);
		}

		System.out.println(separator);
	}

	public Menu record(final BufferedReader input, final IdGeneratorService idGenerator) throws IOException {
		System.out.println("Creating menu...");
		var menu = new Menu();
		menu.setId(idGenerator.generateMenuId());

		System.out.print("	Name ?> ");
		menu.setName(input.readLine());
		menu.setIntakes(new ArrayList<>());

		System.out.println(Util.colorToSuccess("Created : " + menu));
		return menu;
	}

	public long select(final BufferedReader input) throws IOException {
		System.out.print("	Menu id ?> ");
		return Long.parseLong(input.readLine());
	}

//	private static void displayBiColTable(final List<Aliment> data) {
//		System.out.println("---------------------------------------------------");
//		System.out.println(AlimentConsoleService.TABLE_COLS_FORMAT.formatted(Util.addBlankToString("Label", 20), addBlankToString("Amount(int)", 15)));
//		System.out.println("---------------------------------------------------");
//
//		var sum = 0;
//		for (var iterator = data.iterator(); iterator.hasNext();) {
//			var e = (Expense) iterator.next();
//			System.out.println(
//					"%s | %s".formatted(addBlankToString(e.getLabel(), 20), addBlankToString("" + e.getAmount(), 15)));
//			sum += e.getAmount();
//		}
//
//		System.out.println("---------------------------------------------------");
//		// afficher la somme
//		var decimalSum = Util.convertToCurrency(sum);
//
//		System.out.println("%s : %s".formatted(addBlankToStringBefore("Sum", 20, ' '),
//				ConsoleColors.RED_BOLD + addBlankToString(decimalSum.toString() + ConsoleColors.RESET, 15, ' '),
//				addBlankToString("", 50)));
//		System.out.println("---------------------------------------------------");
//	}

}
