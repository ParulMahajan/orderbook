package m2.orderbook.dto;

import java.math.BigDecimal;

import m2.orderbook.enums.OrderSide;
import m2.orderbook.enums.Ordertype;
import m2.orderbook.util.BigDecimalUtility;

public class Order {

	private	String orderId;

	private	BigDecimal price;

	private BigDecimal quantity;

	private	OrderSide side;

	private Ordertype type;


	public Order(String orderId, BigDecimal price, BigDecimal quantity, OrderSide side,Ordertype type) {

		setOrderId(orderId);
		setSide(side);
		this.type = type;
		if(type==Ordertype.LIMIT)
		setPrice(price);
		setQuantity(quantity);

		
	}


	public Ordertype getType() {
		return type;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}


	public void setQuantity(BigDecimal quantity) {
		if(isValidQuantity(quantity)){
			this.quantity = BigDecimalUtility.setScale(quantity) ;
		} else {
			throw new IllegalArgumentException("Invalid Order Quantity:" + quantity);
		}
	}
	
	public boolean isExecutable(BigDecimal comparingPrice) {
        if(isValidOrder()) {
            if(this.getSide() == OrderSide.BUY) {
                return this.getPrice().compareTo(comparingPrice) >= 0;
            } else {
                return this.getPrice().compareTo(comparingPrice) <= 0;
            }
        }
        return false;
    }
	
	 


	public BigDecimal getPrice() {
		return price;
	}


	public void setPrice(BigDecimal price) {
		if(isValidPrice(price)){
			this.price = BigDecimalUtility.setScale(price) ;
		} else {
			throw new IllegalArgumentException("Invalid Order Price:" + price);
		}
	}

	  public void setSide(OrderSide side) {
	        if(isValidOrderSide(side)) {
	            this.side = side;
	        } else {
	            throw new IllegalArgumentException("Invalid Order Side");
	        }
	    }


	public OrderSide getSide() {
		return side;
	}

	public String getOrderId() {
		return orderId;
	}

	public String toString() {
		return String.valueOf(this.quantity);
	}

	public boolean isValidOrder() {
		return quantity.compareTo(BigDecimal.ZERO) > 0 && price.compareTo(BigDecimal.ZERO) > 0;
	}

	private boolean isValidPrice(BigDecimal price) {
		return price != null && price.compareTo(BigDecimal.ZERO) > 0;
	}

	public boolean isValidQuantity(BigDecimal quantity) {
		return quantity != null && quantity.compareTo(BigDecimal.ZERO) >= 0;
	}
	 public boolean hasOrderQuantityLeft() {
	        return quantity.compareTo(BigDecimal.ZERO) > 0;
	    }
	 
	 private boolean isValidOrderSide(OrderSide side) {
	        return side == OrderSide.BUY || side == OrderSide.SELL;
	    }


	public void setOrderId(String orderId) {
		
		if(isValidOrderId(orderId)){
			this.orderId=orderId;
		} else {
			throw new IllegalArgumentException("Invalid Order Price:" + price);
		}
		
	}


	private boolean isValidOrderId(String orderId) {
		  return orderId != null && !orderId.trim().equals("");
	}
	

}
