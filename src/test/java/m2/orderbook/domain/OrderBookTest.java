package m2.orderbook.domain;

import m2.orderbook.OrderBook;
import m2.orderbook.dto.Order;
import m2.orderbook.enums.OrderActionType;
import m2.orderbook.enums.OrderSide;
import m2.orderbook.enums.Ordertype;
import m2.orderbook.enums.Symbol;
import m2.orderbook.exception.OrderException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderBookTest {
    private static OrderBook orderBook;
    private static final Symbol symbol = Symbol.BTC;

    @BeforeAll
    static void setUpAll() {
        orderBook = new OrderBook(symbol);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testOrderBookCreationShouldThrowIllegalArgumentExceptionWhenSymbolIsNullOrEmptyOrWhiteOrOnlySpaces() {
        assertThrows(IllegalArgumentException.class, () -> {
           new OrderBook(null);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetSymbol() {
        assertEquals(symbol, orderBook.getSymbol());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testAddBuyOrder() throws OrderException {
        Order order = new Order("1", new BigDecimal("100"), new BigDecimal("10"), OrderSide.BUY, Ordertype.LIMIT);

        orderBook.addOrder(order);
        assertNotNull(orderBook.getBuyOrders().get(order.getPrice()));
        assertNotNull(orderBook.getBuyOrders().get(order.getPrice()).peek());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testAddSellOrder() throws OrderException {
        Order order = new Order("2", new BigDecimal("105"), new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);
        orderBook.addOrder(order);
        assertNotNull(orderBook.getSellOrders().get(order.getPrice()));
        assertNotNull(orderBook.getSellOrders().get(order.getPrice()).peek());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testRemoveBuyOrder() throws OrderException {
        Order order = new Order("1", new BigDecimal("100"), new BigDecimal("10"), OrderSide.BUY, Ordertype.LIMIT);
        orderBook.removeOrder(order);
        assertFalse(orderBook.getBuyOrders().containsKey(order.getPrice()));
        assertNull(orderBook.getBuyOrders().get(order.getPrice()));
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testRemoveSellOrder() throws OrderException {
        Order order = new Order("2", new BigDecimal("105"), new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);
        orderBook.removeOrder(order);
        assertFalse(orderBook.getSellOrders().containsKey(order.getPrice()));
        assertNull(orderBook.getSellOrders().get(order.getPrice()));
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testExecuteOrderShouldRemoveExistingOrderWhenExecutedWithActionTypeRemove() throws OrderException {
        Order order1 = new Order("5", new BigDecimal("200"), new BigDecimal("10"), OrderSide.BUY, Ordertype.LIMIT);
        orderBook.addOrder(order1);
        orderBook.executeOrder(order1, OrderActionType.REMOVE);

        assertFalse(orderBook.getBuyOrders().containsKey(order1.getPrice()));
        assertNull(orderBook.getBuyOrders().get(order1.getPrice()));
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testRemoveOrderShouldThrowOrderExceptionWhenRemovingOrderWithNonExistingId() throws OrderException {
        Order order1 = new Order("6", new BigDecimal("100"), new BigDecimal("10"), OrderSide.BUY, Ordertype.LIMIT);
        Order order2 = new Order("20", new BigDecimal("100"), new BigDecimal("10"), OrderSide.BUY, Ordertype.LIMIT);

        orderBook.addOrder(order1);

        OrderException exception = assertThrows(OrderException.class, () -> {
            orderBook.removeOrder(order2);
        });

        String exceptionMsg = exception.getMessage();
        String expectedMsg = "Remove fail.Not found, Order:" + order2.getOrderId();
        assertTrue(exceptionMsg.contains(expectedMsg));
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testRemoveOrderShouldThrowOrderExceptionWhenRemovingAlreadyExecuted() throws OrderException {
        orderBook.clear();
        Order order1 = new Order("7", new BigDecimal("100"), new BigDecimal("10"), OrderSide.BUY, Ordertype.LIMIT);
        Order order2 = new Order("8", new BigDecimal("100"), new BigDecimal("10"), OrderSide.BUY, Ordertype.LIMIT);
        Order order3 = new Order("9", new BigDecimal("90"), new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);

        orderBook.executeOrder(order1, OrderActionType.ADD);
        orderBook.executeOrder(order2, OrderActionType.ADD);
        orderBook.executeOrder(order3, OrderActionType.ADD);

        OrderException exception = assertThrows(OrderException.class, () -> {
            orderBook.executeOrder(order1, OrderActionType.REMOVE);
        });

        String exceptionMsg = exception.getMessage();
        String expectedMsg = "Remove fail.Not found, Order:" + order1.getOrderId();
        assertTrue(exceptionMsg.contains(expectedMsg));

        exception = assertThrows(OrderException.class, () -> {
            orderBook.executeOrder(order3, OrderActionType.REMOVE);
        });

        exceptionMsg = exception.getMessage();
        expectedMsg = "Remove fail.Not found, Order:" + order3.getOrderId();
        assertTrue(exceptionMsg.contains(expectedMsg));
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void testClearOrderBook() {
        orderBook.clear();
        assertTrue(orderBook.getBuyOrders().isEmpty());
        assertTrue(orderBook.getSellOrders().isEmpty());
        assertNotNull(orderBook.getSymbol());
        assertNotEquals("", orderBook.getSymbol());
    }
    

}