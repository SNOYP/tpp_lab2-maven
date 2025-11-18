package hello;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.sql.SQLException;
import java.util.Scanner;
import hello.CommandParser.ParsedCommand;

@Component
public class SecureCliRunner implements CommandLineRunner {

    private final SecureCrudService crudService;

    public SecureCliRunner(SecureCrudService crudService) {
        this.crudService = crudService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.setProperty("spring.boot.running.in.cli", "true");

        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=======================================================");
        System.out.println("Консольне CLI (з захистом PreparedStatement) [Пункт 1.3.3]");
        System.out.println("=======================================================");
        System.out.println("Формат: [command] [table] (key='value', ...)");
        System.out.println("--- Схема: genres, groups, songs (duration_seconds) ---");
        System.out.println("Приклад INSERT: insert songs (title='Bohemian Rhapsody', group_id='1', duration_seconds='354')");
        System.out.println("Приклад READ: read groups (id='1') або read songs ()");
        System.out.println("Приклад UPDATE: update groups (name='Queen', genre_id='1', id='1')");
        System.out.println("Приклад DELETE: delete genres (id='3')");
        System.out.println("Введіть 'exit' для завершення.");

        try {
            while (true) {
                System.out.print("CLI > ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) break;

                ParsedCommand command = CommandParser.parse(input);
                if (command == null) {
                    System.err.println("Невірний формат команди. Спробуйте ще раз.");
                    continue;
                }

                try {
                    switch (command.command) {
                        case "insert":
                            crudService.insert(command.table, command.params);
                            break;
                        case "delete":
                            crudService.delete(command.table, command.params);
                            break;
                        case "update":
                            crudService.update(command.table, command.params);
                            break;
                        case "read":
                            crudService.read(command.table, command.params);
                            break;
                        default:
                            System.err.println("Невідома команда: " + command.command);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("❌ Помилка в параметрах: " + e.getMessage());
                } catch (SQLException e) {
                    System.err.println("❌ Помилка БД: " + e.getMessage());
                }
            }
        } finally {
            System.out.println("Застосунок завершено.");
            scanner.close();
        }
    }
}