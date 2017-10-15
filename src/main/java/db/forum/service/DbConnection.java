package db.forum.service;

import java.sql.*;
import org.slf4j.Logger;

public class DbConnection {
    private static String url = "jdbc:postgresql://localhost:5432/forum";
    private static String user = "forum_admin";
    private static String password = "1704";

    public static void executeSqlFunction(String function, String ... arguments) {

        Connection con = null;
        Statement stmt = null;
        ResultSet results = null;

        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            stmt.execute(function);
            stmt.close();

            con.setAutoCommit(false);

            CallableStatement proc = con.prepareCall("{ call createUser(?, ?, ?, ?) }");
//            proc.registerOutParameter(1, Types.OTHER);

            for(int i = 0; i < arguments.length; i++) {
                proc.setString(i + 1, arguments[i]);
            }

            proc.execute();

            results = (ResultSet) proc.getObject(1);
            while (results.next())
            {
                // do something with the results.
            }
            results.close();
            proc.close();


        } catch (SQLException ex) {
            System.out.println("{DBCONNECTION] exception caught : " + ex);

        } finally {
            try {
                if (results != null) {
                    results.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {

            }
        }
    }
}
