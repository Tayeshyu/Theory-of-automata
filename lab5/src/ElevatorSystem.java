import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import javax.swing.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ElevatorSystem {
    private Elevator elevatorA;
    private Elevator elevatorB;
    private boolean inProcess = false;
    private boolean isBetween = false;
    private int userFloor;
    private static final Logger logger = Logger.getLogger(ElevatorSystem.class.getName());

    private ImageView backgroundImage;
    private Image defaultBackground;
    private Image leftOpenBackground;
    private Image rightOpenBackground;
    private Random random = new Random();
    private Timeline autoMovementTimeline;
    private int userDestination;
    private boolean userInside;
    private Elevator elevator;

    private Label userFloorLabel;

    public ElevatorSystem(Elevator a, Elevator b, Label userFloorLabel) {
        this.elevatorA = a;
        this.elevatorB = b;
        this.userFloorLabel = userFloorLabel;
        this.userFloor = userFloor;
        this.userInside = false;
        this.userDestination = -1;
        updateUserFloorLabel();
        // Случайное задание этажей
        this.elevatorA.currentFloor = random.nextInt(99) + 1;
        this.elevatorB.currentFloor = random.nextInt(99) + 1;

        Platform.runLater(this::askUserFloor);
        logger.info("Лифты инициализированы: A=" + elevatorA.currentFloor + ", B=" + elevatorB.currentFloor);
    }

    // Сеттер фонового ImageView
    public void setBackgroundImage(ImageView background) {
        this.backgroundImage = background;
    }

    // Сеттеры изображений для состояний дверей
    public void setDefaultBackground(Image img) {
        this.defaultBackground = img;
    }
    public void setLeftOpenBackground(Image img) {
        this.leftOpenBackground = img;
    }
    public void setRightOpenBackground(Image img) {
        this.rightOpenBackground = img;
    }

    // Методы смены фона в зависимости от состояния дверей
    public void openLeftDoor() {
        if (backgroundImage != null && leftOpenBackground != null) {
            backgroundImage.setImage(leftOpenBackground);
        }
    }
    public void openRightDoor() {
        if (backgroundImage != null && rightOpenBackground != null) {
            backgroundImage.setImage(rightOpenBackground);
        }
    }
    public void resetDoors() {
        if (backgroundImage != null && defaultBackground != null) {
            backgroundImage.setImage(defaultBackground);
        }
    }

    private void askUserFloor() {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Выбор этажа");
        dialog.setHeaderText("Введите этаж, с которого начнете (1-99):");
        dialog.setContentText("Этаж:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int floor = Integer.parseInt(input.trim());
                userFloor = Math.max(1, Math.min(99, floor));
            } catch (NumberFormatException e) {
                userFloor = 1;  // по умолчанию
            }
        });

        // После выбора этажа обновляем счетчик пользователя
        updateUserFloorLabel();
        logger.info("Пользователь выбрал этаж: " + userFloor);
    }

    private void updateUserFloorLabel() {
        userFloorLabel.setText("Current floor: " + userFloor);
    }

    public void callElevator(int floor) {
        elevator.addTarget(floor);
    }

    public void elevatorArrived(int floor) {
        if (!userInside && floor == userFloor) {
            userInside = true;
            String input = JOptionPane.showInputDialog("Введите этаж:");
            if (input != null) {
                try {
                    int dest = Integer.parseInt(input);
                    userDestination = dest;
                    elevator.addTarget(dest);
                } catch (NumberFormatException e) {
                }
            }
        } else if (userInside && floor == userDestination) {
            userInside = false;
            userFloor = floor;
            updateUserFloorLabel();
        }
    }

    /**
     * Обработка вызова лифта (нажатие кнопки "Вверх" или "Вниз").
     *
     * @param isUp если true — вызов "вверх", иначе "вниз"
     */
    public void handleCall(boolean isUp) {
        if (inProcess) return;

        // Выбор лифта (ближайшего по суммарному пути)
        Elevator chosen = chooseElevator(isUp);
        int callFloor = userFloor;

        // Добавляем этаж вызова в очередь лифта
        if (!chosen.targets.contains(callFloor)) {
            List<Integer> arrTargets = new ArrayList<>(chosen.targets);
            if (!chosen.targets.isEmpty() && ((callFloor < arrTargets.get(0) && callFloor > chosen.currentFloor && isUp) || (callFloor > arrTargets.get(0) && callFloor < chosen.currentFloor && !isUp))) {
                arrTargets.add(arrTargets.get(0));
                arrTargets.set(1, arrTargets.get(0));
                arrTargets.set(0, callFloor);
                chosen.targets.clear();
                chosen.targets.addAll(arrTargets);
                isBetween = true;
            }
            else {
                chosen.addTarget(callFloor);
                isBetween = false;
            }
        }

        inProcess = true;
        Elevator finalChosen = chosen;
        moveElevatorAlongTargets(chosen, () -> handlePickup(finalChosen));
        // Если лифт свободен, запускаем движение
//        if (!chosen.isMoving) {
//            Elevator finalChosen = chosen;
//            moveElevatorAlongTargets(chosen, () -> handlePickup(finalChosen));
//        }

        if (chosen == elevatorA) System.out.print("Route A: ");
        else System.out.print("Route B: ");
        System.out.println(chosen.currentFloor + " -> " +
                String.join(" -> ", chosen.targets.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())));
    }

    private Elevator chooseElevator(boolean isUp) {
        int distA = calculateFullDistance(elevatorA, userFloor);
        int distB = calculateFullDistance(elevatorB, userFloor);
        return distA <= distB ? elevatorA : elevatorB;
    }

    // Подсчёт суммарной дистанции лифта до пользователя, учитывая уже запланированные цели
    private int calculateFullDistance(Elevator elevator, int userFloor) {
        int current = elevator.currentFloor;
        if (elevator.targets.isEmpty()) {
            // Если у лифта нет текущей цели, просто прямое расстояние
            return Math.abs(current - userFloor);
        }
        int firstTarget = elevator.targets.peek();
        // Проверяем, находится ли пользователь между current и firstTarget:
        if ((userFloor >= Math.min(current, firstTarget) &&
                userFloor <= Math.max(current, firstTarget))) {
            return Math.abs(current - userFloor);
        } else {
            return Math.abs(current - firstTarget) + Math.abs(firstTarget - userFloor);
        }
    }

    private int getDirection(Elevator elevator) {
        if (elevator.targets.isEmpty()) return 0;
        int nextFloor = elevator.targets.peek();
        if (nextFloor > elevator.currentFloor) return 1;
        else if (nextFloor < elevator.currentFloor) return -1;
        else return 0;
    }

    private boolean canAddTarget(Elevator elevator, int floorToAdd) {
        int dir = getDirection(elevator);
        if (dir == 0) return true;
        if (dir > 0) return floorToAdd > elevator.currentFloor;
        else return floorToAdd < elevator.currentFloor;
    }

    // Обработка высадки (открытие дверей и запрос следующего этажа)
    private void handlePickup(Elevator elevator) {
        if (elevator == elevatorA) {
            openLeftDoor();
        } else {
            openRightDoor();
        }
        // Диалог с пользователем для ввода целевого этажа
        Platform.runLater(() -> showDestinationDialog(elevator));
    }

    // Показ диалога выбора этажа пользователем
    private void showDestinationDialog(Elevator elevator) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Выбор этажа");
        dialog.setHeaderText("Введите номер этажа (1-99):");
        dialog.setContentText("Этаж:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int parsed = Integer.parseInt(result.get().trim());
                int target = Math.max(1, Math.min(99, parsed));

                // Всегда добавляем цель пользователя без проверки направления
                elevator.addTarget(target);

            } catch (NumberFormatException ex) {
                // Некорректный ввод — ничего не добавляем
            }
        }
        // В любом случае закрываем двери и продолжаем движение по всем целям
        resetDoors();
        moveElevatorAlongTargets(elevator, () -> {
            resetDoors();
            inProcess = false;
        });
    }

    // Движение лифта по всем целевым этажам в очереди
    private void moveElevatorAlongTargets(Elevator elevator, Runnable afterAllTargets) {
        if (elevator.targets.isEmpty()) {
            afterAllTargets.run();
            return;
        }

        // Оптимизация: выбираем ближайший этаж из очереди целей
//        if (elevator.targets.size() > 1) {
//            LinkedList<Integer> list = (LinkedList<Integer>) elevator.targets;
//            int current = elevator.currentFloor;
//            int best = list.get(0);
//            int minDist = Math.abs(current - best);
//            for (int t : list) {
//                int d = Math.abs(current - t);
//                if (d < minDist) {
//                    minDist = d;
//                    best = t;
//                }
//            }
//            list.remove((Integer) best);
//            list.addFirst(best);
//        }

        Integer nextTarget = elevator.targets.peek();
        moveElevatorToFloor(elevator, nextTarget, () -> {
            elevator.targets.poll();
            if (nextTarget == userFloor && inProcess) {
                if (!elevator.isHandlingPickup) {
                    elevator.isHandlingPickup = true;
                    handlePickup(elevator);
                }
            } else {
                moveElevatorAlongTargets(elevator, afterAllTargets);
            }
        });
    }

    // Анимация движения лифта к указанному этажу
    private void moveElevatorToFloor(Elevator elevator, int targetFloor, Runnable onFinish) {
        // Проверяем, не движется ли уже лифт
        if (elevator.isMoving && !isBetween) {
            System.out.println("Elevator " + (elevator == elevatorA ? "A" : "B") + " is busy. Skipping.");
            return;
        }

        elevator.isMoving = true;
        int diff = targetFloor - elevator.currentFloor;
        int steps = Math.abs(diff);
        int dir = (diff > 0) ? 1 : -1;

        System.out.println("Elevator " + (elevator == elevatorA ? "A" : "B") +
                " moving from " + elevator.currentFloor +
                " to " + targetFloor);

        // Останавливаем предыдущий таймлайн (если есть)
        if (elevator.movementTimeline != null) {
            elevator.movementTimeline.stop();
            elevator.movementTimeline = null;
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            elevator.currentFloor += dir;
            elevator.updateFloorLabel();

            // Обновляем метку при достижении этажа пользователя
            if (elevator.currentFloor == userFloor) {
                updateUserFloorLabel();
            }

            // Добавляем userFloor в цели если необходимо
            if (elevator.currentFloor == userFloor && inProcess && !elevator.targets.contains(userFloor)) {
                if (canAddTarget(elevator, userFloor)) {
                    elevator.addTarget(userFloor);
                }
            }
        }));

        timeline.setCycleCount(steps);
        timeline.setOnFinished(ev -> {
            elevator.isMoving = false;
            elevator.movementTimeline = null;

            // Обновление состояния пользователя
            if (elevator.isHandlingPickup) {
                elevator.isHandlingPickup = false;
                userFloor = elevator.currentFloor;
                updateUserFloorLabel();
            }

            // Вызываем колбэк завершения
            onFinish.run();
        });

        // Сохраняем ссылку на текущий таймлайн
        elevator.movementTimeline = timeline;
        timeline.play();
    }

    // Автоматическое случайное движение лифтов (эмуляция)
    public void startAutoMovement() {
        if (autoMovementTimeline != null) return;
        autoMovementTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> {
            randomlyMoveElevator(elevatorA);
            randomlyMoveElevator(elevatorB);
        }));
        autoMovementTimeline.setCycleCount(Timeline.INDEFINITE);
        autoMovementTimeline.play();
    }

    public void stopAutoMovement() {
        if (autoMovementTimeline != null) {
            autoMovementTimeline.stop();
            autoMovementTimeline = null;
        }
    }

    // Случайное добавление цели (60% шанс, если лифт свободен)
    private void randomlyMoveElevator(Elevator elevator) {
        if (inProcess || elevator.isMoving || !elevator.targets.isEmpty()) return;
        if (random.nextInt(100) < 60) {
            int targetFloor = random.nextInt(99) + 1;
            if (targetFloor == elevator.currentFloor) {
                targetFloor = (targetFloor % 99) + 1;
            }
            elevator.addTarget(targetFloor);
            moveElevatorAlongTargets(elevator, () -> {});
        }
    }
}
