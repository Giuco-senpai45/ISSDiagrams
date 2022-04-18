package repositories;

import model.JdbcUtils;
import model.Organizer;
import model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepoProduct {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public RepoProduct(Properties props) {
        dbUtils = new JdbcUtils(props);
    }

    public void add(Product el) {
        logger.traceEntry("saving product {}", el);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = con.prepareStatement("insert into Product (Name,Details,Price,Quantity) values (?,?,?,?)")){

            preparedStatement.setString(1, el.getName());
            preparedStatement.setString(2,el.getDetails());
            preparedStatement.setDouble(3,el.getPrice());
            preparedStatement.setInt(4,el.getQuantity());
            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
    }

    public void delete(Product el) {
        logger.traceEntry("deleting product {}", el);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = con.prepareStatement("delete from Product where Id = ?")){

            preparedStatement.setInt(1, el.getId());
            int result = preparedStatement.executeUpdate();
            logger.trace("Removed {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
    }

    public void update(Product el, Integer id) {
        logger.traceEntry("updating product {}", el);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = con.prepareStatement("Update Product set Name = ?, Details = ?, Price = ?, Quantity = ? where Id = ?")){
            preparedStatement.setString(1, el.getName());
            preparedStatement.setString(2, el.getDetails());
            preparedStatement.setDouble(3, el.getPrice());
            preparedStatement.setInt(4, el.getQuantity());
            preparedStatement.setInt(5, el.getId());
            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
    }

    public Product findById(Integer id) {
        logger.traceEntry("Finding product {}", id);
        Connection con = dbUtils.getConnection();
        Product product = new Product();
        try(PreparedStatement preparedStatement = con.prepareStatement("select * from Product where Id = ?")){
            preparedStatement.setInt(1, id);
            try(ResultSet result = preparedStatement.executeQuery()) {

                while(result.next()){
                    int Id = result.getInt("Id");
                    String name = result.getString("Name");
                    String details = result.getString("Details");
                    Double price = result.getDouble("Price");
                    Integer quantity = result.getInt("Quantity");
                    product.setId(Id);
                    product.setName(name);
                    product.setDetails(details);
                    product.setPrice(price);
                    product.setQuantity(quantity);
                    logger.trace("Found {} instances", product);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
        return product;
    }

    public Iterable<Product> findAll() {
        logger.traceEntry("find all products");
        Connection con = dbUtils.getConnection();
        List<Product> products = new ArrayList<>();
        try(PreparedStatement preparedStatement = con.prepareStatement("select * from Product")){
            try(ResultSet result = preparedStatement.executeQuery()){
                while(result.next()){
                    int Id = result.getInt("Id");
                    String name = result.getString("Name");
                    String details = result.getString("Details");
                    Double price = result.getDouble("Price");
                    Integer quantity = result.getInt("Quantity");

                    Product product = new Product();
                    product.setId(Id);
                    product.setName(name);
                    product.setDetails(details);
                    product.setPrice(price);
                    product.setQuantity(quantity);
                    products.add(product);
                }
            }
        }
        catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        logger.traceExit(products);
        return products;
    }

    public Product findByName(String name) {
        logger.traceEntry("Finding product by name {}", name);
        Connection con = dbUtils.getConnection();
        Product product = new Product();
        try(PreparedStatement preparedStatement = con.prepareStatement("select * from Product where Name = ?")){
            preparedStatement.setString(1, name);
            try(ResultSet result = preparedStatement.executeQuery()) {

                while(result.next()){
                    int Id = result.getInt("Id");
                    String namep = result.getString("Name");
                    String details = result.getString("Details");
                    Double price = result.getDouble("Price");
                    Integer quantity = result.getInt("Quantity");
                    product.setId(Id);
                    product.setName(namep);
                    product.setDetails(details);
                    product.setPrice(price);
                    product.setQuantity(quantity);
                    logger.trace("Found {} instances", product);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
        return product;
    }
}
