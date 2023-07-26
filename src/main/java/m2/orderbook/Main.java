package m2.orderbook;

import java.math.BigDecimal;
import java.util.concurrent.Executors;

import m2.orderbook.dto.Order;
import m2.orderbook.enums.OrderSide;
import m2.orderbook.enums.Ordertype;
import m2.orderbook.service.impl.OrderbookServiceImpl;
import m2.orderbook.util.OrderUtility;

public class Main {

	public static void main(String[] args) {

		
		
		// Adding sample code for Matching engine Testing
		final OrderbookServiceImpl serv = new OrderbookServiceImpl();
		serv.initializeOrderBook();

		final Order limitOrder =  new Order(OrderUtility.getOrderId(), new BigDecimal(6), new BigDecimal(9), OrderSide.BUY, Ordertype.LIMIT);
		Executors.newSingleThreadExecutor().execute(new Runnable() {
		    @Override 
		    public void run() {
		    	serv.placeNewOrder(limitOrder);
		    	serv.cancelOrder(limitOrder);
		    }
		});
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
		    @Override 
		    public void run() {
		    	serv.placeNewOrder( new Order(OrderUtility.getOrderId(), null, new BigDecimal(5), OrderSide.BUY, Ordertype.MARKET));
		    }
		});
		
	
		
		
	}

}
