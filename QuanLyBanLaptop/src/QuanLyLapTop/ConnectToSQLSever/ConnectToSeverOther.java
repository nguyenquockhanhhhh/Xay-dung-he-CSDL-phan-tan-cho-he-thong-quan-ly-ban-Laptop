package QuanLyLapTop.ConnectToSQLSever;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectToSeverOther {

    private static Connection conn = null;

   public static Connection connectToDatabaseOther(String serverName) {
        String userName = "sa";
        String password = "123";
        String database = "HeThongQuanLyBanLapTop";
        Connection conn = null;

        String url = "jdbc:sqlserver://" + serverName + ";databaseName=" + database + ";user=" + userName + ";password=" + password + ";encrypt=false;";

        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected successfully : " + serverName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }
}
