package ru.geekbrains;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.geekbrains.persist.Product;
import ru.geekbrains.persist.ProductRepository;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "ProductControllerServlet", urlPatterns = {"/products/*"})
public class ProductControllerServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProductControllerServlet.class);

    private ProductRepository productRepository;

    @Override
    public void init() throws ServletException {
        productRepository = (ProductRepository) getServletContext().getAttribute("productRepository");
        if (productRepository == null) {
            throw new ServletException("Product repository not initialized");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("Index product page");

        if (req.getPathInfo()==null) {
            try {
                req.setAttribute("products", productRepository.findAll());
                req.setAttribute("activePage", "products");
                getServletContext().getRequestDispatcher("/WEB-INF/index.jsp").forward(req, resp);
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
        } else if (req.getPathInfo().equals("/new")) {
            req.setAttribute("product", new Product());
            getServletContext().getRequestDispatcher("/WEB-INF/product.jsp").forward(req, resp);
        } else if (req.getPathInfo().equals("/edit")) {
            String id = req.getParameter("id");
            try {
                Optional<Product> opt = productRepository.findById(Long.parseLong(id));
                if (opt.isPresent()) {
                    req.setAttribute("product", opt.get());
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
            getServletContext().getRequestDispatcher("/WEB-INF/product.jsp").forward(req, resp);
        } else if (req.getPathInfo().equals("/delete")) {
            String id = req.getParameter("id");
            try {
                Optional<Product> opt = productRepository.findById(Long.parseLong(id));
                if (opt.isPresent()) {
                    productRepository.delete(Long.parseLong(id));
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
            resp.sendRedirect(req.getContextPath()+req.getServletPath());
        }

        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo().equals("/upsert")) {
            try {
                String strId = req.getParameter("id");
                if (strId.isEmpty()) {
                    productRepository.insert(new Product(-1L,
                            req.getParameter("name"),
                            req.getParameter("description"),
                            new BigDecimal(req.getParameter("price"))));
                } else {

                    productRepository.update(new Product(Long.parseLong(strId),
                            req.getParameter("name"),
                            req.getParameter("description"),
                            new BigDecimal(req.getParameter("price"))
                            ));
                }
                resp.sendRedirect(getServletContext().getContextPath());
            } catch (SQLException ex) {
                throw new IllegalStateException(ex);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
