import java.sql.*;

public class ConexionOracle {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "sys as sysdba";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void crearTablas(){

    }
}
