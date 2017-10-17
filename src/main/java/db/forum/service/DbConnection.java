package db.forum.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;

public class DbConnection {
    private static String url = "jdbc:postgresql://localhost:5454/forum_user";
    private static String user = "forum_user";
    private static String password = "forum_user";

    class Node{
        String text;
        Integer position;
    }
    private <T,V> void addToArray(ArrayList<Node> arrayOfNodes, HashMap<T, V> tempHashMap) {
        Iterator<HashMap.Entry<T, V>> entries = tempHashMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<T, V> entry = entries.next();
            System.out.println("ID = " + entry.getKey() + " День недели = " + entry.getValue());
        }
    }

    private String createQuery(String functionName, HashMap<Integer, Integer> intHashMap,
                               HashMap<Integer, String> textHashMap,
                               HashMap<Integer, String> citextHashMap) {
        String query = "{ call ";
        query += functionName;
        query += "(";
        ArrayList<Node> arrayOfNodes = new ArrayList<Node>();
        addToArray(arrayOfNodes, intHashMap);

        }

    public static void executeSqlFunction(String functionName, HashMap<Integer, Integer> intHashMap,
                                          HashMap<Integer, String> textHashMap,
                                          HashMap<Integer, String> citextHashMap) {

        Connection con = null;
        Statement stmt = null;
        ResultSet results = null;

        try {
            con = DriverManager.getConnection(url, user, password);


            CallableStatement proc = con.prepareCall("{ call add_user(?::citext, ?::citext, ?, ?, ?, ?, ?, ?, ?) }");
//            proc.registerOutParameter(1, Types.OTHER);
            proc.registerOutParameter(5, Types.INTEGER);
            proc.registerOutParameter(6, Types.VARCHAR);
            proc.registerOutParameter(7, Types.VARCHAR);
            proc.registerOutParameter(8, Types.VARCHAR);
            proc.registerOutParameter(9, Types.VARCHAR);
//            for(int i = 0; i < arguments.length; i++) {
//                proc.setString(i + 1, arguments[i]);
//            }

            proc.execute();
            int res2 = proc.getInt(5);
            String res = proc.getString(6);
             res = proc.getString(7);
             res = proc.getString(8);
             res = proc.getString(9);

            results =  (ResultSet) proc.getResultSet();
//            while (results.next())
//            {
                System.out.println(results);
                // do something with the results.
//            }
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
