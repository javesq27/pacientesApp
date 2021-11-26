package com.ub.practicas.lab.model;


import javax.persistence.Entity;

@Entity
public class Professional extends SystemUser
{   
    private Long idNumber;

    public Professional(){
        super();
        this.setRole("ROLE_PROFESSIONAL");
    }

    public Long getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(Long idNumber) {
        this.idNumber = idNumber;
    }
}






