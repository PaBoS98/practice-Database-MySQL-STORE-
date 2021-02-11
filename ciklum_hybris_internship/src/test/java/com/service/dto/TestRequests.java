package com.service.dto;

import com.service.ConnectionManager;
import com.service.Entity.Order;
import com.service.Entity.OrderItem;
import com.service.Entity.Product;
import com.service.ReadFromProperties;
import com.service.enums.ProductStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Mockito.*;

public class TestRequests {

    private String url = "jdbc:mysql://localhost:3306/test_orderdb?useSSL=false&serverTimezone=UTC";
    private String username = "root";
    private String password = "root";
    private Connection connection;
    private Requests requests;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Product testProduct;
    private Order testOrder;
    private List<OrderItem> testItems;

    @BeforeEach
    public void createConnection() throws SQLException {
        ReadFromProperties.setPASSWORD(password);
        connection = DriverManager.getConnection(url, username, password);
        requests = new Requests(connection);
        Statement statement = connection.createStatement();
        statement.executeUpdate("drop table if exists order_item, orders, products");
        statement.executeUpdate("CREATE TABLE if not exists `orders` (" +
                "  `id` int NOT NULL AUTO_INCREMENT," +
                "  `user_id` int DEFAULT NULL," +
                "  `status` varchar(50) DEFAULT NULL," +
                "  `created_at` varchar(100) DEFAULT NULL," +
                "  PRIMARY KEY (`id`)" +
                ")");
        statement.executeUpdate("CREATE TABLE if not exists `products` (" +
                "  `id` int NOT NULL AUTO_INCREMENT," +
                "  `name` varchar(100) DEFAULT NULL," +
                "  `price` int DEFAULT NULL," +
                "  `status` enum('OUT_OF_STOCK','IN_STOCK','RUNNING_LOW') DEFAULT NULL," +
                "  `created_at` datetime DEFAULT NULL," +
                "  PRIMARY KEY (`id`)" +
                ")");
        statement.executeUpdate("CREATE TABLE if not exists `order_item` (" +
                "  `order_id` int DEFAULT NULL," +
                "  `product_id` int DEFAULT NULL," +
                "  `quantity` int DEFAULT NULL," +
                "  KEY `order_id` (`order_id`)," +
                "  KEY `product_id` (`product_id`)," +
                "  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE," +
                "  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE" +
                ");");

        testProduct = new Product.Builder()
                .setName("test product")
                .setPrice(25)
                .setStatus(String.valueOf(ProductStatus.OUT_OF_STOCK))
                .setDate(dateFormat.format(Calendar.getInstance().getTime()))
                .build();
        requests.createProduct(testProduct);

        testOrder = new Order.Builder()
                .setUserId((int) (Math.random() * 100000))
                .setStatus("test status")
                .setDate("test date").build();
        testItems = new ArrayList<>();
        testItems.add(new OrderItem.Builder()
                .setProductId(1)
                .setQuantity(10).build());
        requests.createOrder(testOrder, testItems);
    }

    @Test
    public void testCreateProduct() throws SQLException {
        System.out.println("Before create new products");
        requests.showAllProduct();
        testProduct = new Product.Builder()
                .setName("mask")
                .setPrice(8)
                .setStatus("IN_STOCK")
                .setDate(dateFormat.format(Calendar.getInstance().getTime()))
                .build();
        requests.createProduct(testProduct);
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery("select * from products");
        Product productFromDB = null;

        if (resultSet.last()) {
            productFromDB = new Product.Builder()
                    .setName(resultSet.getString("name"))
                    .setPrice(resultSet.getInt("price"))
                    .setStatus(resultSet.getString("status"))
                    .setDate(resultSet.getString("created_at"))
                    .build();
        }

        System.out.println("After create new products");
        requests.showAllProduct();
        Assertions.assertEquals(testProduct, productFromDB);
    }

    @Test
    public void testCreateOrder() throws SQLException {
        Order order = new Order.Builder()
                .setUserId((int) (Math.random() * 100000))
                .setStatus("test status")
                .setDate("test date").build();
        List<OrderItem> items = new ArrayList<>();
        items.add(mock(OrderItem.class));
        items.add(mock(OrderItem.class));
        items.add(mock(OrderItem.class));
        Requests r = mock(Requests.class);
        when(r.createOrder(order, items)).thenReturn(true);
        r.createOrder(order, items);
        verify(r, times(1)).createOrder(order, items);
        Assertions.assertTrue(r.createOrder(order, items));
    }

