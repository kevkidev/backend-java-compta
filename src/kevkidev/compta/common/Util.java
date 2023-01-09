package kevkidev.compta.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

import kevkidev.compta.ConsoleColors;

public class Util {

	public static BigDecimal convertToCurrency(int value) {
		var decimalSum = new BigDecimal((double) value / 100);
		return decimalSum.setScale(2, RoundingMode.HALF_EVEN);
	}

	public static String addBlankToString(final String value, final int maxLenght) {
		return addBlankToString(value, maxLenght, ' ');
	}

	public static String addBlankToString(final String value, final int maxLenght, final char symbol) {
		var newValue = "" + value;
		if (newValue.length() < maxLenght) {
			newValue += symbol;
			return addBlankToString(newValue, maxLenght, symbol);
		}
		return newValue;
	}

	public static String addBlankToStringBefore(final String value, final int maxLenght, final char symbol) {
		var newValue = "" + value;
		if (newValue.length() < maxLenght) {
			newValue = symbol + newValue;
			return addBlankToStringBefore(newValue, maxLenght, symbol);
		}
		return newValue;
	}

	public static String buildVerticalSeparator(final int width) {
		var separator = "";
		for (int i = 0; i < width; i++) {
			separator += "-";
		}
		return separator;
	}

	public static String changeColor(final String data, final String color) {
		return color + data + ConsoleColors.RESET;
	}

	public static String colorToSuccess(final String data) {
		return Util.changeColor(data, ConsoleColors.GREEN);
	}

	public static String colorToPrimary(final String data) {
		return Util.changeColor(data, ConsoleColors.BLUE);
	}

	public static String colorToWarnig(final String data) {
		return Util.changeColor(data, ConsoleColors.YELLOW);
	}

	public static String colorToDanger(final String data) {
		return Util.changeColor(data, ConsoleColors.YELLOW);
	}

	public static String invertColor(final String data) {
		return Util.changeColor(data, ConsoleColors.WHITE + ConsoleColors.BLACK_BACKGROUND);
	}

}
