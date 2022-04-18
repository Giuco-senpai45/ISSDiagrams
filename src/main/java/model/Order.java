package model;

import java.io.Serializable;

public class Order implements Serializable {
    private Integer id;
    private Integer organizerId;
    private String address;
    private String comment;
    private String phoneNumber;
    public Order() {
    }

    public Order(Integer id, Integer organizerId, String address, String comment, String phoneNumber) {
        this.id = id;
        this.organizerId = organizerId;
        this.address = address;
        this.comment = comment;
        this.phoneNumber = phoneNumber;
    }

    public Order(Integer organizerId, String address, String comment, String phoneNumber) {
        this.organizerId = organizerId;
        this.address = address;
        this.comment = comment;
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Integer organizerId) {
        this.organizerId = organizerId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
