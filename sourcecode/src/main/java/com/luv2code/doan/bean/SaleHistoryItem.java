package com.luv2code.doan.bean;

public class SaleHistoryItem {
    private String name;
    private Double value;

    public SaleHistoryItem() {
        super();
    }
    public SaleHistoryItem(String name, Double value) {
        super();
        this.name = name;
        this.value = value;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }
}
