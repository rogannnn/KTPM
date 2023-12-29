package com.luv2code.doan.bean;

import javax.persistence.Entity;
import javax.persistence.Id;

public class SoldByCategoryItem {
    private String name;
    private Double totalSold;

    public SoldByCategoryItem() {
    }

    public SoldByCategoryItem(String name, Double totalSold) {
        this.name = name;
        this.totalSold = totalSold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(Double totalSold) {
        this.totalSold = totalSold;
    }
}
