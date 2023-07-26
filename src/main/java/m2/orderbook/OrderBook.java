package m2.orderbook;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import m2.orderbook.dto.Order;
import m2.orderbook.enums.OrderActionType;
import m2.orderbook.enums.OrderSide;
import m2.orderbook.enums.Ordertype;
import m2.orderbook.enums.Symbol;
import m2.orderbook.exception.OrderException;
import m2.orderbook.util.BigDecimalUtility;


public class OrderBook {

	private Symbol symbol;
	private final TreeMap<BigDecimal, Queue<Order>> sellOrders;
	private final TreeMap<BigDecimal, Queue<Order>> buyOrders;

	public synchronized void executeOrder(Order order, OrderActionType orderActionType) throws OrderException {
		System.out.println("\nNEW ORDER:  Price: "+order.getPrice()+" , Quantity: "+order.getQuantity()+ " , side: "+ order.getSide()+" , type: "+order.getType());
		switch (orderActionType) {
		case ADD:

			checkExistingOrderToMatch(getOrderMap(order.getSide(), false), order);
			if(order.hasOrderQuantityLeft()) {
				System.out.println("\nLIMIT: Add remaining order to orderbook at Price: "+order.getPrice()+" , Quantity: "+order.getQuantity());
				addOrder(order);
			}

			break;
		case REMOVE:
			removeOrder(order);
			break;
		default:
			throw new OrderException("Unexpected Order Action Type:" + orderActionType);
		}
	}


	private void checkExistingOrderToMatch(TreeMap<BigDecimal, Queue<Order>> orderMapToCompare, Order order) {

		// iterate through comparing order map
		Iterator<Map.Entry<BigDecimal, Queue<Order>>> iterator = orderMapToCompare.entrySet().iterator();

		if(order.getType() == Ordertype.MARKET) {

			while (iterator.hasNext()) {
				Map.Entry<BigDecimal, Queue<Order>> entry = iterator.next();

				BigDecimal comparingPrice = entry.getKey();

				// iterate through order queue and execute order
				Queue<Order> orderQueue = orderMapToCompare.get(comparingPrice);
				Iterator<Order> queueIterator = orderQueue.iterator();
				while(queueIterator.hasNext()) {
					Order orderInFront = queueIterator.next();
					if(orderInFront.getQuantity().compareTo(order.getQuantity()) > 0) {
						orderInFront.setQuantity(BigDecimalUtility.setScale(orderInFront.getQuantity().subtract(order.getQuantity())));
						System.out.println("\nMARKET: Complete Order matched at Price: "+comparingPrice+" , Quantity: "+order.getQuantity());
						order.setQuantity(BigDecimal.ZERO);
						break;
					} else {
						order.setQuantity(order.getQuantity().subtract(orderInFront.getQuantity()));
						System.out.println("\nMARKET: Partial Order matched at Price: "+comparingPrice+" , Quantity: "+orderInFront.getQuantity());
						queueIterator.remove();
					}
				}

				// remove entry from Order Book if no more orders in the queue
				if(orderQueue.isEmpty()) {
					iterator.remove();
				}
				// exit if order is fully executed
				if(!order.hasOrderQuantityLeft()) {
					break;
				}
			}
			if(order.hasOrderQuantityLeft()){
				System.err.println("Orders not available for Market Order");
			}

		}else if(order.getType() == Ordertype.LIMIT){

			while (iterator.hasNext()) {
				Map.Entry<BigDecimal, Queue<Order>> entry = iterator.next();

				BigDecimal comparingPrice = entry.getKey();
				if(order.isExecutable(comparingPrice)) {
					// iterate through order queue and execute order
					Queue<Order> orderQueue = orderMapToCompare.get(comparingPrice);
					Iterator<Order> queueIterator = orderQueue.iterator();
					while(queueIterator.hasNext()) {
						Order orderInFront = queueIterator.next();
						if(orderInFront.getQuantity().compareTo(order.getQuantity()) > 0) {
							orderInFront.setQuantity(BigDecimalUtility.setScale(orderInFront.getQuantity().subtract(order.getQuantity())));
							System.out.println("\nLIMIT: Complete Order matched at Price: "+comparingPrice+" , Quantity: "+order.getQuantity());
							order.setQuantity(BigDecimal.ZERO);
							break;
						} else {
							order.setQuantity(order.getQuantity().subtract(orderInFront.getQuantity()));
							System.out.println("\nLIMIT: Partial Order matched at Price: "+comparingPrice+" , Quantity: "+orderInFront.getQuantity());
							queueIterator.remove();
						}
					}

					// remove entry from Order Book if no more orders in the queue
					if(orderQueue.isEmpty()) {
						iterator.remove();
					}
					// exit if order is fully executed
					if(!order.hasOrderQuantityLeft()) {
						break;
					}
				}else {
					// exit if no matching price found for execution
					break;
				}
			}
		}
	}

