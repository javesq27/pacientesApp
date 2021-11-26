package com.ub.practicas.lab.model;

import java.time.LocalDate;

import javax.persistence.Entity;

@Entity
public class ParticularTask extends Task {
    private LocalDate dateDay;

    public ParticularTask(){
        super();
    }

    public LocalDate getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = LocalDate.parse(dateDay);
    }

    public static ParticularTask addRutine(RutineTask rTask){
        ParticularTask pTask = new ParticularTask();
        pTask.setTaskName(rTask.getTaskName());
        pTask.setPatientId(rTask.getPatientId());
        pTask.setDescription(rTask.getDescription());
        pTask.setAddress(rTask.getAddress());
        pTask.setTaskTime(rTask.getTaskTime().toString());
        pTask.dateDay = LocalDate.now();
        return pTask;
    }
}