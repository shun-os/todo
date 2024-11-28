package com.example.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.example.model.ToDoManager;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class MainController {
    @FXML
    private Button lowPriorityBtn, mediumPriorityBtn, highPriorityBtn;
    @FXML
    private Button nowButton, fiveMinutesButton, tenMinutesButton, addBtn, setTimeButton;
    @FXML
    private Label clockLabel, selectedTimeLabel;
    @FXML
    private TextField headerTitleField;
    @FXML
    private DatePicker headerDatePicker;
    @FXML
    private ListView<String> taskListView;

    private String selectedPriority = "medium";
    private ToDoManager model;

    @FXML
    public void initialize() {
        headerDatePicker.setValue(LocalDate.now());
        updateClock();
        startClockUpdate();

        lowPriorityBtn.setOnAction(e -> setPriority("low"));
        mediumPriorityBtn.setOnAction(e -> setPriority("medium"));
        highPriorityBtn.setOnAction(e -> setPriority("high"));
        
        nowButton.setOnAction(e -> setCurrentTime());
        fiveMinutesButton.setOnAction(e -> setFutureTime(5));
        tenMinutesButton.setOnAction(e -> setFutureTime(10));
        
        setTimeButton.setOnAction(e -> showTimePicker());

        addBtn.setOnAction(e -> {
            String title = headerTitleField.getText();
            LocalDate date = headerDatePicker.getValue();
            model.create(title, date, false, selectedPriority);

            // タスク名をListViewに追加
            taskListView.getItems().add(title);
            headerTitleField.clear(); // 入力フィールドをクリア
        });
    }


    private void showTimePicker() {
    	// 時刻選択の処理をここに実装
        TextInputDialog timeDialog = new TextInputDialog();
        timeDialog.setTitle("指定時刻");
        timeDialog.setHeaderText("時間と分を入力してください（例: 14:30）");
        LocalTime now = LocalTime.now();
        timeDialog.setContentText("時刻 (HH:mm) 入力: ");

        timeDialog.showAndWait().ifPresent(input -> {
            try {
                LocalTime inputTime = LocalTime.parse(input);
                if (inputTime.isBefore(now)) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("エラー");
                    alert.setHeaderText(null);
                    alert.setContentText("過去の時刻は指定できません。");
                    alert.showAndWait();
                } else {
                    selectedTimeLabel.setText("選択された時刻: " + inputTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            } catch (DateTimeParseException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("エラー");
                alert.setHeaderText(null);
                alert.setContentText("無効な時刻形式です。HH:mmの形式で入力してください（例: 14:30）");
                alert.showAndWait();
            }
        });
    }

    private void setPriority(String priority) {
        selectedPriority = priority;
        lowPriorityBtn.setStyle(priority.equals("low") ? "-fx-background-color: lightgray;" : "");
        mediumPriorityBtn.setStyle(priority.equals("medium") ? "-fx-background-color: lightgray;" : "");
        highPriorityBtn.setStyle(priority.equals("high") ? "-fx-background-color: lightgray;" : "");
    }

    private void updateClock() {
        LocalTime now = LocalTime.now();
        clockLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void startClockUpdate() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(this::updateClock);
            }
        }).start();
    }

    private void setCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        selectedTimeLabel.setText("選択された時刻: " + currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void setFutureTime(int minutes) {
        LocalTime newTime = LocalTime.now().plusMinutes(minutes);
        selectedTimeLabel.setText("選択された時刻: " + newTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
