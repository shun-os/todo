package com.example;
import java.time.LocalDate;
import java.util.List;

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
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
public class MainController {
    private final String TODO_ID_PREFIX = "todo-";
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
    private void showInfo(String txt) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("アプリの情報");
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();
    }
    private HBox createToDoHBox(ToDo todo) {
        // Create View Items
        var completedCheckBox = new CheckBox();
        completedCheckBox.setSelected(todo.isCompleted());
        completedCheckBox.getStyleClass().add("todo-completed");
        var titleField = new TextField(todo.getTitle());
        titleField.getStyleClass().add("todo-title");
        HBox.setHgrow(titleField, Priority.ALWAYS);
        var datePicker = new DatePicker(todo.getDate());
        datePicker.getStyleClass().add("todo-date");
        datePicker.setPrefWidth(105);
        HBox.setHgrow(datePicker, Priority.NEVER);
        var deleteBtn = new Button("削除");
        deleteBtn.getStyleClass().add("todo-delete");
        var todoItem = new HBox(completedCheckBox, titleField, datePicker, deleteBtn);
        todoItem.getStyleClass().add("todo-item");
        todoItem.setId(TODO_ID_PREFIX + todo.getId());
        // Bind Model to View
        completedCheckBox.selectedProperty().bindBidirectional(todo.completedProperty());
        titleField.textProperty().bindBidirectional(todo.titleProperty());
        datePicker.valueProperty().bindBidirectional(todo.dateProperty());
        // Event Handler
        deleteBtn.setOnAction(e -> {
            model.remove(todo);
            todoListVBox.getChildren().remove(todoItem); // Remove from view
        });
        return todoItem;
    }
    public void initModel(ToDoManager manager) {
        if (this.model != null)
            throw new IllegalStateException("Model can only be initialized once");
        this.model = manager;
        loadTaskList(); // Here you load tasks initially.
        ObservableList<Node> todoListItems = todoListVBox.getChildren();
        // Event Handler for adding tasks
        addBtn.setOnAction(e -> {
            String priority = "medium"; // Default priority or implement priority input
            model.create(headerTitleField.getText(), headerDatePicker.getValue(), false, priority);
            headerTitleField.clear();
        });
        // Observe Model to update View
        model.todosProperty().addListener((ListChangeListener<ToDo>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(todo -> {
                        todoListItems.add(createToDoHBox(todo));
                        loadTaskList(); // Add new items to taskListView
                    });
                }
                if (change.wasRemoved()) {
                    List<String> ids = change.getRemoved().stream()
                        .map(todo -> TODO_ID_PREFIX + todo.getId())
                        .toList();
                    todoListItems.removeIf(node -> ids.contains(node.getId()));
                    loadTaskList(); // Cleanup UI list
                }
            }
        });
        model.loadInitialData(); // Load initial data
    }
    private void loadTaskList() {
        taskListView.getItems().clear(); // Clear the list
        for (ToDo todo : model.todosProperty()) {
            // Create formatted text for the task list
            String expectedText = String.format("%s - %s - %s",
                    todo.getDate(),
                    todo.getNowTimestamp().toLocalTime(), // NPC: Be sure this timestamp is what you want to display
                    todo.getPriority());
            taskListView.getItems().add(expectedText); // Corrected to add items to the list
        }
    }
    public void initialize() {
        // Set today's date
        headerDatePicker.setValue(LocalDate.now());
        menuItemAbout.setOnAction(e -> showInfo("ToDo App"));
        menuItemClose.setOnAction(e -> Platform.exit());
    }
}