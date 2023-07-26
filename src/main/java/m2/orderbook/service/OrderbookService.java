package m2.orderbook.service;

import m2.orderbook.dto.Order;

public interface OrderbookService {

	void initializeOrderBook();
	
	void placeNewOrder(Order order);
	
	void cancelOrder(Order order);
}
