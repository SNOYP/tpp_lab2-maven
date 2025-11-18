package hello;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.springframework.stereotype.Service;

@Service
public class VulnerableCrudService {

    /**
     * Виконує SQL-запит, отриманий напряму від користувача.
     * !!! ЦЕ УРАЗЛИВА РЕАЛІЗАЦІЯ ДЛЯ ДЕМОНСТРАЦІЇ SQL INJECTION.
     */
    public String executeVulnerableQuery(String userSql, Connection conn) {
        Statement stmt = null;
        StringBuilder result = new StringBuilder();

        try {
            stmt = conn.createStatement();
            boolean isResultSet = stmt.execute(userSql);

            if (isResultSet) {
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                result.append("<h3>✅ SELECT Успішно:</h3><pre>");

                for (int i = 1; i <= columnCount; i++) {
                    result.append(String.format("%-25s", metaData.getColumnName(i)));
                }
                result.append("\n------------------------------------------------------\n");

                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        result.append(String.format("%-25s", rs.getString(i)));
                    }
                    result.append("\n");
                }
                result.append("</pre>");
                rs.close();
            } else {
                int updateCount = stmt.getUpdateCount();
                result.append("✅ Запит виконано успішно. Змінено/створено записів: ").append(updateCount);
            }
        } catch (SQLException e) {
            result.append("❌ Помилка SQL: ").append(e.getMessage());
        } finally {
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) {}
            }
        }
        return result.toString();
    }
}