	public void addOrder(Order order) throws OrderException {

		if(!order.isValidOrder()) 
			throw new OrderException("Not executable, Order:" + order.getOrderId());


		TreeMap<BigDecimal, Queue<Order>> orderMap = getOrderMap(order.getSide(), true);

		// if price exist add to the end of the existing queue else add a new queue
		if (orderMap.containsKey(order.getPrice())) {
			Queue<Order> orderQueue = orderMap.get(order.getPrice());
			orderQueue.add(order);
		} else {
			Queue<Order> orderQueue = new LinkedList<>();
			orderQueue.add(order);
			orderMap.put(order.getPrice(), orderQueue);
		}

	}

	public void removeOrder(Order order) throws OrderException {
		TreeMap<BigDecimal, Queue<Order>>  orderMap = getOrderMap(order.getSide(), true);
		boolean orderNotFound = true;
		// check a matching price available in the corresponding map
		if(orderMap.containsKey(order.getPrice())) {
			Queue<Order> orderQueue = orderMap.get(order.getPrice());
			Iterator<Order> queueIterator = orderQueue.iterator();

			while(queueIterator.hasNext()) {
				if(order.getOrderId().equals(queueIterator.next().getOrderId())) {
					System.out.println("\nOrder Canceled Price: "+order.getPrice()+" , Quantity: "+order.getQuantity());
					queueIterator.remove();
					orderNotFound = false;
					break;
				}
			}
			if(orderQueue.isEmpty()) {
				orderMap.remove(order.getPrice());
			}
		}
		if(orderNotFound) {
			throw new OrderException("Remove fail.Not found, Order:" + order.getOrderId());
		}
	}

	private TreeMap<BigDecimal, Queue<Order>> getOrderMap(OrderSide side, boolean isSameSide) {
		TreeMap<BigDecimal, Queue<Order>>  orderMap;
		OrderSide orderMapSide = side;
		if(!isSameSide) {
			orderMapSide = (side == OrderSide.BUY) ? OrderSide.SELL : OrderSide.BUY;
		}
		switch (orderMapSide) {
		case BUY:
			orderMap = buyOrders;
			break;
		case SELL:
			orderMap = sellOrders;
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + side);
		}
		return orderMap;
	}





	public OrderBook(Symbol symbol) {
		if(symbol==null)
			throw new IllegalArgumentException();
		setSymbol(symbol);
		sellOrders = new TreeMap<BigDecimal, Queue<Order>>();
		buyOrders = new TreeMap<BigDecimal, Queue<Order>>(Collections.reverseOrder());

	}

	public String toString() {
		StringBuilder orderBookString = new StringBuilder();

		orderBookString.append("\n>>>>>>>>>>>>>>>>>>>>>>>ORDERBOOK>>>>>>>>>>>>>>>>>>>>>>>>>>>\n>>           SELL\n");

		try {
			for (Map.Entry<BigDecimal, Queue<Order>> entry : sellOrders.descendingMap().entrySet()) {
				orderBookString.append(getOrderBookEntryString(entry));
			}
			orderBookString.append(">> PRICE                QUANTITY\n");
			for (Map.Entry<BigDecimal, Queue<Order>> entry : buyOrders.entrySet()) {
				orderBookString.append(getOrderBookEntryString(entry));
			}
			orderBookString.append(">>               BUY\n<<<<<<<<<<<<<<<<<<<<<<<<ORDERBOOK<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
		}catch(Exception e) {
			// To Suppress concurrent modification exception
			//	System.err.println("Exception "+e.getMessage());

		}
		return orderBookString.toString();
	}

	private StringBuilder getOrderBookEntryString(Map.Entry<BigDecimal, Queue<Order>> entry) {
		StringBuilder orderBookEntryString = new StringBuilder();
		orderBookEntryString.append(">> ");
		orderBookEntryString.append(entry.getKey().toString());
		orderBookEntryString.append(":         ");
		// remove ',' from quantity string
		String quantityString = entry.getValue().toString().replaceAll(",","");
		// get order string without brackets '[' and ']'
		orderBookEntryString.append(quantityString.substring(1,quantityString.length() - 1));
		orderBookEntryString.append("\n");

		return orderBookEntryString;
	}

	private void setSymbol(Symbol symbol) {
		this.symbol = symbol;

	}

	public Symbol getSymbol() {
		return symbol;
	}


	public TreeMap<BigDecimal, Queue<Order>> getSellOrders() {
		return sellOrders;
	}


	public TreeMap<BigDecimal, Queue<Order>> getBuyOrders() {
		return buyOrders;
	}

	public void clear() {
		sellOrders.clear();
		buyOrders.clear();
	}


}
