package com.parkin.mike.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "nanas_schedule")
public class NanasSchedule {

    @Id
    private LocalDate date;

    @Column(name = "attending", nullable = false)
    private boolean attending;

    protected NanasSchedule() {}

    public NanasSchedule(LocalDate date, boolean attending) {
        this.date = date;
        this.attending = attending;
    }

    public LocalDate getDate() { return date; }
    public boolean isAttending() { return attending; }
    public void setAttending(boolean attending) { this.attending = attending; }
}
