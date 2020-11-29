package chat.demo;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.io.IOException;
import java.sql.SQLException;

public class  DaoConnexion {

    private JdbcConnectionSource connectionSource;

//Creer la connection
    public JdbcConnectionSource createConnection() throws SQLException {

        this.connectionSource = new JdbcConnectionSource( Constants.URL, Constants.LOGIN, Constants.PASSWORD);

        return connectionSource;
    }

//fermer la connection
    public void closeConnection() throws IOException {
        connectionSource.close();
    }


}
