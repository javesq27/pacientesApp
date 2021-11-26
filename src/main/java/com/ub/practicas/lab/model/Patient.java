package com.ub.practicas.lab.model;


import javax.persistence.Entity;

@Entity
public class Patient extends SystemUser
{
    private boolean emergency;
    private Long proId;

    public Patient(){
        super();
        this.emergency = false;
        this.setRole("ROLE_PATIENT");
    }
  
    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public Long getProId() {
        return proId;
    }

    public void setProId(Long proId) {
        this.proId = proId;
    }
}