package m2.orderbook.service.impl;

import java.math.BigDecimal;

import m2.orderbook.OrderBook;
import m2.orderbook.dto.Order;
import m2.orderbook.enums.OrderActionType;
import m2.orderbook.enums.OrderSide;
import m2.orderbook.enums.Ordertype;
import m2.orderbook.enums.Symbol;
import m2.orderbook.service.OrderbookService;
import m2.orderbook.util.OrderUtility;

public class OrderbookServiceImpl implements OrderbookService {

	OrderBook orderBook;
	
	@Override
	public void initializeOrderBook() {
		
		orderBook = new OrderBook(Symbol.BTC);
		
		for(int i=0;i<10;i++) {
			
			String id = OrderUtility.getOrderId();
			OrderSide side = (i<5) ? OrderSide.BUY : OrderSide.SELL;
			orderBook.addOrder( new Order(id, new BigDecimal(i+1), new BigDecimal(i+1), side, Ordertype.LIMIT));
		}
		
		System.out.println(orderBook);
		
	}
	@Override
	public void placeNewOrder(Order order) {

		orderBook.executeOrder(order, OrderActionType.ADD);
		System.out.println(orderBook);

	}
	
	@Override
	public void cancelOrder(Order order) {
		
		orderBook.executeOrder(order, OrderActionType.REMOVE);
		System.out.println(orderBook);	
	}
	
	 

}
