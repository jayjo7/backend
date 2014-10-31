/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.util;

import java.util.UUID;

/**
 *
 * @author jayakumar
 */
public class SharedService {

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

}
