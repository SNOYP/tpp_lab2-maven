package hello;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Відображає сторінку калькулятора (GET /)
    @GetMapping("/")
    public String showCalculator(Model model) {
        if (!model.containsAttribute("result")) {
            model.addAttribute("result", "Введіть вираз і натисніть 'Обчислити'");
        }
        return "index";
    }

    // Обробляє обчислення (POST /calculate)
    @PostMapping("/calculate")
    public String calculate(@RequestParam("expression") String expression, Model model) {
        String result;

        try {
            // Використання exp4j для обчислення
            Expression exp = new ExpressionBuilder(expression).build();
            double finalResult = exp.evaluate();

            // Форматування результату
            if (finalResult == (long) finalResult) {
                result = String.format("Результат: %d", (long) finalResult);
            } else {
                result = String.format("Результат: %.4f", finalResult);
            }

        } catch (Exception e) {
            result = "Помилка: Невірний або непідтримуваний вираз";
        }

        model.addAttribute("result", result);
        model.addAttribute("lastExpression", expression);

        return "index";
    }
}