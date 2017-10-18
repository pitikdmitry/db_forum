package db.forum.service;

import java.sql.*;
import java.util.*;

class DbConnection {

    static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5454/forum_user";
        String user = "forum_user";
        String password = "forum_user";

        return DriverManager.getConnection(url, user, password);
    }

    static void closeConnection(Connection con) throws SQLException {
            con.close();
    }

    private static void setParameters(CallableStatement proc, HashMap<Integer, Object> parameters) throws SQLException {
        Iterator it = parameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            proc.setObject((int)pair.getKey(), pair.getValue());

            System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove();
        }
    }

    private static void setOutParameters(CallableStatement proc, HashMap<Integer, Integer> outParameters) throws SQLException {
        Iterator it = outParameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            proc.registerOutParameter((int)pair.getKey(), (int)pair.getValue());
            System.out.println(pair.getKey() + " = " + pair.getValue());

            it.remove();
        }
    }

    static CallableStatement prepareCall(String functionWithParameters, Connection con, HashMap<Integer, Object> parameters,
                                         HashMap<Integer, Integer> outParameters) throws SQLException {

        CallableStatement proc = con.prepareCall("{ call " + functionWithParameters + " }");

        setOutParameters(proc, outParameters);
        setParameters(proc, parameters);

        proc.execute();
        return proc;
    }




















//    static void executeSqlFunction(String functionName, ArrayList<Node> parameters, ArrayList<Node> returnParameters) {
//
//        Connection con = null;
//        Statement stmt = null;
//        ResultSet results = null;
//
//        try {
//            con = DriverManager.getConnection(url, user, password);
//
//            CallableStatement proc = con.prepareCall(query);
////            CallableStatement proc = con.prepareCall("{ call add_user(?::citext, ?::citext, ?, ?, ?, ?, ?, ?, ?) }");
////            proc.registerOutParameter(1, Types.OTHER);
//            proc.registerOutParameter(5, Types.INTEGER);
//            proc.registerOutParameter(6, Types.VARCHAR);
//            proc.registerOutParameter(7, Types.VARCHAR);
//            proc.registerOutParameter(8, Types.VARCHAR);
//            proc.registerOutParameter(9, Types.VARCHAR);
////            for(int i = 0; i < arguments.length; i++) {
////                proc.setString(i + 1, arguments[i]);
////            }
//
//            proc.execute();
//            int res2 = proc.getInt(5);
//            String res = proc.getString(6);
//             res = proc.getString(7);
//             res = proc.getString(8);
//             res = proc.getString(9);
//
//            results =  (ResultSet) proc.getResultSet();
////            while (results.next())
////            {
//                System.out.println(results);
//                // do something with the results.
////            }
//            results.close();
//            proc.close();
//
//
//        } catch (SQLException ex) {
//            System.out.println("{DBCONNECTION] exception caught : " + ex);
//
//        } finally {
//            try {
//                if (results != null) {
//                    results.close();
//                }
//                if (stmt != null) {
//                    stmt.close();
//                }
//                if (con != null) {
//                    con.close();
//                }
//
//            } catch (SQLException ex) {
//
//            }
//        }
//    }
}
