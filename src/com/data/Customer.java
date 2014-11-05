/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.data;

import java.util.ArrayList;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author jayakumar
 */
public class Customer {

    private String userName;
    private String password;
    private String id;
    private ArrayList<Email> email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Email> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<Email> email) {
        this.email = email;
    }





    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
        public String toString()
    {
       return ToStringBuilder.reflectionToString(this);
    }

}
