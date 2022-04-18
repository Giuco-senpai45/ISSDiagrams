package repositories;

import model.JdbcUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class RepoOrder {
    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger();

    public RepoOrder(Properties props) {
        dbUtils = new JdbcUtils(props);
    }
}
