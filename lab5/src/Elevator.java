import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Elevator {
    // Текущий этаж и состояние
    public int currentFloor;
    public boolean isMoving;
    public boolean isHandlingPickup = false;
    private ElevatorSystem system;

    // UI: метка и изображение лифта
    public Label floorLabel;
    public ImageView imageView;

    // Анимация движения
    public Timeline movementTimeline;
    public Queue<Integer> targets;

    /**
     * Конструктор лифта
     *
     * @param startFloor начальный этаж
     * @param label метка с номером этажа
     * @param view изображение лифта
     */
    public Elevator(int startFloor, Label label, ImageView view) {
        this.currentFloor = startFloor;
        this.floorLabel = label;
        this.imageView = view;
        this.targets = new LinkedList<>();
        this.movementTimeline = null;
        this.isMoving = false;
        this.system = system;
    }

    /**
     * Обновление текстовой метки текущего этажа.
     */
    public void updateFloorLabel() {
        if (floorLabel != null) {
            Platform.runLater(() -> floorLabel.setText(String.valueOf(currentFloor)));
        }
    }


    /**
     * Остановить движение лифта.
     */
    public void stopMovement() {
        if (movementTimeline != null) {
            movementTimeline.stop();
            movementTimeline = null;
        }
        isMoving = false;
    }

    /**
     * Добавление нового целевого этажа в очередь.
     *
     * @param floor этаж, куда нужно поехать
     */
    public void addTarget(int floor) {
        targets.add(floor);
    }

//    public void step() {
//        if (targets.isEmpty()) return;
//        int next = findClosest();
//        if (currentFloor < next) {
//            currentFloor++;
//        } else if (currentFloor > next) {
//            currentFloor--;
//        }
//        if (currentFloor == next) {
//            targets.remove(Integer.valueOf(next));
//            system.elevatorArrived(currentFloor);
//        }
//    }

//    private int findClosest() {
//        int closest = targets.peek();
//        int minDist = Math.abs(closest - currentFloor);
//        for (int floor : targets) {
//            int dist = Math.abs(floor - currentFloor);
//            if (dist < minDist) {
//                minDist = dist;
//                closest = floor;
//            }
//        }
//        return closest;
//    }
}
