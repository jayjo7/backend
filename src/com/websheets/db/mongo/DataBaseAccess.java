package com.websheets.db.mongo;


import com.mongodb.*;
import com.util.SharedService;
import java.net.UnknownHostException;
import java.util.Map;

public class DataBaseAccess {

	/**
	 *  Returns the DB connection to localhost Mongo database.
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
    public static void sync(Map<String, Map<String, String>> spreadSheetMap,String dbCollection, String uuid) throws UnknownHostException {
        DataBaseAccess.insert(spreadSheetMap, dbCollection);

    }

	public  static void insert(Map<String, Map<String, String>> spreadSheetMap ,String dbCollection
	) throws UnknownHostException {

		DB db = getMongoDBConnection();
		System.out.println(db.getName());
		DBCollection table = db.getCollection(dbCollection);
                
               
		if(spreadSheetMap != null && spreadSheetMap.size()>0)
		{

			for (String uniqueId : spreadSheetMap.keySet()) {
				System.out.println("uniqueId=" + uniqueId);
				BasicDBObject QueryDoc = new BasicDBObject("uniqueId", uniqueId);

				BasicDBObject doc = new BasicDBObject("uniqueId", uniqueId);

				for (String columName : spreadSheetMap.get(uniqueId)
						.keySet()) {
					
					doc.append(columName, spreadSheetMap.get(uniqueId).get(columName));
					
					//System.out.println(columName + "="
					//		+ spreadSheetMap.get(uniqueId).get(columName));
				}
                                
                                
				
				DBObject returnDoc = table.findAndModify(QueryDoc, doc, null, false, doc, true, true);
				System.out.println("returnDoc = " + returnDoc);
			}
		
			
		}
		else
		{
			System.out.println("DataBaseAccess::insert:" +  spreadSheetMap + " is either null or empty");

		}


	}

	public static void main(String[] str) throws UnknownHostException {

		DataBaseAccess dba = new DataBaseAccess();
		//dba.insert();

	}
        
        public static void update()
        {
            
        }

	public void find() throws UnknownHostException {
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

}
