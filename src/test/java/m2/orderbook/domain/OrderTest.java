package m2.orderbook.domain;

import m2.orderbook.enums.OrderSide;
import m2.orderbook.enums.Ordertype;
import m2.orderbook.util.BigDecimalUtility;

import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import m2.orderbook.dto.Order;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderTest {

    private static Order order;

    @BeforeAll
    static void setUpAll() {
        order = new Order("1", new BigDecimal("100"), new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);
    }



    @Test
    @org.junit.jupiter.api.Order(1)
    public void testCreateOrderShouldThrowIllegalArgumentExceptionWhenOrderIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order(null, new BigDecimal("100"), new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenPriceIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", null, new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenQuantityIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", new BigDecimal("100"), null, OrderSide.SELL, Ordertype.LIMIT);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenOrderSideIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", new BigDecimal("100"), new BigDecimal("10"), null, Ordertype.LIMIT);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenPriceIsLessThanOrEqualToZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", new BigDecimal(-1), new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", BigDecimal.ZERO, new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testCreateOrderShouldThrowIllegalArgumentExceptionWhenQuantityIsLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Order("1", new BigDecimal("100"), new BigDecimal("-1"), OrderSide.SELL, Ordertype.LIMIT);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testGetOrderId() {
        assertEquals("1", order.getOrderId());
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testGetQuantity() {
        BigDecimal quantity = BigDecimalUtility.setScale(new BigDecimal("10"));
        order = new Order("1", new BigDecimal("100"), new BigDecimal("10"), OrderSide.SELL, Ordertype.LIMIT);
        assertEquals(quantity, order.getQuantity());
    }

    @Test
    @org.junit.jupiter.api.Order(9)
    void testGetPrice() {
        BigDecimal price = BigDecimalUtility.setScale(new BigDecimal("100"));
        assertEquals(price, order.getPrice());
    }

    @Test
    @org.junit.jupiter.api.Order(10)
    void testGetSide() {
        assertEquals(OrderSide.SELL, order.getSide());
    }

    @Test
    @org.junit.jupiter.api.Order(11)
    void testSetQuantity() {
        BigDecimal quantity = BigDecimalUtility.setScale(new BigDecimal("30"));
        order.setQuantity(quantity);
        assertEquals(quantity, order.getQuantity());
    }

    @Test
    @org.junit.jupiter.api.Order(12)
    void testSetQuantityShouldThrowIllegalArgumentExceptionWhenQuantityIsLessThanZero() {
        BigDecimal quantity = new BigDecimal("-1");
        assertThrows(IllegalArgumentException.class, () -> {
            order.setQuantity(quantity);
        });
    }

    @Test
    @org.junit.jupiter.api.Order(13)
    void testSetPrice() {
        BigDecimal price = new BigDecimal("10.45");
        order.setPrice(price);
        assertEquals(0, order.getPrice().compareTo(price));
    }

    @Test
    @org.junit.jupiter.api.Order(14)
    void testSetSide() {
        OrderSide side = OrderSide.SELL;
        order.setSide(side);
        assertEquals(side, order.getSide());
    }

    @Test
    @org.junit.jupiter.api.Order(15)
    void testSetOrderId() {
        String orderId = "1111";
        order.setOrderId(orderId);
        assertEquals(orderId, order.getOrderId());
    }

    @Test
    @org.junit.jupiter.api.Order(16)
    void testIsExecutableOrderShouldNotBeExecutableWhenOrderQuantityIsZero() {
        order.setQuantity(BigDecimal.ZERO);
        assertFalse(order.hasOrderQuantityLeft());
    }

    @Test
    @org.junit.jupiter.api.Order(17)
    void testIsExecutableOrderShouldBeExecutableWhenOrderQuantityIsGreaterThanZero() {
        order.setQuantity(new BigDecimal("10"));
        assertTrue(order.hasOrderQuantityLeft());
    }

    @Test
    @org.junit.jupiter.api.Order(18)
    void testIsExecutableBuyOrderShouldBeExecutableWhenQuantityIsGreaterThanZeroAndComparingPriceIsLessThanOrderPrice() {
        BigDecimal comparingPrice = new BigDecimal("80");
        order.setQuantity(new BigDecimal("10"));
        order.setPrice(new BigDecimal("90"));
        order.setSide(OrderSide.BUY);

        assertTrue(order.isExecutable(comparingPrice));
    }

    @Test
    @org.junit.jupiter.api.Order(19)
    void testIsExecutableBuyOrderShouldNotBeExecutableWhenQuantityIsGreaterThanZeroAndComparingPriceIsGreaterOrderPrice() {
        BigDecimal comparingPrice = new BigDecimal("80");
        order.setQuantity(new BigDecimal("10"));
        order.setPrice(new BigDecimal("70"));
        order.setSide(OrderSide.BUY);

        assertFalse(order.isExecutable(comparingPrice));
    }

    @Test
    @org.junit.jupiter.api.Order(20)
    void testIsExecutableSellOrderShouldNotBeExecutableWhenQuantityIsGreaterThanZeroAndComparingPriceIsLessThanOrderPrice() {
        BigDecimal comparingPrice = new BigDecimal("80");
        order.setQuantity(new BigDecimal("10"));
        order.setPrice(new BigDecimal("90"));
        order.setSide(OrderSide.SELL);

        assertFalse(order.isExecutable(comparingPrice));
    }

    @Test
    @org.junit.jupiter.api.Order(21)
    void testIsExecutableSellOrderShouldBeExecutableWhenQuantityIsGreaterThanZeroAndComparingPriceIsGreaterOrderPrice() {
        BigDecimal comparingPrice = new BigDecimal("80");
        order.setQuantity(new BigDecimal("10"));
        order.setPrice(new BigDecimal("70"));
        order.setSide(OrderSide.SELL);

        assertTrue(order.isExecutable(comparingPrice));
    }

}