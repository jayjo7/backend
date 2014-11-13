/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import com.dto.EmailDTO;
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

    public Email() throws MalformedURLException {
    }

    public static Response SendSimpleMessage(EmailDTO emailDTO) {

        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder()
                .nonPreemptive().credentials("api", emailDTO.getApiKey()).build();

        Client client = ClientBuilder.newClient();
        client.register(feature);

        WebTarget webTarget = client.target(emailDTO.getTargetURL());
        WebTarget resourceWebTarget = webTarget.path(emailDTO.getResourcePath());

        MultivaluedMap<String, String> mvm = new MultivaluedHashMap<>();

        mvm.add("from", emailDTO.getFrom());
        mvm.add("to", emailDTO.getTo());
        mvm.add("subject", emailDTO.getSubject());
        mvm.add("text", emailDTO.getText());

        Invocation.Builder invocationBuilder
                = resourceWebTarget.request(MediaType.APPLICATION_FORM_URLENCODED);

        Response response = invocationBuilder.post(Entity.form(mvm));
        System.out.println(response.getStatus());
        System.out.println(response.readEntity(String.class));
        return response;

    }

  

}
