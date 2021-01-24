package ru.akirakozov.sd.refactoring.servlet;

import ru.akirakozov.sd.refactoring.html.HtmlBuilder;
import ru.akirakozov.sd.refactoring.model.Product;
import ru.akirakozov.sd.refactoring.sql.Database;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");
        Database database = Database.getInstance();
        HtmlBuilder builder = new HtmlBuilder();

        if ("max".equals(command)) {
            Product maxProduct = database.getProductPriceMax();
            builder.addH1("Product with max price: ");
            builder.addLn(maxProduct.getName() + "\t" + maxProduct.getPrice());
        } else if ("min".equals(command)) {
            Product minProduct = database.getProductPriceMin();
            builder.addH1("Product with min price: ");
            builder.addLn(minProduct.getName() + "\t" + minProduct.getPrice());
        } else if ("sum".equals(command)) {
            long sum = database.getProductPriceSum();
            builder.add("Summary price: " + sum);
        } else if ("count".equals(command)) {
            int count = database.count();
            builder.add("Number of products: " + count);
        } else {
            builder.addLn("Unknown command: " + command);
        }

        response.getWriter().println(builder.buildHtml());
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
