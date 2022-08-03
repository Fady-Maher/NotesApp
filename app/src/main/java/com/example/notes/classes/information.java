package com.example.notes;

public class information {
    public String time;
    public String title;
    public String note;
    public String imageid;

    public information(String time, String title, String note, String imageid) {
        this.time = time;
        this.title = title;
        this.note = note;
        this.imageid = imageid;
    }
    public information(String time, String title, String note) {
        this.time = time;
        this.title = title;
        this.note = note;
    }
    public information() {
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }
}
