import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DaoTest {

    public static String DB_NAME = "testTest.db";
    public static int MAX_SIZE = 100;

    private void insert(String name, int price) {
        try {
            try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME)) {
                String sql = "INSERT INTO PRODUCT " +
                        "(NAME, PRICE) VALUES (\"" + name + "\"," + price + ")";
                Statement stmt = c.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int count() throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM PRODUCT");

            int res = 0;
            if (rs.next()) {
                res = rs.getInt(1);
            }

            rs.close();
            stmt.close();

            return res;
        }
    }

    private int max() throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1");

            int maxPrice = 0;
            if (rs.next()) {
                maxPrice = rs.getInt("price");
            }

            rs.close();
            stmt.close();

            return maxPrice;
        }
    }

    private int min() throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1");

            int minPrice = 0;
            if (rs.next()) {
                minPrice = rs.getInt("price");
            }

            rs.close();
            stmt.close();

            return minPrice;
        }
    }

    private int sum() throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SUM(price) FROM PRODUCT");

            int sum = 0;
            if (rs.next()) {
                sum = rs.getInt(1);
            }

            rs.close();
            stmt.close();

            return sum;
        }
    }

    private List<String> get() throws SQLException {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME)) {
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCT");

            List<String> res = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString("name");
                int price  = rs.getInt("price");
                res.add(name + ", " + price);
            }

            rs.close();
            stmt.close();

            return res;
        }
    }

    @Before
    public void clear() throws SQLException {
        try {
            Files.deleteIfExists(Paths.get(DB_NAME));
        } catch (IOException exeption) {
            exeption.printStackTrace();
        }

        try (Connection c = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME)) {
            String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                    "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " NAME           TEXT    NOT NULL, " +
                    " PRICE          INT     NOT NULL)";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        }
    }

    @Test
    public void testInsert() throws SQLException {
        for (int i = 1; i <= MAX_SIZE; i++) {
            insert("name" + i, i);
            assert count() == i;
        }
    }

    @Test
    public void testQuery() throws SQLException {
        for (int i = 1; i <= MAX_SIZE; i++) {
            insert("name" + i, i);
        }
        assert max() == MAX_SIZE;
        assert min() == 1;
        assert sum() == (MAX_SIZE + 1) * MAX_SIZE / 2;
    }

    @Test
    public void testGetProduct() throws SQLException {
        Set<String> products = new HashSet<>();
        for (int i = 1; i <= MAX_SIZE; i++) {
            insert("name" + i, i);
            products.add("name" + i + ", " + i);
        }
        List<String> productsFromDb = get();
        assert productsFromDb.size() == products.size();
        productsFromDb.forEach((product) -> {
            assert products.contains(product);
        });
    }
}