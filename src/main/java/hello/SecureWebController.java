package hello;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;

@Controller
@RequestMapping("/secure-add")
public class SecureWebController {

    private final SecureCrudService crudService;

    public SecureWebController(SecureCrudService crudService) {
        this.crudService = crudService;
    }

    @GetMapping({"/", ""})
    public String showAddForms(Model model, @RequestParam(required = false) String message) {
        try {
            model.addAttribute("genresList", crudService.getAllGenres());
            model.addAttribute("groupsList", crudService.getAllGroups());

            model.addAttribute("currentGenres", crudService.readAllData("genres"));
            model.addAttribute("currentGroups", crudService.readAllData("groups"));
            model.addAttribute("currentSongs", crudService.readAllData("songs"));

            if (message == null) {
                model.addAttribute("message", "Ласкаво просимо! Додайте нові Жанри, Групи або Пісні.");
            } else {
                model.addAttribute("message", message);
            }
        } catch (SQLException e) {
            model.addAttribute("message", "❌ Помилка підключення до БД. Перевірте консоль: " + e.getMessage());
        }
        return "secure_add_form";
    }
}