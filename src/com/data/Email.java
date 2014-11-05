/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.data;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author jayakumar
 */
public class Email {

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    
    private String  address;
   private boolean verified;
   
            public String toString()
    {
       return ToStringBuilder.reflectionToString(this);
    }
}
