package com.ub.practicas.lab.model;

import javax.persistence.Entity;

@Entity
public class SystemUser extends User
{
    private String name;
    private String phone;

    public SystemUser(){
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}