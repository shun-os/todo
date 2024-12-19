package com.example.model;

import java.time.LocalDate;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class ToDoManager {
    private ListProperty<ToDo> todos = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ListProperty<ToDo> todosProperty() {
        return todos;
    }

    public void remove(ToDo todo) {
        todos.remove(todo);
        System.out.println("Removed #" + todo.getId());
    }

    private void addListener(ToDo todo) {
        todo.titleProperty().addListener((observable, oldValue, newValue) -> 
        todo.titleProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Title changed #" + todo.getId() + " : " + newValue));
        
        todo.dateProperty().addListener((observable, oldValue, newValue) -> 
        todo.dateProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Date changed #" + todo.getId() + " : " + newValue));

        todo.completedProperty().addListener((observable, oldValue, newValue) -> 
        todo.completedProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Completed changed #" + todo.getId() + " : " + newValue));
    }

    public void create(String title, LocalDate date, boolean completed, String priority) {
        int newId = 0;
        if (todos.size() > 0) {
            newId = todos.stream().max((todo1, todo2) -> todo1.getId() - todo2.getId()).get().getId() + 1;
        }
        int newId1= todos.size() > 0 ? todos.stream().mapToInt(ToDo::getId).max().getAsInt() + 1 : 0;

        addNewToDo(newId1, title, date, completed, priority);
        System.out.println("Added #" + newId1);
    }

    private void addNewToDo(int id, String title, LocalDate date, boolean completed, String priority) {
        var todo = new ToDo(id, title, date, completed, priority); // priorityを追加
        var todo = new ToDo(id, title, date, completed, priority);
        addListener(todo);
        todos.add(todo);
    }

    public void loadInitialData() {
        addNewToDo(0, "Design", LocalDate.parse("2022-12-01"), true, "medium");
        addNewToDo(1, "Implementation", LocalDate.parse("2022-12-07"), false, "high");
    }
}