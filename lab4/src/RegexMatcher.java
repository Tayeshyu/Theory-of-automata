import java.util.Scanner;

public class RegexMatcher {
    // Разрешённый алфавит
    private static final String ALPHABET = "01";

    // Регулярные выражения
    private static final String REGEX1 = ".*01*";
    private static final String REGEX2 = "[01]01";
    private static final String REGEX3 = "00[01]*";

    // Проверка, что строка состоит только из 0 и 1
    public static boolean isValid(String input) {
        for (char c : input.toCharArray()) {
            if (ALPHABET.indexOf(c) == -1) {
                return false;
            }
        }
        return true;
    }

    // Метод проверки и вывода
    public static void checkRegex(String input) {
        System.out.println("Результаты проверки:");

        if (input.matches(REGEX1)) {
            System.out.println("Соответствует выражению 1: .*01*");
        } else {
            System.out.println("Не соответствует выражению 1: .*01*");
        }

        if (input.matches(REGEX2)) {
            System.out.println("Соответствует выражению 2: [01]01");
        } else {
            System.out.println("Не соответствует выражению 2: [01]01");
        }

        if (input.matches(REGEX3)) {
            System.out.println("Соответствует выражению 3: 00[01]*");
        } else {
            System.out.println("Не соответствует выражению 3: 00[01]*");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите строку из символов 0 и 1: ");
        String input = scanner.nextLine();

        if (!isValid(input)) {
            System.out.println("Ошибка: строка содержит недопустимые символы. Разрешены только 0 и 1.");
        } else {
            checkRegex(input);
        }

        scanner.close();
    }
}
