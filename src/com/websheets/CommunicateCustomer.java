/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.websheets;

import com.data.Order;
import com.dto.EmailDTO;
import com.websheets.db.mongo.DataBaseAccess;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jayakumar
 */
public final class CommunicateCustomer {
    
    public static final String API_KEY = "key-1180c0836cb719598c0d9448c0a9f400";
    public static final String TARGE_URL="https://api.mailgun.net/v2/websheet.co";
    public static final String RESOURCE ="messages";
    public static final String FROM = "WebSheets.io<jayjo7@hotmail.com>";
    
        private static ArrayList<Order> getOrders() throws UnknownHostException {
        return DataBaseAccess.getOrders("orders", "progress", "Sheet Updated");
    }
        public static void emailNotification() throws UnknownHostException
        {
            ArrayList<Order> orders= CommunicateCustomer.getOrders();
            for (Order order: orders)
            {
                EmailDTO emailDTO = new EmailDTO();
                emailDTO.setApiKey(API_KEY);
                emailDTO.setTargetURL(TARGE_URL);
                emailDTO.setFrom(FROM);
                emailDTO.setResourcePath(RESOURCE);
                emailDTO.setTo(order.getCustomer().getEmail().get(0).getAddress());
                emailDTO.setSubject("Order Received");
                emailDTO.setText("We received your order, you will receive another email when to pick up your order");
                com.util.Email.SendSimpleMessage(emailDTO);
                CommunicateCustomer.updateOrderStatusToCustomerNotified(order);
                
                
                
            }
            
            
            
        }
        
        private static void updateOrderStatusToCustomerNotified(Order order) throws UnknownHostException
        
{

        HashMap<String, String> criteriaMap = new HashMap<>();
        criteriaMap.put("_id", order.getId());
        criteriaMap.put("progress", "Sheet Updated");
    
                HashMap<String, String> toValue = new HashMap<>();
                toValue.put("progress", "Customer Notified");
        
        DataBaseAccess.updateCollection("orders", criteriaMap, toValue, false, false);
               // DataBaseAccess.updateById("order", order.getId(),"status", "inProcess" );

    }
        
        
        public static void main (String[] args) throws UnknownHostException
        {
             
             CommunicateCustomer.emailNotification();
        }
    
}
