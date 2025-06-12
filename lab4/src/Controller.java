import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Controller {
    @FXML private TextField inputField;
    @FXML private ComboBox<String> taskCombo;
    @FXML private Button checkButton;
    @FXML private Label resultLabel;

    private final Map<String, String> patterns = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        patterns.put("1.1: хотя бы один 'a' и один 'b' из {a,b,c}", "^(?=.*a)(?=.*b)[abc]+$");
        patterns.put("1.2: десятый от правого — '1'", "^[01]*1[01]{9}$");
        patterns.put("1.3: не более одной пары '11'", "^(?!.*11.*11)[01]*$");
        patterns.put("2a: каждая '00' перед '11'", "^(?=.*00.*11)[01]*$");
        patterns.put("2b: число '0' кратно 5", "^(?:[^0]*0{5})*[^0]*$");
        patterns.put("3.1: нет '101'", "^(?!.*101)[01]*$");
        patterns.put("3.2: равное число 0 и 1 с ограничением префиксов", "^((01|10|0011|1100)*)$");
        patterns.put("3.3: '0' %5=0 и '1' чётно", "^(?=(?:[^0]*0){5}[^0]*$)(?=(?:[^1]*1){2}[^1]*$)[01]*$");
        patterns.put("4: телефон (международные/локальные)",
                "^(?:\\+?[0-9]{1,3}(?:[ -]?[0-9]){6,10}|[0-9]{6,7})$");
        patterns.put("5: зарплата (разные периоды)",
                "(?i)^(?:зп|зарплата|оплата)?\\s*-?\\s*(?:от|до)?\\s*[0-9]{1,3}(?:[ ,.]?[0-9]{3})*(?:[.,][0-9]{2})?(?:\\s*(?:руб(?:лей)?|\\$|usd|eur))?(?:\\s*(?:час|недел(?:я|ю)|месяц|год))?$");

        taskCombo.getItems().addAll(patterns.keySet());
        taskCombo.getSelectionModel().selectFirst();
        checkButton.setOnAction(e -> handleCheck());
    }

    @FXML
    private void handleCheck() {
        String task = taskCombo.getValue();
        String input = inputField.getText().trim();

        if (input.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Поле ввода не может быть пустым.");
            return;
        }
        
        if (task.startsWith("1.1") && !input.matches("^[abc]+$")) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Для этого задания допустимы только символы a, b, c.");
            return;
        }
        if ((task.startsWith("1.2") || task.startsWith("1.3") || task.startsWith("2") || task.startsWith("3"))
                && !input.matches("^[01]+$")) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Для этого задания допустимы только символы 0 и 1.");
            return;
        }

        String regex = patterns.get(task);
        if (regex == null) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Регулярное выражение не задано.");
            return;
        }

        boolean matches;
        try {
            matches = Pattern.matches(regex, input);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Ошибка", "Невозможно проверить: " + ex.getMessage());
            return;
        }

        resultLabel.setText(matches ? "Соответствует" : "Не соответствует");
        resultLabel.setStyle(matches ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}