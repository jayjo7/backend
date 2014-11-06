/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.websheets;

import com.data.*;
import com.mongodb.DBObject;
import com.websheets.db.mongo.DataBaseAccess;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;
import com.util.SharedService;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 *
 * @author jayakumar
 */
public class ProcessOrders {

    private static ArrayList<Order> getNewOrders() throws UnknownHostException {
        return DataBaseAccess.getOrders("orders", "status", "new");
    }
    
public static void synchronizeWithSheets(String userName,
            String password,
            String sheetUrl,
            String sheetName,
            String outDataWorkSheetName,
            String inDataWorkSheetName,
            String uuid) throws AuthenticationException,
            MalformedURLException, IOException, ServiceException
{
    ArrayList<Order> orders= getNewOrders();
    System.out.println("No of orders to be processed = " + orders.size());
    if(orders.isEmpty())
    {
        System.out.println("No new orders at this time " + new Date());
      
    }
    else
    {
        System.out.println("got new orders at this time " + new Date());
        ordersToSpreadSheet(orders, userName, password, sheetUrl,sheetName,null,null,uuid);
    }
}


private static void updateOrderStatusToInProcess(ArrayList<Order> orders) throws UnknownHostException
        
{
    for(Order order: orders)
    {
        HashMap<String, String> criteriaMap = new HashMap<>();
        criteriaMap.put("_id", order.getId());
        criteriaMap.put("status", "new");
    
                HashMap<String, String> toValue = new HashMap<>();
                toValue.put("status", "inProcess");
                toValue.put("progress", "Sheet Updated");
        
        DataBaseAccess.updateCollection("orders", criteriaMap, toValue, false, false);
               // DataBaseAccess.updateById("order", order.getId(),"status", "inProcess" );

    }
    

    
}
    private static void ordersToSpreadSheet(ArrayList<Order> orders,
            String userName,
            String password,
            String sheetUrl,
            String sheetName,
            String outDataWorkSheetName,
            String inDataWorkSheetName,
            String uuid) throws AuthenticationException, MalformedURLException, IOException, ServiceException {

        {
            
                    SpreadsheetService service = new SpreadsheetService(sheetName);
        service.setUserCredentials(userName, password);

        URL SPREADSHEET_FEED_URL = new URL(sheetUrl);

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
                SpreadsheetFeed.class);
        List<SpreadsheetEntry> spreadsheets = feed.getEntries();

            System.out.println("Size = " + spreadsheets.size());

            if (    spreadsheets.isEmpty()) {
                // TODO: There were no spreadsheets, act accordingly.
            }

    // TODO: Choose a spreadsheet more intelligently based on your
            // app's needs.
            SpreadsheetEntry spreadsheet = spreadsheets.get(0);
            System.out.println("Sheet Name =" + spreadsheet.getTitle().getPlainText());

    // Get the first worksheet of the first spreadsheet.
            // TODO: Choose a worksheet more intelligently based on your
            // app's needs.
            WorksheetFeed worksheetFeed = service.getFeed(
                    spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
            List<WorksheetEntry> worksheets = worksheetFeed.getEntries();

            WorksheetEntry worksheet = null;

            for (WorksheetEntry worksheetEntry : worksheets) {
                if (worksheetEntry.getTitle().getPlainText().equalsIgnoreCase("Orders")) {
                    worksheet = worksheetEntry;
                }

            }
            System.out.println("worksheet = " + worksheet);
            if (worksheet != null) 
            {

                // Fetch the list feed of the worksheet.
                URL listFeedUrl = worksheet.getListFeedUrl();
               // ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

                for(Order order:orders)
                {
                // Create a local representation of the new row.
                ListEntry row = new ListEntry();
                row.getCustomElements().setValueLocal("status", order.getStatus());

                row.getCustomElements().setValueLocal("foodName", order.getFoodName());
                row.getCustomElements().setValueLocal("foodQuantity", order.getFoodQuantity());
                row.getCustomElements().setValueLocal("customerId", order.getCustomer().getId());
                row.getCustomElements().setValueLocal("orderedAt", order.getOrderedAt().toString());
                row.getCustomElements().setValueLocal("id", order.getId());
                row.getCustomElements().setValueLocal("email", order.getCustomer().getEmail().get(0).getAddress());


                // Send the new row to the API for insertion.
                service.insert(listFeedUrl, row);
                order.setStatus("inProcess");
                }
                updateOrderStatusToInProcess(orders);
            }
            else{
                System.out.println("Please create the worksheet \"Orders\" and try again");
            }
            
        }
    }

    public static void main(String[] args)  throws AuthenticationException,
            MalformedURLException, IOException, ServiceException, Exception {
        String uuid = SharedService.getUUID();
        System.out.println("uuid = " + uuid);
        System.out.println(uuid + ":Sync Start = " + new Date());

        //Default values
        String USERNAME = "fifthdonut@gmail.com";
        String PASSWORD = "fifthdonut123";
        String sheetUrl = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
        String spreadSheetName = "ProductData";
        String outDataWorkSheetName = "";
        String inDataWorkSheetName = "";

        if (args != null && args.length > 0) {
            if (args.length < 4) {
                System.out.println(uuid + ": Need Minimum of 4 and Maximum of 6 parameters seperated by a space");
                System.out.println(uuid + "1. Google Sheets account user name");
                System.out.println(uuid + "2. Google Sheets account password");
                System.out.println(uuid + "3. Google Sheets absolute URL");
                System.out.println(uuid + "4. Name of the Spread Sheet");
                System.out.println(uuid + "5. Optional: Name of the Worksheet in the given spread sheet (Parameter 4), used as Sheet --> Web if not provided the first worksheet will used");
                System.out.println(uuid + "6. Optional: Name of the Worksheet in the given spread sheet (Parameter 4), used as Web --> Sheet");

                throw new Exception("Not enough Parameters, refer to the log for how to use");
            }
            
            

            System.out.println(uuid + ":Received paramater size = " + args.length);
            for (int i = 0; i < args.length; i++) {
                System.out.println(uuid + ": arg [" + i + "] = " + args.length);
            }
            

        }


        /*
         The fpllowing data need to either feed in on invoking or read from a configuration file
         take security measure to protect the credentials.
         */
       ProcessOrders.synchronizeWithSheets(USERNAME,
                PASSWORD,
                sheetUrl,
                spreadSheetName,
                outDataWorkSheetName,
                inDataWorkSheetName,
                uuid);
        System.out.println(uuid + ":Sync End = " + new Date());


    }

}
