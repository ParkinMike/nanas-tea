package com.parkin.mike.model;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class NanasRequest {

    @NotNull(message = "date is required")
    private LocalDate date;
    private boolean on;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
