package m2.orderbook.util;

import java.time.Instant;

public class OrderUtility {

	public static String getOrderId() {
		return  String.valueOf(Instant.now().toEpochMilli());
	}
}
