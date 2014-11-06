/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.util;

import java.net.MalformedURLException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

/**
 *
 * @author jayakumar
 */
public class Email {
    
    
    public Email() throws MalformedURLException
    {
    }
    
public static Response SendSimpleMessage() {
    
    
     HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
     .nonPreemptive().credentials("api", "key-1180c0836cb719598c0d9448c0a9f400").build();
     
     
    
       Client client = ClientBuilder.newClient();
        client.register(feature);

 
WebTarget webTarget = client.target("https://api.mailgun.net/v2/websheet.co");
WebTarget resourceWebTarget = webTarget.path("messages");


/**
    EmailInfo ei = new EmailInfo();
    ei.setFrom("WebSheets.io<jayjo7@hotmail.com>");


ei.setTo( "jayjo7@hotmail.com");
ei.setSubject("Order Received");
ei.setText("We have received your order, you will receive another email on when to pick up your order");

 */
MultivaluedMap<String, String> mvm = new MultivaluedHashMap<>();

mvm.add("from", "WebSheets.io<jayjo7@hotmail.com>");
mvm.add("to", "jayjo7@hotmail.com");
mvm.add("subject", "Order Received");
mvm.add("text", "We have received your order, you will receive another email on when to pick up your order");

 
Invocation.Builder invocationBuilder =
        resourceWebTarget.request(MediaType.APPLICATION_FORM_URLENCODED);

 
Response response = invocationBuilder.post(Entity.form(mvm));
System.out.println(response.getStatus());
System.out.println(response.readEntity(String.class));      
return response;       

}

public static void main(String[] args)
{
    Response response = Email.SendSimpleMessage() ;
}
    
}
