package com.service.dto;

import com.service.Entity.Order;
import com.service.Entity.OrderItem;
import com.service.Entity.Product;
import com.service.ReadFromProperties;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class Requests {

    private Connection connection;
    private PreparedStatement prepareStatement;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Requests(Connection connection) {
        this.connection = connection;
    }

    public boolean createProduct(Product product) throws SQLException {
        prepareStatement = connection.prepareStatement("insert into products(name, price, status, created_at) values (?, ?, ?, ?)");
        prepareStatement.setString(1, product.getName());
        prepareStatement.setDouble(2, product.getPrice());
        prepareStatement.setObject(3, product.getStatus());
        prepareStatement.setString(4, product.getCreatedAt());
        prepareStatement.executeUpdate();
        return true;
    }

    public boolean createOrder(Order order, List<OrderItem> list) throws SQLException {
        int id = 1;
        prepareStatement = connection.prepareStatement("insert into orders(user_id, status, created_at) values(?, ?, ?)");
        prepareStatement.setInt(1, order.getUserId());
        prepareStatement.setString(2, order.getStatus());
        prepareStatement.setString(3, order.getCreatedAt());
        prepareStatement.executeUpdate();

        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery("select id from orders");
        if (resultSet.last()) {
            id = resultSet.getInt("id");
        }

        // create order item
        for (OrderItem e : list) {
            createOrderItem(id, e);
        }
        return true;
    }

    public boolean updateOrder(int id, int id_product, int quantity) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("update order_item set quantity = " +
                +quantity + " where order_id = " + id + " and product_id = " + id_product);
        return true;
    }

    public boolean showAllProduct() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select name, price, status from products");
        while (resultSet.next()) {
            System.out.printf("%-30s", "| Product Name: " + resultSet.getString("name"));
            System.out.printf("%-25s", "| Product Price: " + resultSet.getInt("price"));
            System.out.printf("%-10s", "| Product Status: " + resultSet.getString("status") + " |\n");
        }
        return true;
    }

    public boolean showHistoryOrdered() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select product_id, name, sum(quantity) from order_item \n" +
                "left join products on product_id = id\n" +
                "where quantity >= 1 group by product_id\n" +
                "order by quantity desc");
        while (resultSet.next()) {
            System.out.printf("%-20s", "| Product id: " + resultSet.getInt("product_id"));
            System.out.printf("%-30s", "| Product name: " + resultSet.getString("name"));
            System.out.printf("%-10s", "| Product quantities: " + resultSet.getInt("sum(quantity)") + "\t|\n");
        }
        return true;
    }

    public boolean showOrder(int id) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select orders.id, price * quantity as price, name, quantity, orders.created_at from orders \n" +
                "left join order_item on id = order_id\n" +
                "left join products on products.id = order_item.product_id where orders.id = " + id);
        while (resultSet.next()) {
            System.out.printf("%-20s", "| Order id: " + resultSet.getInt("id"));
            System.out.printf("%-25s", "| Product price: " + resultSet.getInt("price"));
            System.out.printf("%-30s", "| Product name: " + resultSet.getString("name"));
            System.out.printf("%-15s", "| Product quantities: " + resultSet.getInt("quantity"));
            System.out.printf("%-8s", "| created_at: " + resultSet.getString("created_at") + "\t|\n");
        }
        return true;
    }

    public boolean showOrder() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select orders.id, price * quantity as price, name, quantity, orders.created_at from orders \n" +
                "left join order_item on id = order_id\n" +
                "left join products on products.id = order_item.product_id");
        while (resultSet.next()) {
            System.out.printf("%-20s", "| Order id: " + resultSet.getInt("id"));
            System.out.printf("%-25s", "| Product price: " + resultSet.getInt("price"));
            System.out.printf("%-30s", "| Product name: " + resultSet.getString("name"));
            System.out.printf("%-25s", "| Product quantities: " + resultSet.getInt("quantity"));
            System.out.printf("%-8s", "| created_at: " + resultSet.getString("created_at") + "\t|\n");
        }
        return true;
    }

    public boolean removeProduct(int id) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("delete from products where id = " + id);
        return true;
    }

    public boolean removeAllProduct(String password) throws SQLException {
        if (password.equals(ReadFromProperties.getPASSWORD())) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from products");
            return true;
        } else {
            System.err.println("wrong password");
            return false;
        }

    }

    public boolean createOrderItem(int id, OrderItem item) throws SQLException {
        int id_product;
        int quantity;

        Statement statement = connection.createStatement();
        PreparedStatement prepareStatement = connection.prepareStatement("insert into order_item(order_id, product_id, quantity) values(?, ?, ?)");

        boolean newItem = true;
        id_product = item.getProductId();
        quantity = item.getQuantity();

        ResultSet resultSet = statement.executeQuery("select product_id, quantity from order_item where order_id = " + id);
        while (resultSet.next()) {
            if (resultSet.getInt("product_id") == id_product) {
                newItem = false;
                int newQuantity = resultSet.getInt("quantity") + quantity;
                updateOrder(id, id_product, newQuantity);
            }
        }
        if (newItem) {
            prepareStatement.setInt(1, id);
            prepareStatement.setInt(2, id_product);
            prepareStatement.setInt(3, quantity);
            prepareStatement.executeUpdate();

        }
        return true;
    }
}
