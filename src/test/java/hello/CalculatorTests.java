package hello;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTests {

    // Тест, що проходить (Успішний тест): "1+1" повертає 2.0
    // Цей тест завжди проходить, якщо функціонал калькулятора працює.
    @Test
    void additionShouldPass() {
        String expressionString = "1+1";
        double expected = 2.0;

        Expression exp = new ExpressionBuilder(expressionString).build();
        double result = exp.evaluate();

        assertEquals(expected, result, 0.001, "Тест на додавання успішно пройшов.");
    }

    /*
     * ********************************
     * Тест, що провалюється (Неуспішний тест): "5-1" повертає 4.0, а ми очікуємо 2.0
     * * Цей тест повинен бути ЗАКОМЕНТОВАНИЙ для успішної збірки JAR-файлу
     * (фінальна вимога п. 1.2.6), але має бути розкоментований для демонстрації
     * провалу тестів у консолі.
     * *********************************
     */
    //@Test
    void subtractionShouldFail() {
        String expressionString = "5-1";
        double expected = 2.0; // Навмисно НЕПРАВИЛЬНЕ очікування (5-1 = 4.0)

        Expression exp = new ExpressionBuilder(expressionString).build();
        double result = exp.evaluate();

        // Цей метод ПРОВАЛИТЬСЯ, бо 4.0 не дорівнює 2.0
        assertEquals(expected, result, 0.001, "Тест на віднімання ПРОВАЛЕНО (Очікується 2.0, отримано 4.0).");
    }
}