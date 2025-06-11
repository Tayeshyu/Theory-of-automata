package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class SampleProc {
    // === Глобальные переменные (процедурный стиль) ===
    private static boolean isAlarmOn = false;
    private static LocalTime currentTime = LocalTime.now().withNano(0);
    private static LocalTime alarmTime = LocalTime.of(0, 0, 0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML private Text textTime;
    @FXML private Text textInstruction;
    @FXML private Button btnH, btnM, btnA;
    @FXML private ImageView gif1;

    // === Инициализация (входная точка контроллера) ===
    @FXML
    public void initialize() {
        showInstructionDialog();
        updateDisplay();

        btnH.setOnAction(event -> onHourButton());
        btnM.setOnAction(event -> onMinuteButton());
        btnA.setOnAction(event -> onAlarmToggle());

        startClock();
    }

    // === Процедуры ===

    private void onHourButton() {
        alarmTime = alarmTime.plusHours(1);
        updateDisplay();
    }

    private void onMinuteButton() {
        alarmTime = alarmTime.plusMinutes(1);
        updateDisplay();
    }

    private void onAlarmToggle() {
        isAlarmOn = !isAlarmOn;
        textInstruction.setText(isAlarmOn ? "Будильник включен!" : "Установите время будильника");
        updateDisplay();
    }

    private void updateDisplay() {
        if (isAlarmOn) {
            gif1.setImage(new Image(getClass().getResourceAsStream("resources/gif1g.gif")));
            gif1.setVisible(true);
            btnH.setVisible(false);
            btnM.setVisible(false);
            textTime.setText("Текущее: " + currentTime.format(formatter) +
                    "\nБудильник: " + alarmTime.format(formatter));
        } else {
            gif1.setVisible(false);
            btnH.setVisible(true);
            btnM.setVisible(true);
            textTime.setText(alarmTime.format(formatter));
        }
    }

    private void startClock() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    currentTime = currentTime.plusSeconds(1);
                    updateDisplay();
                    if (isAlarmOn && currentTime.equals(alarmTime)) {
                        showAlert("Будильник сработал!", "Оно работает!");
                    }
                });
            }
        }, 0, 1000);
    }

    private void showInstructionDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Инструкция");
        alert.setHeaderText("Как пользоваться часами");
        alert.setContentText("""
                - Нажмите 'H' для увеличения часов.
                - Нажмите 'M' для увеличения минут.
                - Нажмите 'A' для включения будильника.
                - В режиме будильника кнопки 'H' и 'M' настраивают время срабатывания.
                - Повторное нажатие 'A' возвращает обычный режим.
                - Если будильник включен, он сработает при совпадении времени.""");
        alert.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}
