package com;

import com.service.ConnectionManager;
import com.service.Entity.Order;
import com.service.Entity.OrderItem;
import com.service.Entity.Product;
import com.service.ReadFromProperties;
import com.service.Specify;
import com.service.dto.Requests;

import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Main {

    static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static Connection connection;

    static {
        try {
            new ReadFromProperties();
            connection = ConnectionManager.getConnection(ReadFromProperties.getURL(), ReadFromProperties.getUserName(), ReadFromProperties.getPASSWORD());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, SQLException {
        connection.setAutoCommit(false);
        Savepoint savepoint;


        out:
        while (true) {
            savepoint = connection.setSavepoint("savePoint");
            try {
                specify(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback(savepoint);
                e.printStackTrace();
            }
            System.out.println("Exit? 'y'");
            int x = System.in.read();
            char c = (char) x;
            if (c == 'y') break out;
        }
        connection.close();
    }

    private static void specify(Connection connection) throws SQLException {
        Requests requests = new Requests(connection);
        int answer = Specify.specifyInt("1: Create product | 2: Create order | 3: Update order | 4: Show information | 5: Remove product | 6: Remove all products");
        System.out.println();
        switch (answer) {
            case 1:
                requests.createProduct(buildProduct());
                break;
            case 2:
                requests.createOrder(buildOrder(), buildListItem());
                break;
            case 3:
                requests.showOrder();
                answer = Specify.specifyInt("1: Add item | 2: Update quantity");
                switch (answer) {
                    case 1:
                        List<OrderItem> items = buildListItem();
                        requests.showOrder();
                        int id = Specify.specifyInt("specify order id");
                        for (OrderItem e: items){
                            requests.createOrderItem(id, e);
                        }
                        break;
                    case 2:
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("select * from products");
                        while (resultSet.next()) {
                            System.out.printf("%-8s", resultSet.getMetaData().getColumnName(1) + ": " + resultSet.getInt("id"));
                            System.out.printf("%-20s", resultSet.getMetaData().getColumnName(2) + ": " + resultSet.getString("name"));
                            System.out.printf("%-15s", resultSet.getMetaData().getColumnName(3) + ": " + resultSet.getInt("price"));
                            System.out.printf("%-20s", resultSet.getMetaData().getColumnName(4) + ": " + resultSet.getString("status"));
                            System.out.printf("%-8s", resultSet.getMetaData().getColumnName(5) + ": " + resultSet.getDate("created_at"));
                            System.out.println();
                        }
                        requests.updateOrder(
                                Specify.specifyInt("specify order id"),
                                Specify.specifyInt("specify product id"),
                                Specify.specifyInt("specify quantity")
                        );
                        break;
                    default:
                        System.err.println("wrong number");
                }
                break;
            case 4:
                showInformation(requests);
                break;
            case 5:
                showProductsId(connection);
                requests.removeProduct(Specify.specifyInt("specify product id"));
                break;
            case 6:
                requests.removeAllProduct(Specify.specifyString("Enter password"));
                break;
            default:
                System.err.println("wrong number");
        }
    }

    private static void showInformation(Requests requests) throws SQLException {
        int answer = Specify.specifyInt("1: Show all products | 2: Show product history | 3: Show order | 4: Show all orders");

        switch (answer) {
            case 1:
                requests.showAllProduct();
                break;
            case 2:
                requests.showHistoryOrdered();
                break;
            case 3:
                requests.showOrder();
                requests.showOrder(Specify.specifyInt("specify order id"));
                break;
            case 4:
                requests.showOrder();
                break;
            default:
                System.err.println("wrong number");
        }
    }

    private static void showProductsId(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select id, name from products");
        while (resultSet.next()) {
            System.out.printf("%-10s", "| Id: " + resultSet.getInt("id"));
            System.out.printf("%-35s", "| Product Name: " + resultSet.getString("name"));
            System.out.println("|");
        }
    }

    private static Product buildProduct() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return new Product.Builder()
                .setName(Specify.specifyString("Specify the product name"))
                .setPrice(Specify.specifyInt("Specify the product price"))
                .setStatus(Specify.specifyEnum("Specify the status price |out_of_stock|in_stock|running_low|"))
                .setDate(dateFormat.format(Calendar.getInstance().getTime()))
                .build();
    }

    private static Order buildOrder() {
        return new Order.Builder()
                .setUserId((int) (Math.random() * 100000))
                .setStatus("processed")
                .setDate(dateFormat.format(Calendar.getInstance().getTime())).build();
    }

    private static List<OrderItem> buildListItem() throws SQLException {
        Statement statement = connection.createStatement();
        List<OrderItem> items = new ArrayList<>();
        while (true) {
            ResultSet resultSet = statement.executeQuery("select * from products");
            System.out.println("Add item to order");
            while (resultSet.next()) {
                System.out.printf("%-8s", resultSet.getMetaData().getColumnName(1) + ": " + resultSet.getInt("id"));
                System.out.printf("%-20s", resultSet.getMetaData().getColumnName(2) + ": " + resultSet.getString("name"));
                System.out.printf("%-15s", resultSet.getMetaData().getColumnName(3) + ": " + resultSet.getInt("price"));
                System.out.printf("%-20s", resultSet.getMetaData().getColumnName(4) + ": " + resultSet.getString("status"));
                System.out.printf("%-8s", resultSet.getMetaData().getColumnName(5) + ": " + resultSet.getDate("created_at"));
                System.out.println();
            }
            items.add(new OrderItem.Builder()
            .setProductId(Specify.specifyInt("Specify the product id"))
                    .setQuantity(Specify.specifyInt("Specify the quantity of item")).build());

            System.out.println("Need more item? 'Y' to continue");
            Scanner scanner = new Scanner(System.in);
            String answer = scanner.nextLine().toUpperCase();
            if (!answer.equals("Y")) break;
        }
        return items;
    }
}
