package ru.akirakozov.sd.refactoring.sql;

import ru.akirakozov.sd.refactoring.model.Product;

import java.util.*;
import java.util.function.*;
import java.sql.*;

public class Database {
    private static String DATABASE_CREATION_SQL = "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";

    private static String GET_PRODUCTS_SQL  = "SELECT * FROM PRODUCT";
    private static String GET_SUM_SQL = "SELECT SUM(price) FROM PRODUCT";
    private static String GET_MAX_SQL = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
    private static String GET_MIN_SQL = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
    private static String GET_COUNT_SQL = "SELECT COUNT(*) FROM PRODUCT";
    private String databaseName;

    public static Database instance;

    public Database(String dbName) {
        databaseName = "jdbc:sqlite:" + dbName;
        try (Connection c = DriverManager.getConnection(databaseName)) {

            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(DATABASE_CREATION_SQL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database("test.db");  // TODO move name to config
        }
        return instance;
    }

    private <R> R doRequest(String request, Function<ResultSet, R> f) {
        try (Connection c = DriverManager.getConnection(databaseName);
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(request)) {

            return f.apply(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Product> readProducts(ResultSet rs) {
        try {
            List<Product> products = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                long price = rs.getLong("price");
                Product product = new Product(name, price);

                products.add(product);
            }

            return products;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Product> getProducts() {
        return doRequest(GET_PRODUCTS_SQL, this::readProducts);
    }

    private Long sumProducts(ResultSet rs) {
        try {
            if (rs.next()) {
                return rs.getLong(1);
            } else {
                throw new RuntimeException("next fail");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Long getProductPriceSum() {
        return doRequest(GET_SUM_SQL, this::sumProducts);
    }

    private Product takeFirstProduct(ResultSet rs) {
        try {
            if (rs.next()) {
                String name = rs.getString("name");
                long price = rs.getLong("price");

                return new Product(name, price);
            } else {
                throw new RuntimeException("first product");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Product getProductPriceMax() {
        return doRequest(GET_MAX_SQL, this::takeFirstProduct);
    }

    public Product getProductPriceMin() {
        return doRequest(GET_MIN_SQL, this::takeFirstProduct);
    }

    private int countProducts(ResultSet rs) {
        try {
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int count() {
        return doRequest(GET_COUNT_SQL, this::countProducts);
    }

    public void insert(Product product) {
        try (Connection c = DriverManager.getConnection(databaseName);
             Statement stmt = c.createStatement()) {

            String sql = "INSERT INTO PRODUCT " +
                    "(NAME, PRICE) VALUES " +
                    "(\"" + product.getName() + "\", " + product.getPrice() + ")";
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
