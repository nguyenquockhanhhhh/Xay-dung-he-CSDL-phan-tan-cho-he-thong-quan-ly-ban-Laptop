package QuanLyLapTop.ConnectToSQLSever;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import javax.swing.JOptionPane;


public class ConnectToSQL {
    private static Connection conn = null;

    public static Connection connectToDatabase() {
        String serverName = "NGUYENQUOCKHANH\\SEVER";
        String userName = "sa";
        String password = "123";
        String database = "HeThongQuanLyBanLapTop";
        int port = 1433;

        String url = "jdbc:sqlserver://" + serverName + ":" + port + ";databaseName=" + database + ";user=" + userName + ";password=" + password + ";encrypt=false;";

        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Connected successfully : " + serverName);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

}
