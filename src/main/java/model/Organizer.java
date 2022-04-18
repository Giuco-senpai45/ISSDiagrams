package model;

import java.io.Serializable;

public class Organizer implements Serializable {
    private Integer id;
    private String password;
    private String name;

    public Organizer() {
    }

    public Organizer(Integer id, String password, String name) {
        this.id = id;
        this.password = password;
        this.name = name;
    }

    public Organizer(String password, String name) {
        this.password = password;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
