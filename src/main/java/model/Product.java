package model;

import java.io.Serializable;

public class Product implements Serializable {
    private Integer id;
    private String name;
    private String details;
    private Double price;
    private Integer quantity;
    public Product() {
    }

    public Product(Integer id, String name, String details, Double price, Integer quantity) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(String name, String details, Double price, Integer quantity) {
        this.name = name;
        this.details = details;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name + " | " + price + " | " + quantity;
    }
}
