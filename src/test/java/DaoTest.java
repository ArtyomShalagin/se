import org.junit.*;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.sql.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DaoTest {
    public static String DB_NAME = "testTest.db";
    public static int MAX_SIZE = 100;
    public Database db;

    @Before
    public void clear() {
        try {
            Files.deleteIfExists(Paths.get(DB_NAME));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        db = new Database(DB_NAME);
    }

    @Test
    public void testInsert() {
        for (int i = 1; i <= MAX_SIZE; i++) {
            db.insert(new Product("name" + i, i));
            assert db.count() == i;
        }
    }

    @Test
    public void testQuery() {
        for (int i = 1; i <= MAX_SIZE; i++) {
            db.insert(new Product("name" + i, i));
        }
        assert db.getProductPriceMax().getPrice() == MAX_SIZE;
        assert db.getProductPriceMin().getPrice() == 1;
        assert db.getProductPriceSum() == (MAX_SIZE + 1) * MAX_SIZE / 2;
    }

    @Test
    public void testGetProduct() {
        Set<Product> products = new HashSet<>();
        for (int i = 1; i <= MAX_SIZE; i++) {
            db.insert(new Product("name" + i, i));
            products.add(new Product("name" + i,  i));
        }
        List<Product> productsFromDb = db.getProducts();
        assert productsFromDb.size() == products.size();
        productsFromDb.forEach(product -> {
            assert products.contains(product);
        });
    }
}