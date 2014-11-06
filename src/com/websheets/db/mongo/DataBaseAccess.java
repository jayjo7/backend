package com.websheets.db.mongo;

import com.data.BizCustomer;
import com.data.Email;
import com.data.Order;
import com.mongodb.*;
import com.util.SharedService;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataBaseAccess {

    /**
     * Returns the DB connection to localhost Mongo database.
     *
     * @return DB
     * @throws UnknownHostException
     */
    private static DB getMongoDBConnection() throws UnknownHostException {
        MongoClientURI mongoClientURI = new MongoClientURI("mongodb://fifthdonut:fifthdonut123@kahana.mongohq.com:10051/Websheets");
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        DB db = mongoClient.getDB("Websheets");
        return db;
    }

    /**
     *
     * @param spreadSheetMap
     * @param dbCollection
     * @param uuid
     * @throws UnknownHostException
     */
    public static void sync(Map<String, Map<String, String>> spreadSheetMap, String dbCollection, String uuid) throws UnknownHostException {
        DataBaseAccess.insert(spreadSheetMap, dbCollection);

    }

    public static void insert(Map<String, Map<String, String>> spreadSheetMap, String dbCollection
    ) throws UnknownHostException {

        DB db = getMongoDBConnection();
        System.out.println(db.getName());
        DBCollection table = db.getCollection(dbCollection);

        if (spreadSheetMap != null && spreadSheetMap.size() > 0) {

            spreadSheetMap.keySet().stream().map((uniqueId) -> {
                System.out.println("uniqueId=" + uniqueId);
                return uniqueId;
            }).map((uniqueId) -> {
                BasicDBObject queryDoc = new BasicDBObject("uniqueId", uniqueId);
                BasicDBObject doc = new BasicDBObject("uniqueId", uniqueId);
                spreadSheetMap.get(uniqueId)
                        .keySet().stream().forEach((columName) -> {
                            doc.append(columName, spreadSheetMap.get(uniqueId).get(columName));
                        });
                DBObject returnDoc = table.findAndModify(queryDoc, doc, null, false, doc, true, true);
                return returnDoc;
            }).forEach((returnDoc) -> {
                System.out.println("returnDoc = " + returnDoc);
            });

        } else {
            System.out.println("DataBaseAccess::insert:" + spreadSheetMap + " is either null or empty");

        }

    }



    public static void find() throws UnknownHostException {
        DB db = getMongoDBConnection();
        System.out.println(db.getName());
        DBCollection table = db.getCollection("productData");

        BasicDBObject doc = new BasicDBObject("name", "MongoDB")
                .append("type", "database").append("count", 1)
                .append("info", new BasicDBObject("x", 203).append("y", 102));
        table.insert(doc);

        DBObject myDoc = table.findOne();
        System.out.println(myDoc);

        DBCursor cursor = table.find();
        try {
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        } finally {
            cursor.close();
        }
    }

    public static DBCollection getCollection(String collectionName) throws UnknownHostException {
        System.out.println("Requested Collection : " + collectionName);
        DB db = getMongoDBConnection();
        System.out.println(db.getName());
        DBCollection table = db.getCollection(collectionName);
        System.out.println("table =" + table);
        return table;
    }

    public static DBCursor getDBCursor(String collectionName, String criteriaFieldName, String criteriaValue) throws UnknownHostException {

        BasicDBObject queryDoc = new BasicDBObject(criteriaFieldName, criteriaValue);
        return getCollection(collectionName).find(queryDoc);

    }

    public static void main(String[] str) throws UnknownHostException {

       // DataBaseAccess.getOrders("orders", "status", "new");
       // DataBaseAccess.updateCollection("orders", null,"status",   "inprocess","new", false, true);
       // DataBaseAccess.updateById("orders", "48wsudthWQvgEp4p4", "status", "inProcess");
        
        HashMap<String, String> condition = new HashMap<>();
        condition.put("_id", "GGAtWqevF7JgXpEnt");
        
                HashMap<String, String> toValue = new HashMap<>();
                toValue.put("status", "inProcess");
                toValue.put("progress", "Sheet Updated");
        
        DataBaseAccess.updateCollection("orders", condition, toValue, false, false);
      // DBCursor dbCursor = dba.getDBCursor("orders", "status", "new");
        //System.out.println ("dbCursor = " + dbCursor);
       
        /**
         * while(dbCursor.hasNext()) { DBObject dbObject = dbCursor.next();
         *
         * System.out.println (dbObject);
         *
         * for (String key :dbObject.keySet()) { System.out.println( "Key = " +
         * key); System.out.println( "Value = " + dbObject.get(key));
         *
         * }
         * }
         *
         */

    }
    
    public static void updateById(String collectionName, String documentIdToBeUpdated, String fieldNameToBeUpdated, String newValueToBe) throws UnknownHostException
    {
         DBCollection dbCollection= DataBaseAccess.getCollection(collectionName);
		BasicDBObject searchObject = new BasicDBObject();

                searchObject.put("_id", documentIdToBeUpdated);

                DBObject modifiedObject =new BasicDBObject();
		modifiedObject.put("$set", new BasicDBObject().append(fieldNameToBeUpdated, newValueToBe));
       dbCollection.update(searchObject,modifiedObject);
       
    }
    /**
     * Warning - This method is working as expected, need to spend some time on this later
     * @param collectionName
     * @param condition
     * @param fieldName
     * @param toValue
     * @param upsert
     * @param multi
     * @throws UnknownHostException 
     */
    public static  void updateCollection(   String collectionName, 
                                            HashMap<String, String> condition,
                                            String fieldName, 
                                            String toValue, 
                                            boolean upsert, 
                                            boolean multi ) throws UnknownHostException
    {
    		DBCollection dbCollection= DataBaseAccess.getCollection(collectionName);
		BasicDBObject searchObject = new BasicDBObject();
                if(condition != null && ! condition.isEmpty())
                {
                    condition.keySet().stream().forEach((key) -> {
                            searchObject.put(key, condition.get(key));
                        });
                }
		
                System.out.println("searchObject : " + searchObject);
                
		DBObject modifiedObject =new BasicDBObject();
		modifiedObject.put("$set", new BasicDBObject().append(fieldName,toValue));
		System.out.println("modifiedObject : " + modifiedObject);

		dbCollection.update(searchObject, modifiedObject,upsert,multi);
        
    }
    
        public static  void updateCollection(   String collectionName, 
                                            HashMap<String, String> condition,
                                            HashMap<String, String> toValues ,
                                            boolean upsert, 
                                            boolean multi ) throws UnknownHostException
    {
    		DBCollection dbCollection= DataBaseAccess.getCollection(collectionName);
		BasicDBObject searchObject = new BasicDBObject();
                if(condition != null && ! condition.isEmpty())
                {
                    condition.keySet().stream().forEach((key) -> {
                            searchObject.put(key, condition.get(key));
                        });
                }
		
                System.out.println("searchObject : " + searchObject);
                
		DBObject modifiedObject =new BasicDBObject();
                
                BasicDBObject dbObjectValues = new BasicDBObject();
                
                
                if(toValues !=null && !toValues.isEmpty())
                {
                    for(String key : toValues.keySet())
                    {
                        dbObjectValues.append(key, toValues.get(key));
                    }
                }
                
		modifiedObject.put("$set", dbObjectValues);
		System.out.println("modifiedObject : " + modifiedObject);

		dbCollection.update(searchObject, modifiedObject,upsert,multi);
        
    }
    


    /**
     *
     * @param collectionName
     * @param criteriaFieldName
     * @param criteriaValue
     * @return
     * @throws UnknownHostException
     */
    public static ArrayList<Order> getOrders(String collectionName, String criteriaFieldName, String criteriaValue) throws UnknownHostException {

        DBCursor dbCursor = getDBCursor(collectionName, criteriaFieldName, criteriaValue);

        ArrayList<Order> ordersList = new ArrayList<>();

        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();

            System.out.println(dbObject);

            Order order = new Order();
            
            DBObject dbObjectCustomer = (DBObject)dbObject.get("customer");
            
            BizCustomer bizCustomer = new BizCustomer();
            
                bizCustomer.setId((String) dbObjectCustomer.get("_id"));              
                BasicDBList emails=   (BasicDBList)dbObjectCustomer.get("emails");
                System.out.println(emails);
                ArrayList<Email> emailList = new ArrayList<>();
                
               for(Object dbOjectEmails: emails)
                {
                    Email email = new Email();
                    email.setAddress((String) ((DBObject)dbOjectEmails).get("address"));
                    email.setVerified((boolean) ((DBObject)dbOjectEmails).get("verified"));
                    emailList.add(email);
            
                }
                  
            bizCustomer.setEmail(emailList);
            order.setCustomer(bizCustomer);
            order.setFoodName((String) dbObject.get("foodName"));
            order.setFoodQuantity((String) dbObject.get("foodQuantity"));
            order.setId((String) dbObject.get("_id"));
            order.setOrderedAt((Date) dbObject.get("orderedAt"));
            order.setStatus((String) dbObject.get("status"));

            System.out.println(order);

            ordersList.add(order);
        }

        return ordersList;
    }

}
