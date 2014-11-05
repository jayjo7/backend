/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.data;

import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author jayakumar
 */
public class Order {
    
    private String foodName;
    private String foodQuantity;
    private BizCustomer customer;
    private Date orderedAt;
    private String status;
    private String id;

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(String foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public BizCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(BizCustomer customer) {
        this.customer = customer;
    }




    public Date getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(Date orderedAt) {
        this.orderedAt = orderedAt;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
    
    public String toString()
    {
       return ToStringBuilder.reflectionToString(this);
    }
    
    
}
