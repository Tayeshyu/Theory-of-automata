import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ElevatorAppController {
    @FXML private ImageView backgroundImage;
    @FXML private Label userFloorLabel;
    @FXML private Label labelA;
    @FXML private ImageView viewA;
    @FXML private Label labelB;
    @FXML private ImageView viewB;
    @FXML private Button upButton;
    @FXML private Button downButton;
    @FXML private CheckBox autoModeCheckBox;

    private Elevator elevatorA;
    private Elevator elevatorB;
    private ElevatorSystem elevatorSystem;

    private Image defaultBackground;
    private Image leftOpenBackground;
    private Image rightOpenBackground;
    private Image upArrow;
    private Image downArrow;

    @FXML
    public void initialize() {
        // Загрузка изображений (в папке resources)
        defaultBackground = loadImage("resources/noOpen.png");
        leftOpenBackground = loadImage("resources/leftOpen.png");
        rightOpenBackground = loadImage("resources/rightOpen.png");
        upArrow = loadImage("resources/upBtn.png");
        downArrow = loadImage("resources/downBtn.png");

        // Установим начальный фон и иконки на кнопки
        if (backgroundImage != null && defaultBackground != null) {
            backgroundImage.setImage(defaultBackground);
        }
        if (upButton != null && upArrow != null) {
            upButton.setGraphic(new ImageView(upArrow));
        }
        if (downButton != null && downArrow != null) {
            downButton.setGraphic(new ImageView(downArrow));
        }

        // Создание лифтов (начальный этаж 1)
        elevatorA = new Elevator(1, labelA, viewA);
        elevatorB = new Elevator(1, labelB, viewB);

        // Установка начальных этажей на UI
        elevatorA.updateFloorLabel();
        elevatorB.updateFloorLabel();

        // Создание системы управления лифтами
        elevatorSystem = new ElevatorSystem(elevatorA, elevatorB, userFloorLabel);

        // Передача элементов UI в систему
        elevatorSystem.setBackgroundImage(backgroundImage);
        elevatorSystem.setDefaultBackground(defaultBackground);
        elevatorSystem.setLeftOpenBackground(leftOpenBackground);
        elevatorSystem.setRightOpenBackground(rightOpenBackground);

        // Установка текста с текущим этажом пользователя
        if (userFloorLabel != null) {
            userFloorLabel.setText("Пользователь находится на этаже: 1");
        }

        // Действия кнопок вызова лифтов
        if (upButton != null) {
            upButton.setOnAction(e -> elevatorSystem.handleCall(true));
        }
        if (downButton != null) {
            downButton.setOnAction(e -> elevatorSystem.handleCall(false));
        }

        // Переключатель автоматического режима
        if (autoModeCheckBox != null) {
            autoModeCheckBox.setOnAction(e -> {
                if (autoModeCheckBox.isSelected()) {
                    elevatorSystem.startAutoMovement();
                } else {
                    elevatorSystem.stopAutoMovement();
                }
            });
        }
    }

    // Вспомогательный метод загрузки изображения из ресурсов
    private Image loadImage(String fileName) {
        try {
            return new Image(getClass().getResource("/" + fileName).toExternalForm());
        } catch (Exception e) {
            System.err.println("Не удалось загрузить изображение: " + fileName);
            return null;
        }
    }
}
