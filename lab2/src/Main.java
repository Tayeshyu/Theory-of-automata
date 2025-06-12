import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    private static volatile LocalTime currentTime = LocalTime.now().withNano(0);
    private static volatile LocalTime alarmTime = LocalTime.of(0, 0, 0);
    private static volatile boolean isAlarmOn = false;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        System.out.println("=== Консольный будильник ===");
        System.out.println("Доступные команды:");
        System.out.println("  h - прибавить час");
        System.out.println("  m - прибавить минуту");
        System.out.println("  a - включить/выключить будильник");
        System.out.println("  q - выход");

        startClock();
        startCommandListener();
    }

    private static void startClock() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                currentTime = currentTime.plusSeconds(1);
                System.out.print("\rТекущее время: " + currentTime.format(formatter));
                checkAlarm();
            }
        }, 0, 1000);
    }

    private static void startCommandListener() {
        // Поток для прослушивания команд пользователя
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\n\nВведите команду: ");
                String input = scanner.nextLine().trim().toLowerCase();

                switch (input) {
                    case "h":
                        alarmTime = alarmTime.plusHours(1);
                        System.out.println("Будильник: " + alarmTime.format(formatter));
                        break;
                    case "m":
                        alarmTime = alarmTime.plusMinutes(1);
                        System.out.println("Будильник: " + alarmTime.format(formatter));
                        break;
                    case "a":
                        isAlarmOn = !isAlarmOn;
                        System.out.println("Будильник " + (isAlarmOn ? "включен" : "выключен"));
                        break;
                    case "q":
                        System.out.println("Выход...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Неизвестная команда.");
                }
            }
        });

        inputThread.setDaemon(false);
        inputThread.start();
    }

    private static void checkAlarm() {
        if (isAlarmOn && currentTime.equals(alarmTime)) {
            System.out.println("\n🔔🔔🔔 Будильник сработал! 🔔🔔🔔");
            isAlarmOn = false;
        }
    }
}
