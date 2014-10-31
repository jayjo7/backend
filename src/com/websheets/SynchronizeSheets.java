/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.websheets;

import com.google.gdata.client.authn.oauth.*;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.*;
import com.google.gdata.data.batch.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import com.util.SharedService;
import com.websheets.db.mongo.DataBaseAccess;
import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 *
 * @author jayakumar
 */
public class SynchronizeSheets {

    private Map<String, Map<String, String>> spreadSheetMap;

    /**
     *
     * @param userName Google docs user name
     * @param password Google docs passowrd
     * @param sheetUrl Google docs URL
     * @param sheetName Google sheets in the above Google docs location.
     * @param outDatataWorkSheetName Optional. Worksheet name that will have
     * data to be synchronized with database
     * @param inDatataWorkSheetName Optional. Worksheet name where the data
     * collected from customer will be updated for business consumer to act.
     * @throws AuthenticationException
     * @throws MalformedURLException
     * @throws IOException
     * @throws ServiceException
     */
    private void getSpreadSheetData(String userName,
            String password,
            String sheetUrl,
            String sheetName,
            String outDataWorkSheetName,
            String inDataWorkSheetName,
            String uuid) throws AuthenticationException,
            MalformedURLException, IOException, ServiceException {

        SpreadsheetService service = new SpreadsheetService(sheetName);
        service.setUserCredentials(userName, password);

        URL SPREADSHEET_FEED_URL = new URL(sheetUrl);

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL,
                SpreadsheetFeed.class);
        List<SpreadsheetEntry> spreadsheets = feed.getEntries();

        if (spreadsheets.isEmpty()) {
            System.out.println("No Spreadsheet in assocaited to this account");
        }
        else{

        
        // Iterate through all of the spreadsheets returned
        System.out.println("Here is all availabe SpreadSheets - Start");

            for (SpreadsheetEntry spreadsheet : spreadsheets) {
                // Print the title of this spreadsheet to the screen
                System.out.println(spreadsheet.getTitle().getPlainText());

                System.out.println("Here is all availabe SpreadSheets - End");

            }
        System.out.println("Here is all availabe SpreadSheets - End");

        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            System.out.println("Working with the spreadsheet: "
                    + spreadsheet.getTitle().getPlainText());

            /**
             * // Make a request to the API to fetch information about all //
             * worksheets in the spreadsheet.
             */
            List<WorksheetEntry> worksheets
                    = spreadsheet.getWorksheets();
            System.out.println("SpreadSheets MetaData - Start");

                worksheets.stream().forEach((worksheet) -> {
                    String title = worksheet.getTitle().getPlainText();
                    int rowCount = worksheet.getRowCount();
                    int colCount = worksheet.getColCount();

                    // Print the fetched information to the screen for this worksheet. 
                    System.out.println("\t" + title + "- rows:" + rowCount
                            + " cols: " + colCount);
                });
            System.out.println("SpreadSheets MetaData - End");

            WorksheetFeed worksheetFeed = service.getFeed(
                    spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
            List<WorksheetEntry> worksheetsList = worksheetFeed.getEntries();

            
            for( WorksheetEntry worksheet : worksheetsList)
            {

            // Fetch the list feed of the worksheet.
            URL listFeedUrl = worksheet.getListFeedUrl();
            ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

            // Iterate through each row, printing its cell values.
            spreadSheetMap = new HashMap<>();
            for (ListEntry row : listFeed.getEntries()) {
                // Print the first column's cell value
                System.out.print(row.getTitle().getPlainText() + "\t");
                // Iterate over the remaining columns, and print each cell value

                Map<String, String> spreadSheeRowMap = new HashMap<>();
                for (String tag : row.getCustomElements().getTags()) {

                    System.out.print(row.getCustomElements().getValue(tag)
                            + "\t");
                    spreadSheeRowMap.put(tag,
                            row.getCustomElements().getValue(tag));

                }
                spreadSheetMap.put(spreadSheeRowMap.get("webid"),
                        spreadSheeRowMap);
                System.out.println("Size of spreadSheeRowMap : "
                        + spreadSheeRowMap.size() + "\t");
                System.out.println("Size of spreadSheetMap : "
                        + spreadSheetMap.size() + "\t");

                System.out.println("");
            }
        
        printMap();
        System.out.println("Staring to sync the WorkShee : " + worksheet.getTitle().getPlainText());
        DataBaseAccess.sync(this.spreadSheetMap, worksheet.getTitle().getPlainText(), uuid);
        System.out.println("Done WorkSheet Name : " + worksheet.getTitle().getPlainText());

    
            }
        }
    }
    }

    public static void main(String[] args) throws AuthenticationException,
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

        SynchronizeSheets synchronizeSheets = new SynchronizeSheets();

        /*
         The fpllowing data need to either feed in on invoking or read from a configuration file
         take security measure to protect the credentials.
         */
        synchronizeSheets.getSpreadSheetData(USERNAME,
                PASSWORD,
                sheetUrl,
                spreadSheetName,
                outDataWorkSheetName,
                inDataWorkSheetName,
                uuid);
        System.out.println(uuid + ":Sync End = " + new Date());

    }

    private void printMap() {
        if (this.spreadSheetMap != null && spreadSheetMap.size() > 0) {
            for (String webid : this.spreadSheetMap.keySet()) {
                System.out.println("webid=" + webid);
                for (String columName : this.spreadSheetMap.get(webid)
                        .keySet()) {
                    System.out.println(columName + "="
                            + this.spreadSheetMap.get(webid).get(columName));
                }
                System.out.println();
            }
        } else {
            System.out.println("No records reterived from the spread sheet");
        }

    }

}
