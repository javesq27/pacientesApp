package com.ub.practicas.lab.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class RutineTask extends Task {
    private String weekDays;
    private LocalDate lastUpdated;
    private Boolean toRepeat;

    public RutineTask(){
        super();
        toRepeat = true;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getToRepeat() {
        return toRepeat;
    }

    public void setToRepeat(Boolean toRepeat) {
        this.toRepeat = toRepeat;
    }

    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<String> weekDays) {
        this.weekDays = "";
        for (String s : weekDays){
            this.weekDays += s + " . ";
        }
    }

    public String weekDaysDisplay(){
        String w = "";
        String[] week = weekDays.split(" . ");
        for (String s : week){
            w += "[" + Week.valueOf(s).getDisplayValue() + "]";
        }
        return w;
    }

    public RutineTask switchRepeat(){
        this.toRepeat = !this.toRepeat;
        return this;
    }

    public RutineTask updateToday(){
        this.lastUpdated = LocalDate.now();
        return this;
    }
}