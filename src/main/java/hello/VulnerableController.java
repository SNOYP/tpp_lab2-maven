package hello;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.sql.Connection;
import java.util.Map;

@Controller
@RequestMapping("/vulnerable")
public class VulnerableController {

    private final DatabaseConnector connector;
    private final VulnerableCrudService service;

    public VulnerableController(DatabaseConnector connector, VulnerableCrudService service) {
        this.connector = connector;
        this.service = service;
    }

    @GetMapping("/")
    public String showForm(Map<String, Object> model) {
        model.put("result", "Введіть SQL-запит для демонстрації.");
        model.put("query", "");
        return "vulnerable_form";
    }

    @PostMapping(path = "/execute", produces = "text/html; charset=UTF-8")
    public String executeSql(@RequestParam String query, Map<String, Object> model) {
        Connection conn = connector.getConnection();
        String result;

        if (conn == null) {
            result = "Помилка: Не вдалося встановити з’єднання з БД. Перевірте консоль.";
        } else {
            try {
                result = service.executeVulnerableQuery(query, conn);
            } finally {
                DatabaseConnector.close(conn);
            }
        }

        model.put("result", result);
        model.put("query", query);
        return "vulnerable_form";
    }
}