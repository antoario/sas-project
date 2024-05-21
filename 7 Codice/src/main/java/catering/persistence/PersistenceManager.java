package catering.persistence;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// import com.sun.javafx.binding.StringFormatter;

import java.sql.*;

public class PersistenceManager {
    private static String url = "jdbc:mysql://localhost:3306/catering";
    private static String username = "root";
    private static String password = "";

    private static int lastId;

    public static String escapeString(String input) {
        input = input.replace("\\", "\\\\");
        input = input.replace("\'", "\\\'");
        input = input.replace("\"", "\\\"");
        input = input.replace("\n", "\\n");
        input = input.replace("\t", "\\t");
        return input;
    }
    public static void testSQLConnection() {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Users");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("username");
                System.out.println(name + " ha id = " + id);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void executeQuery(String query, ResultHandler handler) {
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                handler.handle(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static int[] executeBatchUpdate(String parametrizedQuery, int itemNumber, BatchUpdateHandler handler) {
        int[] result = new int[0];
        try (
                Connection conn = DriverManager.getConnection(url, username, password);
                PreparedStatement ps = conn.prepareStatement(parametrizedQuery, Statement.RETURN_GENERATED_KEYS);
        ) {
            for (int i = 0; i < itemNumber; i++) {
                handler.handleBatchItem(ps, i);
                ps.addBatch();
            }
            result = ps.executeBatch();
            ResultSet keys = ps.getGeneratedKeys();
            int count = 0;
            while (keys.next()) {
                handler.handleGeneratedIds(keys, count);
                count++;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public static int executeUpdate(String update) {
        int result = 0;
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(update, Statement.RETURN_GENERATED_KEYS)) {
            result = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                lastId = rs.getInt(1);
            } else {
                lastId = 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static int getLastId() {
        return lastId;
    }

    public static void resetDb() {
        try (
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement statement = conn.createStatement();) {
    
          try {
            BufferedReader reader = new BufferedReader(new FileReader("dbreset.sql"));
            StringBuilder scriptContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
              scriptContent.append(line);
              scriptContent.append("\n");
            }
            reader.close();
    
            String[] queries = scriptContent.toString().split(";");
            for (String query : queries) {
                query = query.trim();
                if (query.isEmpty()) {
                    continue;
                }
                // statement.addBatch(query.trim().replace("\n", " "));
                try{
                    statement.execute(query);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            // statement.executeBatch();
          } catch (IOException e) {
            System.out.println("Cant't read file");
          }
    
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
      }
}
