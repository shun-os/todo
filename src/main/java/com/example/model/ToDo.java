package com.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ToDo {
    private int id;
    private StringProperty title = new SimpleStringProperty();          // タイトル
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(); // 日付
    private BooleanProperty completed = new SimpleBooleanProperty();        // 完了状態
    // 重要度を示すプロパティ
    private StringProperty priority = new SimpleStringProperty();           // 重要度
    // 現在時刻
    private ObjectProperty<LocalDateTime> nowTimestamp = new SimpleObjectProperty<>(LocalDateTime.now());
    // +5分後と+10分後を設定するためのプロパティ
    private ObjectProperty<LocalDateTime> addFiveTime = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDateTime> addTenTime = new SimpleObjectProperty<>();

    // コンストラクタ
    public ToDo(int id, String title, LocalDate date, boolean completed, String priority) {
        this.id = id;
        setTitle(title);
        setDate(date);
        setCompleted(completed);
        setPriority(priority);
        updateTimeProperties(); // コンストラクタで初期設定を行う
    }

    // idは読み取り専用
    public int getId() {
        return id;
    }

    // タイトルに関するメソッド
    public StringProperty titleProperty() {
        return title;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    // 日付に関するメソッド
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate localDate) {
        this.date.set(localDate);
    }

    // 完了状態に関するメソッド
    public BooleanProperty completedProperty() {
        return completed;
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }

    // 重要度プロパティ
    public StringProperty priorityProperty() {
        return priority;
    }

    public String getPriority() {
        return priority.get();
    }

    public void setPriority(String priority) {
        this.priority.set(priority);
    }

    // 現在時刻に関するメソッド
    public ObjectProperty<LocalDateTime> nowTimestampProperty() {
        return nowTimestamp;
    }

    public LocalDateTime getNowTimestamp() {
        return nowTimestamp.get();
    }

    public void updateNowTimestamp() {
        nowTimestamp.set(LocalDateTime.now());
    }

    // +5分後の時間を設定
    public void setAddFiveTime() {
        addFiveTime.set(getNowTimestamp().plusMinutes(5));
    }

    public ObjectProperty<LocalDateTime> addFiveTimeProperty() {
        return addFiveTime;
    }

    public LocalDateTime getAddFiveTime() {
        return addFiveTime.get();
    }

    // +10分後の時間を設定
    public void setAddTenTime() {
        addTenTime.set(getNowTimestamp().plusMinutes(10));
    }

    public ObjectProperty<LocalDateTime> addTenTimeProperty() {
        return addTenTime;
    }

    public LocalDateTime getAddTenTime() {
        return addTenTime.get();
    }

    // 現在時刻、+5分、+10分の時間を一度に更新するメソッド
    public void updateTimeProperties() {
        updateNowTimestamp(); // 現在時刻を取得して設定
        setAddFiveTime();     // +5分後を設定
        setAddTenTime();      // +10分後を設定
    }
}