    @Test
    public void testUpdateOrder() throws SQLException {
        System.out.println("Before update order");
        requests.showOrder();
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery("select * from orders left join order_item on id = order_id");

        Order orderBeforeChange;
        OrderItem orderItemBeforeChane;
        while (resultSet.next()) {
            orderBeforeChange = new Order.Builder()
                    .setUserId(resultSet.getInt("user_id"))
                    .setStatus(resultSet.getString("status"))
                    .setDate(resultSet.getString("created_at")).build();
            orderItemBeforeChane = new OrderItem.Builder()
                    .setProductId(resultSet.getInt("product_id"))
                    .setQuantity(resultSet.getInt("quantity")).build();

            requests.updateOrder(resultSet.getInt("id"), 1, 2);
            resultSet = statement.executeQuery("select * from orders left join order_item on id = order_id");
            Order orderAfterChange = null;
            OrderItem orderItemAfterChange = null;
            while (resultSet.next()) {
                orderAfterChange = new Order.Builder()
                        .setUserId(resultSet.getInt("user_id"))
                        .setStatus(resultSet.getString("status"))
                        .setDate(resultSet.getString("created_at")).build();
                orderItemAfterChange = new OrderItem.Builder()
                        .setProductId(resultSet.getInt("product_id"))
                        .setQuantity(resultSet.getInt("quantity")).build();

            }
            System.out.println("After update order");
            requests.showOrder();
            Assertions.assertEquals(orderBeforeChange, orderAfterChange);
            Assertions.assertNotEquals(orderItemBeforeChane, orderItemAfterChange);
        }
    }

    @Test
    public void testShowAllProduct() throws SQLException {
        System.out.println("Test show all product");
        Assertions.assertTrue(requests.showAllProduct());
    }

    @Test
    public void testShowHistoryOrdered() throws SQLException {
        System.out.println("Test show history ordered");
        Statement statement = connection.createStatement();
        statement.executeUpdate("insert into products(name, price, status, created_at) " +
                "value('chocolate', 23, '0', '28.01.2021')," +
                "('tea', 15, '0', '28.01.2021')," +
                "('coffee', 15, '0', '28.01.2021')," +
                "('juce', 18.90, '0', '28.01.2021')," +
                "('pizza', 60, '0', '28.01.2021')," +
                "('meat', 120, '0', '28.01.2021')");
        statement.executeUpdate("insert into orders(user_id, status, created_at)" +
                "value(85624, 'processed', '28/01/2021 15:30')");
        statement.executeUpdate("insert into order_item(order_id, product_id, quantity)" +
                "value(1, 5, 1)," +
                "(1, 5, 5)," +
                "(1, 3, 6)," +
                "(1, 6, 99)");

        Assertions.assertTrue(requests.showHistoryOrdered());
    }

    @Test
    public void testShowOrderById() throws SQLException {
        System.out.println("Test show order by id = 1");
        Assertions.assertTrue(requests.showOrder(1));
    }

    @Test
    public void testShowAllOrder() throws SQLException {
        System.out.println("Test show all orders");
        Assertions.assertTrue(requests.showOrder());
    }

    @Test
    public void testRemoveProduct() throws SQLException {
        System.out.println("Before remove");
        requests.showAllProduct();
        Assertions.assertTrue(requests.removeProduct(1));
        System.out.println("After remove");
        requests.showAllProduct();
    }

    @Test
    public void testRemoveAllProduct() throws SQLException {
        System.out.println("Before remove");
        requests.showAllProduct();
        Assertions.assertTrue(requests.removeAllProduct(password));
        System.out.println("After remove");
        requests.showAllProduct();
    }

    @Test
    public void FailedTestRemoveAllProduct() throws SQLException {
        System.out.println("Before unsuccessful deletion");
        requests.showAllProduct();
        Assertions.assertFalse(requests.removeAllProduct("fail"));
        System.out.println("after unsuccessful deletion");
        requests.showAllProduct();
    }

    @Test
    public void testCreateOrderItem() throws SQLException {
        System.out.println("Before create");
        requests.showOrder(1);
        requests.createProduct(new Product.Builder()
                .setName("some product")
                .setStatus("in_stock")
                .setDate(dateFormat.format(Calendar.getInstance().getTime()))
                .setPrice(25).build());
        OrderItem item = new OrderItem.Builder()
                .setProductId(2)
                .setQuantity(5).build();
        Assertions.assertTrue(requests.createOrderItem(1, item));
        System.out.println("After create");
        requests.showOrder(1);
    }
}