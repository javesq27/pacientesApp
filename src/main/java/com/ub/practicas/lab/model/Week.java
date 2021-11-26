package com.ub.practicas.lab.model;

public enum Week {
    SUNDAY("Domingo"),
    MONDAY("Lunes"),
    TUESDAY("Martes"),
    WEDNESDAY("Miercoles"),
    THURSDAY("Jueves"),
    FRIDAY("Viernes"),
    SATURDAY("Sabado");

    private String displayValue;
    
    private Week(String displayValue) 
    {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() 
    {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) 
    {
        this.displayValue = displayValue;
    }
}
