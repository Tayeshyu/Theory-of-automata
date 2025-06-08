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

public class Sample {
    private boolean isAlarmOn = false;
    LocalTime currentTime = LocalTime.now().withNano(0);
    private LocalTime alarmTime = LocalTime.of(0, 0, 0);

    @FXML
    private Text textTime;

    @FXML
    private Text textInstruction;

    @FXML
    private Button btnH, btnM, btnA;

    @FXML
    private ImageView gif1;

    @FXML
    void initialize() {
        showInstructionDialog();
        updateDisplay();

        btnH.setOnAction(event -> increaseHours());
        btnM.setOnAction(event -> increaseMinutes());
        btnA.setOnAction(event -> toggleAlarmMode());

        startClock();
    }

    private void increaseHours() {
        alarmTime = alarmTime.plusHours(1);
        DBLogger.log("SetAlarmTime", "Alarm set to " + alarmTime);
        updateDisplay();
    }

    private void increaseMinutes() {
        alarmTime = alarmTime.plusMinutes(1);
        DBLogger.log("SetAlarmTime", "Alarm set to " + alarmTime);
        updateDisplay();
    }

    private void toggleAlarmMode() {
        if (isAlarmOn) {
            textInstruction.setText("Установите время будильника");
        } else {
            textInstruction.setText("Будильник включен!");
        }
        isAlarmOn = !isAlarmOn;
        String mode = isAlarmOn ? "AlarmOn" : "AlarmOff";
        DBLogger.log("ModeChange", mode);
        updateDisplay();
    }

    private void updateDisplay() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        if (isAlarmOn) {
            gif1.setImage(new Image(getClass().getResourceAsStream("resources/gif1g.gif")));
            gif1.setVisible(true);
            btnM.setVisible(false);
            btnH.setVisible(false);
            textTime.setText("Текущее: " + currentTime.format(formatter) +
                             "\nБудильник: " + alarmTime.format(formatter));
        } else {
            textTime.setText(alarmTime.format(formatter));
            gif1.setVisible(false);
            btnM.setVisible(true);
            btnH.setVisible(true);
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
                    checkAlarm();
                });
            }
        }, 0, 1000);
    }

    private void checkAlarm() {
        if (isAlarmOn && currentTime.equals(alarmTime)) {
            showAlert("Будильник сработал!", "Оно работает!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
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
}
