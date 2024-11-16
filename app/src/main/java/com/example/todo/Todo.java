package com.example.todo;


import com.google.firebase.Timestamp;

public class Todo {

    String title;
    String note;
    boolean isCompleted;

    String taskID;

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    String date1;
    Timestamp createdAt;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public void setisCompleted(boolean isCompleted){
        this.isCompleted = isCompleted;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean getisCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }


    public Todo() {
        //Empty constructor
    }
    public Todo(String title,String note,boolean isCompleted,String date1){
        this.title=title;
        this.note=note;
        this.isCompleted=isCompleted;
        this.date1=date1;

    }
}

