package m2.orderbook.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalUtility {

	 public static BigDecimal setScale(BigDecimal value) {
	        return value.setScale(8, RoundingMode.DOWN);
	    }
}
