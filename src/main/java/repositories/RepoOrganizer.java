package repositories;

import model.JdbcUtils;
import model.Organizer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepoOrganizer {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public RepoOrganizer(Properties props) {
        dbUtils = new JdbcUtils(props);
    }

    public void add(Organizer el) {
        logger.traceEntry("saving organiser {}", el);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = con.prepareStatement("insert into Organizer (Name,Password) values (?,?)")){

            preparedStatement.setString(1, el.getName());
            preparedStatement.setString(2,el.getPassword());
            int result = preparedStatement.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
    }

    public void delete(Organizer el) {
        logger.traceEntry("deleting organiser {}", el);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = con.prepareStatement("delete from Organizer where Id = ?")){

            preparedStatement.setInt(1, el.getId());
            int result = preparedStatement.executeUpdate();
            logger.trace("Removed {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
    }

    public void update(Organizer el, Integer id) {
        logger.traceEntry("updating organiser {}", el);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = con.prepareStatement("Update Organizer set Name = ?, Password = ? where Id = ?")){

            preparedStatement.setString(1, el.getName());
            preparedStatement.setString(2, el.getPassword());
            preparedStatement.setInt(3, el.getId());
            int result = preparedStatement.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
    }

    public Organizer findById(Integer id) {
        logger.traceEntry("Finding organiser {}", id);
        Connection con = dbUtils.getConnection();
        Organizer organiser = new Organizer();
        try(PreparedStatement preparedStatement = con.prepareStatement("select * from Organizer where Id = ?")){
            preparedStatement.setInt(1, id);
            try(ResultSet result = preparedStatement.executeQuery()) {

                while(result.next()){
                    int Id = result.getInt("Id");
                    String name = result.getString("Name");
                    String password = result.getString("Password");
                    organiser.setId(Id);
                    organiser.setName(name);
                    organiser.setPassword(password);
                    logger.trace("Found {} instances", organiser);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
        return organiser;
    }

    public Iterable<Organizer> findAll() {
        logger.traceEntry("find all organisers");
        Connection con = dbUtils.getConnection();
        List<Organizer> organisers = new ArrayList<>();
        try(PreparedStatement preparedStatement = con.prepareStatement("select * from Organizer")){
            try(ResultSet result = preparedStatement.executeQuery()){
                while(result.next()){
                    int id = result.getInt("Id");
                    String name = result.getString("Name");
                    String password = result.getString("Password");
                    Organizer organiser = new Organizer(id,name,password);
                    organisers.add(organiser);
                }
            }
        }
        catch (SQLException e) {
            logger.error(e);
            System.err.println("Error DB" + e);
        }
        logger.traceExit(organisers);
        return organisers;
    }

    public Organizer findOrganiserLogin(String name, String password) {
        logger.traceEntry("Finding by name {}", name);
        Connection con = dbUtils.getConnection();
        Organizer organiser = new Organizer();
        try(PreparedStatement preparedStatement = con.prepareStatement("select * from Organizer where Name = ? and Password = ?")){
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            try(ResultSet result = preparedStatement.executeQuery()) {
                while(result.next()){
                    Integer Id = result.getInt("Id");
                    String orgName = result.getString("Name");
                    String orgPass = result.getString("Password");
                    organiser.setId(Id);
                    organiser.setName(orgName);
                    organiser.setPassword(orgPass);
                    logger.trace("Found {} instances", organiser);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB" + ex);
        }
        logger.traceExit();
        return organiser;
    }
}
