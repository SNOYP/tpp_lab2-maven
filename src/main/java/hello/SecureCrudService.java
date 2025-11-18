package hello;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class SecureCrudService {

    private final DatabaseConnector connector;

    public SecureCrudService(DatabaseConnector connector) {
        this.connector = connector;
    }

    public void insert(String table, Map<String, String> params) throws SQLException {
        Connection conn = connector.getConnection();
        if (conn == null) throw new SQLException("Немає підключення до БД.");

        Set<String> keys = params.keySet();
        String columns = String.join(", ", keys);
        String placeholders = String.join(", ", java.util.Collections.nCopies(keys.size(), "?"));
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columns, placeholders);

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            int index = 1;
            for (String key : keys) {
                String value = params.get(key);
                try {
                    pstmt.setInt(index, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    pstmt.setString(index, value);
                }
                index++;
            }

            int rowsAffected = pstmt.executeUpdate();
            if (System.getProperty("spring.boot.running.in.cli") != null) {
                System.out.printf("✅ Успішно: Додано %d записів у таблицю %s.\n", rowsAffected, table);
            }

        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.close(conn);
        }
    }

    public void delete(String table, Map<String, String> params) throws SQLException {
        Connection conn = connector.getConnection();
        if (conn == null) throw new SQLException("Немає підключення до БД.");
        if (!params.containsKey("id")) throw new IllegalArgumentException("Для DELETE потрібен id.");

        String sql = String.format("DELETE FROM %s WHERE id = ?", table);

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            String idValue = params.get("id");
            pstmt.setInt(1, Integer.parseInt(idValue));

            int rowsAffected = pstmt.executeUpdate();
            if (System.getProperty("spring.boot.running.in.cli") != null) {
                System.out.printf("✅ Успішно: Видалено %d записів із таблиці %s.\n", rowsAffected, table);
            }
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.close(conn);
        }
    }

    public void update(String table, Map<String, String> params) throws SQLException {
        Connection conn = connector.getConnection();
        if (conn == null) throw new SQLException("Немає підключення до БД.");
        if (!params.containsKey("id")) throw new IllegalArgumentException("Для UPDATE потрібен id.");

        Set<String> keys = params.keySet();
        keys.remove("id");
        if (keys.isEmpty()) throw new IllegalArgumentException("Немає полів для оновлення.");

        StringBuilder setClauses = new StringBuilder();
        for (String key : keys) {
            setClauses.append(key).append(" = ?, ");
        }
        setClauses.setLength(setClauses.length() - 2);

        String sql = String.format("UPDATE %s SET %s WHERE id = ?", table, setClauses);

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);

            int index = 1;
            for (String key : keys) {
                String value = params.get(key);
                try {
                    pstmt.setInt(index, Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    pstmt.setString(index, value);
                }
                index++;
            }

            String idValue = params.get("id");
            pstmt.setInt(index, Integer.parseInt(idValue));

            int rowsAffected = pstmt.executeUpdate();
            if (System.getProperty("spring.boot.running.in.cli") != null) {
                System.out.printf("✅ Успішно: Оновлено %d записів у таблиці %s.\n", rowsAffected, table);
            }
        } finally {
            if (pstmt != null) pstmt.close();
            DatabaseConnector.close(conn);
        }
    }

    public void read(String table, Map<String, String> params) throws SQLException {
        Connection conn = connector.getConnection();
        if (conn == null) throw new SQLException("Немає підключення до БД.");

        String sql;
        boolean hasCondition = params.containsKey("id");

        if (hasCondition) {
            sql = String.format("SELECT * FROM %s WHERE id = ?", table);
        } else {
            sql = String.format("SELECT * FROM %s", table);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);

            if (hasCondition) {
                String idValue = params.get("id");
                pstmt.setInt(1, Integer.parseInt(idValue));
            }

            rs = pstmt.executeQuery();
            printResultSet(rs, table);

        } finally {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            DatabaseConnector.close(conn);
        }
    }

    private void printResultSet(ResultSet rs, String table) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        System.out.printf("\n--- Результати запиту для таблиці %s ---\n", table);

        for (int i = 1; i <= columnCount; i++) {
            System.out.printf("%-20s", metaData.getColumnName(i));
        }
        System.out.println("\n---------------------------------------------------------");

        int count = 0;
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-20s", rs.getString(i));
            }
            System.out.println();
            count++;
        }
        System.out.printf("--- Виведено записів: %d ---\n", count);
    }

    public Map<Integer, String> getAllGenres() throws SQLException {
        Map<Integer, String> genres = new HashMap<>();
        Connection conn = connector.getConnection();
        if (conn == null) return genres;

        try (PreparedStatement pstmt = conn.prepareStatement("SELECT id, name FROM genres ORDER BY name")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    genres.put(rs.getInt("id"), rs.getString("name"));
                }
            }
        } finally {
            DatabaseConnector.close(conn);
        }
        return genres;
    }

    public Map<Integer, String> getAllGroups() throws SQLException {
        Map<Integer, String> groups = new HashMap<>();
        Connection conn = connector.getConnection();
        if (conn == null) return groups;

        try (PreparedStatement pstmt = conn.prepareStatement("SELECT id, name FROM groups ORDER BY name")) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    groups.put(rs.getInt("id"), rs.getString("name"));
                }
            }
        } finally {
            DatabaseConnector.close(conn);
        }
        return groups;
    }

    public Map<Integer, String> getGroupsByGenre(int genreId) throws SQLException {
        Map<Integer, String> groups = new HashMap<>();
        Connection conn = connector.getConnection();
        if (conn == null) return groups;

        String sql = "SELECT id, name FROM groups WHERE genre_id = ? ORDER BY name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, genreId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    groups.put(rs.getInt("id"), rs.getString("name"));
                }
            }
        } finally {
            DatabaseConnector.close(conn);
        }
        return groups;
    }

    public List<Map<String, Object>> readAllData(String table) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        Connection conn = connector.getConnection();
        if (conn == null) return results;

        String sql = String.format("SELECT * FROM %s ORDER BY id", table);

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    row.put(columnName, rs.getString(i));
                }
                results.add(row);
            }
        } finally {
            DatabaseConnector.close(conn);
        }
        return results;
    }
}