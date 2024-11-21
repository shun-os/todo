package com.example;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List; // 追加インポート

import com.example.model.ToDo;
import com.example.model.ToDoManager;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {
    private final String TODO_ID_PREFIX = "todo-";

    @FXML
    private Label clockLabel;

    @FXML
    private Label selectedTimeLabel;  // 選択された時間を表示するラベル

    @FXML
    private Button nowButton;

    @FXML
    private Button fiveMinutesButton;

    @FXML
    private Button tenMinutesButton;

    @FXML
    private MenuItem menuItemAbout;

    @FXML
    private MenuItem menuItemClose;

    @FXML
    private Button addBtn;

    @FXML
    private DatePicker headerDatePicker;

    @FXML
    private TextField headerTitleField;

    @FXML
    private VBox todoListVBox;

    @FXML
    private ListView<String> taskListView;

    private ToDoManager model;

    private LocalTime selectedTime;  // ユーザーが選択した時刻を保持

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private void showInfo(String txt) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("アプリの情報");
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();
    }

    private HBox createToDoHBox(ToDo todo) {
        // チェックリスト
        var completedCheckBox = new CheckBox();
        completedCheckBox.setSelected(todo.isCompleted());
        completedCheckBox.getStyleClass().add("todo-completed");

        // タイトル
        var titleLabel = new Label(todo.getTitle());
        titleLabel.getStyleClass().add("todo-title");

        // 重要度
        var priorityLabel = new Label("優先度: " + todo.getPriority()); // 日本語の名称を追加
        priorityLabel.getStyleClass().add("todo-priority");

        // 時間
        var timeLabel = new Label(todo.getNowTimestamp().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.getStyleClass().add("todo-time");

        // 日付
        var dateLabel = new Label(todo.getDate().toString());
        dateLabel.getStyleClass().add("todo-date");

        // 削除ボタン
        var deleteBtn = new Button("削除");
        deleteBtn.getStyleClass().add("todo-delete");

        // HBoxを作成して各要素を追加
        HBox todoItem = new HBox(10); // 10 pxのスペースを設定
        todoItem.getChildren().addAll(completedCheckBox, titleLabel, priorityLabel, timeLabel, dateLabel, deleteBtn);
        todoItem.getStyleClass().add("todo-item");
        todoItem.setId(TODO_ID_PREFIX + todo.getId());

        // Event Handlerを追加
        completedCheckBox.selectedProperty().bindBidirectional(todo.completedProperty());
        deleteBtn.setOnAction(e -> {
            model.remove(todo);
            todoListVBox.getChildren().remove(todoItem); // Remove from view
        });

        return todoItem; // HBoxを直接返す
    }

    public void initModel(ToDoManager manager) {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");

        this.model = manager;
        loadTaskList(); // Here you load tasks initially.

        ObservableList<Node> todoListItems = todoListVBox.getChildren();

        addBtn.setOnAction(e -> {
            String priority = "medium"; 
            model.create(headerTitleField.getText(), headerDatePicker.getValue(), false, priority);
            headerTitleField.clear();
        });

        model.todosProperty().addListener((ListChangeListener<ToDo>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(todo -> {
                        todoListItems.add(createToDoHBox(todo));
                        loadTaskList();
                    });
                }
                if (change.wasRemoved()) {
                    List<String> ids = change.getRemoved().stream()
                        .map(todo -> TODO_ID_PREFIX + todo.getId())
                        .toList();
                    todoListItems.removeIf(node -> ids.contains(node.getId()));
                    loadTaskList();
                }
            }
        });

        model.loadInitialData(); // Load initial data
    }

    private void loadTaskList() {
        taskListView.getItems().clear(); 
        for (ToDo todo : model.todosProperty()) {
            String expectedText = String.format("%s - %s - %s",
                    todo.getDate(),
                    todo.getNowTimestamp().toLocalTime(), 
                    todo.getPriority());
            taskListView.getItems().add(expectedText);
        }
    }

    public void initialize() {
        headerDatePicker.setValue(LocalDate.now());
        updateClock(); // 時計の初期化
        startClockUpdate(); // 時計の更新を開始

        menuItemAbout.setOnAction(e -> showInfo("ToDo App"));
        menuItemClose.setOnAction(e -> Platform.exit());

        // ボタンのイベントハンドラ設定
        nowButton.setOnAction(e -> setCurrentTime());
        fiveMinutesButton.setOnAction(e -> setFutureTime(5));
        tenMinutesButton.setOnAction(e -> setFutureTime(10));
    }

    private void updateClock() {
        LocalTime now = LocalTime.now();
        clockLabel.setText(now.format(timeFormatter));
    }

    private void startClockUpdate() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // 1秒ごとに更新
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(this::updateClock);
            }
        }).start();
    }

    private void setCurrentTime() {
        selectedTime = LocalTime.now();
        updateSelectedTimeLabel();
    }

    private void setFutureTime(int minutes) {
        if (selectedTime == null) {
            selectedTime = LocalTime.now();
        }
        selectedTime = selectedTime.plusMinutes(minutes);
        updateSelectedTimeLabel();
    }

    private void updateSelectedTimeLabel() {
        if (selectedTime != null) {
            clockLabel.setText(selectedTime.format(timeFormatter));
            selectedTimeLabel.setText("選択された時刻: " + selectedTime.format(timeFormatter));
            selectedTimeLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        }
    }
}
