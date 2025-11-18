// src/main/java/hello/SecureRestController.java
package hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/secure-add") // Новий шлях для API-запитів
public class SecureRestController {

    private final SecureCrudService crudService;

    public SecureRestController(SecureCrudService crudService) {
        this.crudService = crudService;
    }

    private static class ApiResponse {
        public String status;
        public String message;

        public ApiResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }

    // -----------------------------------------------------------------
    // API ENDPOINT: Отримати групи за ID жанру (для фільтрації)
    // -----------------------------------------------------------------
    @GetMapping("/groups-by-genre")
    public ResponseEntity<Map<Integer, String>> getGroupsByGenre(@RequestParam Integer genreId) {
        try {
            Map<Integer, String> groups = crudService.getGroupsByGenre(genreId);
            return new ResponseEntity<>(groups, HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(new HashMap<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -----------------------------------------------------------------
    // API ENDPOINT: ДОДАВАННЯ ЖАНРУ (Genres)
    // -----------------------------------------------------------------
    @PostMapping("/add-genre")
    public ResponseEntity<ApiResponse> addGenre(@RequestParam String name) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);

            crudService.insert("genres", params);
            return new ResponseEntity<>(new ApiResponse("success", "✅ Жанр '" + name + "' успішно додано!"), HttpStatus.OK);

        } catch (SQLException e) {
            return new ResponseEntity<>(new ApiResponse("error", "❌ Помилка БД: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // -----------------------------------------------------------------
    // API ENDPOINT: ДОДАВАННЯ ГРУПИ (Groups)
    // -----------------------------------------------------------------
    @PostMapping("/add-group")
    public ResponseEntity<ApiResponse> addGroup(
            @RequestParam String name,
            @RequestParam Integer genre_id) {

        try {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("genre_id", String.valueOf(genre_id));

            crudService.insert("groups", params);
            return new ResponseEntity<>(new ApiResponse("success", "✅ Групу '" + name + "' успішно додано!"), HttpStatus.OK);

        } catch (SQLException e) {
            return new ResponseEntity<>(new ApiResponse("error", "❌ Помилка БД: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-song")
    public ResponseEntity<ApiResponse> addSong(
            @RequestParam String title,
            @RequestParam Integer group_id,
            @RequestParam(required = false) Integer duration_seconds) {

        try {
            Map<String, String> params = new HashMap<>();

            params.put("title", title);
            params.put("group_id", String.valueOf(group_id));
            if (duration_seconds != null && duration_seconds > 0) {
                params.put("duration_seconds", String.valueOf(duration_seconds));
            }

            crudService.insert("songs", params);
            return new ResponseEntity<>(new ApiResponse("success", "✅ Пісню '" + title + "' успішно додано!"), HttpStatus.OK);

        } catch (SQLException e) {
            return new ResponseEntity<>(new ApiResponse("error", "❌ Помилка БД: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}