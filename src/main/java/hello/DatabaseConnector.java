package hello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnector {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user;
    @Value("${spring.datasource.password}")
    private String password;

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("❌ Помилка підключення до бази даних: " + url);
            System.err.println("Перевірте, чи працює Docker-контейнер на вказаному порту!");
            System.err.println("Переконайтесь, що використовуєте коректні логін/пароль.");
            return null;
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Помилка закриття з'єднання: " + e.getMessage());
            }
        }
    }
}