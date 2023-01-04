package kevkidev.compta.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Common {

	public static BigDecimal convertToCurrency(int value) {
		var decimalSum = new BigDecimal((double) value / 100);
		return decimalSum.setScale(2, RoundingMode.HALF_EVEN);
	}
}
